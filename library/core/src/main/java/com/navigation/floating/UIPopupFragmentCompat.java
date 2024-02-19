package com.navigation.floating;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.framework.core.R;
import com.framework.core.compat.UIRes;
import com.framework.core.rx.view.RxView;
import com.framework.widget.compat.UIViewCompat;
import com.navigation.widget.UIPopupWindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-12-22
 * @Email : 171905184@qq.com
 * @Description : 常规参数配置
 */
public class UIPopupFragmentCompat {
    public static final int MODE_MATCH = 1;
    public static final int MODE_WRAP = 2;

    @IntDef({MODE_MATCH, MODE_WRAP})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface Mode {
    }

    @NonNull
    public static UIPopupFragmentCompat with(@NonNull UIPopupFragment uiPopupFragment) {
        return new UIPopupFragmentCompat(uiPopupFragment);
    }

    @NonNull
    private final UIPopupFragment mUIPopupFragment;
    @NonNull
    private final int[] mTempRootScreenLocation = new int[2];
    @NonNull
    private final int[] mTempAnchorScreenLocation = new int[2];

    @Mode
    private int mLayoutMode = MODE_WRAP;
    private int mWidth = WindowManager.LayoutParams.MATCH_PARENT;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

    protected UIPopupFragmentCompat(@NonNull UIPopupFragment uiPopupFragment) {
        this.mUIPopupFragment = uiPopupFragment;
        new ComponentObserver(uiPopupFragment);
    }

    private void preview(@NonNull LifecycleOwner owner) {
        this.setLayoutMode(MODE_WRAP);
        this.setExitAnim(R.anim.slide_popup_out_to_top);
        this.setEnterAnim(R.anim.slide_popup_in_from_top);
        this.setAnimationStyle(R.style.UIAnimation_PopupWindow);
        this.setBackgroundAlpha(0.45f);
    }

    @CallSuper
    protected void init(@NonNull LifecycleOwner owner) {
        final UIPopupFragment f = this.mUIPopupFragment;
        if (!f.isShowPopup()) {
            return;
        }
        final PopupWindow popupWindow = f.getPopupWindow();
        if (popupWindow == null) {
            throw new IllegalStateException("ERROR");
        }
        final View contentView = f.requireView();
        final View backgroundView = (View) contentView.getParent();
        ViewGroup.LayoutParams lp;
        lp = contentView.getLayoutParams();
        lp.width = this.mWidth;
        lp.height = this.mHeight;

        if (lp instanceof FrameLayout.LayoutParams) {
            final FrameLayout.LayoutParams ll;
            ll = (FrameLayout.LayoutParams) lp;
            ll.gravity = f.getGravity();
        }
        contentView.setLayoutParams(lp);

        lp = backgroundView.getLayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        int backgroundWidth = lp.width;
        int backgroundHeight = lp.height;

        if (MODE_MATCH == this.mLayoutMode) {
            final int[] drawingLocation = new int[2];
            this.getDrawingLocationOnBackground(drawingLocation);
            contentView.setTranslationX(drawingLocation[0]);
            contentView.setTranslationY(drawingLocation[1]);
        } else if (MODE_WRAP == this.mLayoutMode) {
            final Rect displayFrame = new Rect();
            this.getBackgroundVisibleDisplayFrame(displayFrame);
            backgroundWidth = displayFrame.right - displayFrame.left;
            backgroundHeight = displayFrame.bottom - displayFrame.top;
        }
        lp.width = backgroundWidth;
        lp.height = backgroundHeight;
        backgroundView.setLayoutParams(lp);

        RxView.of(f).click(it -> {
                    if (backgroundView == it) {
                        f.dismiss();
                    }
                },
                contentView,
                backgroundView);
        popupWindow.update(backgroundWidth, backgroundHeight);
    }

    @Nullable
    public final PopupWindow getPopupWindow() {
        return this.mUIPopupFragment.getPopupWindow();
    }

    @NonNull
    public final UIPopupFragment getUIPopupFragment() {
        return this.mUIPopupFragment;
    }

    @NonNull
    public UIPopupFragmentCompat setWidth(int width) {
        this.mWidth = width;
        return this;
    }

    @NonNull
    public UIPopupFragmentCompat setWidth(@FloatRange(from = 0.f, to = 1.f) float screenWidthScale) {
        final Context context = this.mUIPopupFragment.getContext();
        if (context == null) {
            return this;
        }
        int screenWidth;
        screenWidth = UIViewCompat.getScreenWidth(context);
        screenWidth = Math.round(screenWidth * screenWidthScale);
        return this.setWidth(screenWidth);
    }

    @NonNull
    public UIPopupFragmentCompat setHeight(int height) {
        this.mHeight = height;
        return this;
    }

    @NonNull
    public UIPopupFragmentCompat setHeight(@FloatRange(from = 0.f, to = 1.f) float screenHeightScale) {
        final Context context = this.mUIPopupFragment.getContext();
        if (context == null) {
            return this;
        }
        int screenHeight;
        screenHeight = UIViewCompat.getScreenHeight(context);
        screenHeight = Math.round(screenHeight * screenHeightScale);
        return this.setHeight(screenHeight);
    }

    /**
     * 设置背景透明度模式: 填满/自适应
     * </pre>
     *
     * @see UIPopupFragmentCompat#MODE_MATCH
     * @see UIPopupFragmentCompat#MODE_WRAP
     */
    @NonNull
    public UIPopupFragmentCompat setLayoutMode(@Mode int layoutMode) {
        this.mLayoutMode = layoutMode;
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(MODE_WRAP == layoutMode
                    ? WindowManager.LayoutParams.WRAP_CONTENT
                    : WindowManager.LayoutParams.MATCH_PARENT);
        }
        return this;
    }

    /**
     * 设置背景
     */
    @NonNull
    public UIPopupFragmentCompat setBackgroundColor(@ColorInt int colorInt) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(colorInt));
        }
        return this;
    }

    /**
     * 设置背景
     */
    @NonNull
    public UIPopupFragmentCompat setBackgroundResource(@DrawableRes int resId) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.setBackgroundDrawable(UIRes.getDrawable(resId));
        }
        return this;
    }

    /**
     * 设置背景
     */
    @NonNull
    public UIPopupFragmentCompat setBackgroundDrawable(@Nullable Drawable background) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.setBackgroundDrawable(background);
        }
        return this;
    }

    /**
     * 设置背景透明度
     */
    @NonNull
    public UIPopupFragmentCompat setBackgroundAlpha(@FloatRange(from = 0.f, to = 1.f) float alpha) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            Drawable backgroundDrawable = popupWindow.getBackground();
            if (backgroundDrawable == null) {
                backgroundDrawable = new ColorDrawable(Color.BLACK);
            }
            backgroundDrawable.setAlpha(Math.round(255.f * alpha));
            this.setBackgroundDrawable(backgroundDrawable);
        }
        return this;
    }

    /**
     * 设置动画样式
     */
    @NonNull
    public UIPopupFragmentCompat setAnimationStyle(@StyleRes int animationStyle) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.setAnimationStyle(animationStyle);
        }
        return this;
    }

    /**
     * 设置内容视图的开始动画
     */
    @NonNull
    public UIPopupFragmentCompat setEnterAnim(@AnimRes @AnimatorRes int enterAnim) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow instanceof UIPopupWindow) {
            final UIPopupWindow uiPopupWindow;
            uiPopupWindow = (UIPopupWindow) popupWindow;
            uiPopupWindow.setEnterAnim(enterAnim);
        }
        return this;
    }

    /**
     * 设置内容视图的结束动画
     */
    @NonNull
    public UIPopupFragmentCompat setExitAnim(@AnimRes @AnimatorRes int exitAnim) {
        final PopupWindow popupWindow = this.getPopupWindow();
        if (popupWindow instanceof UIPopupWindow) {
            final UIPopupWindow uiPopupWindow;
            uiPopupWindow = (UIPopupWindow) popupWindow;
            uiPopupWindow.setExitAnim(exitAnim);
        }
        return this;
    }

    @NonNull
    private View getAppRootView(@NonNull View anchorView) {
        final Activity activity = this.mUIPopupFragment.getActivity();
        if (activity != null) {
            final Window window = activity.getWindow();
            if (window != null) {
                return window.getDecorView().getRootView();
            }
        }
        return anchorView.getRootView();
    }

    private void getWindowVisibleDisplayFrame(@NonNull View anchorView,
                                              @NonNull Rect outRect) {
        this.getAppRootView(anchorView).getWindowVisibleDisplayFrame(outRect);
    }

    private void getBackgroundVisibleDisplayFrame(@NonNull Rect outRect) {
        final UIPopupFragment uiPopupFragment = this.mUIPopupFragment;
        final View anchorView = uiPopupFragment.requireAnchorView();

        final int[] drawingLocation = new int[2];
        this.getDrawingLocationOnBackground(drawingLocation);
        this.getWindowVisibleDisplayFrame(anchorView, outRect);
        outRect.top += drawingLocation[1];
        outRect.left += uiPopupFragment.getOffsetX();
    }

    private void getDrawingLocationOnScreen(@NonNull View anchorView,
                                            @NonNull int[] outLocation) {
        final int[] rootScreenLocation = this.mTempRootScreenLocation;
        final View rootView = this.getAppRootView(anchorView);
        rootView.getLocationOnScreen(rootScreenLocation);
        rootScreenLocation[1] += UIViewCompat.getStatusBarHeight(anchorView.getContext());

        final int[] anchorScreenLocation = this.mTempAnchorScreenLocation;
        anchorView.getLocationOnScreen(anchorScreenLocation);

        outLocation[0] = anchorScreenLocation[0] - rootScreenLocation[0];
        outLocation[1] = anchorScreenLocation[1] - rootScreenLocation[1];
    }

    private void getDrawingLocationOnBackground(@NonNull int[] outLocation) {
        final UIPopupFragment uiPopupFragment = this.mUIPopupFragment;
        final View anchorView = uiPopupFragment.requireAnchorView();

        this.getDrawingLocationOnScreen(anchorView, outLocation);
        outLocation[0] += uiPopupFragment.getOffsetX();
        outLocation[1] += uiPopupFragment.getOffsetY() + anchorView.getHeight();
    }

    private final class ComponentObserver implements DefaultLifecycleObserver {
        private ComponentObserver(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.addObserver(this);
        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            UIPopupFragmentCompat.this.preview(owner);
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            UIPopupFragmentCompat.this.init(owner);
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.removeObserver(this);
        }
    }
}
