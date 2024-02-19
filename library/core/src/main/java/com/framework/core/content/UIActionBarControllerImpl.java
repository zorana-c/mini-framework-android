package com.framework.core.content;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import com.framework.core.R;
import com.framework.core.compat.UIRes;
import com.framework.core.widget.AppCompatActionBar;
import com.framework.core.widget.UIDecorLayout;

/**
 * @Author create by Zhengzelong on 2022/9/29
 * @Email : 171905184@qq.com
 * @Description :
 */
final class UIActionBarControllerImpl implements UIActionBarController {
    @NonNull
    private final UIDecorController mUIDecorController;

    public UIActionBarControllerImpl(@NonNull UIDecorController uiDecorController) {
        this.mUIDecorController = uiDecorController;

        final Context context = uiDecorController.requireContext();
        final int backgroundColorInt;
        backgroundColorInt = UIRes.getColor(context, R.color.colorPrimaryDark);
        final int statusBarColorInt;
        statusBarColorInt = UIRes.getColor(context, R.color.colorStatusBar);
        final int toolsBarColorInt;
        toolsBarColorInt = UIRes.getColor(context, android.R.color.transparent);

        final UIDecorLayout parent = uiDecorController.getUIDecorLayout();
        final AppCompatActionBar appCompatActionBar;
        appCompatActionBar = new AppCompatActionBar(context);
        appCompatActionBar.setStatusBarEnabled(true);
        appCompatActionBar.setStatusBarBackgroundColor(statusBarColorInt);
        appCompatActionBar.setToolsBar(R.layout.ui_decor_toolsbar_layout);
        appCompatActionBar.setToolsBarEnabled(true);
        appCompatActionBar.setToolsBarBackgroundColor(toolsBarColorInt);
        appCompatActionBar.setId(R.id.uiDecorActionBar);
        appCompatActionBar.setBackgroundColor(backgroundColorInt);
        parent.setActionBar(appCompatActionBar);

        // remeasure actionBar size.
        final int widthMeasureSpec = View.MeasureSpec.UNSPECIFIED;
        final int heightMeasureSpec = View.MeasureSpec.UNSPECIFIED;
        appCompatActionBar.measure(widthMeasureSpec, heightMeasureSpec);

        this.setBackClickListener(view -> this.getUIPageController()
                .getUINavigatorController()
                .navigateUp());
    }

    @NonNull
    @Override
    public <T extends UIDecorController> T getUIPageController() {
        return (T) this.mUIDecorController;
    }

    @Nullable
    @Override
    public <T extends View> T getActionBar() {
        return this.getUIPageController().getActionBar();
    }

    @NonNull
    @Override
    public <T extends View> T requireActionBar() {
        return this.getUIPageController().requireActionBar();
    }

    @Nullable
    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        return this.getUIPageController().findViewById(id);
    }

    @NonNull
    @Override
    public <T extends View> T requireViewById(@IdRes int id) {
        return this.getUIPageController().requireViewById(id);
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setActionBar(@LayoutRes int layoutId) {
        this.getUIPageController().setActionBar(layoutId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setActionBar(@NonNull View actionBar) {
        this.getUIPageController().setActionBar(actionBar);
        return (T) this;
    }

    @Override
    public int getWidth() {
        final View actionBar = this.getActionBar();
        if (actionBar == null) {
            return 0;
        }
        return actionBar.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        final View actionBar = this.getActionBar();
        if (actionBar == null) {
            return 0;
        }
        return actionBar.getMeasuredHeight();
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setEnabled(boolean enabled) {
        this.requireActionBar().setVisibility(enabled ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackground(@Nullable Drawable background) {
        this.requireActionBar().setBackground(background);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackgroundColor(@ColorInt int color) {
        this.requireActionBar().setBackgroundColor(color);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackgroundResource(@DrawableRes int resId) {
        this.requireActionBar().setBackgroundResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha) {
        final View actionBar = this.requireActionBar();
        Drawable backgroundDrawable = actionBar.getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.setAlpha((int) (255 * alpha));
        }
        return (T) this;
    }

    @Override
    public int getStatusWidth() {
        return this.<AppCompatActionBar>requireActionBar().getStatusBarWidth();
    }

    @Override
    public int getStatusHeight() {
        return this.<AppCompatActionBar>requireActionBar().getStatusBarHeight();
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setStatusBarEnabled(boolean enabled) {
        this.<AppCompatActionBar>requireActionBar().setStatusBarEnabled(enabled);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setStatusBarBackground(@Nullable Drawable background) {
        this.<AppCompatActionBar>requireActionBar().setStatusBarBackground(background);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setStatusBarBackgroundColor(@ColorInt int color) {
        this.<AppCompatActionBar>requireActionBar().setStatusBarBackgroundColor(color);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setStatusBarBackgroundResource(@DrawableRes int resId) {
        this.<AppCompatActionBar>requireActionBar().setStatusBarBackgroundResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setStatusBarBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha) {
        this.<AppCompatActionBar>requireActionBar().setStatusBarBackgroundAlpha(alpha);
        return (T) this;
    }

    @Override
    public int getToolsWidth() {
        return this.<AppCompatActionBar>requireActionBar().getToolsBarWidth();
    }

    @Override
    public int getToolsHeight() {
        return this.<AppCompatActionBar>requireActionBar().getToolsBarHeight();
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBarEnabled(boolean enabled) {
        this.<AppCompatActionBar>requireActionBar().setToolsBarEnabled(enabled);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBarBackground(@Nullable Drawable background) {
        this.<AppCompatActionBar>requireActionBar().setToolsBarBackground(background);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBarBackgroundColor(@ColorInt int color) {
        this.<AppCompatActionBar>requireActionBar().setToolsBarBackgroundColor(color);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBarBackgroundResource(@DrawableRes int resId) {
        this.<AppCompatActionBar>requireActionBar().setToolsBarBackgroundResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBarBackgroundAlpha(@FloatRange(from = 0.0f, to = 1.f) float alpha) {
        this.<AppCompatActionBar>requireActionBar().setToolsBarBackgroundAlpha(alpha);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBar(@LayoutRes int layoutId) {
        this.<AppCompatActionBar>requireActionBar().setToolsBar(layoutId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setToolsBar(@Nullable View toolsBar) {
        this.<AppCompatActionBar>requireActionBar().setToolsBar(toolsBar);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setLineEnabled(boolean enabled) {
        final View child = this.requireViewById(R.id.uiDecorToolsLine);
        child.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setLineBackground(@Nullable Drawable background) {
        final View child = this.requireViewById(R.id.uiDecorToolsLine);
        child.setBackground(background);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setLineBackgroundColor(@ColorInt int color) {
        final View child = this.requireViewById(R.id.uiDecorToolsLine);
        child.setBackgroundColor(color);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setLineBackgroundResource(@DrawableRes int resId) {
        final View child = this.requireViewById(R.id.uiDecorToolsLine);
        child.setBackgroundResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackEnabled(boolean enabled) {
        final View child = this.requireViewById(R.id.uiDecorToolsBack);
        child.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackResource(@DrawableRes int resId) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsBackIcon);
        child.setImageResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackDrawable(@Nullable Drawable drawable) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsBackIcon);
        child.setImageDrawable(drawable);
        return (T) this;
    }

    @NonNull
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public <T extends UIActionBarController> T setBackTintColor(@ColorInt int colorInt) {
        return this.setBackTintList(ColorStateList.valueOf(colorInt));
    }

    @NonNull
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public <T extends UIActionBarController> T setBackTintColorResource(@ColorRes int resId) {
        return this.setBackTintColor(UIRes.getColor(resId));
    }

    @NonNull
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public <T extends UIActionBarController> T setBackTintList(@Nullable ColorStateList tint) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsBackIcon);
        child.setImageTintList(tint);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackText(@StringRes int resId) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsBackText);
        child.setText(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackText(@Nullable CharSequence text) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsBackText);
        child.setText(text);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackTextColor(@ColorInt int colorInt) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsBackText);
        child.setTextColor(colorInt);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackTextColorResource(@ColorRes int resId) {
        return this.setBackTextColor(UIRes.getColor(resId));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackTextColorStateList(@NonNull ColorStateList colors) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsBackText);
        child.setTextColor(colors);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackTextAppearance(@StyleRes int textAppearance) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsBackText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            child.setTextAppearance(textAppearance);
        } else {
            child.setTextAppearance(child.getContext(), textAppearance);
        }
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackView(@LayoutRes int layoutId) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsBack);
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(parent.getContext());
        return this.setBackView(inflater.inflate(layoutId, parent, false));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackView(@NonNull View customView) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsBack);
        parent.removeAllViews();
        parent.addView(customView);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setBackClickListener(@Nullable View.OnClickListener listener) {
        final View child = this.requireViewById(R.id.uiDecorToolsBack);
        child.setOnClickListener(listener);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleEnabled(boolean enabled) {
        final View child = this.requireViewById(R.id.uiDecorToolsTitle);
        child.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleResource(@DrawableRes int resId) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsTitleIcon);
        child.setImageResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleDrawable(@Nullable Drawable drawable) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsTitleIcon);
        child.setImageDrawable(drawable);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleText(@StringRes int resId) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsTitleText);
        child.setText(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleText(@Nullable CharSequence text) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsTitleText);
        child.setText(text);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleTextColor(@ColorInt int colorInt) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsTitleText);
        child.setTextColor(colorInt);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleTextColorResource(@ColorRes int resId) {
        return this.setTitleTextColor(UIRes.getColor(resId));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleTextColorStateList(@NonNull ColorStateList colors) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsTitleText);
        child.setTextColor(colors);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleTextAppearance(@StyleRes int textAppearance) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsTitleText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            child.setTextAppearance(textAppearance);
        } else {
            child.setTextAppearance(child.getContext(), textAppearance);
        }
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleView(@LayoutRes int layoutId) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsTitle);
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(parent.getContext());
        return this.setTitleView(inflater.inflate(layoutId, parent, false));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setTitleView(@NonNull View customView) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsTitle);
        parent.removeAllViews();
        parent.addView(customView);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuEnabled(boolean enabled) {
        final View child = this.requireViewById(R.id.uiDecorToolsMenu);
        child.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuResource(@DrawableRes int resId) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsMenuIcon);
        child.setImageResource(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuDrawable(@Nullable Drawable drawable) {
        final ImageView child = this.requireViewById(R.id.uiDecorToolsMenuIcon);
        child.setImageDrawable(drawable);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuText(@StringRes int resId) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsMenuText);
        child.setText(resId);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuText(@Nullable CharSequence text) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsMenuText);
        child.setText(text);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuTextColor(@ColorInt int colorInt) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsMenuText);
        child.setTextColor(colorInt);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuTextColorResource(@ColorRes int resId) {
        return this.setMenuTextColor(UIRes.getColor(resId));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuTextColorStateList(@NonNull ColorStateList colors) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsMenuText);
        child.setTextColor(colors);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuTextAppearance(@StyleRes int textAppearance) {
        final TextView child = this.requireViewById(R.id.uiDecorToolsMenuText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            child.setTextAppearance(textAppearance);
        } else {
            child.setTextAppearance(child.getContext(), textAppearance);
        }
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuView(@LayoutRes int layoutId) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsMenu);
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(parent.getContext());
        return this.setMenuView(inflater.inflate(layoutId, parent, false));
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuView(@NonNull View customView) {
        final ViewGroup parent = this.requireViewById(R.id.uiDecorToolsMenu);
        parent.removeAllViews();
        parent.addView(customView);
        return (T) this;
    }

    @NonNull
    @Override
    public <T extends UIActionBarController> T setMenuClickListener(@Nullable View.OnClickListener listener) {
        final View child = this.requireViewById(R.id.uiDecorToolsMenu);
        child.setOnClickListener(listener);
        return (T) this;
    }
}
