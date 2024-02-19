package com.framework.core.rx.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.framework.core.UIFramework;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * @Author create by Zhengzelong on 2022-12-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class RxPermission {

    @SuppressLint("AnnotateVersionCheck")
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isGranted(@NonNull Context context,
                                    @NonNull String permission) {
        if (!RxPermission.isMarshmallow()) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isRevoked(@NonNull Context context,
                                    @NonNull String permission) {
        if (!RxPermission.isMarshmallow()) {
            return false;
        }
        final String packageName = context.getPackageName();
        return context.getPackageManager()
                .isPermissionRevokedByPolicy(permission, packageName);
    }

    @NonNull
    public static RxPermission of(@NonNull UIPageControllerOwner owner) {
        return of(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static RxPermission of(@NonNull UIPageController uiPageController) {
        return of(uiPageController.getChildFragmentManager());
    }

    @NonNull
    public static RxPermission of(@NonNull FragmentManager fragmentManager) {
        return new RxPermission(fragmentManager);
    }

    @NonNull
    private final PermissionFragmentLazy permissionFragmentLazy;

    /* package */ RxPermission(@NonNull FragmentManager fragmentManager) {
        this.permissionFragmentLazy = new PermissionFragmentLazy(fragmentManager);
    }

    /**
     * 立即请求权限, 必须在应用程序的初始化阶段调用
     */
    @NonNull
    public Observable<Boolean> request(final String... permissions) {
        return Observable.just(permissions).compose(new ObservableTransformer<String[], Boolean>() {
            @NonNull
            @Override
            public ObservableSource<Boolean> apply(@NonNull Observable<String[]> upstream) {
                final String[] permissions = upstream.blockingLast();
                return flatMap(upstream, permissions)
                        .buffer(permissions.length)
                        .flatMap(new Function<List<Permission>, ObservableSource<Boolean>>() {
                            @Override
                            public ObservableSource<Boolean> apply(@NonNull List<Permission> permissions) {
                                if (permissions.isEmpty()) {
                                    return Observable.empty();
                                }
                                // 如果授予了所有权限, 则返回true
                                for (Permission permission : permissions) {
                                    if (!permission.isGranted()) {
                                        return Observable.just(false);
                                    }
                                }
                                return Observable.just(true);
                            }
                        });
            }
        });
    }

    /**
     * 立即请求权限, 必须在应用程序的初始化阶段调用
     */
    @NonNull
    public Observable<Permission> requestEach(final String... permissions) {
        return Observable.just(permissions).compose(new ObservableTransformer<String[], Permission>() {
            @NonNull
            @Override
            public ObservableSource<Permission> apply(@NonNull Observable<String[]> upstream) {
                return flatMap(upstream, upstream.blockingLast());
            }
        });
    }

    @NonNull
    private Observable<Permission> flatMap(@NonNull final Observable<String[]> upstream,
                                           final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("request/requestEach " +
                    "requires at least one input permission.");
        }
        return Observable.merge(upstream, this.pending(permissions))
                .flatMap(new Function<String[], Observable<Permission>>() {
                    @Override
                    public Observable<Permission> apply(@NonNull String[] permissions) {
                        return requestInner(permissions);
                    }
                });
    }

    @NonNull
    private Observable<String[]> pending(final String... permissions) {
        final RxPermissionHandler handler = this.getHandler();

        for (String permission : permissions) {
            if (!handler.containsKey(permission)) {
                return Observable.empty();
            }
        }
        return Observable.just(permissions);
    }

    @NonNull
    private Observable<Permission> requestInner(final String... permissions) {
        final List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        final RxPermissionHandler handler = this.getHandler();
        Permission permission;

        // 在多个权限的情况下,
        // 为每个权限创建一个可观察的,
        // 最后将观测值组合起来得到一个独特的响应
        for (String permissionStr : permissions) {

            if (this.isGranted(permissionStr)) {
                permission = new Permission(permissionStr, true);
                // 返回被授予的权限对象
                list.add(Observable.just(permission));
                continue;
            }

            if (this.isRevoked(permissionStr)) {
                permission = new Permission(permissionStr, false);
                // 返回被拒绝的权限对象
                list.add(Observable.just(permission));
                continue;
            }

            PublishSubject<Permission> publishSubject;
            publishSubject = handler.get(permissionStr);
            if (publishSubject == null) {
                publishSubject = PublishSubject.create();

                handler.put(permissionStr, publishSubject);
            }
            list.add(publishSubject);
        }
        if (!handler.isEmpty()) {
            final PermissionFragment permissionFragment;
            permissionFragment = this.permissionFragmentLazy.get();
            permissionFragment.requestPermissions();
        }
        return Observable.concat(Observable.fromIterable(list));
    }

    @NonNull
    public RxPermissionHandler getHandler() {
        final PermissionFragment permissionFragment;
        permissionFragment = this.permissionFragmentLazy.get();
        return permissionFragment.getHandler();
    }

    public boolean isGranted(@NonNull final String permission) {
        return isGranted(UIFramework.getApplicationContext(), permission);
    }

    public boolean isRevoked(@NonNull final String permission) {
        return isRevoked(UIFramework.getApplicationContext(), permission);
    }

    private static final class PermissionFragmentLazy {
        private static final String TAG = PermissionFragment.class.getName();
        @NonNull
        private final FragmentManager fragmentManager;

        public PermissionFragmentLazy(@NonNull FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        public PermissionFragment get() {
            final FragmentManager fragmentManager = this.fragmentManager;
            PermissionFragment fragment;
            fragment = (PermissionFragment) fragmentManager.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new PermissionFragment();

                fragmentManager.beginTransaction()
                        .add(fragment, TAG)
                        .commitNow();
            }
            return fragment;
        }
    }
}
