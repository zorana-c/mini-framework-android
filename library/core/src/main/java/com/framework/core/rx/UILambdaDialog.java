package com.framework.core.rx;

import android.os.Looper;

import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.ui.dialog.UIProgressDialogFragment;
import com.navigation.UINavigatorController;
import com.navigation.floating.UIDialogFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;

/**
 * @Author create by Zhengzelong on 2023-06-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UILambdaDialog<T> implements UIDialogCallback<T> {
    @NonNull
    private final UIPageController uiPageController;
    private final boolean cancelable;
    private final boolean allowLossRequest;

    @Nullable
    private Disposable upstream;
    @Nullable
    private UIDialogFragment uiDialogFragment;
    @Nullable
    private ComponentListener componentListener;

    protected UILambdaDialog(@NonNull Builder builder) {
        this.cancelable = builder.cancelable;
        this.allowLossRequest = builder.allowLossRequest;
        this.uiPageController = builder.uiPageController;
    }

    @Override
    public void onSubscribe(@NonNull Disposable upstream) {
        if (DisposableHelper.validate(this.upstream, upstream)) {
            this.upstream = upstream;

            if (!upstream.isDisposed()) {
                this.showProgressDialog();
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        this.dismissProgressDialog();
    }

    @Override
    public void onComplete() {
        this.dismissProgressDialog();
    }

    @Override
    public void onDispose() {
        if (this.upstream != DisposableHelper.DISPOSED) {
            this.dismissProgressDialog();
        }
    }

    public final boolean isCancelable() {
        return this.cancelable;
    }

    public final boolean isAllowLossRequest() {
        return this.allowLossRequest;
    }

    @NonNull
    public final UIPageController getUiPageController() {
        return this.uiPageController;
    }

    private void disposeFromDismiss() {
        final Disposable upstream = this.upstream;
        if (this.upstream != DisposableHelper.DISPOSED) {
            this.upstream = DisposableHelper.DISPOSED;

            if (this.allowLossRequest) {
                if (upstream != null) {
                    upstream.dispose();
                }
            }
        }
    }

    private void showProgressDialog() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            AndroidSchedulers.mainThread().scheduleDirect(this::showProgressDialog);
            return;
        }
        if (this.componentListener == null) {
            this.componentListener = new ComponentListener();
        }
        this.uiDialogFragment = new UIProgressDialogFragment();
        this.uiDialogFragment.addOnCancelListener(this.componentListener);
        this.uiDialogFragment.addOnDismissListener(this.componentListener);
        this.uiDialogFragment.setCancelable(this.cancelable);
        this.uiDialogFragment.showNow(this.uiPageController);
    }

    private void dismissProgressDialog() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            AndroidSchedulers.mainThread().scheduleDirect(this::dismissProgressDialog);
            return;
        }
        final UIDialogFragment uiDialogFragment = this.uiDialogFragment;
        if (uiDialogFragment != null) {
            try {
                UINavigatorController.navigateFragmentUpImmediate(uiDialogFragment);
            } catch (@NonNull Exception e) {
                uiDialogFragment.dismiss();
            }
        }
    }

    private final class ComponentListener implements UIDialogFragment.OnCancelListener,
            UIDialogFragment.OnDismissListener {
        @Override
        public void onCancel(@NonNull UIDialogFragment uiDialogFragment) {
            uiDialogFragment.removeOnCancelListener(this);
            UILambdaDialog.this.disposeFromDismiss();
        }

        @Override
        public void onDismiss(@NonNull UIDialogFragment uiDialogFragment) {
            uiDialogFragment.removeOnDismissListener(this);
            UILambdaDialog.this.disposeFromDismiss();
        }
    }

    public static class Builder {
        @NonNull
        private final UIPageController uiPageController;
        private boolean cancelable = true;
        private boolean allowLossRequest = true;

        public Builder(@NonNull UIPageControllerOwner owner) {
            this(owner.<UIPageController>getUIPageController());
        }

        public Builder(@NonNull UIPageController uiPageController) {
            this.uiPageController = uiPageController;
        }

        /**
         * @param cancelable 提示对话框阴影部分是否可点击取消
         */
        @NonNull
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * @param allowLossRequest 提示对话框被取消的同时是否允许取消响应
         */
        @NonNull
        public Builder setAllowLossRequest(boolean allowLossRequest) {
            this.allowLossRequest = allowLossRequest;
            return this;
        }

        @NonNull
        public <T> UILambdaDialog<T> build() {
            return new UILambdaDialog<>(this);
        }
    }
}
