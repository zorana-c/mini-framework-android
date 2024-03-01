package com.framework.core.content;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.framework.core.R;
import com.framework.core.exception.UILayoutException;
import com.framework.core.widget.UIDecorLayout;
import com.navigation.UINavigatorOptions;

import retrofit2.HttpException;

/**
 * @Author create by Zhengzelong on 2021/12/1
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDecorOptions implements UIPageOptions {
    public static final long MS_ANIM = 400L;

    interface RuntimeAction {
        void apply(@NonNull UIDecorController uiDecorController);
    }

    private boolean mIsPendingShowing = false;

    @Override
    @CallSuper
    public void apply(@NonNull UIPageController uiPageController) {
        uiPageController
                .getUINavigatorController()
                .setUINavigatorOptions(UINavigatorOptions.DEFAULT);

        if (uiPageController.isCreatedView()) {
            this.applyPageOptions(uiPageController);

            if (uiPageController instanceof UIDecorController) {
                this.mIsPendingShowing = true;
                this.applyDecorOptions((UIDecorController) uiPageController);
            }
            if (uiPageController instanceof UIListController) {
                this.mIsPendingShowing = false;
                this.applyListOptions((UIListController<?>) uiPageController);
            }
        }
    }

    @CallSuper
    protected void applyPageOptions(@NonNull UIPageController uiPageController) {
        // nothing
    }

    @CallSuper
    protected void applyDecorOptions(@NonNull UIDecorController uiDecorController) {
        final UIDecorLayout uiDecorLayout = uiDecorController.getUIDecorLayout();
        uiDecorLayout.setEnterAnimation(R.anim.anim_alpha_action_v);
        uiDecorLayout.setExitAnimation(R.anim.anim_alpha_action_inv);

        uiDecorController.setErrorView(R.layout.ui_decor_error_layout);
        uiDecorController.setEmptyView(R.layout.ui_decor_empty_layout);
        uiDecorController.setLoadingView(R.layout.ui_decor_loading_layout);
        uiDecorController.layoutLoading();
        UIDecorStateLifecycle.bind(uiDecorController, (controller) -> {
            if (this.mIsPendingShowing) {
                final int currentDecorKey = controller.getCurrentDecorKey();
                final int pendingDecorKey = controller.getPendingDecorKey();

                if (controller.isCreatedView()
                        && UIDecorLayout.INVALID_DECOR == pendingDecorKey
                        && UIDecorLayout.DECOR_LOADING == currentDecorKey) {
                    controller.postContentOnAnimation(MS_ANIM);
                }
            }
        });

        final View reloading;
        reloading = uiDecorController.findViewById(R.id.uiDecorErrorReloading);
        if (reloading != null) {
            reloading.setOnClickListener(new RefreshClickListener(uiDecorController));
        }
    }

    @CallSuper
    protected void applyListOptions(@NonNull UIListController<?> uiListDecorController) {
        uiListDecorController.setEmptyComponent(R.layout.ui_decor_list_empty_layout);
    }

    @CallSuper
    public void applyException(@NonNull UIDecorController uiDecorController,
                               @NonNull Throwable throwable) {
        final UILayoutException uiLayoutException;
        if (throwable instanceof UILayoutException) {
            uiLayoutException = (UILayoutException) throwable;
        } else if (throwable instanceof HttpException) {
            uiLayoutException = new UILayoutException((HttpException) throwable);
        } else {
            uiLayoutException = new UILayoutException(throwable.getMessage(), throwable);
        }
        uiLayoutException.printStackTrace();

        final TextView uiDecorErrorCode;
        uiDecorErrorCode = uiDecorController.findViewById(R.id.uiDecorErrorCode);
        if (uiDecorErrorCode != null) {
            uiDecorErrorCode.setText(uiLayoutException.formatCodeString());
        }
    }

    private static final class UIDecorStateLifecycle implements DefaultLifecycleObserver {

        public static void bind(@NonNull UIDecorController uiDecorController,
                                @NonNull RuntimeAction runtimeAction) {
            new UIDecorStateLifecycle(uiDecorController, runtimeAction);
        }

        private final NetworkStateReceiver mNetworkStateReceiver;
        private final UIDecorController mUIDecorController;
        private final RuntimeAction mRuntimeAction;
        private boolean mIsExecutedPauseAfter;

        public UIDecorStateLifecycle(@NonNull UIDecorController uiDecorController,
                                     @NonNull RuntimeAction runtimeAction) {
            this.mNetworkStateReceiver = new NetworkStateReceiver(uiDecorController);
            this.mUIDecorController = uiDecorController;
            this.mRuntimeAction = runtimeAction;

            final LifecycleOwner owner = uiDecorController.getUIComponent();
            final Lifecycle l = owner.getLifecycle();
            l.addObserver(this);
        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            if (this.mNetworkStateReceiver != null) {
                this.mNetworkStateReceiver.register();
            }
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            final UIDecorController uiDecorController = this.mUIDecorController;
            final int currentDecorKey = uiDecorController.getCurrentDecorKey();
            final int pendingDecorKey = uiDecorController.getPendingDecorKey();

            if (this.mIsExecutedPauseAfter
                    && UIDecorLayout.INVALID_DECOR == pendingDecorKey
                    && UIDecorLayout.DECOR_LOADING == currentDecorKey) {
                this.mIsExecutedPauseAfter = false;
                uiDecorController.notifyDataSetRefresh();
            } else {
                this.mRuntimeAction.apply(uiDecorController);
            }
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            this.mIsExecutedPauseAfter = true;
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.removeObserver(this);

            if (this.mNetworkStateReceiver != null) {
                this.mNetworkStateReceiver.unregister();
            }
        }
    }

    private static final class NetworkStateReceiver extends BroadcastReceiver {
        private final UIDecorController mUIDecorController;
        private final IntentFilter mIntentFilter;
        private boolean mLastNetworkConnected;

        public NetworkStateReceiver(@NonNull UIDecorController uiDecorController) {
            this.mUIDecorController = uiDecorController;
            this.mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        }

        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                final boolean newNetworkConnected = this.isNetworkConnected(context);
                final boolean oldNetworkConnected = this.mLastNetworkConnected;
                /*
                 * 当前布局是错误布局的时候, 才触发下面事件
                 * 1.显示加载中的布局
                 * 2.执行布局刷新方法
                 * */
                if (oldNetworkConnected == newNetworkConnected) {
                    return;
                }
                this.mLastNetworkConnected = newNetworkConnected;
                if (oldNetworkConnected) {
                    return;
                }
                final UIDecorController uiDecorController = this.mUIDecorController;
                final int currentDecorKey = uiDecorController.getCurrentDecorKey();
                final int pendingDecorKey = uiDecorController.getPendingDecorKey();

                if (UIDecorLayout.DECOR_ERROR == pendingDecorKey
                        || UIDecorLayout.DECOR_ERROR == currentDecorKey) {
                    uiDecorController.layoutLoadingOnAnimation();
                    uiDecorController.notifyDataSetRefresh();
                }
            }
        }

        @SuppressLint("MissingPermission")
        private boolean isNetworkConnected(@NonNull Context context) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }
            final NetworkInfo[] networkInfoArr = connectivityManager.getAllNetworkInfo();
            if (networkInfoArr == null) {
                return false;
            }
            for (final NetworkInfo networkInfo : networkInfoArr) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
            return false;
        }

        private void register() {
            final Context context = UIPageControllers.getContext(this.mUIDecorController);
            if (context != null) {
                context.registerReceiver(this, this.mIntentFilter);
            }
        }

        private void unregister() {
            final Context context = UIPageControllers.getContext(this.mUIDecorController);
            if (context != null) {
                context.unregisterReceiver(this);
            }
        }
    }

    public static final class RefreshClickListener implements View.OnClickListener {
        private final UIDecorController mUIDecorController;

        public RefreshClickListener(@NonNull UIDecorController uiDecorController) {
            this.mUIDecorController = uiDecorController;
        }

        @Override
        public void onClick(@NonNull View view) {
            final UIDecorController uiDecorController = this.mUIDecorController;
            final int currentDecorKey = uiDecorController.getCurrentDecorKey();
            final int pendingDecorKey = uiDecorController.getPendingDecorKey();

            if (uiDecorController.isCreatedView()
                    && UIDecorLayout.DECOR_LOADING != pendingDecorKey
                    && UIDecorLayout.DECOR_LOADING != currentDecorKey) {
                uiDecorController.layoutLoadingOnAnimation();
                uiDecorController.notifyDataSetRefresh();
            }
        }
    }
}
