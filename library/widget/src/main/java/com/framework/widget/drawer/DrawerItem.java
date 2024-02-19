package com.framework.widget.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.compat.UIViewCompat;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2024-01-19
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DrawerItem extends FrameLayout {
    @NonNull
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>();

    public DrawerItem(@NonNull Context context) {
        this(context, null);
    }

    public DrawerItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(true); // No need to draw until the insets are adjusted
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        boolean measureMatchParentChildren = false;
        measureMatchParentChildren |= widthMode != MeasureSpec.EXACTLY;
        measureMatchParentChildren |= heightMode != MeasureSpec.EXACTLY;
        this.mMatchParentChildren.clear();

        int maxWidth = 0;
        int maxHeight = 0;
        int childState = 0;

        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (!this.getMeasureAllChildren()
                    && child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childWidthMeasureSpec;
            childWidthMeasureSpec = makeChildMeasureSpec(widthMeasureSpec,
                    this.getPaddingLeft() + this.getPaddingRight()
                            + layoutParams.leftMargin
                            + layoutParams.rightMargin, layoutParams.width);
            final int childHeightMeasureSpec;
            childHeightMeasureSpec = makeChildMeasureSpec(heightMeasureSpec,
                    this.getPaddingTop() + this.getPaddingBottom()
                            + layoutParams.topMargin
                            + layoutParams.bottomMargin, layoutParams.height);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            maxWidth = Math.max(maxWidth, UIViewCompat.getMeasuredWidth(child));
            maxHeight = Math.max(maxHeight, UIViewCompat.getMeasuredHeight(child));
            childState = combineMeasuredStates(childState, child.getMeasuredState());
            if (measureMatchParentChildren) {
                if (layoutParams.width == LayoutParams.MATCH_PARENT
                        || layoutParams.height == LayoutParams.MATCH_PARENT) {
                    this.mMatchParentChildren.add(child);
                }
            }
        }
        // Account for padding too
        maxWidth += this.getPaddingLeft() + this.getPaddingRight();
        maxHeight += this.getPaddingTop() + this.getPaddingBottom();

        // Check against our minimum height and width
        maxWidth = Math.max(maxWidth, this.getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, this.getSuggestedMinimumHeight());

        // Check against our foreground's minimum height and width
        final Drawable drawable = this.getForeground();
        if (drawable != null) {
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
        }
        this.setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        for (View child : this.mMatchParentChildren) {
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childWidthMeasureSpec;
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                final int childWidth = Math.max(0, this.getMeasuredWidth()
                        - this.getPaddingLeft()
                        - this.getPaddingRight()
                        - layoutParams.leftMargin
                        - layoutParams.rightMargin);
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        this.getPaddingLeft() + this.getPaddingRight()
                                + layoutParams.leftMargin
                                + layoutParams.rightMargin, layoutParams.width);
            }
            final int childHeightMeasureSpec;
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                final int childHeight = Math.max(0, this.getMeasuredHeight()
                        - this.getPaddingTop()
                        - this.getPaddingBottom()
                        - layoutParams.topMargin - layoutParams.bottomMargin);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        this.getPaddingTop() + this.getPaddingBottom()
                                + layoutParams.topMargin
                                + layoutParams.bottomMargin, layoutParams.height);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private static int makeChildMeasureSpec(int spec, int padding, int childDimension) {
        final int specMode = MeasureSpec.getMode(spec);
        final int specSize = MeasureSpec.getSize(spec);
        final int size = Math.max(0, specSize - padding);
        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST: {
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    resultSize = size;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;
            }
            case MeasureSpec.UNSPECIFIED: {
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    resultSize = size;
                    resultMode = MeasureSpec.UNSPECIFIED;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    resultSize = size;
                    resultMode = MeasureSpec.UNSPECIFIED;
                }
                break;
            }
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }
}
