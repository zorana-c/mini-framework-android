package com.framework.core.content;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

/**
 * @Author create by Zhengzelong on 2022/9/29
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIActionBarController {

    @NonNull
    <T extends UIDecorController> T getUIPageController();

    @Nullable
    <T extends View> T getActionBar();

    @NonNull
    <T extends View> T requireActionBar();

    @Nullable
    <T extends View> T findViewById(@IdRes int id);

    @NonNull
    <T extends View> T requireViewById(@IdRes int id);

    @NonNull
    <T extends UIActionBarController> T setActionBar(@LayoutRes int layoutId);

    @NonNull
    <T extends UIActionBarController> T setActionBar(@NonNull View actionBar);

    int getWidth();

    int getHeight();

    @NonNull
    <T extends UIActionBarController> T setEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setBackground(@Nullable Drawable background);

    @NonNull
    <T extends UIActionBarController> T setBackgroundColor(@ColorInt int color);

    @NonNull
    <T extends UIActionBarController> T setBackgroundResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha);

    int getStatusWidth();

    int getStatusHeight();

    @NonNull
    <T extends UIActionBarController> T setStatusBarEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setStatusBarBackground(@Nullable Drawable background);

    @NonNull
    <T extends UIActionBarController> T setStatusBarBackgroundColor(@ColorInt int color);

    @NonNull
    <T extends UIActionBarController> T setStatusBarBackgroundResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setStatusBarBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha);

    int getToolsWidth();

    int getToolsHeight();

    @NonNull
    <T extends UIActionBarController> T setToolsBarEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setToolsBarBackground(@Nullable Drawable background);

    @NonNull
    <T extends UIActionBarController> T setToolsBarBackgroundColor(@ColorInt int color);

    @NonNull
    <T extends UIActionBarController> T setToolsBarBackgroundResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setToolsBarBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha);

    @NonNull
    <T extends UIActionBarController> T setToolsBar(@LayoutRes int layoutId);

    @NonNull
    <T extends UIActionBarController> T setToolsBar(@Nullable View toolsBar);

    @NonNull
    <T extends UIActionBarController> T setLineEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setLineBackground(@Nullable Drawable background);

    @NonNull
    <T extends UIActionBarController> T setLineBackgroundColor(@ColorInt int color);

    @NonNull
    <T extends UIActionBarController> T setLineBackgroundResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setBackEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setBackResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setBackDrawable(@Nullable Drawable drawable);

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    <T extends UIActionBarController> T setBackTintColor(@ColorInt int colorInt);

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    <T extends UIActionBarController> T setBackTintColorResource(@ColorRes int resId);

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    <T extends UIActionBarController> T setBackTintList(@Nullable ColorStateList tint);

    @NonNull
    <T extends UIActionBarController> T setBackText(@StringRes int resId);

    @NonNull
    <T extends UIActionBarController> T setBackText(@Nullable CharSequence text);

    @NonNull
    <T extends UIActionBarController> T setBackTextColor(@ColorInt int colorInt);

    @NonNull
    <T extends UIActionBarController> T setBackTextColorResource(@ColorRes int resId);

    @NonNull
    <T extends UIActionBarController> T setBackTextColorStateList(@Nullable ColorStateList tint);

    @NonNull
    <T extends UIActionBarController> T setBackTextAppearance(@StyleRes int textAppearance);

    @NonNull
    <T extends UIActionBarController> T setBackView(@LayoutRes int layoutId);

    @NonNull
    <T extends UIActionBarController> T setBackView(@NonNull View customView);

    @NonNull
    <T extends UIActionBarController> T setBackClickListener(@Nullable View.OnClickListener listener);

    @NonNull
    <T extends UIActionBarController> T setTitleEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setTitleResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setTitleDrawable(@Nullable Drawable drawable);

    @NonNull
    <T extends UIActionBarController> T setTitleText(@StringRes int resId);

    @NonNull
    <T extends UIActionBarController> T setTitleText(@Nullable CharSequence text);

    @NonNull
    <T extends UIActionBarController> T setTitleTextColor(@ColorInt int colorInt);

    @NonNull
    <T extends UIActionBarController> T setTitleTextColorResource(@ColorRes int resId);

    @NonNull
    <T extends UIActionBarController> T setTitleTextColorStateList(@NonNull ColorStateList colors);

    @NonNull
    <T extends UIActionBarController> T setTitleTextAppearance(@StyleRes int textAppearance);

    @NonNull
    <T extends UIActionBarController> T setTitleView(@LayoutRes int layoutId);

    @NonNull
    <T extends UIActionBarController> T setTitleView(@NonNull View customView);

    @NonNull
    <T extends UIActionBarController> T setMenuEnabled(boolean enabled);

    @NonNull
    <T extends UIActionBarController> T setMenuResource(@DrawableRes int resId);

    @NonNull
    <T extends UIActionBarController> T setMenuDrawable(@Nullable Drawable drawable);

    @NonNull
    <T extends UIActionBarController> T setMenuText(@StringRes int resId);

    @NonNull
    <T extends UIActionBarController> T setMenuText(@Nullable CharSequence text);

    @NonNull
    <T extends UIActionBarController> T setMenuTextColor(@ColorInt int colorInt);

    @NonNull
    <T extends UIActionBarController> T setMenuTextColorResource(@ColorRes int resId);

    @NonNull
    <T extends UIActionBarController> T setMenuTextColorStateList(@NonNull ColorStateList colors);

    @NonNull
    <T extends UIActionBarController> T setMenuTextAppearance(@StyleRes int textAppearance);

    @NonNull
    <T extends UIActionBarController> T setMenuView(@LayoutRes int layoutId);

    @NonNull
    <T extends UIActionBarController> T setMenuView(@NonNull View customView);

    @NonNull
    <T extends UIActionBarController> T setMenuClickListener(@Nullable View.OnClickListener listener);
}
