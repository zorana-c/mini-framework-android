package com.framework.widget.sliver;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2021/9/6
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class ViewOrientationHelper {
    public static final int HORIZONTAL = LinearLayoutCompat.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutCompat.VERTICAL;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface Orientation {
    }

    @NonNull
    public static ViewOrientationHelper create(@NonNull ViewGroup parent,
                                               @Orientation int orientation) {
        switch (orientation) {
            case VERTICAL:
                return createVerticalHelper(parent);
            case HORIZONTAL:
                return createHorizontalHelper(parent);
        }
        throw new IllegalArgumentException("invalid orientation");
    }

    @NonNull
    public static ViewOrientationHelper createVerticalHelper(@NonNull ViewGroup parent) {
        return new ViewVerticalHelper(parent);
    }

    @NonNull
    public static ViewOrientationHelper createHorizontalHelper(@NonNull ViewGroup parent) {
        return new ViewHorizontalHelper(parent);
    }

    protected final ViewGroup mParent;

    ViewOrientationHelper(@NonNull ViewGroup parent) {
        this.mParent = parent;
    }

    public abstract int getStartAfterPadding();

    public abstract int getEndAfterPadding();

    public abstract int getEnd();

    public abstract int getEndPadding();

    public abstract int getTotalSpace();

    public abstract int getTotalSpaceInOther();

    public abstract int getDecoratedStart(@NonNull View child);

    public abstract int getDecoratedEnd(@NonNull View child);

    public abstract int getDecoratedMeasurement(@NonNull View child);

    public abstract int getDecoratedMeasurementInOther(@NonNull View child);

    public abstract void offsetChild(@NonNull View child, int offset);

    static class ViewHorizontalHelper extends ViewOrientationHelper {

        ViewHorizontalHelper(@NonNull ViewGroup parent) {
            super(parent);
        }

        @Override
        public int getStartAfterPadding() {
            return this.mParent.getPaddingLeft();
        }

        @Override
        public int getEndAfterPadding() {
            return this.mParent.getWidth() - this.mParent.getPaddingRight();
        }

        @Override
        public int getEnd() {
            return this.mParent.getWidth();
        }

        @Override
        public int getEndPadding() {
            return this.mParent.getPaddingRight();
        }

        @Override
        public int getTotalSpace() {
            return this.mParent.getWidth()
                    - this.mParent.getPaddingLeft()
                    - this.mParent.getPaddingRight();
        }

        @Override
        public int getTotalSpaceInOther() {
            return this.mParent.getHeight()
                    - this.mParent.getPaddingTop()
                    - this.mParent.getPaddingBottom();
        }

        @Override
        public int getDecoratedStart(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getLeft() - layoutParams.leftMargin;
        }

        @Override
        public int getDecoratedEnd(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getRight() + layoutParams.rightMargin;
        }

        @Override
        public int getDecoratedMeasurement(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getMeasuredWidth()
                    + layoutParams.leftMargin
                    + layoutParams.rightMargin;
        }

        @Override
        public int getDecoratedMeasurementInOther(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getMeasuredHeight()
                    + layoutParams.topMargin
                    + layoutParams.bottomMargin;
        }

        @Override
        public void offsetChild(@NonNull View child, int offset) {
            ViewCompat.offsetLeftAndRight(child, offset);
        }
    }

    static class ViewVerticalHelper extends ViewOrientationHelper {

        ViewVerticalHelper(@NonNull ViewGroup parent) {
            super(parent);
        }

        @Override
        public int getStartAfterPadding() {
            return this.mParent.getPaddingTop();
        }

        @Override
        public int getEndAfterPadding() {
            return this.mParent.getHeight() - this.mParent.getPaddingBottom();
        }

        @Override
        public int getEnd() {
            return this.mParent.getHeight();
        }

        @Override
        public int getEndPadding() {
            return this.mParent.getPaddingBottom();
        }

        @Override
        public int getTotalSpace() {
            return this.mParent.getHeight()
                    - this.mParent.getPaddingTop()
                    - this.mParent.getPaddingBottom();
        }

        @Override
        public int getTotalSpaceInOther() {
            return this.mParent.getWidth()
                    - this.mParent.getPaddingLeft()
                    - this.mParent.getPaddingRight();
        }

        @Override
        public int getDecoratedStart(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getTop() - layoutParams.topMargin;
        }

        @Override
        public int getDecoratedEnd(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getBottom() + layoutParams.bottomMargin;
        }

        @Override
        public int getDecoratedMeasurement(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getMeasuredHeight()
                    + layoutParams.topMargin
                    + layoutParams.bottomMargin;
        }

        @Override
        public int getDecoratedMeasurementInOther(@NonNull View child) {
            final ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getMeasuredWidth()
                    + layoutParams.leftMargin
                    + layoutParams.rightMargin;
        }

        @Override
        public void offsetChild(@NonNull View child, int offset) {
            ViewCompat.offsetTopAndBottom(child, offset);
        }
    }
}
