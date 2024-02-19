package com.navigation.floating;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.framework.widget.compat.UIViewCompat;

/**
 * @Author create by Zhengzelong on 2023-12-22
 * @Email : 171905184@qq.com
 * @Description : 常规参数配置
 */
public class UIDialogFragmentCompat {
    @NonNull
    public static UIDialogFragmentCompat with(@NonNull UIDialogFragment uiDialogFragment) {
        return new UIDialogFragmentCompat(uiDialogFragment);
    }

    @NonNull
    private final UIDialogFragment mUIDialogFragment;

    // Window alpha
    private float mAlpha = -1f;
    // Window width
    private int mWidth = 0;
    // Window height
    private int mHeight = 0;
    // Window gravity
    private int mGravity = 0;
    // Window animation
    private int mAnimation = 0;

    protected UIDialogFragmentCompat(@NonNull UIDialogFragment uiDialogFragment) {
        this.mUIDialogFragment = uiDialogFragment;
        new ComponentObserver(uiDialogFragment);
    }

    @CallSuper
    protected void init(@NonNull LifecycleOwner owner) {
        final UIDialogFragment f = this.mUIDialogFragment;
        if (f.isShowDialog()) {
            final Dialog dialog = f.getDialog();
            if (dialog == null) {
                throw new IllegalStateException("ERROR");
            }
            final Window window = dialog.getWindow();
            if (window == null) {
                return;
            }
            final WindowManager.LayoutParams lp;
            lp = window.getAttributes();

            if (this.mAlpha != -1f) {
                lp.alpha = this.mAlpha;
            }
            if (this.mWidth != 0) {
                lp.width = this.mWidth;
            }
            if (this.mHeight != 0) {
                lp.height = this.mHeight;
            }
            if (this.mGravity != 0) {
                lp.gravity = this.mGravity;
            }
            if (this.mAnimation != 0) {
                lp.windowAnimations = this.mAnimation;
            }
            window.setAttributes(lp);
        }
    }

    @NonNull
    public final UIDialogFragment getUIDialogFragment() {
        return this.mUIDialogFragment;
    }

    @NonNull
    public UIDialogFragmentCompat setAlpha(@FloatRange(from = 0.f, to = 1.f) float alpha) {
        this.mAlpha = alpha;
        return this;
    }

    @NonNull
    public UIDialogFragmentCompat setWidth(int width) {
        this.mWidth = width;
        return this;
    }

    @NonNull
    public UIDialogFragmentCompat setWidth(@FloatRange(from = 0.f, to = 1.f) float screenWidthScale) {
        final Context context = this.mUIDialogFragment.getContext();
        if (context == null) {
            return this;
        }
        int screenWidth;
        screenWidth = UIViewCompat.getScreenWidth(context);
        screenWidth = Math.round(screenWidth * screenWidthScale);
        return this.setWidth(screenWidth);
    }

    @NonNull
    public UIDialogFragmentCompat setHeight(int height) {
        this.mHeight = height;
        return this;
    }

    @NonNull
    public UIDialogFragmentCompat setHeight(@FloatRange(from = 0.f, to = 1.f) float screenHeightScale) {
        final Context context = this.mUIDialogFragment.getContext();
        if (context == null) {
            return this;
        }
        int screenHeight;
        screenHeight = UIViewCompat.getScreenHeight(context);
        screenHeight = Math.round(screenHeight * screenHeightScale);
        return this.setHeight(screenHeight);
    }

    /**
     * @param gravity location in window
     * @see Gravity
     */
    @NonNull
    public UIDialogFragmentCompat setGravity(int gravity) {
        this.mGravity = gravity;
        return this;
    }

    @NonNull
    public UIDialogFragmentCompat setAnimationStyle(@StyleRes int animationStyle) {
        this.mAnimation = animationStyle;
        return this;
    }

    private final class ComponentObserver implements DefaultLifecycleObserver {
        private ComponentObserver(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.addObserver(this);
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            UIDialogFragmentCompat.this.init(owner);
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.removeObserver(this);
        }
    }
}
