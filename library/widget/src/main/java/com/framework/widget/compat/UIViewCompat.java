package com.framework.widget.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

/**
 * @Author create by Zoran on 2020/8/14
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIViewCompat {

    /**
     * dp 转成px
     */
    public static int dip2px(@NonNull Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }

    /**
     * px转成dp
     */
    public static int px2dip(@NonNull Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(pxValue / scale);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(@NonNull Context context) {
        final Resources resources = context.getResources();
        return resources.getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(@NonNull Context context) {
        final Resources resources = context.getResources();
        return resources.getDisplayMetrics().heightPixels;
    }

    /**
     * 获取视图的测量宽度
     */
    public static int getMeasuredWidth(@Nullable View child) {
        if (child == null) {
            return 0;
        }
        final ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams marginLayoutParams;
            marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            return child.getMeasuredWidth()
                    + marginLayoutParams.leftMargin
                    + marginLayoutParams.rightMargin;
        }
        return child.getMeasuredWidth();
    }

    /**
     * 获取视图的测量高度
     */
    public static int getMeasuredHeight(@Nullable View child) {
        if (child == null) {
            return 0;
        }
        final ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams marginLayoutParams;
            marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            return child.getMeasuredHeight()
                    + marginLayoutParams.topMargin
                    + marginLayoutParams.bottomMargin;
        }
        return child.getMeasuredHeight();
    }

    /**
     * 获取状态栏高度
     */
    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    public static int getStatusBarHeight(@NonNull Context context) {
        final Resources resources = context.getResources();
        final int resourceId;
        resourceId = resources.getIdentifier("status_bar_height",
                "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 从父类移除控件
     */
    @Nullable
    public static <V extends View> V removeViewInParent(@Nullable V decorView) {
        if (decorView != null && decorView.getParent() != null) {
            ((ViewGroup) decorView.getParent()).removeView(decorView);
        }
        return decorView;
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(@NonNull View view) {
        final Context context = view.getContext();
        final InputMethodManager imm;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(@NonNull View view) {
        final Context context = view.getContext();
        final InputMethodManager imm;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
    }

    /**
     * 获取TransitionName
     */
    @NonNull
    public static String getTransitionName(@NonNull View view) {
        final String transitionName = ViewCompat.getTransitionName(view);
        return transitionName == null ? "" : transitionName;
    }

    /**
     * 展开视图
     */
    public static void expand(@NonNull final View view) {
        view.setVisibility(View.VISIBLE);
        view.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED);
        final int height = view.getMeasuredHeight();
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               @NonNull Transformation t) {
                final ViewGroup.LayoutParams ll = view.getLayoutParams();
                if (interpolatedTime >= 1.f) {
                    ll.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    ll.height = Math.round(height * interpolatedTime);
                }
                view.setLayoutParams(ll);
            }
        };
        animation.setDuration(250);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        view.clearAnimation();
        view.startAnimation(animation);
    }

    /**
     * 收起视图
     */
    public static void collapse(@NonNull final View view) {
        final int height = view.getMeasuredHeight();
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               @NonNull Transformation t) {
                final ViewGroup.LayoutParams ll = view.getLayoutParams();
                if (interpolatedTime >= 1.f) {
                    ll.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    ll.height = Math.round((1.f - interpolatedTime) * height);
                }
                view.setLayoutParams(ll);

                if (interpolatedTime >= 1.f) {
                    view.setVisibility(View.GONE);
                }
            }
        };
        animation.setDuration(250);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        view.clearAnimation();
        view.startAnimation(animation);
    }

    @Nullable
    public static String getResourceEntryName(@Nullable View view) {
        if (view == null) {
            return null;
        }
        final int id = view.getId();
        if (id == View.NO_ID) {
            return null;
        }
        final StringBuilder out = new StringBuilder();
        final Resources resources = view.getResources();
        if (resources != null) {
            try {
                final String pkgName;
                switch (id & 0xff000000) {
                    case 0x7f000000:
                        pkgName = "app";
                        break;
                    case 0x01000000:
                        pkgName = "android";
                        break;
                    default:
                        pkgName = resources.getResourcePackageName(id);
                        break;
                }
                final String typeName = resources.getResourceTypeName(id);
                final String entryName = resources.getResourceEntryName(id);
                out.append(" ");
                out.append(pkgName);
                out.append(":");
                out.append(typeName);
                out.append("/");
                out.append(entryName);
            } catch (@NonNull Resources.NotFoundException e) {
                // no-op
            }
        }
        return out.toString();
    }
}
