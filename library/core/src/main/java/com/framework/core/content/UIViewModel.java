package com.framework.core.content;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.framework.core.exception.UISuperNotCalledException;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 * <p>
 * <pre>
 *     UIViewModelProviders
 *                 .of(UIPageController.class)
 *                 .get(UIViewModel.class);
 * </pre>
 */
public abstract class UIViewModel extends ViewModel
        implements UIPageControllerOwner, LifecycleOwner {
    @Nullable
    private UIPageController mUIPageController;
    private boolean mIsCalled;

    @CallSuper
    protected void onCreated(@NonNull UIPageController uiPageController) {
        this.mIsCalled = true;
    }

    @CallSuper
    @Override
    protected void onCleared() {
        // no-op
    }

    @NonNull
    @Override
    public final <T extends UIPageController> T getUIPageController() {
        final T uiPageController = (T) this.mUIPageController;
        if (uiPageController == null) {
            throw new NullPointerException("UIViewModel " + this
                    + " destroyed or not created");
        }
        return uiPageController;
    }

    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        final LifecycleOwner owner = this.mUIPageController.getUIComponent();
        return owner.getLifecycle();
    }

    final void dispatchOnCreated(@NonNull UIPageController uiPageController) {
        this.mUIPageController = uiPageController;
        this.mIsCalled = false;
        this.onCreated(uiPageController);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("UIViewModel " + this
                    + " did not call through to super.onCreated()");
        }
    }
}
