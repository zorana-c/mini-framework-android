package com.framework.widget.sliver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;
import androidx.customview.view.AbsSavedState;

import com.framework.widget.FitSystemWindow;
import com.framework.widget.FitSystemWindowCompat;
import com.framework.widget.R;
import com.framework.widget.compat.UIViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description : 滑动容器控件
 * <p>
 * 1.线性布局
 * 2.支持横竖屏
 * 3.支持阻尼效果
 * 4.支持视图向上滑动嵌套
 * 5.支持视图向下滑动嵌套
 * 6. ...
 * </p>
 * 其他嵌套视图:
 * {@link SliverViewPager}
 * {@link SliverScrollView}
 * {@link SliverRecyclerView}
 * {@link SliverRefreshLayout}
 * ...
 */
public class SliverContainer extends ViewGroup implements
        SliverScroll,
        SliverScrollProvider,
        NestedScroll,
        NestedScrollProvider,
        InheritScroll,
        InheritScrollProvider,
        ScrollingView,
        FitSystemWindow {
    private static final boolean DEBUG = false;
    private static final String TAG = "SliverContainer";

    /**
     * 未定义持续时长
     */
    public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;

    /**
     * 滑动方向: 垂直
     */
    public static final int VERTICAL = ViewOrientationHelper.VERTICAL;
    /**
     * 滑动方向: 水平
     */
    public static final int HORIZONTAL = ViewOrientationHelper.HORIZONTAL;

    @IntDef({VERTICAL,
            HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface Orientation {
    }

    /**
     * 滑动处于闲置状态
     */
    public static final int SCROLL_STATE_IDLE = 0;
    /**
     * 滑动处于拖拽状态
     */
    public static final int SCROLL_STATE_DRAGGING = 1;
    /**
     * 滑动处于沉降状态
     */
    public static final int SCROLL_STATE_SETTLING = 2;

    @IntDef({SCROLL_STATE_IDLE,
            SCROLL_STATE_DRAGGING,
            SCROLL_STATE_SETTLING})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface ScrollState {
    }

    /**
     * 无定义滑动
     */
    public static final int SCROLL_LOCATE_NONE = 0;
    /**
     * 头部/向下滑动
     */
    public static final int SCROLL_LOCATE_HEAD = -1;
    /**
     * 底部/向上滑动
     */
    public static final int SCROLL_LOCATE_TAIL = 1;
    /**
     * 所有/全部滑动
     */
    public static final int SCROLL_LOCATE_ALL = 2;

    @IntDef({SCROLL_LOCATE_NONE,
            SCROLL_LOCATE_HEAD,
            SCROLL_LOCATE_TAIL,
            SCROLL_LOCATE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface ScrollLocate {
    }

    /**
     * 临时存放重新测量的视图
     */
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>();
    /**
     * 缓存滑动监听事件对象
     */
    private final ArrayList<OnScrollListener> mOnScrollListeners = new ArrayList<>();

    /**
     * 手势滑动拦截事件对象
     */
    private OnFlingListener mOnFlingListener;

    public SliverContainer(@NonNull Context context) {
        this(context, null);
    }

    public SliverContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setScrollContainer(true);
        this.setFocusableInTouchMode(true);
        this.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        this.setWillNotDraw(View.OVER_SCROLL_NEVER == this.getOverScrollMode());

        this.mViewScroller = ViewScroller.create(this, new ComponentCallback());
        this.mVerOrientationHelper = ViewOrientationHelper.createVerticalHelper(this);
        this.mHorOrientationHelper = ViewOrientationHelper.createHorizontalHelper(this);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mScaledTouchSlop = configuration.getScaledTouchSlop();
        this.mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliverContainer);
        final int orientation;
        orientation = typedArray.getInt(R.styleable.SliverContainer_android_orientation, VERTICAL);
        final int bounceLocate;
        bounceLocate = typedArray.getInt(R.styleable.SliverContainer_sliverBounceLocate, SCROLL_LOCATE_ALL);
        final boolean childFocusable;
        childFocusable = typedArray.getBoolean(R.styleable.SliverContainer_sliverChildFocusable, false);
        final boolean scrollFloating;
        scrollFloating = typedArray.getBoolean(R.styleable.SliverContainer_sliverScrollFloating, false);
        final boolean userScrollEnabled;
        userScrollEnabled = typedArray.getBoolean(R.styleable.SliverContainer_userScrollEnabled, true);
        final boolean fitSystemWindows;
        fitSystemWindows = typedArray.getBoolean(R.styleable.SliverContainer_sliverFitSystemWindows, false);
        final boolean sliverScrollingEnabled;
        sliverScrollingEnabled = typedArray.getBoolean(R.styleable.SliverContainer_sliverScrollingEnabled, true);
        final boolean nestedScrollingEnabled;
        nestedScrollingEnabled = typedArray.getBoolean(R.styleable.SliverContainer_android_nestedScrollingEnabled, true);
        final boolean inheritScrollingEnabled;
        inheritScrollingEnabled = typedArray.getBoolean(R.styleable.SliverContainer_inheritScrollingEnabled, true);
        typedArray.recycle();

        this.setOrientation(orientation);
        this.setBounceLocate(bounceLocate);
        this.setChildFocusable(childFocusable);
        this.setScrollFloating(scrollFloating);
        this.setUserScrollEnabled(userScrollEnabled);
        this.setFitsSystemWindowsCompat(fitSystemWindows);
        this.setSliverScrollingEnabled(sliverScrollingEnabled);
        this.setNestedScrollingEnabled(nestedScrollingEnabled);
        this.setInheritScrollingEnabled(inheritScrollingEnabled);
    }

    @Override
    protected void onAttachedToWindow() {
        this.mIsLayoutDirty = false;
        if (this.mIsFirstLayoutComplete) {
            this.mIsFirstLayoutComplete = !this.isLayoutRequested();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopSliverScroll();
        this.stopNestedScroll();
        this.stopInheritScroll();
        this.stopAnimatedOverScroll();
    }

    // ########## Layout ##########

    @Override
    public boolean checkLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @NonNull
    @Override
    public LayoutParams generateDefaultLayoutParams() {
        if (this.canScrollVertically()) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    }

    @NonNull
    public <T extends LayoutParams> T generateLayoutParams(@NonNull View child) {
        final ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            return (T) this.generateDefaultLayoutParams();
        }
        if (!this.checkLayoutParams(layoutParams)) {
            return (T) this.generateLayoutParams(layoutParams);
        }
        return (T) layoutParams;
    }

    @NonNull
    @Override
    public LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @NonNull
    @Override
    public LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        boolean measureMatchParentChildren;
        measureMatchParentChildren = widthMeasureMode != MeasureSpec.EXACTLY;
        measureMatchParentChildren |= heightMeasureMode != MeasureSpec.EXACTLY;

        this.mMatchParentChildren.clear();
        int state = 0;
        int width = 0;
        int height = 0;
        int maxWidth = 0;
        int maxHeight = 0;

        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE && !this.mIsMeasureAllChildren) {
                continue;
            }
            this.measureChildWithMargins(child, widthMeasureSpec, heightMeasureSpec);
            final int childMeasuredWidth = this.getDecoratedMeasuredWidth(child);
            final int childMeasuredHeight = this.getDecoratedMeasuredHeight(child);
            final LayoutParams layoutParams;

            layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.scrolling) {
                if (this.canScrollVertically()) {
                    width = Math.max(width, childMeasuredWidth);
                    height += childMeasuredHeight;
                } else {
                    width += childMeasuredWidth;
                    height = Math.max(height, childMeasuredHeight);
                }
            } else {
                maxWidth = Math.max(maxWidth, childMeasuredWidth);
                maxHeight = Math.max(maxHeight, childMeasuredHeight);
            }
            state = combineMeasuredStates(state, child.getMeasuredState());

            if (measureMatchParentChildren) {
                if (LayoutParams.MATCH_PARENT == layoutParams.width
                        || LayoutParams.MATCH_PARENT == layoutParams.height) {
                    this.mMatchParentChildren.add(child);
                }
            }
        }
        width = Math.max(width, maxWidth);
        height = Math.max(height, maxHeight);

        // Check against our minimum width and height
        width = Math.max(width, this.getSuggestedMinimumWidth());
        height = Math.max(height, this.getSuggestedMinimumHeight());

        // Check against our foreground's minimum width and height
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Drawable drawable = this.getForeground();
            if (drawable != null) {
                width = Math.max(width, drawable.getMinimumWidth());
                height = Math.max(height, drawable.getMinimumHeight());
            }
        }

        // Setup real width and height
        this.setMeasuredDimension(
                resolveSizeAndState(width, widthMeasureSpec, state),
                resolveSizeAndState(height, heightMeasureSpec,
                        state << MEASURED_HEIGHT_STATE_SHIFT));

        for (final View child : this.mMatchParentChildren) {
            final LayoutParams layoutParams;
            final int measuredWidth = this.getMeasuredWidth();
            final int measuredHeight = this.getMeasuredHeight();
            layoutParams = (LayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec = widthMeasureSpec;
            int childHeightMeasureSpec = heightMeasureSpec;

            if (this.canScrollVertically()) {
                if (LayoutParams.MATCH_PARENT == layoutParams.width) {
                    childWidthMeasureSpec =
                            MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
                }
            } else {
                if (LayoutParams.MATCH_PARENT == layoutParams.height) {
                    childHeightMeasureSpec =
                            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
                }
            }
            this.measureChildWithMargins(child, childWidthMeasureSpec, childHeightMeasureSpec);
        } // End
    }

    @Override
    protected void measureChild(@NonNull View child, int widthMeasureSpec, int heightMeasureSpec) {
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        final int widthPadding = layoutParams.widthUsed
                + this.getPaddingLeft()
                + this.getPaddingRight();
        final int heightPadding = layoutParams.heightUsed
                + this.getPaddingTop()
                + this.getPaddingBottom();
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        // Default measure spec
        childWidthMeasureSpec =
                getChildMeasureSpec(widthMeasureSpec, widthPadding, layoutParams.width);
        childHeightMeasureSpec =
                getChildMeasureSpec(heightMeasureSpec, heightPadding, layoutParams.height);

        if (!layoutParams.scrolling) {
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            return;
        }
        if (LayoutParams.WRAP_CONTENT == layoutParams.width) {
            if (this.canScrollHorizontally()) {
                childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        }
        if (LayoutParams.WRAP_CONTENT == layoutParams.height) {
            if (this.canScrollVertically()) {
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    protected void measureChildWithMargins(@NonNull View child,
                                           int widthMeasureSpec,
                                           int heightMeasureSpec) {
        this.measureChildWithMargins(child,
                widthMeasureSpec, 0,
                heightMeasureSpec, 0);
    }

    @Override
    protected void measureChildWithMargins(@NonNull View child,
                                           int widthMeasureSpec, int widthUsed,
                                           int heightMeasureSpec, int heightUsed) {
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        final int widthMargins = layoutParams.leftMargin + layoutParams.rightMargin;
        final int heightMargins = layoutParams.topMargin + layoutParams.bottomMargin;
        final int widthPadding = layoutParams.widthUsed
                + this.getPaddingLeft()
                + this.getPaddingRight() + widthUsed + widthMargins;
        final int heightPadding = layoutParams.heightUsed
                + this.getPaddingTop()
                + this.getPaddingBottom() + heightUsed + heightMargins;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        // Default measure spec
        childWidthMeasureSpec =
                getChildMeasureSpec(widthMeasureSpec, widthPadding, layoutParams.width);
        childHeightMeasureSpec =
                getChildMeasureSpec(heightMeasureSpec, heightPadding, layoutParams.height);

        if (!layoutParams.scrolling) {
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            return;
        }
        if (LayoutParams.WRAP_CONTENT == layoutParams.width) {
            if (this.canScrollHorizontally()) {
                childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(widthMargins, MeasureSpec.UNSPECIFIED);
            }
        }
        if (LayoutParams.WRAP_CONTENT == layoutParams.height) {
            if (this.canScrollVertically()) {
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(heightMargins, MeasureSpec.UNSPECIFIED);
            }
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    @CallSuper
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.onLayoutChildren(changed, left, top, right, bottom);
        final int parentW = right - left - this.getPaddingLeft() - this.getPaddingRight();
        final int parentH = bottom - top - this.getPaddingTop() - this.getPaddingBottom();
        final int childrenW = this.computeHorizontalScrollRange();
        final int childrenH = this.computeVerticalScrollRange();
        final int oldScrollX = clamp(this.getScrollOffsetX(), parentW, childrenW);
        final int oldScrollY = clamp(this.getScrollOffsetY(), parentH, childrenH);
        final int newScrollX = this.computeHorizontalScrollOffset();
        final int newScrollY = this.computeVerticalScrollOffset();
        final int dx = (oldScrollX + this.getExtraOffsetX()) - newScrollX;
        final int dy = (oldScrollY + this.getExtraOffsetY()) - newScrollY;
        if (dx != 0 || dy != 0) {
            this.offsetChildren(-dx, -dy);
        }
        if (newScrollX != oldScrollX || newScrollY != oldScrollY) {
            this.dispatchOnScrolled(0, 0);
        }
        this.mIsLayoutDirty = true;
        this.mIsFirstLayoutComplete = true;
    }

    @Override
    public void requestLayout() {
        this.mIsLayoutDirty = false;
        super.requestLayout();
    }

    protected void onLayoutChildren(boolean changed, int left, int top, int right, int bottom) {
        if (this.canScrollVertically()) {
            this.layoutVertical(left, top, right, bottom);
        } else {
            this.layoutHorizontal(left, top, right, bottom);
        }
    }

    private void layoutHorizontal(int left, int top, int right, int bottom) {
        final int layoutTop = this.getPaddingTop();
        final int layoutLeft = this.getPaddingLeft();
        final int layoutRight = right - left - this.getPaddingRight();
        final int layoutBottom = bottom - top - this.getPaddingBottom();
        int layoutLeftLast = layoutLeft;

        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            final int childMeasuredWidth = this.getDecoratedMeasuredWidth(child);
            final int childMeasuredHeight = this.getDecoratedMeasuredHeight(child);
            final int realLayoutLeft;
            final int realLayoutRight;
            final int realLayoutOffset;
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();

            if (layoutParams.scrolling) {
                realLayoutLeft = layoutLeftLast;
                realLayoutRight = layoutLeftLast + childMeasuredWidth;
                realLayoutOffset = childMeasuredWidth;
            } else {
                realLayoutLeft = layoutLeft;
                realLayoutRight = layoutRight;
                realLayoutOffset = 0;
            }
            layoutLeftLast += realLayoutOffset;
            this.layoutChild(child, realLayoutLeft, layoutTop, realLayoutRight, layoutBottom);
        }
    }

    private void layoutVertical(int left, int top, int right, int bottom) {
        final int layoutTop = this.getPaddingTop();
        final int layoutLeft = this.getPaddingLeft();
        final int layoutRight = right - left - this.getPaddingRight();
        final int layoutBottom = bottom - top - this.getPaddingBottom();
        int layoutTopLast = layoutTop;

        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            final int childMeasuredWidth = this.getDecoratedMeasuredWidth(child);
            final int childMeasuredHeight = this.getDecoratedMeasuredHeight(child);
            final int realLayoutTop;
            final int realLayoutBottom;
            final int realLayoutOffset;
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();

            if (layoutParams.scrolling) {
                realLayoutTop = layoutTopLast;
                realLayoutBottom = layoutTopLast + childMeasuredHeight;
                realLayoutOffset = childMeasuredHeight;
            } else {
                realLayoutTop = layoutTop;
                realLayoutBottom = layoutBottom;
                realLayoutOffset = 0;
            }
            layoutTopLast += realLayoutOffset;
            this.layoutChild(child, layoutLeft, realLayoutTop, layoutRight, realLayoutBottom);
        }
    }

    @SuppressLint("RtlHardcoded")
    protected void layoutChild(@NonNull View child, int left, int top, int right, int bottom) {
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        int gravity = layoutParams.gravity;
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = LayoutParams.DEFAULT_GRAVITY;
        }
        final int measuredWidth = child.getMeasuredWidth();
        final int measuredHeight = child.getMeasuredHeight();
        final int layoutDirection = this.getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        int layoutLeft;
        int layoutTop;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                layoutLeft = left + (right - left - measuredWidth) / 2
                        + layoutParams.leftMargin
                        - layoutParams.rightMargin;
                break;
            case Gravity.RIGHT:
                layoutLeft = right - measuredWidth - layoutParams.rightMargin;
                break;
            case Gravity.LEFT:
            default:
                layoutLeft = left + layoutParams.leftMargin;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.CENTER_VERTICAL:
                layoutTop = top + (bottom - top - measuredHeight) / 2
                        + layoutParams.topMargin
                        - layoutParams.bottomMargin;
                break;
            case Gravity.BOTTOM:
                layoutTop = bottom - measuredHeight - layoutParams.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                layoutTop = top + layoutParams.topMargin;
        }
        child.layout(layoutLeft, layoutTop, layoutLeft + measuredWidth, layoutTop + measuredHeight);
    }

    public final int getDecoratedMeasuredWidth(@NonNull View child) {
        final MarginLayoutParams layoutParams;
        layoutParams = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth()
                + layoutParams.leftMargin
                + layoutParams.rightMargin;
    }

    public final int getDecoratedMeasuredHeight(@NonNull View child) {
        final MarginLayoutParams layoutParams;
        layoutParams = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight()
                + layoutParams.topMargin
                + layoutParams.bottomMargin;
    }

    // ########## InstanceState ##########

    @NonNull
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mScrollRanges[0] = this.getScrollRangeX();
        savedState.mScrollRanges[1] = this.getScrollRangeY();
        savedState.mScrollOffsets[0] = this.getScrollOffsetX();
        savedState.mScrollOffsets[1] = this.getScrollOffsetY();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mScrollRanges[0] = savedState.mScrollRanges[0];
            this.mScrollRanges[1] = savedState.mScrollRanges[1];
            this.mScrollOffsets[0] = savedState.mScrollOffsets[0];
            this.mScrollOffsets[1] = savedState.mScrollOffsets[1];
            this.requestLayout();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    // ########## Keyboard ##########

    private final Rect mTempRect = new Rect();

    private OnFitSystemWindowListener mOnFitSystemWindowListener;

    @Override
    public void requestChildFocus(@NonNull View child, @Nullable View focused) {
        if (focused != null
                && this.mScrollState == SCROLL_STATE_IDLE
                && this.mIsLayoutDirty
                && this.mIsChildFocusable) {
            this.requestChildOnScreen(child, focused);
        }
        super.requestChildFocus(child, focused);
    }

    private void requestChildOnScreen(@NonNull View child, @Nullable View focused) {
        final View rectView = focused == null ? child : focused;
        final Rect rectangle = this.mTempRect;
        rectangle.set(0, 0, rectView.getWidth(), rectView.getHeight());

        if (focused != null) {
            this.offsetDescendantRectToMyCoords(focused, rectangle);
            this.offsetRectIntoDescendantCoords(child, rectangle);
        }
        this.scrollToChildRect(child, rectangle, !this.mIsFirstLayoutComplete);
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull View child,
                                                 @NonNull Rect rectangle, boolean immediate) {
        return this.scrollToChildRect(child, rectangle, immediate);
    }

    public boolean scrollToChildRect(@NonNull View child, @NonNull Rect rectangle, boolean immediate) {
        final int[] scrollAmount = this.getChildRectangleOnScreenScrollAmount(child, rectangle);
        if (scrollAmount[0] != 0 || scrollAmount[1] != 0) {
            if (immediate) {
                this.scrollBy(scrollAmount[0], scrollAmount[1]);
            } else {
                this.smoothScrollBy(scrollAmount[0], scrollAmount[1]);
            }
        }
        return scrollAmount[0] != 0 || scrollAmount[1] != 0;
    }

    @SuppressLint("WrongConstant")
    private int[] getChildRectangleOnScreenScrollAmount(@NonNull View child, @NonNull Rect rectangle) {
        final int[] scrollAmount = new int[2];
        final int parentTop = this.getPaddingTop();
        final int parentLeft = this.getPaddingLeft();
        final int parentRight = this.getWidth() - this.getPaddingRight();
        final int parentBottom = this.getHeight() - this.getPaddingBottom();

        final int childTop = child.getTop() + rectangle.top - child.getScrollY();
        final int childLeft = child.getLeft() + rectangle.left - child.getScrollX();
        final int childRight = childLeft + rectangle.width();
        final int childBottom = childTop + rectangle.height();

        final int offScreenTop = Math.min(0, childTop - parentTop);
        final int offScreenLeft = Math.min(0, childLeft - parentLeft);
        final int offScreenRight = Math.max(0, childRight - parentRight);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom);

        if (ViewCompat.LAYOUT_DIRECTION_RTL == this.getLayoutDirection()) {
            scrollAmount[0] = offScreenRight == 0
                    ? Math.max(offScreenLeft, childRight - parentRight)
                    : offScreenRight;
        } else {
            scrollAmount[0] = offScreenLeft == 0
                    ? Math.min(childLeft - parentLeft, offScreenRight)
                    : offScreenLeft;
        }
        scrollAmount[1] = offScreenTop == 0
                ? Math.min(childTop - parentTop, offScreenBottom)
                : offScreenTop;
        return scrollAmount;
    }

    @Override
    protected boolean fitSystemWindows(@NonNull Rect insets) {
        if (this.mOnFitSystemWindowListener != null) {
            this.mOnFitSystemWindowListener.onFitSystemWindows(this, insets);
        }
        return super.fitSystemWindows(insets);
    }

    public void setFitsSystemWindowsCompat(boolean fitSystemWindows) {
        if (fitSystemWindows) {
            FitSystemWindowCompat.setFitsSystemWindows(this);
        } else {
            this.setFitsSystemWindows(false);
            this.setOnFitSystemWindowListener(null);
        }
    }

    @Nullable
    public OnFitSystemWindowListener getOnFitSystemWindowListener() {
        return this.mOnFitSystemWindowListener;
    }

    @Override
    public void setOnFitSystemWindowListener(@Nullable OnFitSystemWindowListener listener) {
        this.mOnFitSystemWindowListener = listener;
    }

    // ########## Touch ##########

    private final int[] mLastTouch = new int[2];
    private final int[] mInitialTouch = new int[2];
    private final int[] mNestedOffsets = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private final int[] mOffsetInWindow = new int[2];

    private final int mScaledTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private final ViewScroller mViewScroller;

    @Orientation
    private int mOrientation;
    @ScrollState
    private int mScrollState;
    @ScrollLocate
    private int mBounceLocate;
    private int mScrollPointerId;
    private boolean mIsLayoutDirty;
    private boolean mIsUnableToDrag;
    private boolean mIsChildFocusable;
    private boolean mIsScrollFloating;
    private boolean mIsUserScrollEnabled;
    private boolean mIsMeasureAllChildren;
    private boolean mIsFirstLayoutComplete;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Defensive cleanup for new gesture
            this.stopSliverScroll();
            this.stopTargetNestedScroll(SliverCompat.TYPE_NON_TOUCH);
            this.stopTargetInheritScroll(SliverCompat.TYPE_NON_TOUCH);
        }
        final boolean result = super.dispatchTouchEvent(event);
        if (actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_CANCEL
                || (actionMasked == MotionEvent.ACTION_DOWN && !result)) {
            // Clean up after inherit scrolls if this is the end of a gesture;
            // also cancel it if we tried an ACTION_DOWN but we didn't want the rest
            // of the gesture.
            this.stopSliverScroll();
            this.stopTargetNestedScroll();
            this.stopTargetInheritScroll();
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            if (this.performInterceptTouchEvent(event)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    protected boolean performInterceptTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_CANCEL) {
            this.mIsUnableToDrag = false;
            this.cancelTouchScroll(SliverCompat.TYPE_TOUCH);
            return false;
        }
        if (actionMasked != MotionEvent.ACTION_DOWN) {
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                this.mScrollPointerId = event.getPointerId(0);
                this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX() + 0.5f);
                this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY() + 0.5f);
                this.mNestedOffsets[0] = 0;
                this.mNestedOffsets[1] = 0;
                this.mIsUnableToDrag = false;

                if (SCROLL_STATE_SETTLING == this.mScrollState) {
                    this.stopSliverScroll(SliverCompat.TYPE_NON_TOUCH);
                    this.stopNestedScroll(SliverCompat.TYPE_NON_TOUCH);
                    this.stopInheritScroll(SliverCompat.TYPE_NON_TOUCH);
                    this.requestParentDisallowInterceptTouchEvent();
                }

                int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
                if (this.canScrollHorizontally()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
                }
                if (this.canScrollVertically()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
                }
                this.startSliverScroll(scrollAxes);
                this.startNestedScroll(scrollAxes);
                this.startInheritScroll(scrollAxes);
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex;
                pointerIndex = event.findPointerIndex(this.mScrollPointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                if (this.getNestedScrollAxes() != SliverCompat.SCROLL_AXIS_NONE) {
                    this.mIsUnableToDrag = true;
                    return false;
                }
                if (SCROLL_STATE_DRAGGING != this.mScrollState) {
                    final int x = (int) (event.getX(pointerIndex) + 0.5f);
                    final int y = (int) (event.getY(pointerIndex) + 0.5f);
                    final int dx = this.mInitialTouch[0] - x;
                    final int dy = this.mInitialTouch[1] - y;
                    boolean dragStarted = false;
                    int dxAbs = 0;
                    int dyAbs = 0;

                    if (this.canScrollHorizontally()) {
                        dxAbs = Math.abs(dx);
                    }
                    if (this.canScrollVertically()) {
                        dyAbs = Math.abs(dy);
                    }
                    if (dxAbs > this.mScaledTouchSlop) {
                        dragStarted = true;
                    }
                    if (dyAbs > this.mScaledTouchSlop) {
                        dragStarted = true;
                    }
                    if (dragStarted) {
                        this.mLastTouch[0] = x;
                        this.mLastTouch[1] = y;
                        this.setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                this.onSecondaryPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                this.onSecondaryPointerUp(event);
                break;
        }
        return SCROLL_STATE_DRAGGING == this.mScrollState;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            if (this.performTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    protected boolean performTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            this.mNestedOffsets[0] = 0;
            this.mNestedOffsets[1] = 0;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        final MotionEvent motionEvent = MotionEvent.obtain(event);
        motionEvent.offsetLocation(
                this.mNestedOffsets[0],
                this.mNestedOffsets[1]);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                this.mScrollPointerId = event.getPointerId(0);
                this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX() + 0.5f);
                this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY() + 0.5f);

                int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
                if (this.canScrollHorizontally()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
                }
                if (this.canScrollVertically()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
                }
                this.startSliverScroll(scrollAxes);
                this.startNestedScroll(scrollAxes);
                this.startInheritScroll(scrollAxes);
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex;
                pointerIndex = event.findPointerIndex(this.mScrollPointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int x = (int) (event.getX(pointerIndex) + 0.5f);
                final int y = (int) (event.getY(pointerIndex) + 0.5f);
                int dx = this.mLastTouch[0] - x;
                int dy = this.mLastTouch[1] - y;

                if (SCROLL_STATE_DRAGGING != this.mScrollState) {
                    boolean dragStarted = false;
                    int dxAbs = 0;
                    int dyAbs = 0;

                    if (this.canScrollHorizontally()) {
                        dxAbs = Math.abs(dx);
                    }
                    if (this.canScrollVertically()) {
                        dyAbs = Math.abs(dy);
                    }
                    if (dxAbs > this.mScaledTouchSlop) {
                        if (dx > 0) {
                            dx = Math.max(0, dx - this.mScaledTouchSlop);
                        } else {
                            dx = Math.min(dx + this.mScaledTouchSlop, 0);
                        }
                        if (dx != 0) {
                            dragStarted = true;
                        }
                    }
                    if (dyAbs > this.mScaledTouchSlop) {
                        if (dy > 0) {
                            dy = Math.max(0, dy - this.mScaledTouchSlop);
                        } else {
                            dy = Math.min(dy + this.mScaledTouchSlop, 0);
                        }
                        if (dy != 0) {
                            dragStarted = true;
                        }
                    }
                    if (dragStarted) {
                        this.setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }
                if (SCROLL_STATE_DRAGGING == this.mScrollState) {
                    this.mLastTouch[0] = x;
                    this.mLastTouch[1] = y;

                    final int[] scrollConsumed = this.mScrollConsumed;
                    dx = this.canScrollHorizontally() ? dx : 0;
                    dy = this.canScrollVertically() ? dy : 0;

                    if (this.scrollByInternal(dx, dy, scrollConsumed, SliverCompat.TYPE_TOUCH)) {
                        dx -= scrollConsumed[0];
                        dy -= scrollConsumed[1];
                        this.requestParentDisallowInterceptTouchEvent();
                    }
                    if (this.pullEdgeGlows(x, y, dx, dy)) {
                        this.releasingEdgeGlows(dx, dy);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                this.onSecondaryPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                this.onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.cancelTouchScroll(SliverCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_UP:
                this.mVelocityTracker.addMovement(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
                int velocityX = 0;
                int velocityY = 0;
                if (this.canScrollHorizontally()) {
                    velocityX = (int) -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
                }
                if (this.canScrollVertically()) {
                    velocityY = (int) -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
                }
                if (!this.flingByInternal(velocityX, velocityY)) {
                    this.setScrollState(SCROLL_STATE_IDLE);
                }
                this.resetTouchScroll(SliverCompat.TYPE_TOUCH);
                break;
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.addMovement(motionEvent);
        }
        motionEvent.recycle();
        return true;
    }

    public void requestParentDisallowInterceptTouchEvent() {
        final ViewParent parent = this.getParent();
        if (parent == null) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(true);
    }

    public boolean isChildFocusable() {
        return this.mIsChildFocusable;
    }

    public void setChildFocusable(boolean childFocusable) {
        if (this.mIsChildFocusable != childFocusable) {
            this.mIsChildFocusable = childFocusable;
        }
    }

    public boolean isScrollFloating() {
        return this.mIsScrollFloating;
    }

    public void setScrollFloating(boolean scrollFloating) {
        if (this.mIsScrollFloating != scrollFloating) {
            this.mIsScrollFloating = scrollFloating;
        }
    }

    public boolean isMeasureAllChildren() {
        return this.mIsMeasureAllChildren;
    }

    public void setMeasureAllChildren(boolean enabled) {
        if (this.mIsMeasureAllChildren != enabled) {
            this.mIsMeasureAllChildren = enabled;
        }
    }

    public boolean isUserScrollEnabled() {
        return this.mIsUserScrollEnabled;
    }

    public void setUserScrollEnabled(boolean enabled) {
        if (this.mIsUserScrollEnabled != enabled) {
            this.mIsUserScrollEnabled = enabled;
        }
    }

    @ScrollState
    public int getScrollState() {
        return this.mScrollState;
    }

    @Orientation
    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(@Orientation int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            this.requestLayout();
        }
    }

    @ScrollLocate
    public int getBounceLocate() {
        return this.mBounceLocate;
    }

    public void setBounceLocate(@ScrollLocate int bounceLocate) {
        if (this.mBounceLocate != bounceLocate) {
            this.mBounceLocate = bounceLocate;
        }
    }

    public final int getScaledTouchSlop() {
        return this.mScaledTouchSlop;
    }

    public final int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    public final int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public void stopAnimatedOverScroll() {
        this.setScrollState(SCROLL_STATE_IDLE);
        this.abortAnimatedOverScroll();
    }

    public void abortAnimatedOverScroll() {
        this.mViewScroller.abortScroll();
    }

    @NonNull
    public final ViewScroller getViewScroller() {
        return this.mViewScroller;
    }

    public final boolean canScrollHorizontally() {
        return HORIZONTAL == this.mOrientation;
    }

    public final boolean canScrollVertically() {
        return VERTICAL == this.mOrientation;
    }

    private void onSecondaryPointerDown(@NonNull MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        this.mScrollPointerId = event.getPointerId(actionIndex);
        this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX(actionIndex) + 0.5f);
        this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY(actionIndex) + 0.5f);
    }

    private void onSecondaryPointerUp(@NonNull MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        if (event.getPointerId(actionIndex) == this.mScrollPointerId) {
            final int newIndex = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = event.getPointerId(newIndex);
            this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX(newIndex) + 0.5f);
            this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY(newIndex) + 0.5f);
        }
    }

    private boolean scrollByInternal(int dx, int dy, @NonNull int[] scrollConsumed, int scrollType) {
        final int[] offsetInWindow = this.mOffsetInWindow;
        boolean needsAwakenScrollBars = false;
        int dxConsumed = 0;
        int dyConsumed = 0;
        scrollConsumed[0] = 0;
        scrollConsumed[1] = 0;

        if (this.dispatchSliverPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
            needsAwakenScrollBars = true;
        }

        if (this.dispatchNestedPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        if (this.dispatchInheritPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
            needsAwakenScrollBars = true;
        }

        if (this.dispatchNestedScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        if (this.dispatchInheritScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        if (this.dispatchBounceScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
            needsAwakenScrollBars = true;
        }

        if (needsAwakenScrollBars && !this.awakenScrollBars()) {
            this.invalidate();
        }
        scrollConsumed[0] = dxConsumed;
        scrollConsumed[1] = dyConsumed;
        return scrollConsumed[0] != 0 || scrollConsumed[1] != 0;
    }

    private boolean flingByInternal(int velocityX, int velocityY) {
        if (Math.abs(velocityX) < this.mMinFlingVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < this.mMinFlingVelocity) {
            velocityY = 0;
        }
        if (velocityX == 0 && velocityY == 0) {
            return false;
        }
        if (this.dispatchSliverPreFling(velocityX, velocityY)
                || this.dispatchNestedPreFling(velocityX, velocityY)
                || this.dispatchInheritPreFling(velocityX, velocityY)) {
            return false;
        }
        final boolean canScrollVertically = this.canScrollVertically();
        final boolean canScrollHorizontally = this.canScrollHorizontally();
        final boolean consumed = canScrollVertically || canScrollHorizontally;

        if (this.dispatchSliverFling(velocityX, velocityY, consumed)
                || this.dispatchNestedFling(velocityX, velocityY, consumed)
                || this.dispatchInheritFling(velocityX, velocityY, consumed)) {
            return false;
        }
        if (this.dispatchOnFling(velocityX, velocityY)) {
            return true;
        }
        if (consumed) {
            int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
            if (canScrollHorizontally) {
                scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
            }
            if (canScrollVertically) {
                scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
            }
            this.startSliverScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            this.startNestedScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            this.startInheritScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            velocityX = Math.min(velocityX, this.mMaxFlingVelocity);
            velocityY = Math.min(velocityY, this.mMaxFlingVelocity);
            velocityX = Math.max(-this.mMaxFlingVelocity, velocityX);
            velocityY = Math.max(-this.mMaxFlingVelocity, velocityY);
            this.mViewScroller.fling(0, 0, velocityX, velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
        return consumed;
    }

    private void cancelTouchScroll(int scrollType) {
        this.setScrollState(SCROLL_STATE_IDLE);
        this.resetTouchScroll(scrollType);
    }

    private void resetTouchScroll(int scrollType) {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.stopSliverScroll(scrollType);
        this.stopNestedScroll(scrollType);
        this.stopInheritScroll(scrollType);
        this.releaseEdgeGlows();
    }

    private void setScrollState(int scrollState) {
        if (this.mScrollState != scrollState) {
            this.mScrollState = scrollState;

            if (SCROLL_STATE_SETTLING != scrollState) {
                this.abortAnimatedOverScroll();
            }
            this.dispatchOnScrollStateChanged(scrollState);
        }
    }

    // ########## Draw ##########

    private EdgeEffect mEdgeGlowTop;
    private EdgeEffect mEdgeGlowLeft;
    private EdgeEffect mEdgeGlowRight;
    private EdgeEffect mEdgeGlowBottom;

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int paddingTop = this.getPaddingTop();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int paddingBottom = this.getPaddingBottom();
        boolean clipToPadding = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToPadding = this.getClipToPadding();
        }
        boolean needsInvalidate = false;
        int dx;
        int dy;

        final EdgeEffect edgeGlowLeft = this.mEdgeGlowLeft;
        if (edgeGlowLeft != null
                && !edgeGlowLeft.isFinished()) {
            if (clipToPadding) {
                dx = -height + paddingBottom;
            } else {
                dx = -height;
            }
            dy = 0;
            final int restore = canvas.save();
            canvas.rotate(270);
            canvas.translate(dx, dy);
            needsInvalidate |= edgeGlowLeft.draw(canvas);
            canvas.restoreToCount(restore);
        }

        final EdgeEffect edgeGlowRight = this.mEdgeGlowRight;
        if (edgeGlowRight != null
                && !edgeGlowRight.isFinished()) {
            if (clipToPadding) {
                dx = -paddingTop;
            } else {
                dx = 0;
            }
            dy = -width;
            final int restore = canvas.save();
            canvas.rotate(90);
            canvas.translate(dx, dy);
            needsInvalidate |= edgeGlowRight.draw(canvas);
            canvas.restoreToCount(restore);
        }

        final EdgeEffect edgeGlowTop = this.mEdgeGlowTop;
        if (edgeGlowTop != null
                && !edgeGlowTop.isFinished()) {
            dx = paddingLeft;
            dy = paddingTop;
            final int restore = canvas.save();
            if (clipToPadding) {
                canvas.translate(dx, dy);
            }
            needsInvalidate |= edgeGlowTop.draw(canvas);
            canvas.restoreToCount(restore);
        }

        final EdgeEffect edgeGlowBottom = this.mEdgeGlowBottom;
        if (edgeGlowBottom != null
                && !edgeGlowBottom.isFinished()) {
            if (clipToPadding) {
                dx = -width + paddingRight;
                dy = -height + paddingBottom;
            } else {
                dx = -width;
                dy = -height;
            }
            final int restore = canvas.save();
            canvas.rotate(180);
            canvas.translate(dx, dy);
            needsInvalidate |= edgeGlowBottom.draw(canvas);
            canvas.restoreToCount(restore);
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void ensureLeftEdgeGlow() {
        if (this.mEdgeGlowLeft == null) {
            this.mEdgeGlowLeft = new EdgeEffect(this.getContext());
            int width = this.getMeasuredHeight();
            int height = this.getMeasuredWidth();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (this.getClipToPadding()) {
                    width -= this.getPaddingTop();
                    width -= this.getPaddingBottom();
                    height -= this.getPaddingLeft();
                    height -= this.getPaddingRight();
                }
            }
            this.mEdgeGlowLeft.setSize(width, height);
        }
    }

    private void ensureRightEdgeGlow() {
        if (this.mEdgeGlowRight == null) {
            this.mEdgeGlowRight = new EdgeEffect(this.getContext());
            int width = this.getMeasuredHeight();
            int height = this.getMeasuredWidth();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (this.getClipToPadding()) {
                    width -= this.getPaddingTop();
                    width -= this.getPaddingBottom();
                    height -= this.getPaddingLeft();
                    height -= this.getPaddingRight();
                }
            }
            this.mEdgeGlowRight.setSize(width, height);
        }
    }

    private void ensureTopEdgeGlow() {
        if (this.mEdgeGlowTop == null) {
            this.mEdgeGlowTop = new EdgeEffect(this.getContext());
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (this.getClipToPadding()) {
                    width -= this.getPaddingLeft();
                    width -= this.getPaddingRight();
                    height -= this.getPaddingTop();
                    height -= this.getPaddingBottom();
                }
            }
            this.mEdgeGlowTop.setSize(width, height);
        }
    }

    private void ensureBottomEdgeGlow() {
        if (this.mEdgeGlowBottom == null) {
            this.mEdgeGlowBottom = new EdgeEffect(this.getContext());
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (this.getClipToPadding()) {
                    width -= this.getPaddingLeft();
                    width -= this.getPaddingRight();
                    height -= this.getPaddingTop();
                    height -= this.getPaddingBottom();
                }
            }
            this.mEdgeGlowBottom.setSize(width, height);
        }
    }

    private boolean pullEdgeGlows(int x, int y, int dx, int dy) {
        if (View.OVER_SCROLL_NEVER == this.getOverScrollMode()) {
            return false;
        }
        final int width = this.getWidth();
        final int height = this.getHeight();
        boolean needsInvalidate = false;

        if (dx < 0) {
            this.ensureLeftEdgeGlow();
            EdgeEffectCompat.onPull(this.mEdgeGlowLeft,
                    (float) -dx / width,
                    1.f - (float) y / height);
            needsInvalidate = true;
        } else if (dx > 0) {
            this.ensureRightEdgeGlow();
            EdgeEffectCompat.onPull(this.mEdgeGlowRight,
                    (float) dx / width,
                    (float) y / height);
            needsInvalidate = true;
        }

        if (dy < 0) {
            this.ensureTopEdgeGlow();
            EdgeEffectCompat.onPull(this.mEdgeGlowTop,
                    (float) -dy / height,
                    (float) x / width);
            needsInvalidate = true;
        } else if (dy > 0) {
            this.ensureBottomEdgeGlow();
            EdgeEffectCompat.onPull(this.mEdgeGlowBottom,
                    (float) dy / height,
                    1.f - (float) x / width);
            needsInvalidate = true;
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return needsInvalidate;
    }

    private void absorbEdgeGlows(int velocityX, int velocityY) {
        if (View.OVER_SCROLL_NEVER == this.getOverScrollMode()) {
            return;
        }
        boolean needsInvalidate = false;

        if (velocityX < 0) {
            this.ensureLeftEdgeGlow();
            if (this.mEdgeGlowLeft.isFinished()) {
                this.mEdgeGlowLeft.onAbsorb(-velocityX);
            }
            needsInvalidate = true;
        } else if (velocityX > 0) {
            this.ensureRightEdgeGlow();
            if (this.mEdgeGlowRight.isFinished()) {
                this.mEdgeGlowRight.onAbsorb(velocityX);
            }
            needsInvalidate = true;
        }

        if (velocityY < 0) {
            this.ensureTopEdgeGlow();
            if (this.mEdgeGlowTop.isFinished()) {
                this.mEdgeGlowTop.onAbsorb(-velocityY);
            }
            needsInvalidate = true;
        } else if (velocityY > 0) {
            this.ensureBottomEdgeGlow();
            if (this.mEdgeGlowBottom.isFinished()) {
                this.mEdgeGlowBottom.onAbsorb(velocityY);
            }
            needsInvalidate = true;
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void releasingEdgeGlows(int dx, int dy) {
        if (View.OVER_SCROLL_NEVER == this.getOverScrollMode()) {
            return;
        }
        boolean needsInvalidate = false;

        final EdgeEffect edgeGlowLeft = this.mEdgeGlowLeft;
        if (edgeGlowLeft != null
                && !edgeGlowLeft.isFinished() && dx > 0) {
            edgeGlowLeft.onRelease();
            needsInvalidate |= edgeGlowLeft.isFinished();
        }

        final EdgeEffect edgeGlowRight = this.mEdgeGlowRight;
        if (edgeGlowRight != null
                && !edgeGlowRight.isFinished() && dx < 0) {
            edgeGlowRight.onRelease();
            needsInvalidate |= edgeGlowRight.isFinished();
        }

        final EdgeEffect edgeGlowTop = this.mEdgeGlowTop;
        if (edgeGlowTop != null
                && !edgeGlowTop.isFinished() && dy > 0) {
            edgeGlowTop.onRelease();
            needsInvalidate |= edgeGlowTop.isFinished();
        }

        final EdgeEffect edgeGlowBottom = this.mEdgeGlowBottom;
        if (edgeGlowBottom != null
                && !edgeGlowBottom.isFinished() && dy < 0) {
            edgeGlowBottom.onRelease();
            needsInvalidate |= edgeGlowBottom.isFinished();
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void releaseEdgeGlows() {
        boolean needsInvalidate = false;

        final EdgeEffect edgeGlowLeft = this.mEdgeGlowLeft;
        if (edgeGlowLeft != null) {
            edgeGlowLeft.onRelease();
            needsInvalidate |= edgeGlowLeft.isFinished();
        }

        final EdgeEffect edgeGlowRight = this.mEdgeGlowRight;
        if (edgeGlowRight != null) {
            edgeGlowRight.onRelease();
            needsInvalidate |= edgeGlowRight.isFinished();
        }

        final EdgeEffect edgeGlowTop = this.mEdgeGlowTop;
        if (edgeGlowTop != null) {
            edgeGlowTop.onRelease();
            needsInvalidate |= edgeGlowTop.isFinished();
        }

        final EdgeEffect edgeGlowBottom = this.mEdgeGlowBottom;
        if (edgeGlowBottom != null) {
            edgeGlowBottom.onRelease();
            needsInvalidate |= edgeGlowBottom.isFinished();
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    // ########## ScrollingView ##########

    private final int[] mScrollRanges = new int[2];
    private final int[] mScrollOffsets = new int[2];
    private final ViewOrientationHelper mHorOrientationHelper;
    private final ViewOrientationHelper mVerOrientationHelper;

    public final int getExtraOffsetX() {
        final int range;
        range = this.getScrollRangeX();
        final int scroll;
        scroll = this.getScrollOffsetX();
        if (range == 0) {
            return scroll;
        }
        if (scroll <= range) {
            return Math.min(scroll, 0);
        }
        return Math.max(0, scroll - range);
    }

    public final int getExtraOffsetY() {
        final int range;
        range = this.getScrollRangeY();
        final int scroll;
        scroll = this.getScrollOffsetY();
        if (range == 0) {
            return scroll;
        }
        if (scroll <= range) {
            return Math.min(scroll, 0);
        }
        return Math.max(0, scroll - range);
    }

    public final int getScrollRangeX() {
        return this.mScrollRanges[0];
    }

    public final int getScrollRangeY() {
        return this.mScrollRanges[1];
    }

    public final int getScrollOffsetX() {
        return this.mScrollOffsets[0];
    }

    public final int getScrollOffsetY() {
        return this.mScrollOffsets[1];
    }

    public final int computeMaxScrollRangeX() {
        int range = 0;
        range += this.computeHorizontalScrollRange();
        range -= this.computeHorizontalScrollExtent();
        return Math.max(0, range);
    }

    public final int computeMaxScrollRangeY() {
        int range = 0;
        range += this.computeVerticalScrollRange();
        range -= this.computeVerticalScrollExtent();
        return Math.max(0, range);
    }

    @Override
    public int computeHorizontalScrollRange() {
        return this.computeScrollRange(this.mHorOrientationHelper);
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return this.computeScrollOffset(this.mHorOrientationHelper);
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return this.computeScrollExtent(this.mHorOrientationHelper);
    }

    @Override
    public int computeVerticalScrollRange() {
        return this.computeScrollRange(this.mVerOrientationHelper);
    }

    @Override
    public int computeVerticalScrollOffset() {
        return this.computeScrollOffset(this.mVerOrientationHelper);
    }

    @Override
    public int computeVerticalScrollExtent() {
        return this.computeScrollExtent(this.mVerOrientationHelper);
    }

    @NonNull
    public ViewOrientationHelper getHorOrientationHelper() {
        return this.mHorOrientationHelper;
    }

    @NonNull
    public ViewOrientationHelper getVerOrientationHelper() {
        return this.mVerOrientationHelper;
    }

    @Nullable
    protected View getChildClosestToHead() {
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();

            if (layoutParams.scrolling) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    protected View getChildClosestToTail() {
        final int N = this.getChildCount();
        for (int index = N - 1; index >= 0; index--) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();

            if (layoutParams.scrolling) {
                return child;
            }
        }
        return null;
    }

    private int computeScrollRange(@NonNull ViewOrientationHelper helper) {
        if (this.getChildCount() <= 0) {
            return 0;
        }
        final View head = this.getChildClosestToHead();
        final View tail = this.getChildClosestToTail();
        if (head == null || tail == null) {
            return 0;
        }
        final int scrollRange;
        scrollRange = helper.getDecoratedEnd(tail) - helper.getDecoratedStart(head);
        return Math.max(0, scrollRange);
    }

    private int computeScrollOffset(@NonNull ViewOrientationHelper helper) {
        if (this.getChildCount() <= 0) {
            return 0;
        }
        final View head = this.getChildClosestToHead();
        if (head == null) {
            return 0;
        }
        final int scrollOffset;
        scrollOffset = helper.getStartAfterPadding() - helper.getDecoratedStart(head);
        return scrollOffset;
    }

    private int computeScrollExtent(@NonNull ViewOrientationHelper helper) {
        if (this.getChildCount() <= 0) {
            return 0;
        }
        final View head = this.getChildClosestToHead();
        final View tail = this.getChildClosestToTail();
        if (head == null || tail == null) {
            return 0;
        }
        final int scrollExtent;
        scrollExtent = helper.getDecoratedEnd(tail) - helper.getDecoratedStart(head);
        final int totalExtent;
        totalExtent = helper.getTotalSpace();
        return Math.max(0, Math.min(totalExtent, scrollExtent));
    }

    // ########## Offsets ##########

    @Override
    public void scrollTo(int x, int y) {
        final int scrollX = this.getScrollOffsetX();
        final int scrollY = this.getScrollOffsetY();
        final int dx = scrollX - x;
        final int dy = scrollY - y;
        this.scrollBy(-dx, -dy);
    }

    @Override
    public void scrollBy(int dx, int dy) {
        if (!this.canScrollHorizontally()) {
            dx = 0;
        }
        if (!this.canScrollVertically()) {
            dy = 0;
        }
        if (dx != 0 || dy != 0) {
            this.offsetChildren(-dx, -dy);
            this.mScrollOffsets[0] += dx;
            this.mScrollOffsets[1] += dy;
            this.dispatchOnScrolled(dx, dy);
        }
    }

    public void onScrolled(int dx, int dy) {
        // no-op
        printLog("onScrolled [" + this + "]"
                + "\n[EntryId=" + UIViewCompat.getResourceEntryName(this) + "]"
                + "\n[ScrollState=" + this.getScrollState() + "]"
                + "\n[SliverScrollAxes=" + this.getSliverScrollAxes() + "]"
                + "\n[SliverScrollType=" + this.getSliverScrollType() + "]"
                + "\n[NestedScrollAxes=" + this.getNestedScrollAxes() + "]"
                + "\n[NestedScrollType=" + this.getNestedScrollType() + "]"
                + "\n[NestedScrollChild=" + this.getNestedScrollChild() + "]"
                + "\n[NestedScrollTarget=" + this.getNestedScrollTarget() + "]"
                + "\n[InheritScrollAxes=" + this.getInheritScrollAxes() + "]"
                + "\n[InheritScrollType=" + this.getInheritScrollType() + "]"
                + "\n[InheritScrollParent=" + this.getInheritScrollParent() + "]"
                + "\n[InheritScrollTarget=" + this.getInheritScrollTarget() + "]"
                + "\n[Dx=" + dx + "]"
                + "\n[Dy=" + dy + "]"
        );
    }

    public void onScrollStateChanged(int scrollState) {
        // no-op
        printLog("onScrollStateChanged [" + this + "]"
                + "\n[EntryId=" + UIViewCompat.getResourceEntryName(this) + "]"
                + "\n[ScrollState=" + this.getScrollState() + "]"
                + "\n[SliverScrollAxes=" + this.getSliverScrollAxes() + "]"
                + "\n[SliverScrollType=" + this.getSliverScrollType() + "]"
                + "\n[NestedScrollAxes=" + this.getNestedScrollAxes() + "]"
                + "\n[NestedScrollType=" + this.getNestedScrollType() + "]"
                + "\n[NestedScrollChild=" + this.getNestedScrollChild() + "]"
                + "\n[NestedScrollTarget=" + this.getNestedScrollTarget() + "]"
                + "\n[InheritScrollAxes=" + this.getInheritScrollAxes() + "]"
                + "\n[InheritScrollType=" + this.getInheritScrollType() + "]"
                + "\n[InheritScrollParent=" + this.getInheritScrollParent() + "]"
                + "\n[InheritScrollTarget=" + this.getInheritScrollTarget() + "]"
        );
    }

    public boolean flingBy(int velocityX, int velocityY) {
        return this.flingBy(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public boolean flingBy(int startX,
                           int startY,
                           int velocityX,
                           int velocityY,
                           int minX, int maxX, int minY, int maxY) {
        return this.flingBy(startX, startY,
                velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
    }

    public boolean flingBy(int startX,
                           int startY,
                           int velocityX,
                           int velocityY,
                           int minX, int maxX, int minY, int maxY, int overX, int overY) {
        if (!this.canScrollHorizontally()) {
            velocityX = 0;
        }
        if (!this.canScrollVertically()) {
            velocityY = 0;
        }
        if (velocityX == 0 && velocityY == 0) {
            return false;
        }
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        if (velocityX != 0) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
        }
        if (velocityY != 0) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
        }
        if (this.startSliverScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH)) {
            this.mViewScroller.fling(startX, startY,
                    velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
        }
        return this.hasSliverScrolling(SliverCompat.TYPE_NON_TOUCH);
    }

    public boolean smoothScrollBy(int dx, int dy) {
        return this.smoothScrollBy(dx, dy, UNDEFINED_DURATION);
    }

    public boolean smoothScrollBy(int dx, int dy, int duration) {
        return this.smoothScrollBy(dx, dy, duration, null);
    }

    public boolean smoothScrollBy(int dx, int dy, @NonNull Interpolator interpolator) {
        return this.smoothScrollBy(dx, dy, UNDEFINED_DURATION, interpolator);
    }

    public boolean smoothScrollBy(int dx, int dy, int duration, @Nullable Interpolator interpolator) {
        if (!this.canScrollHorizontally()) {
            dx = 0;
        }
        if (!this.canScrollVertically()) {
            dy = 0;
        }
        if (dx == 0 && dy == 0) {
            return false;
        }
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        if (dx != 0) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
        }
        if (dy != 0) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
        }
        if (this.startSliverScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH)) {
            this.mViewScroller.smoothScrollBy(dx, dy, duration, interpolator);
        }
        return this.hasSliverScrolling(SliverCompat.TYPE_NON_TOUCH);
    }

    public final boolean offsetChildren(int dx, int dy) {
        final int N = this.getChildCount();
        if (N <= 0) {
            return false;
        }
        for (int index = 0; index < N; index++) {
            this.offsetChild(this.getChildAt(index), dx, dy);
        }
        return true;
    }

    public final void offsetChild(@NonNull View child, int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return;
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();

        if (layoutParams.scrolling) {
            if (dx != 0) {
                this.mHorOrientationHelper.offsetChild(child, dx);
            }
            if (dy != 0) {
                this.mVerOrientationHelper.offsetChild(child, dy);
            }
            return;
        }
        if (child instanceof ScrollPhysicsChild) {
            final ScrollPhysicsChild<SliverContainer> scrollPhysics;
            scrollPhysics = (ScrollPhysicsChild<SliverContainer>) child;
            scrollPhysics.onScrollStep(this, dx, dy);
        }
    }

    @Nullable
    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }

    public void setOnFlingListener(@Nullable OnFlingListener listener) {
        this.mOnFlingListener = listener;
    }

    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        this.mOnScrollListeners.add(listener);
    }

    public void removeOnScrollListener(@NonNull OnScrollListener listener) {
        this.mOnScrollListeners.remove(listener);
    }

    public void clearOnScrollListeners() {
        this.mOnScrollListeners.clear();
    }

    private void dispatchOnScrolled(int dx, int dy) {
        this.onScrolled(dx, dy);

        for (OnScrollListener listener : this.mOnScrollListeners) {
            listener.onScrolled(this, dx, dy);
        }
    }

    private void dispatchOnScrollStateChanged(int scrollState) {
        this.onScrollStateChanged(scrollState);

        for (OnScrollListener listener : this.mOnScrollListeners) {
            listener.onScrollStateChanged(this, scrollState);
        }
    }

    private boolean dispatchOnFling(int velocityX, int velocityY) {
        if (this.mOnFlingListener == null) {
            return false;
        }
        return this.mOnFlingListener.onFling(this, velocityX, velocityY);
    }

    // ########## SliverScroll ##########

    private ScrollingAxesHelper mSliverScrollingAxesHelper;
    private int mSliverScrollType;

    @Override
    public boolean onStartSliverScroll(int scrollAxes, int scrollType) {
        boolean needsSliverScroll = false;

        if (this.canScrollHorizontally()) {
            needsSliverScroll = (SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0;
        }
        if (this.canScrollVertically()) {
            needsSliverScroll |= (SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0;
        }
        return needsSliverScroll;
    }

    @Override
    @CallSuper
    public void onSliverScrollAccepted(int scrollAxes, int scrollType) {
        this.getSliverScrollingAxesHelper().save(scrollType, scrollAxes);
        this.abortAnimatedOverScroll();
        this.mSliverScrollType = scrollType;
        // 计算偏移范围
        this.mScrollRanges[0] = this.computeMaxScrollRangeX();
        this.mScrollRanges[1] = this.computeMaxScrollRangeY();
        // 计算偏移像素
        this.mScrollOffsets[0] = this.computeHorizontalScrollOffset();
        this.mScrollOffsets[1] = this.computeVerticalScrollOffset();
    }

    @Override
    public void onSliverPreScroll(int dx, int dy, @NonNull int[] consumed, int scrollType) {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        int dxConsumed = 0;
        int dyConsumed = 0;

        if (dx > 0 && scrollX < 0) {
            dxConsumed = Math.min(dx, -scrollX);
        }
        if (dx < 0 && scrollX > 0) {
            dxConsumed = Math.max(dx, -scrollX);
        }
        if (dy > 0 && scrollY < 0) {
            dyConsumed = Math.min(dy, -scrollY);
        }
        if (dy < 0 && scrollY > 0) {
            dyConsumed = Math.max(dy, -scrollY);
        }

        if (dxConsumed != 0 || dyConsumed != 0) {
            this.scrollBy(dxConsumed, dyConsumed);
            consumed[0] += dxConsumed;
            consumed[1] += dyConsumed;
            dx -= dxConsumed;
            dy -= dyConsumed;
        }

        if (this.mIsScrollFloating && (dx < 0 || dy < 0)) {
            dxConsumed = consumed[0];
            dyConsumed = consumed[1];

            if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dx, dy, consumed, null, scrollType)) {
                consumed[0] += dxConsumed;
                consumed[1] += dyConsumed;
            }
        }
    }

    @Override
    public void onSliverScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        final int scrollRangeX = this.getScrollRangeX();
        final int scrollRangeY = this.getScrollRangeY();
        int oldScrollX = this.getScrollOffsetX();
        int oldScrollY = this.getScrollOffsetY();
        oldScrollX = Math.max(0, Math.min(oldScrollX, scrollRangeX));
        oldScrollY = Math.max(0, Math.min(oldScrollY, scrollRangeY));

        int newScrollX = oldScrollX + dxUnconsumed;
        int newScrollY = oldScrollY + dyUnconsumed;
        newScrollX = Math.max(0, Math.min(newScrollX, scrollRangeX));
        newScrollY = Math.max(0, Math.min(newScrollY, scrollRangeY));
        final int offsetX = newScrollX - oldScrollX;
        final int offsetY = newScrollY - oldScrollY;

        if (offsetX != 0 || offsetY != 0) {
            this.scrollBy(offsetX, offsetY);
            consumed[0] += offsetX;
            consumed[1] += offsetY;
        }
    }

    @Override
    public void onBounceScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        if (SliverCompat.TYPE_TOUCH == scrollType) {
            final int oldScrollX = this.getExtraOffsetX();
            final int oldScrollY = this.getExtraOffsetY();
            float dampingX;
            float dampingY;
            dampingX = (float) Math.abs(oldScrollX) / Math.max(1, this.getWidth());
            dampingY = (float) Math.abs(oldScrollY) / Math.max(1, this.getHeight());
            dampingX = Math.max(0.00f, 1.0f - dampingX);
            dampingY = Math.max(0.00f, 1.0f - dampingY);
            dampingX *= 0.15f;
            dampingY *= 0.15f;
            dampingX += 0.10f;
            dampingY += 0.10f;

            float newScrollX = oldScrollX;
            float newScrollY = oldScrollY;
            newScrollX = ((newScrollX / dampingX) + dxUnconsumed) * dampingX;
            newScrollY = ((newScrollY / dampingY) + dyUnconsumed) * dampingY;
            final int dx = (int) (newScrollX - oldScrollX);
            final int dy = (int) (newScrollY - oldScrollY);
            final int bounceLocate = this.mBounceLocate;

            if (SCROLL_LOCATE_ALL == bounceLocate
                    || (SCROLL_LOCATE_HEAD == bounceLocate && (dxUnconsumed < 0 || dyUnconsumed < 0))
                    || (SCROLL_LOCATE_TAIL == bounceLocate && (dxUnconsumed > 0 || dyUnconsumed > 0))) {
                this.scrollBy(dx, dy);
                consumed[0] += dxUnconsumed;
                consumed[1] += dyUnconsumed;
            }
        } else {
            if (ViewScroller.MODE_SCROLL == this.mViewScroller.getCurrentMode()) {
                this.scrollBy(dxUnconsumed, dyUnconsumed);
                consumed[0] += dxUnconsumed;
                consumed[1] += dyUnconsumed;
            }
        }
    }

    @Override
    public boolean onSliverPreFling(float velocityX, float velocityY) {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        return scrollX != 0 || scrollY != 0;
    }

    @Override
    public boolean onSliverFling(float velocityX, float velocityY, boolean consumed) {
        if (consumed) {
            return false;
        }
        return this.flingByInternal((int) velocityX, (int) velocityY);
    }

    @Override
    @CallSuper
    public void onStopSliverScroll(int scrollType) {
        this.getSliverScrollingAxesHelper().clear(scrollType);

        if (SliverCompat.TYPE_NON_TOUCH == scrollType) {
            this.setScrollState(SCROLL_STATE_IDLE);
        }
        if (SliverCompat.TYPE_TOUCH == this.mSliverScrollType) {
            if (SCROLL_STATE_DRAGGING == this.mScrollState) {
                this.setScrollState(SCROLL_STATE_IDLE);
            }
        }
        if (SliverCompat.TYPE_NON_TOUCH == scrollType
                || SliverCompat.TYPE_TOUCH == this.mSliverScrollType) {
            this.onSliverScrollFinished(scrollType);
            this.mSliverScrollType = SliverCompat.TYPE_TOUCH;
        }
    }

    public void onSliverScrollFinished(int scrollType) {
        printLog("onSliverScrollFinished [" + this + "]"
                + "\n[EntryId=" + UIViewCompat.getResourceEntryName(this) + "]"
                + "\n[ScrollType=" + scrollType + "]"
                + "\n[ScrollState=" + this.getScrollState() + "]"
                + "\n[SliverScrollAxes=" + this.getSliverScrollAxes() + "]"
                + "\n[SliverScrollType=" + this.getSliverScrollType() + "]"
                + "\n[NestedScrollAxes=" + this.getNestedScrollAxes() + "]"
                + "\n[NestedScrollType=" + this.getNestedScrollType() + "]"
                + "\n[NestedScrollChild=" + this.getNestedScrollChild() + "]"
                + "\n[NestedScrollTarget=" + this.getNestedScrollTarget() + "]"
                + "\n[InheritScrollAxes=" + this.getInheritScrollAxes() + "]"
                + "\n[InheritScrollType=" + this.getInheritScrollType() + "]"
                + "\n[InheritScrollParent=" + this.getInheritScrollParent() + "]"
                + "\n[InheritScrollTarget=" + this.getInheritScrollTarget() + "]"
                + "\n[ExtraOffsetX=" + this.getExtraOffsetX() + "]"
                + "\n[ExtraOffsetY=" + this.getExtraOffsetY() + "]"
                + "\n[ScrollOffsetX=" + this.getScrollOffsetX() + "]"
                + "\n[ScrollOffsetY=" + this.getScrollOffsetY() + "]"
        );
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        this.smoothScrollBy(-scrollX, -scrollY);
    }

    @Override
    public int getSliverScrollAxes() {
        return this.getSliverScrollingAxesHelper().getScrollAxes();
    }

    @SliverCompat.ScrollType
    public int getSliverScrollType() {
        return this.mSliverScrollType;
    }

    @NonNull
    public ScrollingAxesHelper getSliverScrollingAxesHelper() {
        if (this.mSliverScrollingAxesHelper == null) {
            this.mSliverScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mSliverScrollingAxesHelper;
    }

    // ########## SliverScrollProvider ##########

    private SliverScrollHelper mSliverScrollHelper;

    @Override
    public boolean isSliverScrollingEnabled() {
        return this.getSliverScrollHelper().isSliverScrollingEnabled();
    }

    @Override
    public void setSliverScrollingEnabled(boolean enabled) {
        this.getSliverScrollHelper().setSliverScrollingEnabled(enabled);
    }

    @Override
    public boolean startSliverScroll(int scrollAxes) {
        return this.startSliverScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startSliverScroll(int scrollAxes, int scrollType) {
        return this.getSliverScrollHelper().startSliverScroll(scrollAxes, scrollType);
    }

    @Override
    public boolean dispatchSliverPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchSliverPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchSliverPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getSliverScrollHelper().dispatchSliverPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchSliverScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchSliverScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getSliverScrollHelper().dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchBounceScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchBounceScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getSliverScrollHelper().dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchSliverPreFling(float velocityX, float velocityY) {
        return this.getSliverScrollHelper().dispatchSliverPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchSliverFling(float velocityX, float velocityY, boolean consumed) {
        return this.getSliverScrollHelper().dispatchSliverFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopSliverScroll() {
        this.stopSliverScroll(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public void stopSliverScroll(int scrollType) {
        this.getSliverScrollHelper().stopSliverScroll(scrollType);
    }

    @Override
    public boolean hasSliverScrolling() {
        return this.hasSliverScrolling(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasSliverScrolling(int scrollType) {
        return this.getSliverScrollHelper().hasSliverScrolling(scrollType);
    }

    @NonNull
    public SliverScrollHelper getSliverScrollHelper() {
        if (this.mSliverScrollHelper == null) {
            this.mSliverScrollHelper = new SliverScrollHelper(this);
        }
        return this.mSliverScrollHelper;
    }

    // ########## NestedScroll ##########

    private ScrollingAxesHelper mNestedScrollingAxesHelper;
    private int mNestedScrollType;
    private View mNestedScrollChild;
    private View mNestedScrollTarget;

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int scrollAxes, int scrollType) {
        boolean needsNestedScroll = false;

        if (this.canScrollHorizontally()) {
            needsNestedScroll = (SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0;
        }
        if (this.canScrollVertically()) {
            needsNestedScroll |= (SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0;
        }
        return needsNestedScroll;
    }

    @Override
    @CallSuper
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int scrollAxes, int scrollType) {
        this.getNestedScrollingAxesHelper().save(scrollType, scrollAxes);
        this.mNestedScrollType = scrollType;
        this.mNestedScrollChild = child;
        this.mNestedScrollTarget = target;
        this.startSliverScroll(scrollAxes, scrollType);
        this.startNestedScroll(scrollAxes, scrollType);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int scrollType) {
        final int[] scrollConsumed = this.mScrollConsumed;
        final int[] offsetInWindow = this.mOffsetInWindow;

        if (this.dispatchSliverPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchNestedPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (dx > 0 || dy > 0) {
            final int dxConsumed = consumed[0];
            final int dyConsumed = consumed[1];

            if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
                consumed[0] += scrollConsumed[0];
                consumed[1] += scrollConsumed[1];
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        final int[] scrollConsumed = this.mScrollConsumed;
        final int[] offsetInWindow = this.mOffsetInWindow;

        if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            dxUnconsumed -= scrollConsumed[0];
            dyUnconsumed -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            dxUnconsumed -= scrollConsumed[0];
            dyUnconsumed -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (this.dispatchSliverPreFling(velocityX, velocityY)) {
            return true;
        }
        return this.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (this.dispatchSliverFling(velocityX, velocityY, consumed)) {
            return true;
        }
        return this.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    @CallSuper
    public void onStopNestedScroll(@NonNull View target, int scrollType) {
        this.getNestedScrollingAxesHelper().clear(scrollType);
        this.stopSliverScroll(scrollType);
        this.stopNestedScroll(scrollType);

        if (SliverCompat.TYPE_NON_TOUCH == scrollType
                || SliverCompat.TYPE_TOUCH == this.mNestedScrollType) {
            this.onNestedScrollFinished(scrollType);
            this.mNestedScrollType = SliverCompat.TYPE_TOUCH;
            this.mNestedScrollChild = null;
            this.mNestedScrollTarget = null;
        }
    }

    public void onNestedScrollFinished(int scrollType) {
        printLog("onNestedScrollFinished [" + this + "]"
                + "\n[EntryId=" + UIViewCompat.getResourceEntryName(this) + "]"
                + "\n[ScrollType=" + scrollType + "]"
                + "\n[ScrollState=" + this.getScrollState() + "]"
                + "\n[SliverScrollAxes=" + this.getSliverScrollAxes() + "]"
                + "\n[SliverScrollType=" + this.getSliverScrollType() + "]"
                + "\n[NestedScrollAxes=" + this.getNestedScrollAxes() + "]"
                + "\n[NestedScrollType=" + this.getNestedScrollType() + "]"
                + "\n[NestedScrollChild=" + this.getNestedScrollChild() + "]"
                + "\n[NestedScrollTarget=" + this.getNestedScrollTarget() + "]"
                + "\n[InheritScrollAxes=" + this.getInheritScrollAxes() + "]"
                + "\n[InheritScrollType=" + this.getInheritScrollType() + "]"
                + "\n[InheritScrollParent=" + this.getInheritScrollParent() + "]"
                + "\n[InheritScrollTarget=" + this.getInheritScrollTarget() + "]"
        );
    }

    @Override
    public int getNestedScrollAxes() {
        return this.getNestedScrollingAxesHelper().getScrollAxes();
    }

    @SliverCompat.ScrollType
    public int getNestedScrollType() {
        return this.mNestedScrollType;
    }

    @Nullable
    public View getNestedScrollChild() {
        return this.mNestedScrollChild;
    }

    @Nullable
    public View getNestedScrollTarget() {
        return this.mNestedScrollTarget;
    }

    public void stopTargetNestedScroll() {
        this.stopTargetNestedScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopTargetNestedScroll(@SliverCompat.ScrollType int scrollType) {
        final View nestedScrollTarget = this.mNestedScrollTarget;
        if (nestedScrollTarget != null) {
            SliverCompat.stopNestedScroll(nestedScrollTarget, scrollType);
        }
    }

    @NonNull
    public ScrollingAxesHelper getNestedScrollingAxesHelper() {
        if (this.mNestedScrollingAxesHelper == null) {
            this.mNestedScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mNestedScrollingAxesHelper;
    }

    // ########## NestedScrollProvider ##########

    private NestedScrollHelper mNestedScrollHelper;

    @Override
    public boolean isNestedScrollingEnabled() {
        return this.getNestedScrollHelper().isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        this.getNestedScrollHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int scrollAxes) {
        return this.startNestedScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startNestedScroll(int scrollAxes, int scrollType) {
        return this.getNestedScrollHelper().startNestedScroll(scrollAxes, scrollType);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getNestedScrollHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
    }

    /**
     * @deprecated
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    /**
     * @deprecated
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, offsetInWindow, scrollType);
    }

    /**
     * @deprecated
     */
    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int scrollType, @Nullable int[] consumed) {
        this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getNestedScrollHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.getNestedScrollHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.getNestedScrollHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopNestedScroll() {
        this.stopNestedScroll(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll(int scrollType) {
        this.getNestedScrollHelper().stopNestedScroll(scrollType);
    }

    @Nullable
    @Override
    public ViewParent getNestedScrollingParent() {
        return this.getNestedScrollingParent(SliverCompat.TYPE_TOUCH);
    }

    @Nullable
    @Override
    public ViewParent getNestedScrollingParent(int scrollType) {
        return this.getNestedScrollHelper().getNestedScrollingParent(scrollType);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return this.hasNestedScrollingParent(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent(int scrollType) {
        return this.getNestedScrollHelper().hasNestedScrollingParent(scrollType);
    }

    @NonNull
    public NestedScrollHelper getNestedScrollHelper() {
        if (this.mNestedScrollHelper == null) {
            this.mNestedScrollHelper = new NestedScrollHelper(this);
        }
        return this.mNestedScrollHelper;
    }

    // ########## InheritScroll ##########

    private ScrollingAxesHelper mInheritScrollingAxesHelper;
    private int mInheritScrollType;
    private ViewParent mInheritScrollParent;
    private ViewParent mInheritScrollTarget;

    @Override
    public boolean onStartInheritScroll(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        boolean needsInheritScroll = false;

        if (this.canScrollHorizontally()) {
            needsInheritScroll = (SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0;
        }
        if (this.canScrollVertically()) {
            needsInheritScroll |= (SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0;
        }
        return needsInheritScroll;
    }

    @Override
    @CallSuper
    public void onInheritScrollAccepted(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        this.getInheritScrollingAxesHelper().save(scrollType, scrollAxes);
        this.mInheritScrollType = scrollType;
        this.mInheritScrollParent = parent;
        this.mInheritScrollTarget = target;
        this.startSliverScroll(scrollAxes, scrollType);
        this.startInheritScroll(scrollAxes, scrollType);
    }

    @Override
    public void onInheritPreScroll(@NonNull ViewParent target, int dx, int dy, @NonNull int[] consumed, int scrollType) {
        final int[] scrollConsumed = this.mScrollConsumed;
        final int[] offsetInWindow = this.mOffsetInWindow;

        if (this.dispatchSliverPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchInheritPreScroll(dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (dx < 0 || dy < 0) {
            final int dxConsumed = consumed[0];
            final int dyConsumed = consumed[1];

            if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dx, dy, scrollConsumed, offsetInWindow, scrollType)) {
                consumed[0] += scrollConsumed[0];
                consumed[1] += scrollConsumed[1];
            }
        }
    }

    @Override
    public void onInheritScroll(@NonNull ViewParent target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        final int[] scrollConsumed = this.mScrollConsumed;
        final int[] offsetInWindow = this.mOffsetInWindow;

        if (this.dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            dxUnconsumed -= scrollConsumed[0];
            dyUnconsumed -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            dxUnconsumed -= scrollConsumed[0];
            dyUnconsumed -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }

        if (this.dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollConsumed, offsetInWindow, scrollType)) {
            consumed[0] += scrollConsumed[0];
            consumed[1] += scrollConsumed[1];
        }
    }

    @Override
    public boolean onInheritPreFling(@NonNull ViewParent target, float velocityX, float velocityY) {
        if (this.dispatchSliverPreFling(velocityX, velocityY)) {
            return true;
        }
        return this.dispatchInheritPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onInheritFling(@NonNull ViewParent target, float velocityX, float velocityY, boolean consumed) {
        if (this.dispatchSliverFling(velocityX, velocityY, consumed)) {
            return true;
        }
        return this.dispatchInheritFling(velocityX, velocityY, consumed);
    }

    @Override
    @CallSuper
    public void onStopInheritScroll(@NonNull ViewParent target, int scrollType) {
        this.getInheritScrollingAxesHelper().clear(scrollType);
        this.stopSliverScroll(scrollType);
        this.stopInheritScroll(scrollType);

        if (SliverCompat.TYPE_NON_TOUCH == scrollType
                || SliverCompat.TYPE_TOUCH == this.mInheritScrollType) {
            this.onInheritScrollFinished(scrollType);
            this.mInheritScrollType = SliverCompat.TYPE_TOUCH;
            this.mInheritScrollParent = null;
            this.mInheritScrollTarget = null;
        }
    }

    public void onInheritScrollFinished(int scrollType) {
        printLog("onInheritScrollFinished [" + this + "]"
                + "\n[EntryId=" + UIViewCompat.getResourceEntryName(this) + "]"
                + "\n[ScrollType=" + scrollType + "]"
                + "\n[ScrollState=" + this.getScrollState() + "]"
                + "\n[SliverScrollAxes=" + this.getSliverScrollAxes() + "]"
                + "\n[SliverScrollType=" + this.getSliverScrollType() + "]"
                + "\n[NestedScrollAxes=" + this.getNestedScrollAxes() + "]"
                + "\n[NestedScrollType=" + this.getNestedScrollType() + "]"
                + "\n[NestedScrollChild=" + this.getNestedScrollChild() + "]"
                + "\n[NestedScrollTarget=" + this.getNestedScrollTarget() + "]"
                + "\n[InheritScrollAxes=" + this.getInheritScrollAxes() + "]"
                + "\n[InheritScrollType=" + this.getInheritScrollType() + "]"
                + "\n[InheritScrollParent=" + this.getInheritScrollParent() + "]"
                + "\n[InheritScrollTarget=" + this.getInheritScrollTarget() + "]"
        );
    }

    @Override
    public int getInheritScrollAxes() {
        return this.getInheritScrollingAxesHelper().getScrollAxes();
    }

    @SliverCompat.ScrollType
    public int getInheritScrollType() {
        return this.mInheritScrollType;
    }

    @Nullable
    public ViewParent getInheritScrollParent() {
        return this.mInheritScrollParent;
    }

    @Nullable
    public ViewParent getInheritScrollTarget() {
        return this.mInheritScrollTarget;
    }

    public void stopTargetInheritScroll() {
        this.stopTargetInheritScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopTargetInheritScroll(@SliverCompat.ScrollType int scrollType) {
        final View inheritScrollTarget = (View) this.mInheritScrollTarget;
        if (inheritScrollTarget != null) {
            SliverCompat.stopInheritScroll(inheritScrollTarget, scrollType);
        }
    }

    @NonNull
    public ScrollingAxesHelper getInheritScrollingAxesHelper() {
        if (this.mInheritScrollingAxesHelper == null) {
            this.mInheritScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mInheritScrollingAxesHelper;
    }

    // ########## InheritScrollProvider ##########

    private InheritScrollHelper mInheritScrollHelper;

    @Override
    public boolean isInheritScrollingEnabled() {
        return this.getInheritScrollHelper().isInheritScrollingEnabled();
    }

    @Override
    public void setInheritScrollingEnabled(boolean enabled) {
        this.getInheritScrollHelper().setInheritScrollingEnabled(enabled);
    }

    @Override
    public boolean startInheritScroll(int scrollAxes) {
        return this.startInheritScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startInheritScroll(int scrollAxes, int scrollType) {
        return this.getInheritScrollHelper().startInheritScroll(scrollAxes, scrollType);
    }

    @Override
    public boolean dispatchInheritPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchInheritPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getInheritScrollHelper().dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchInheritScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchInheritScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getInheritScrollHelper().dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchInheritPreFling(float velocityX, float velocityY) {
        return this.getInheritScrollHelper().dispatchInheritPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchInheritFling(float velocityX, float velocityY, boolean consumed) {
        return this.getInheritScrollHelper().dispatchInheritFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopInheritScroll() {
        this.stopInheritScroll(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public void stopInheritScroll(int scrollType) {
        this.getInheritScrollHelper().stopInheritScroll(scrollType);
    }

    @Nullable
    @Override
    public View getInheritScrollingChild() {
        return this.getInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    @Nullable
    @Override
    public View getInheritScrollingChild(int scrollType) {
        return this.getInheritScrollHelper().getInheritScrollingChild(scrollType);
    }

    @Override
    public boolean hasInheritScrollingChild() {
        return this.hasInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasInheritScrollingChild(int scrollType) {
        return this.getInheritScrollHelper().hasInheritScrollingChild(scrollType);
    }

    @NonNull
    public InheritScrollHelper getInheritScrollHelper() {
        if (this.mInheritScrollHelper == null) {
            this.mInheritScrollHelper = new InheritScrollHelper(this);
        }
        return this.mInheritScrollHelper;
    }

    private static int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            /* my >= child is this case:
             *                    |--------------- me ---------------|
             *     |------ child ------|
             * or
             *     |--------------- me ---------------|
             *            |------ child ------|
             * or
             *     |--------------- me ---------------|
             *                                  |------ child ------|
             *
             * n < 0 is this case:
             *     |------ me ------|
             *                    |-------- child --------|
             *     |-- mScrollX --|
             */
            return 0;
        }
        if ((my + n) > child) {
            /* this case:
             *                    |------ me ------|
             *     |------ child ------|
             *     |-- mScrollX --|
             */
            return child - my;
        }
        return n;
    }

    private static void printLog(@NonNull String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    // ########## ComponentCallback ##########

    private static final class ComponentCallback implements ViewScroller.Callback {
        @Override
        public void scrollStep(@NonNull ViewScroller scroller, int dx, int dy,
                               @NonNull int[] consumed) {
            final SliverContainer parent = scroller.getView();
            if (parent.scrollByInternal(dx, dy, consumed, SliverCompat.TYPE_NON_TOUCH)) {
                dx -= consumed[0];
                dy -= consumed[1];
            }
            parent.releasingEdgeGlows(dx, dy);

            if (scroller.doneScrolling(dx, dy)) {
                final OverScroller overScroller = scroller.getScroller();
                final int velocity = (int) overScroller.getCurrVelocity();
                final int velocityX = dx < 0 ? -velocity : dx > 0 ? velocity : 0;
                final int velocityY = dy < 0 ? -velocity : dy > 0 ? velocity : 0;
                parent.absorbEdgeGlows(velocityX, velocityY);
            }
        }

        @Override
        public void scrollStart(@NonNull ViewScroller scroller, int mode) {
            final SliverContainer parent = scroller.getView();
            parent.setScrollState(SCROLL_STATE_SETTLING);
        }

        @Override
        public void scrollStop(@NonNull ViewScroller scroller, int mode) {
            final SliverContainer parent = scroller.getView();
            parent.cancelTouchScroll(SliverCompat.TYPE_NON_TOUCH);
        }
    }

    // ########## LayoutParams ##########

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        static final int DEFAULT_GRAVITY = Gravity.START | Gravity.TOP;

        public int gravity = Gravity.NO_GRAVITY;
        public int widthUsed;
        public int heightUsed;
        public boolean scrolling = true;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = layoutParams.gravity;
            this.scrolling = layoutParams.scrolling;
            this.widthUsed = layoutParams.widthUsed;
            this.heightUsed = layoutParams.heightUsed;
        }

        public LayoutParams(@NonNull MarginLayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull Context context, @NonNull AttributeSet attrs) {
            super(context, attrs);
            final TypedArray typedArray;
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliverContainer_Layout);
            this.gravity = typedArray.getInt(R.styleable.SliverContainer_Layout_android_layout_gravity, this.gravity);
            this.scrolling = typedArray.getBoolean(R.styleable.SliverContainer_Layout_sliverScrolling, true);
            this.widthUsed = typedArray.getDimensionPixelOffset(R.styleable.SliverContainer_Layout_sliverWidthUsed, this.widthUsed);
            this.heightUsed = typedArray.getDimensionPixelOffset(R.styleable.SliverContainer_Layout_sliverHeightUsed, this.heightUsed);
            typedArray.recycle();
        }
    }

    // ########## SavedState ##########

    public static class SavedState extends AbsSavedState {
        @NonNull
        private final int[] mScrollRanges = new int[2];
        @NonNull
        private final int[] mScrollOffsets = new int[2];

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.resetInstance();
        }

        public SavedState(@NonNull Parcel source) {
            this(source, null);
        }

        public SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            this.resetInstance();
            source.readIntArray(this.mScrollRanges);
            source.readIntArray(this.mScrollOffsets);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeIntArray(this.mScrollRanges);
            dest.writeIntArray(this.mScrollOffsets);
        }

        private void resetInstance() {
            Arrays.fill(this.mScrollRanges, 0);
            Arrays.fill(this.mScrollOffsets, 0);
        }

        @NonNull
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(@NonNull Parcel source, @Nullable ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(@NonNull Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface ScrollPhysicsChild<T extends SliverContainer> {

        void onScrollStep(@NonNull T parent, int dx, int dy);
    }

    public interface OnScrollListener {

        void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy);

        void onScrollStateChanged(@NonNull SliverContainer sliverContainer, @ScrollState int scrollState);
    }

    public static class SimpleOnScrollListener implements OnScrollListener {

        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
        }

        public void onScrollStateChanged(@NonNull SliverContainer sliverContainer, @ScrollState int scrollState) {
        }
    }

    public interface OnFlingListener {

        boolean onFling(@NonNull SliverContainer sliverContainer, int velocityX, int velocityY);
    }
}
