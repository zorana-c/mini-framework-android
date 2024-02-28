package com.framework.core.content;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.R;
import com.framework.core.widget.UIDecorLayout;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDecorController extends UIPageController {

    public interface UIComponent extends UIPageController.UIComponent {

        @NonNull
        default UIActionBarController getUIActionBarController() {
            return this.<UIDecorController>getUIPageController().getUIActionBarController();
        }
    }

    private UIDecorLayout mUIDecorLayout;
    private UIActionBarController mUIActionBarController;

    public UIDecorController(@NonNull UIComponent uiComponent) {
        super(uiComponent);
    }

    @Nullable
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable View contentView,
                                @Nullable Bundle savedInstanceState) {
        final UIDecorLayout uiDecorLayout = new UIDecorLayout(inflater.getContext());
        uiDecorLayout.setId(R.id.uiDecorContainer);
        uiDecorLayout.setContentView(contentView);
        return uiDecorLayout;
    }

    @Override
    protected void onViewCreated(@NonNull View contentView,
                                 @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);
        this.mUIActionBarController = new UIActionBarControllerImpl(this);
    }

    @Override
    protected void onCreated(@Nullable Bundle savedInstanceState) {
        super.onCreated(savedInstanceState);
        final int currentDecorKey = this.getCurrentDecorKey();
        final int pendingDecorKey = this.getPendingDecorKey();
        if (this.isCreatedView()
                && UIDecorLayout.INVALID_DECOR == pendingDecorKey
                && UIDecorLayout.INVALID_DECOR == currentDecorKey) {
            this.layoutContent();
        }
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        this.mUIDecorLayout = null;
        this.mUIActionBarController = null;
    }

    @Nullable
    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        T child = super.findViewById(id);
        if (child == null) {
            final UIDecorLayout uiDecorLayout;
            uiDecorLayout = this.getUIDecorLayout();
            child = uiDecorLayout.findViewTraversalBy(id);
        }
        return child;
    }

    @NonNull
    public <T extends UIDecorLayout> T getUIDecorLayout() {
        if (this.mUIDecorLayout != null) {
            return (T) this.mUIDecorLayout;
        }
        T uiDecorLayout = null;
        final View root = this.getContentView();
        if (root != null) {
            uiDecorLayout = root.findViewById(R.id.uiDecorContainer);
        }
        if (uiDecorLayout == null) {
            throw new NullPointerException("NOT DECOR VIEW.");
        }
        return (T) (this.mUIDecorLayout = uiDecorLayout);
    }

    @NonNull
    public <T extends UIActionBarController> T getUIActionBarController() {
        final T uiActionBarController = (T) this.mUIActionBarController;
        if (uiActionBarController == null) {
            throw new NullPointerException("NOT DECOR VIEW.");
        }
        return uiActionBarController;
    }

    @Nullable
    public final <T extends View> T getActionBar() {
        return this.getUIDecorLayout().getActionBar();
    }

    @NonNull
    public final <T extends View> T requireActionBar() {
        return this.getUIDecorLayout().requireActionBar();
    }

    @NonNull
    public final UIDecorController setActionBar(@LayoutRes int layoutId) {
        this.getUIDecorLayout().setActionBar(layoutId);
        return this;
    }

    @NonNull
    public final UIDecorController setActionBar(@NonNull View actionBar) {
        this.getUIDecorLayout().setActionBar(actionBar);
        return this;
    }

    public final int getCurrentDecorKey() {
        return this.getUIDecorLayout().getCurrentDecorKey();
    }

    public final int getPendingDecorKey() {
        return this.getUIDecorLayout().getPendingDecorKey();
    }

    @Nullable
    public final <T extends View> T findDecorViewByKey(int decorKey) {
        return this.getUIDecorLayout().findDecorViewByKey(decorKey);
    }

    @NonNull
    public final <T extends View> T requireDecorViewByKey(int decorKey) {
        return this.getUIDecorLayout().requireDecorViewByKey(decorKey);
    }

    @NonNull
    public final UIDecorController addDecorView(int decorKey, @LayoutRes int layoutId) {
        this.getUIDecorLayout().addDecorView(decorKey, layoutId);
        return this;
    }

    @NonNull
    public final UIDecorController addDecorView(int decorKey, @Nullable View child) {
        this.getUIDecorLayout().addDecorView(decorKey, child);
        return this;
    }

    @NonNull
    public final UIDecorController removeDecorViewByKey(int decorKey) {
        this.getUIDecorLayout().removeDecorViewByKey(decorKey);
        return this;
    }

    @NonNull
    public final UIDecorController removeDecorView(@NonNull View child) {
        this.getUIDecorLayout().removeDecorView(child);
        return this;
    }

    @NonNull
    public final UIDecorController setLoadingView(@LayoutRes int layoutId) {
        return this.addDecorView(UIDecorLayout.DECOR_LOADING, layoutId);
    }

    @NonNull
    public final UIDecorController setLoadingView(@Nullable View child) {
        return this.addDecorView(UIDecorLayout.DECOR_LOADING, child);
    }

    @NonNull
    public final UIDecorController setEmptyView(@LayoutRes int layoutId) {
        return this.addDecorView(UIDecorLayout.DECOR_EMPTY, layoutId);
    }

    @NonNull
    public final UIDecorController setEmptyView(@Nullable View child) {
        return this.addDecorView(UIDecorLayout.DECOR_EMPTY, child);
    }

    @NonNull
    public final UIDecorController setErrorView(@LayoutRes int layoutId) {
        return this.addDecorView(UIDecorLayout.DECOR_ERROR, layoutId);
    }

    @NonNull
    public final UIDecorController setErrorView(@Nullable View child) {
        return this.addDecorView(UIDecorLayout.DECOR_ERROR, child);
    }

    @NonNull
    public final UIDecorController fitsParentLayouts(int decorKey) {
        this.getUIDecorLayout().fitsParentLayouts(decorKey);
        return this;
    }

    @NonNull
    public final UIDecorController setFitsParentLayouts(int decorKey, boolean fitsParentLayouts) {
        this.getUIDecorLayout().setFitsParentLayouts(decorKey, fitsParentLayouts);
        return this;
    }

    @NonNull
    public final UIDecorController setFitsParentLayouts(@NonNull View child, boolean fitsParentLayouts) {
        this.getUIDecorLayout().setFitsParentLayouts(child, fitsParentLayouts);
        return this;
    }

    @NonNull
    public final UIDecorController addOnLayoutChangedListener(@NonNull UIDecorLayout.OnLayoutChangedListener listener) {
        this.getUIDecorLayout().addOnLayoutChangedListener(listener);
        return this;
    }

    @NonNull
    public final UIDecorController removeOnLayoutChangedListener(@NonNull UIDecorLayout.OnLayoutChangedListener listener) {
        this.getUIDecorLayout().removeOnLayoutChangedListener(listener);
        return this;
    }

    // content.

    @NonNull
    public final UIDecorController layoutContent() {
        return this.layoutBy(UIDecorLayout.DECOR_CONTENT);
    }

    @NonNull
    public final UIDecorController layoutContentOnAnimation() {
        return this.layoutOnAnimationBy(UIDecorLayout.DECOR_CONTENT);
    }

    @NonNull
    public final UIDecorController postContent() {
        return this.postContent(0L);
    }

    @NonNull
    public final UIDecorController postContent(long delayedMillis) {
        return this.postLayoutBy(UIDecorLayout.DECOR_CONTENT, delayedMillis);
    }

    @NonNull
    public final UIDecorController postContentOnAnimation() {
        return this.postContentOnAnimation(0L);
    }

    @NonNull
    public final UIDecorController postContentOnAnimation(long delayedMillis) {
        return this.postLayoutOnAnimationBy(UIDecorLayout.DECOR_CONTENT, delayedMillis);
    }

    // loading.

    @NonNull
    public final UIDecorController layoutLoading() {
        return this.layoutBy(UIDecorLayout.DECOR_LOADING);
    }

    @NonNull
    public final UIDecorController layoutLoadingOnAnimation() {
        return this.layoutOnAnimationBy(UIDecorLayout.DECOR_LOADING);
    }

    @NonNull
    public final UIDecorController postLoading() {
        return this.postLoading(0L);
    }

    @NonNull
    public final UIDecorController postLoading(long delayedMillis) {
        return this.postLayoutBy(UIDecorLayout.DECOR_LOADING, delayedMillis);
    }

    @NonNull
    public final UIDecorController postLoadingOnAnimation() {
        return this.postLoadingOnAnimation(0L);
    }

    @NonNull
    public final UIDecorController postLoadingOnAnimation(long delayedMillis) {
        return this.postLayoutOnAnimationBy(UIDecorLayout.DECOR_LOADING, delayedMillis);
    }

    // empty.

    @NonNull
    public final UIDecorController layoutEmpty() {
        return this.layoutBy(UIDecorLayout.DECOR_EMPTY);
    }

    @NonNull
    public final UIDecorController layoutEmptyOnAnimation() {
        return this.layoutOnAnimationBy(UIDecorLayout.DECOR_EMPTY);
    }

    @NonNull
    public final UIDecorController postEmpty() {
        return this.postEmpty(0L);
    }

    @NonNull
    public final UIDecorController postEmpty(long delayedMillis) {
        return this.postLayoutBy(UIDecorLayout.DECOR_EMPTY, delayedMillis);
    }

    @NonNull
    public final UIDecorController postEmptyOnAnimation() {
        return this.postEmptyOnAnimation(0L);
    }

    @NonNull
    public final UIDecorController postEmptyOnAnimation(long delayedMillis) {
        return this.postLayoutOnAnimationBy(UIDecorLayout.DECOR_EMPTY, delayedMillis);
    }

    // error.

    @NonNull
    public final UIDecorController layoutError() {
        return this.layoutBy(UIDecorLayout.DECOR_ERROR);
    }

    @NonNull
    public final UIDecorController layoutError(@NonNull Throwable t) {
        final UIPageOptions uiPageOptions = this.getUIPageOptions();
        if (uiPageOptions instanceof UIDecorOptions) {
            ((UIDecorOptions) uiPageOptions).applyException(this, t);
        }
        return this.layoutError();
    }

    @NonNull
    public final UIDecorController layoutErrorOnAnimation() {
        return this.layoutOnAnimationBy(UIDecorLayout.DECOR_ERROR);
    }

    @NonNull
    public final UIDecorController layoutErrorOnAnimation(@NonNull Throwable t) {
        final UIPageOptions uiPageOptions = this.getUIPageOptions();
        if (uiPageOptions instanceof UIDecorOptions) {
            ((UIDecorOptions) uiPageOptions).applyException(this, t);
        }
        return this.layoutErrorOnAnimation();
    }

    @NonNull
    public final UIDecorController postError() {
        return this.postError(0L);
    }

    @NonNull
    public final UIDecorController postError(long delayedMillis) {
        return this.postLayoutBy(UIDecorLayout.DECOR_ERROR, delayedMillis);
    }

    @NonNull
    public final UIDecorController postError(@NonNull Throwable t) {
        return this.postError(t, 0L);
    }

    @NonNull
    public final UIDecorController postError(@NonNull Throwable t, long delayedMillis) {
        final UIPageOptions uiPageOptions = this.getUIPageOptions();
        if (uiPageOptions instanceof UIDecorOptions) {
            ((UIDecorOptions) uiPageOptions).applyException(this, t);
        }
        return this.postError(delayedMillis);
    }

    @NonNull
    public final UIDecorController postErrorOnAnimation() {
        return this.postErrorOnAnimation(0L);
    }

    @NonNull
    public final UIDecorController postErrorOnAnimation(long delayedMillis) {
        return this.postLayoutOnAnimationBy(UIDecorLayout.DECOR_ERROR, delayedMillis);
    }

    @NonNull
    public final UIDecorController postErrorOnAnimation(@NonNull Throwable t) {
        return this.postErrorOnAnimation(t, 0L);
    }

    @NonNull
    public final UIDecorController postErrorOnAnimation(@NonNull Throwable t, long delayedMillis) {
        final UIPageOptions uiPageOptions = this.getUIPageOptions();
        if (uiPageOptions instanceof UIDecorOptions) {
            ((UIDecorOptions) uiPageOptions).applyException(this, t);
        }
        return this.postErrorOnAnimation(delayedMillis);
    }

    // custom.

    @NonNull
    public final UIDecorController layoutBy(int decorLayoutKey) {
        this.getUIDecorLayout().layoutBy(decorLayoutKey);
        return this;
    }

    @NonNull
    public final UIDecorController layoutOnAnimationBy(int decorLayoutKey) {
        this.getUIDecorLayout().layoutOnAnimationBy(decorLayoutKey);
        return this;
    }

    @NonNull
    public final UIDecorController postLayoutBy(int decorLayoutKey) {
        this.getUIDecorLayout().postLayoutBy(decorLayoutKey);
        return this;
    }

    @NonNull
    public final UIDecorController postLayoutBy(int decorLayoutKey, long delayedMillis) {
        this.getUIDecorLayout().postLayoutBy(decorLayoutKey, delayedMillis);
        return this;
    }

    @NonNull
    public final UIDecorController postLayoutOnAnimationBy(int decorLayoutKey) {
        this.getUIDecorLayout().postLayoutOnAnimationBy(decorLayoutKey);
        return this;
    }

    @NonNull
    public final UIDecorController postLayoutOnAnimationBy(int decorLayoutKey, long delayedMillis) {
        this.getUIDecorLayout().postLayoutOnAnimationBy(decorLayoutKey, delayedMillis);
        return this;
    }

    // end.
}
