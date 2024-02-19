package com.framework.core.content;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.framework.core.R;
import com.framework.core.compat.UIRes;

import java.util.Arrays;

/**
 * @Author create by Zhengzelong on 2024-01-17
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIThemeEnforcement {
    @NonNull
    public static UIThemeEnforcement with(@NonNull UIPageControllerOwner owner) {
        return new UIThemeEnforcement(owner) {
        };
    }

    @NonNull
    public static UIThemeEnforcement with(@NonNull UIPageController uiPageController) {
        return new UIThemeEnforcement(uiPageController) {
        };
    }

    @NonNull
    protected final UIPageController mUIPageController;
    @NonNull
    protected final float[] mOuterRadii = new float[8];

    protected UIThemeEnforcement(@NonNull UIPageControllerOwner owner) {
        this(owner.<UIPageController>getUIPageController());
    }

    protected UIThemeEnforcement(@NonNull UIPageController uiPageController) {
        this.mUIPageController = uiPageController;
    }

    @NonNull
    public final UIThemeEnforcement setRadius(float radius) {
        this.setTopRadius(radius);
        this.setBottomRadius(radius);
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setTopRadius(float radius) {
        this.setTopLeftRadius(radius);
        this.setTopRightRadius(radius);
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setLeftRadius(float radius) {
        this.setTopLeftRadius(radius);
        this.setBottomLeftRadius(radius);
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setRightRadius(float radius) {
        this.setTopRightRadius(radius);
        this.setBottomRightRadius(radius);
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setBottomRadius(float radius) {
        this.setBottomLeftRadius(radius);
        this.setBottomRightRadius(radius);
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setTopLeftRadius(float radius) {
        this.mOuterRadii[0] = radius;
        this.mOuterRadii[1] = radius;
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setTopRightRadius(float radius) {
        this.mOuterRadii[2] = radius;
        this.mOuterRadii[3] = radius;
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setBottomLeftRadius(float radius) {
        this.mOuterRadii[6] = radius;
        this.mOuterRadii[7] = radius;
        return this;
    }

    @NonNull
    public final UIThemeEnforcement setBottomRightRadius(float radius) {
        this.mOuterRadii[4] = radius;
        this.mOuterRadii[5] = radius;
        return this;
    }

    @NonNull
    @CallSuper
    public UIThemeEnforcement reset() {
        Arrays.fill(this.mOuterRadii, 0.f);
        return this;
    }

    @NonNull
    public UIThemeEnforcement setColorPrimary(@IdRes int id) {
        final View view = this.mUIPageController.findViewById(id);
        if (view != null) {
            this.setColorPrimary(view);
        }
        return this;
    }

    @NonNull
    public UIThemeEnforcement setColorPrimary(@NonNull View view) {
        final Drawable background;
        background = UIRes.getRoundedDrawableResource(R.color.colorPrimary, this.mOuterRadii);
        view.setBackground(background);
        return this;
    }

    @NonNull
    public UIThemeEnforcement setColorPrimaryDark(@IdRes int id) {
        final View view = this.mUIPageController.findViewById(id);
        if (view != null) {
            this.setColorPrimaryDark(view);
        }
        return this;
    }

    @NonNull
    public UIThemeEnforcement setColorPrimaryDark(@NonNull View view) {
        final Drawable background;
        background = UIRes.getRoundedDrawableResource(R.color.colorPrimaryDark, this.mOuterRadii);
        view.setBackground(background);
        return this;
    }

    @NonNull
    public UIThemeEnforcement setDecorBackground(@IdRes int id) {
        final View view = this.mUIPageController.findViewById(id);
        if (view != null) {
            this.setDecorBackground(view);
        }
        return this;
    }

    @NonNull
    public UIThemeEnforcement setDecorBackground(@NonNull View view) {
        final Drawable background;
        background = UIRes.getRoundedDrawableResource(R.color.decorBackground, this.mOuterRadii);
        view.setBackground(background);
        return this;
    }

    @NonNull
    public UIThemeEnforcement applyToPageBackground() {
        final UIPageController uiPageController = this.mUIPageController;
        View view;
        view = uiPageController.getContentView();

        if (view != null) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                final int colorInt;
                colorInt = ((ColorDrawable) background).getColor();
                background = UIRes.getRoundedDrawable(colorInt, this.mOuterRadii);
                view.setBackground(background);
            } else {
                this.setDecorBackground(view);
            }
        }

        if (uiPageController instanceof UIDecorController) {
            view = ((UIDecorController) uiPageController).getActionBar();
            if (view == null) {
                return this;
            }
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                final int colorInt;
                colorInt = ((ColorDrawable) background).getColor();
                background = UIRes.getRoundedDrawable(colorInt, this.mOuterRadii);
                view.setBackground(background);
            } else {
                this.setColorPrimaryDark(view);
            }
        }
        return this;
    }
}
