package com.framework.core.content;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

import com.framework.core.R;
import com.framework.core.compat.UIRes;
import com.framework.core.exception.UISuperNotCalledException;
import com.navigation.UINavigatorController;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIPageController {
    private static final String KEY_VIEW_CACHE = "androidx:controller:cache";

    public interface UIComponent extends ActivityResultCaller,
            UIPageControllerOwner,
            ViewModelStoreOwner,
            LifecycleOwner {

        @LayoutRes
        int onUILayoutId(@Nullable Bundle savedInstanceState);

        void onUICreated(@Nullable Bundle savedInstanceState);

        void onUIRefresh(@Nullable Bundle savedInstanceState);

        @Nullable
        default View createView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
            return this.getUIPageController()
                    .createView(inflater, container, savedInstanceState);
        }

        default void notifyDataSetRefresh() {
            this.getUIPageController().notifyDataSetRefresh();
        }

        @NonNull
        default UIViewModelProvider getUIViewModelProvider() {
            return this.getUIPageController().getUIViewModelProvider();
        }

        @NonNull
        default UINavigatorController getUINavigatorController() {
            return this.getUIPageController().getUINavigatorController();
        }

        @NonNull
        default <T extends UIViewModel> T getViewModel(@NonNull Class<T> tClass) {
            return this.getUIViewModelProvider().get(tClass);
        }

        @NonNull
        default <T extends UIViewModel> T getViewModel(@NonNull String key,
                                                       @NonNull Class<T> tClass) {
            return this.getUIViewModelProvider().get(key, tClass);
        }
    }

    private View mContentView;
    private Bundle mSavedState;
    private Handler mUIHandler;
    private UIComponent mUIComponent;
    private UIPageOptions mUIPageOptions;
    private UIViewModelProvider mUIViewModelProvider;
    private UINavigatorController mUINavigatorController;

    private boolean mIsCalled;
    private boolean mIsCreated;
    private boolean mIsRefresh;
    private boolean mIsCreatedView;
    private boolean mIsUseViewCache;

    public UIPageController(@NonNull UIComponent uiComponent) {
        this.mUIComponent = uiComponent;
    }

    @CallSuper
    protected void onInit(@NonNull Object target,
                          @Nullable Bundle savedInstanceState) {
        this.mIsCalled = true;
    }

    @Nullable
    protected View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable View contentView,
                                @Nullable Bundle savedInstanceState) {
        return contentView;
    }

    @CallSuper
    protected void onViewCreated(@NonNull View contentView,
                                 @Nullable Bundle savedInstanceState) {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onCreated(@Nullable Bundle savedInstanceState) {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onRefresh(@Nullable Bundle savedInstanceState) {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onSaveInstanceState(@NonNull Bundle outInstanceState) {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onStart() {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onResume() {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onPause() {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onStop() {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onDestroyView() {
        this.mIsCalled = true;
    }

    @CallSuper
    protected void onDestroy() {
        this.mIsCalled = true;
    }

    public final boolean isCreated() {
        return this.mIsCreated;
    }

    public final boolean isRefresh() {
        return this.mIsRefresh;
    }

    public final boolean isCreatedView() {
        return this.mIsCreatedView;
    }

    public final boolean isUseViewCache() {
        return this.mIsUseViewCache;
    }

    public final void setUseViewCache(boolean useViewCache) {
        this.mIsUseViewCache = useViewCache;
    }

    @Nullable
    public <T extends View> T getContentView() {
        return (T) this.mContentView;
    }

    @NonNull
    public final <T extends View> T requireContentView() {
        final View contentView = this.getContentView();
        if (contentView == null) {
            throw new NullPointerException("Controller " + this
                    + " the page does not view set");
        }
        return (T) contentView;
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        final View contentView = this.getContentView();
        if (contentView == null) {
            return null;
        }
        return contentView.findViewById(id);
    }

    @NonNull
    public final <T extends View> T requireViewById(@IdRes int id) {
        final View view = this.findViewById(id);
        if (view == null) {
            throw new NullPointerException("Controller " + this
                    + " the view with ID " + id + " was not found");
        }
        return (T) view;
    }

    @NonNull
    public final <T extends UIComponent> T getUIComponent() {
        final UIComponent uiComponent;
        uiComponent = this.mUIComponent;
        if (uiComponent == null) {
            throw new NullPointerException("Controller " + this
                    + " the page does not implement"
                    + " the UIComponent interface");
        }
        return (T) uiComponent;
    }

    @NonNull
    public final <T extends UIPageOptions> T getUIPageOptions() {
        final UIPageOptions uiPageOptions;
        uiPageOptions = this.mUIPageOptions;
        if (uiPageOptions == null) {
            throw new NullPointerException("Controller " + this
                    + " the page has not been created yet");
        }
        return (T) uiPageOptions;
    }

    @NonNull
    public final UIViewModelProvider getUIViewModelProvider() {
        final UIViewModelProvider uiViewModelProvider;
        uiViewModelProvider = this.mUIViewModelProvider;
        if (uiViewModelProvider == null) {
            throw new NullPointerException("Controller " + this
                    + " the page has not been init yet");
        }
        return uiViewModelProvider;
    }

    @NonNull
    public final UINavigatorController getUINavigatorController() {
        final UINavigatorController uiNavigatorController;
        uiNavigatorController = this.mUINavigatorController;
        if (uiNavigatorController == null) {
            throw new NullPointerException("Controller " + this
                    + " the page has not been init yet");
        }
        return uiNavigatorController;
    }

    @NonNull
    public final Bundle getArguments() {
        return UINavigatorController.getArguments(this);
    }

    @Nullable
    public final Context getContext() {
        return UIPageControllers.getContext(this);
    }

    @NonNull
    public final Context requireContext() {
        return UIPageControllers.requireContext(this);
    }

    @Nullable
    public final FragmentActivity getFragmentActivity() {
        return UIPageControllers.getFragmentActivity(this);
    }

    @NonNull
    public final FragmentActivity requireFragmentActivity() {
        return UIPageControllers.requireFragmentActivity(this);
    }

    @Nullable
    public final UIPageController getUIHostController() {
        return UIPageControllers.getUIHostController(this);
    }

    @NonNull
    public final UIPageController requireUIHostController() {
        return UIPageControllers.requireUIHostController(this);
    }

    @Nullable
    public final UIPageController getUIParentController() {
        return UIPageControllers.getUIParentController(this);
    }

    @NonNull
    public final UIPageController requireUIParentController() {
        return UIPageControllers.requireUIParentController(this);
    }

    @NonNull
    public final FragmentManager getChildFragmentManager() {
        return UIPageControllers.getChildFragmentManager(this);
    }

    @NonNull
    public final FragmentManager getParentFragmentManager() {
        return UIPageControllers.getParentFragmentManager(this);
    }

    @Nullable
    public final UIActivityDispatcher getUIActivityDispatcher() {
        return UIPageControllers.getUIActivityDispatcher(this);
    }

    @NonNull
    public final UIActivityDispatcher requireUIActivityDispatcher() {
        return UIPageControllers.requireUIActivityDispatcher(this);
    }

    @NonNull
    public final UIPageController post(@NonNull Runnable action) {
        return this.postDelayed(action, 0L);
    }

    @NonNull
    public final UIPageController postDelayed(@NonNull Runnable action,
                                              long delayMillis) {
        synchronized (UIPageController.class) {
            if (this.mUIHandler == null) {
                this.mUIHandler = new Handler(Looper.getMainLooper());
            }
            this.mUIHandler.removeCallbacks(action);
            this.mUIHandler.postDelayed(action, delayMillis);
        }
        return this;
    }

    @NonNull
    public final UIPageController removeCallbacks(@NonNull Runnable action) {
        if (this.mUIHandler != null) {
            this.mUIHandler.removeCallbacks(action);
        }
        return this;
    }

    @NonNull
    public final UIPageController removeCallbacksAndMessages() {
        if (this.mUIHandler != null) {
            this.mUIHandler.removeCallbacksAndMessages(null);
        }
        return this;
    }

    @NonNull
    public final UIPageController notifyDataSetRefresh() {
        final UIComponent uiComponent = this.mUIComponent;
        final Lifecycle lifecycle = uiComponent.getLifecycle();
        final Lifecycle.State state = lifecycle.getCurrentState();
        if (Lifecycle.State.RESUMED == state) {
            this.performRefresh(this.mSavedState);
        } else {
            this.mIsRefresh = false;
        }
        return this;
    }

    @Nullable
    final View createView(@NonNull LayoutInflater inflater,
                          @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {
        View contentView = this.mContentView;
        if (contentView != null) {
            contentView.clearAnimation();
            final ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null) {
                parent.removeView(contentView);
            }
            return contentView;
        }
        final UIComponent uiComponent = this.mUIComponent;
        final int layoutId = uiComponent.onUILayoutId(savedInstanceState);
        if (Resources.ID_NULL != layoutId) {
            contentView = inflater.inflate(layoutId, container, false);
        }
        contentView = this.onCreateView(
                inflater, container, contentView, savedInstanceState);
        return contentView;
    }

    final void performInit(@NonNull Object target,
                           @Nullable Bundle savedInstanceState) {
        this.mSavedState = savedInstanceState;
        this.mUIViewModelProvider = new UIViewModelProvider(this);
        this.mUINavigatorController = new UINavigatorController(this);
        if (savedInstanceState == null) {
            this.mIsUseViewCache = true;
        } else {
            this.mIsUseViewCache = savedInstanceState.getBoolean(KEY_VIEW_CACHE);
        }
        this.mIsCalled = false;
        this.onInit(target, savedInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onInit()");
        }
    }

    final void performViewCreated(@NonNull View contentView,
                                  @Nullable Bundle savedInstanceState) {
        if (contentView.getBackground() == null) {
            contentView.setBackgroundColor(UIRes.getColor(R.color.decorBackground));
        }
        this.mContentView = contentView;
        this.mIsCreatedView = true;
        this.mIsCalled = false;
        this.onViewCreated(contentView, savedInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onViewCreated()");
        }
    }

    final void performCreated(@Nullable Bundle savedInstanceState) {
        this.mUIPageOptions = UIPagePlugins.apply(this);
        this.mIsCreated = true;
        this.mIsCalled = false;
        this.onCreated(savedInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onCreated()");
        }
        this.mUIComponent.onUICreated(savedInstanceState);
    }

    final void performRefresh(@Nullable Bundle savedInstanceState) {
        this.mSavedState = null;
        this.mIsRefresh = true;
        this.mIsCalled = false;
        this.onRefresh(savedInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onRefresh()");
        }
        this.mUIComponent.onUIRefresh(savedInstanceState);
    }

    final void performSaveInstanceState(@NonNull Bundle outInstanceState) {
        this.mIsCalled = false;
        this.onSaveInstanceState(outInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onSaveInstanceState()");
        }
        outInstanceState.putBoolean(KEY_VIEW_CACHE, this.mIsUseViewCache);
    }

    final void performRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        this.mIsCalled = false;
        this.onRestoreInstanceState(savedInstanceState);
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onRestoreInstanceState()");
        }
    }

    final void performStart() {
        this.mIsCalled = false;
        this.onStart();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onStart()");
        }
    }

    final void performResume() {
        this.mIsCalled = false;
        this.onResume();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onResume()");
        }
        if (!this.mIsRefresh) {
            this.performRefresh(this.mSavedState);
        }
    }

    final void performPause() {
        this.mIsCalled = false;
        this.onPause();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onPause()");
        }
    }

    final void performStop() {
        this.mIsCalled = false;
        this.onStop();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onStop()");
        }
    }

    final void performDestroyView() {
        this.mIsCreatedView = false;
        this.mIsRefresh = false;
        this.mIsCalled = false;
        this.onDestroyView();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onDestroyView()");
        }
        this.mContentView = null;
        this.mUIPageOptions = null;
    }

    final void performDestroy() {
        this.mIsCreated = false;
        this.mIsCalled = false;
        this.onDestroy();
        if (!this.mIsCalled) {
            throw new UISuperNotCalledException("Controller " + this
                    + " did not call through to super.onDestroy()");
        }
        if (this.mUIHandler != null) {
            this.mUIHandler.removeCallbacksAndMessages(null);
            this.mUIHandler = null;
        }
        this.mSavedState = null;
        this.mUIComponent = null;
        this.mUIViewModelProvider = null;
        this.mUINavigatorController = null;
    }
}
