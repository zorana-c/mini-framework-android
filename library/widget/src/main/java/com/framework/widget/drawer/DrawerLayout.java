package com.framework.widget.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import androidx.customview.widget.ViewDragHelper;

import com.framework.widget.R;
import com.framework.widget.sliver.NestedScroll;
import com.framework.widget.sliver.ScrollingAxesHelper;

/**
 * @Author create by Zoran on 2024/1/20
 * @Email : 171905184@qq.com
 * @Description : 参考 DrawerLayout
 */
public class DrawerLayout extends ViewGroup implements NestedScroll {
    public static final int FLAG_CLOSED = 1;
    public static final int FLAG_CLOSING = 1 << 1;
    public static final int FLAG_OPENED = 1 << 2;
    public static final int FLAG_OPENING = 1 << 3;

    @IntDef(value = {
            FLAG_CLOSED,
            FLAG_CLOSING,
            FLAG_OPENED,
            FLAG_OPENING}, flag = true)
    @Retention(RetentionPolicy.SOURCE)
    public @interface OpenState {
        // nothing
    }

    public static final int LOCK_UNLOCKED = 0;
    public static final int LOCK_UNDEFINED = 1;
    public static final int LOCK_LOCKED_OPENED = 2;
    public static final int LOCK_LOCKED_CLOSED = 3;

    @IntDef({
            LOCK_UNLOCKED,
            LOCK_UNDEFINED,
            LOCK_LOCKED_OPENED,
            LOCK_LOCKED_CLOSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LockMode {
        // nothing
    }

    public static final int EDGE_NODE = 0;
    public static final int EDGE_TOP = ViewDragHelper.EDGE_TOP;
    public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;
    public static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;
    public static final int EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM;

    @IntDef(value = {
            EDGE_NODE,
            EDGE_TOP,
            EDGE_LEFT,
            EDGE_RIGHT,
            EDGE_BOTTOM}, flag = true)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeMode {
        // nothing
    }

    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;

    @IntDef({
            STATE_IDLE,
            STATE_DRAGGING,
            STATE_SETTLING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawerState {
        // nothing
    }

    /**
     * The size of an edge in pixels.
     */
    private static final int EDGE_SIZE = 20;
    /**
     * Length of time to delay before peeking the drawer.
     */
    private static final int PEEK_DELAY = 160;
    /**
     * Minimum velocity that will be detected as a fling.
     */
    private static final int MIN_FLING_VELOCITY = 400;
    /**
     * The base elevation of the drawer(s) relative to
     * the parent, in pixels.
     */
    private static final int DRAWER_ELEVATION = 10;
    /**
     * A color to use for the scrim that obscures primary
     * content while a drawer is open.
     */
    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    @NonNull
    private final Paint mScrimPaint = new Paint();
    @NonNull
    private final ArrayList<View> mNonDrawerViews = new ArrayList<>();
    @NonNull
    private final ArrayList<DrawerListener> mDrawerListeners = new ArrayList<>();

    @NonNull
    private final ViewDragHelper mTopDragged;
    @NonNull
    private final ViewDragHelper mLeftDragged;
    @NonNull
    private final ViewDragHelper mRightDragged;
    @NonNull
    private final ViewDragHelper mBottomDragged;

    @LockMode
    private int mLockModeTop = LOCK_UNDEFINED;
    @LockMode
    private int mLockModeLeft = LOCK_UNDEFINED;
    @LockMode
    private int mLockModeRight = LOCK_UNDEFINED;
    @LockMode
    private int mLockModeBottom = LOCK_UNDEFINED;

    @LockMode
    private int mNestedLockModeTop = LOCK_UNDEFINED;
    @LockMode
    private int mNestedLockModeLeft = LOCK_UNDEFINED;
    @LockMode
    private int mNestedLockModeRight = LOCK_UNDEFINED;
    @LockMode
    private int mNestedLockModeBottom = LOCK_UNDEFINED;

    @Nullable
    private Drawable mShadowTop;
    @Nullable
    private Drawable mShadowLeft;
    @Nullable
    private Drawable mShadowRight;
    @Nullable
    private Drawable mShadowBottom;

    @Nullable
    private Rect mChildHitRect;
    @Nullable
    private Matrix mChildInvertedMatrix;
    @Nullable
    private Drawable mStatusBarBackground;
    @Nullable
    private WindowInsets mStatusBarInsets;

    @ColorInt
    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private float mScrimOpacity;
    private float mDrawerElevation;

    @DrawerState
    private int mDrawerViewState = STATE_IDLE;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mInLayout = false;
    private boolean mFirstLayout = false;
    private boolean mIsUnableToDrag = false;
    private boolean mChildrenCanceledTouch = false;
    private boolean mStatusBarBackgroundEnabled = false;

    public DrawerLayout(@NonNull Context context) {
        this(context, null);
    }

    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.drawerlayout.R.attr.drawerLayoutStyle);
    }

    @SuppressLint({"ResourceType", "PrivateResource"})
    public DrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final float density;
        density = this.getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        final float elevation = DRAWER_ELEVATION * density;
        final float horEdgeSize = (EDGE_SIZE * 1.2f) * density;
        final float verEdgeSize = (EDGE_SIZE * 2.4f) * density;

        TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawerLayout);
        this.mDrawerElevation = typedArray.getDimension(R.styleable.DrawerLayout_elevation, elevation);
        this.mStatusBarBackground = typedArray.getDrawable(R.styleable.DrawerLayout_statusBarBackground);
        typedArray.recycle();

        ViewDragCallback callback;
        callback = new ViewDragCallback(EDGE_TOP);
        this.mTopDragged = ViewDragHelper.create(this, 0.5f, callback);
        this.mTopDragged.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        this.mTopDragged.setMinVelocity(minVel);
        this.mTopDragged.setEdgeSize((int) (verEdgeSize + 0.5f));
        callback.setDragged(this.mTopDragged);

        callback = new ViewDragCallback(EDGE_LEFT);
        this.mLeftDragged = ViewDragHelper.create(this, 0.5f, callback);
        this.mLeftDragged.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        this.mLeftDragged.setMinVelocity(minVel);
        this.mLeftDragged.setEdgeSize((int) (horEdgeSize + 0.5f));
        callback.setDragged(this.mLeftDragged);

        callback = new ViewDragCallback(EDGE_RIGHT);
        this.mRightDragged = ViewDragHelper.create(this, 0.5f, callback);
        this.mRightDragged.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
        this.mRightDragged.setMinVelocity(minVel);
        this.mRightDragged.setEdgeSize((int) (horEdgeSize + 0.5f));
        callback.setDragged(this.mRightDragged);

        callback = new ViewDragCallback(EDGE_BOTTOM);
        this.mBottomDragged = ViewDragHelper.create(this, 0.5f, callback);
        this.mBottomDragged.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
        this.mBottomDragged.setMinVelocity(minVel);
        this.mBottomDragged.setEdgeSize((int) (verEdgeSize + 0.5f));
        callback.setDragged(this.mBottomDragged);

        // So that we can catch the back button
        this.setFocusableInTouchMode(true);
        this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        this.setMotionEventSplittingEnabled(false);
        ViewCompat.setImportantForAccessibility(this,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        if (ViewCompat.getFitsSystemWindows(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.setOnApplyWindowInsetsListener((widget, insets) -> {
                    ((DrawerLayout) widget).setChildInsets(insets);
                    return insets.consumeSystemWindowInsets();
                });
                this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                if (this.mStatusBarBackground == null) {
                    final int[] THEME_ATTRS = {
                            android.R.attr.colorPrimaryDark
                    };
                    typedArray = context.obtainStyledAttributes(THEME_ATTRS);
                    this.mStatusBarBackground = typedArray.getDrawable(0);
                    typedArray.recycle();
                }
            } else {
                this.mStatusBarBackground = null;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }

    @Override
    public void addView(@NonNull View child, int index,
                        @NonNull ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        final View openDrawerChild = this.findOpenDrawerView();
        if (openDrawerChild != null || this.isDrawerView(child)) {
            // A drawer is already open or the new view is a drawer, so the
            // new view should start out hidden.
            ViewCompat.setImportantForAccessibility(child,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        } else {
            // Otherwise this is a content view and no drawer is open, so the
            // new view should start out visible.
            ViewCompat.setImportantForAccessibility(child,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    @Override
    public boolean checkLayoutParams(@Nullable ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return false;
        }
        return layoutParams instanceof LayoutParams;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(parentWidthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(parentHeightMeasureSpec);
        int width = MeasureSpec.getSize(parentWidthMeasureSpec);
        int height = MeasureSpec.getSize(parentHeightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            if (this.isInEditMode()) {
                // Don't crash the layout editor. Consume all of the space if specified
                // or pick a magic number from thin air otherwise.
                // TODO Better communication with tools of this bogus state.
                // It will crash on a real device.
                if (widthMode == MeasureSpec.UNSPECIFIED) {
                    width = 300;
                }
                if (heightMode == MeasureSpec.UNSPECIFIED) {
                    height = 300;
                }
            } else {
                throw new IllegalArgumentException(
                        "DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
        }
        this.setMeasuredDimension(width, height);

        WindowInsets insets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            insets = this.mStatusBarInsets;
        }
        // Only one drawer is permitted along each edge. These flags
        // are tracking the presence of the edge drawers.
        int drawerOnEdgeFlags = 0;
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null
                    || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();
            final int edgeMode = this.getDrawerViewEdgeMode(child);

            if (insets != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                        insets = insets.replaceSystemWindowInsets(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                0,
                                insets.getSystemWindowInsetBottom());
                    } else if (this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                        insets = insets.replaceSystemWindowInsets(
                                0,
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom());
                    } else if (this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                        insets = insets.replaceSystemWindowInsets(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                0);
                    } else if (this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                        insets = insets.replaceSystemWindowInsets(
                                insets.getSystemWindowInsetLeft(),
                                0,
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom());
                    }
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        child.dispatchApplyWindowInsets(insets);
                    } else {
                        layoutParams.leftMargin = insets.getSystemWindowInsetLeft();
                        layoutParams.topMargin = insets.getSystemWindowInsetTop();
                        layoutParams.rightMargin = insets.getSystemWindowInsetRight();
                        layoutParams.bottomMargin = insets.getSystemWindowInsetBottom();
                    }
                }
            }

            if (this.isDrawerView(child)) {
                if (ViewCompat.getElevation(child) != this.mDrawerElevation) {
                    ViewCompat.setElevation(child, this.mDrawerElevation);
                }
                if ((drawerOnEdgeFlags & edgeMode) != 0) {
                    throw new IllegalStateException("Child drawer has edge mode "
                            + edgeMode + " but this ViewGroup already has a "
                            + "drawer view along that edge");
                }
                drawerOnEdgeFlags |= edgeMode;
                int drawerWidthSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                        layoutParams.leftMargin + layoutParams.rightMargin,
                        layoutParams.width);
                int drawerHeightSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                        layoutParams.topMargin + layoutParams.bottomMargin,
                        layoutParams.height);
                if (this.checkDrawerViewHorOri(child)) {
                    drawerHeightSpec = MeasureSpec.makeMeasureSpec(height
                            - layoutParams.topMargin
                            - layoutParams.bottomMargin, MeasureSpec.EXACTLY);
                } else {
                    drawerWidthSpec = MeasureSpec.makeMeasureSpec(width
                            - layoutParams.leftMargin
                            - layoutParams.rightMargin, MeasureSpec.EXACTLY);
                }
                child.measure(drawerWidthSpec, drawerHeightSpec);
            } else {
                // Content views get measured at exactly the layout's size.
                final int contentWidthSpec = MeasureSpec.makeMeasureSpec(width
                        - layoutParams.leftMargin
                        - layoutParams.rightMargin, MeasureSpec.EXACTLY);
                final int contentHeightSpec = MeasureSpec.makeMeasureSpec(height
                        - layoutParams.topMargin
                        - layoutParams.bottomMargin, MeasureSpec.EXACTLY);
                child.measure(contentWidthSpec, contentHeightSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mInLayout = true;
        final int width = right - left;
        final int height = bottom - top;
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null
                    || child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            int childTop = layoutParams.topMargin;
            int childLeft = layoutParams.leftMargin;
            // Drawer, if it wasn't onMeasure would have thrown an exception.
            if (this.isDrawerView(child)) {
                float newOffset = 0.f;
                if (this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                    childLeft = -childWidth + (int) (childWidth * layoutParams.screen);
                    newOffset = (float) (childWidth + childLeft) / childWidth;
                } else if (this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                    childLeft = width - (int) (childWidth * layoutParams.screen);
                    newOffset = (float) (width - childLeft) / childWidth;
                } else if (this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                    childTop = -childHeight + (int) (childHeight * layoutParams.screen);
                    newOffset = (float) (childHeight + childTop) / childHeight;
                } else if (this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                    childTop = height - (int) (childHeight * layoutParams.screen);
                    newOffset = (float) (height - childTop) / childHeight;
                }
                child.layout(childLeft, childTop,
                        childLeft + childWidth, childTop + childHeight);
                if (newOffset != layoutParams.screen) {
                    this.setDrawerViewScreen(child, newOffset);
                }
                final int newVisibility = layoutParams.screen > 0
                        ? View.VISIBLE
                        : View.INVISIBLE;
                if (child.getVisibility() != newVisibility) {
                    child.setVisibility(newVisibility);
                }
            } else {
                child.layout(childLeft, childTop,
                        childLeft + childWidth, childTop + childHeight);
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (!this.mStatusBarBackgroundEnabled
                || this.mStatusBarBackground == null) {
            return;
        }
        int inset = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this.mStatusBarInsets != null) {
                inset = this.mStatusBarInsets.getSystemWindowInsetTop();
            }
        }
        if (inset > 0) {
            this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), inset);
            this.mStatusBarBackground.draw(canvas);
        }
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        final boolean drawingContent = this.isContentView(child);
        final int width = this.getWidth();
        final int height = this.getHeight();
        int clipTop = 0;
        int clipLeft = 0;
        int clipRight = width;
        int clipBottom = height;

        final int restoreCount = canvas.save();
        if (drawingContent) {
            final int N = this.getChildCount();
            for (int index = 0; index < N; index++) {
                final View view = this.getChildAt(index);
                if (view == null
                        || view == child
                        || view.getHeight() < height
                        || view.getVisibility() != View.VISIBLE
                        || !this.isDrawerView(view)
                        || !this.hasViewOpaqueBackground(view)) {
                    continue;
                }
                if (this.checkDrawerViewEdgeMode(view, EDGE_LEFT)) {
                    final int right = view.getRight();
                    if (right > clipLeft) {
                        clipLeft = right;
                    }
                } else if (this.checkDrawerViewEdgeMode(view, EDGE_RIGHT)) {
                    final int left = view.getLeft();
                    if (left < clipRight) {
                        clipRight = left;
                    }
                } else if (this.checkDrawerViewEdgeMode(view, EDGE_TOP)) {
                    final int bottom = view.getBottom();
                    if (bottom > clipTop) {
                        clipTop = bottom;
                    }
                } else if (this.checkDrawerViewEdgeMode(view, EDGE_BOTTOM)) {
                    final int top = view.getTop();
                    if (top < clipBottom) {
                        clipBottom = top;
                    }
                }
            }
            canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom);
        }
        final boolean result;
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        if (this.mScrimOpacity > 0 && drawingContent) {
            final int baseAlpha = (this.mScrimColor & 0xff000000) >>> 24;
            final int image = (int) (baseAlpha * this.mScrimOpacity);
            final int color = image << 24 | (this.mScrimColor & 0xffffff);
            this.mScrimPaint.setColor(color);
            canvas.drawRect(clipLeft, clipTop, clipRight, clipBottom, this.mScrimPaint);
        } else if (this.mShadowLeft != null
                && this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
            final int shadowWidth = this.mShadowLeft.getIntrinsicWidth();
            final int childRight = child.getRight();
            final int drawerPeekDistance = this.mLeftDragged.getEdgeSize();
            final float alpha;
            alpha = Math.max(0, Math.min((float) childRight / drawerPeekDistance, 1.f));
            this.mShadowLeft.setBounds(childRight, child.getTop(),
                    childRight + shadowWidth, child.getBottom());
            this.mShadowLeft.setAlpha((int) (0xff * alpha));
            this.mShadowLeft.draw(canvas);
        } else if (this.mShadowRight != null
                && this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
            final int shadowWidth = this.mShadowRight.getIntrinsicWidth();
            final int childLeft = child.getLeft();
            final int showing = width - childLeft;
            final int drawerPeekDistance = this.mRightDragged.getEdgeSize();
            final float alpha;
            alpha = Math.max(0, Math.min((float) showing / drawerPeekDistance, 1.f));
            this.mShadowRight.setBounds(childLeft - shadowWidth, child.getTop(),
                    childLeft, child.getBottom());
            this.mShadowRight.setAlpha((int) (0xff * alpha));
            this.mShadowRight.draw(canvas);
        } else if (this.mShadowTop != null
                && this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
            final int shadowHeight = this.mShadowTop.getIntrinsicHeight();
            final int childBottom = child.getBottom();
            final int drawerPeekDistance = this.mTopDragged.getEdgeSize();
            final float alpha;
            alpha = Math.max(0, Math.min((float) childBottom / drawerPeekDistance, 1.f));
            this.mShadowTop.setBounds(child.getLeft(), childBottom,
                    child.getRight(), childBottom + shadowHeight);
            this.mShadowTop.setAlpha((int) (0xff * alpha));
            this.mShadowTop.draw(canvas);
        } else if (this.mShadowBottom != null
                && this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
            final int shadowHeight = this.mShadowBottom.getIntrinsicHeight();
            final int childTop = child.getTop();
            final int showing = height - childTop;
            final int drawerPeekDistance = this.mBottomDragged.getEdgeSize();
            final float alpha;
            alpha = Math.max(0, Math.min((float) showing / drawerPeekDistance, 1.f));
            this.mShadowBottom.setBounds(child.getLeft(), childTop - shadowHeight,
                    child.getRight(), childTop);
            this.mShadowBottom.setAlpha((int) (0xff * alpha));
            this.mShadowBottom.draw(canvas);
        }
        return result;
    }

    @Override
    public void addFocusables(@NonNull ArrayList<View> children, int direction, int focusableMode) {
        if (this.getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
            return;
        }
        // Only the views in the open drawers are focusable. Add normal child views when
        // no drawers are opened.
        boolean isDrawerOpen = false;
        final int N = getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (this.isDrawerView(child)) {
                if (this.isDrawerOpen(child)) {
                    isDrawerOpen = true;
                    child.addFocusables(children, direction, focusableMode);
                }
            } else {
                this.mNonDrawerViews.add(child);
            }
        }
        if (!isDrawerOpen) {
            final int nonDrawerViewsCount = this.mNonDrawerViews.size();
            for (int index = 0; index < nonDrawerViewsCount; ++index) {
                final View child = this.mNonDrawerViews.get(index);
                if (child == null) {
                    continue;
                }
                if (child.getVisibility() == View.VISIBLE) {
                    child.addFocusables(children, direction, focusableMode);
                }
            }
        }
        this.mNonDrawerViews.clear();
    }

    @Override
    public boolean dispatchGenericMotionEvent(@NonNull MotionEvent event) {
        // If this is not a pointer event, or if this is an hover exit, or we are not displaying
        // that the content view can't be interacted with, then don't override and do anything
        // special.
        final int action = event.getAction();
        final int source = event.getSource();
        if (this.mScrimOpacity <= 0
                || action == MotionEvent.ACTION_HOVER_EXIT
                || (source & InputDevice.SOURCE_CLASS_POINTER) == 0) {
            return super.dispatchGenericMotionEvent(event);
        }
        final int N = this.getChildCount();
        final float x = event.getX();
        final float y = event.getY();
        // Walk through children from top to bottom.
        for (int index = N - 1; index >= 0; index--) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            // If the event is out of bounds or the child is the content view,
            // don't dispatch to it.
            if (this.isContentView(child)
                    || !this.isInBoundsOfChild(child, x, y)) {
                continue;
            }
            // If a child handles it, return true.
            if (this.dispatchTransformedGenericPointerEvent(event, child)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBoundsOfChild(@NonNull View child, float x, float y) {
        if (this.mChildHitRect == null) {
            this.mChildHitRect = new Rect();
        }
        child.getHitRect(this.mChildHitRect);
        return this.mChildHitRect.contains((int) x, (int) y);
    }

    private boolean dispatchTransformedGenericPointerEvent(@NonNull MotionEvent event,
                                                           @NonNull View child) {
        final Matrix childMatrix = child.getMatrix();
        final boolean handled;
        if (!childMatrix.isIdentity()) {
            final MotionEvent transformedEvent;
            transformedEvent = this.getTransformedMotionEvent(event, child);
            handled = child.dispatchGenericMotionEvent(transformedEvent);
            transformedEvent.recycle();
        } else {
            final float offsetX = this.getScrollX() - child.getLeft();
            final float offsetY = this.getScrollY() - child.getTop();
            event.offsetLocation(offsetX, offsetY);
            handled = child.dispatchGenericMotionEvent(event);
            event.offsetLocation(-offsetX, -offsetY);
        }
        return handled;
    }

    @NonNull
    private MotionEvent getTransformedMotionEvent(@NonNull MotionEvent event,
                                                  @NonNull View child) {
        final float offsetX = this.getScrollX() - child.getLeft();
        final float offsetY = this.getScrollY() - child.getTop();
        final MotionEvent transformedEvent;
        transformedEvent = MotionEvent.obtain(event);
        transformedEvent.offsetLocation(offsetX, offsetY);
        final Matrix childMatrix = child.getMatrix();
        if (!childMatrix.isIdentity()) {
            if (this.mChildInvertedMatrix == null) {
                this.mChildInvertedMatrix = new Matrix();
            }
            childMatrix.invert(this.mChildInvertedMatrix);
            transformedEvent.transform(this.mChildInvertedMatrix);
        }
        return transformedEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (this.disallowInterceptTouchEvent(event)) {
            return false;
        }
        // "|" used deliberately here; both methods should be invoked.
        boolean interceptForDrag = false;
        interceptForDrag |= this.mLeftDragged.shouldInterceptTouchEvent(event);
        interceptForDrag |= this.mRightDragged.shouldInterceptTouchEvent(event);
        interceptForDrag |= this.mTopDragged.shouldInterceptTouchEvent(event);
        interceptForDrag |= this.mBottomDragged.shouldInterceptTouchEvent(event);

        final int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mChildrenCanceledTouch = false;

                if (this.mScrimOpacity > 0) {
                    final View child;
                    child = this.mLeftDragged.findTopChildUnder((int) x, (int) y);

                    if (this.isContentView(child)) {
                        interceptForDrag = true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                this.mChildrenCanceledTouch = false;
                break;
            }
        }
        return interceptForDrag || this.mChildrenCanceledTouch;
    }

    private boolean disallowInterceptTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_CANCEL
                || actionMasked == MotionEvent.ACTION_UP) {
            this.mIsUnableToDrag = false;
            return false;
        }
        if (actionMasked != MotionEvent.ACTION_DOWN) {
            if (this.mIsUnableToDrag) {
                return true;
            }
            if (this.mChildrenCanceledTouch) {
                return false;
            }
        }
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mIsUnableToDrag = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                final float y = event.getY();
                final View child;
                child = this.mLeftDragged.findTopChildUnder((int) x, (int) y);

                if (this.isDrawerView(child)) {
                    final float dx = this.mInitialMotionX - x;
                    final float dy = this.mInitialMotionY - y;

                    if (this.canScroll(child, (int) x, (int) y,
                            this.checkDrawerViewHorOri(child) ? (int) dx : 0,
                            this.checkDrawerViewHorOri(child) ? 0 : (int) dy, true)) {
                        this.mIsUnableToDrag = true;
                    }
                }
                break;
            }
        }
        return this.mIsUnableToDrag;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        this.mTopDragged.processTouchEvent(event);
        this.mLeftDragged.processTouchEvent(event);
        this.mRightDragged.processTouchEvent(event);
        this.mBottomDragged.processTouchEvent(event);
        boolean wantTouchEvents = true;

        final int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mChildrenCanceledTouch = false;
                break;
            }
            case MotionEvent.ACTION_UP: {
                final float x = event.getX();
                final float y = event.getY();
                boolean peekingOnly = true;
                View child;
                child = this.mLeftDragged.findTopChildUnder((int) x, (int) y);

                if (this.isContentView(child)) {
                    final float touchSlop = this.mLeftDragged.getTouchSlop();
                    final float dx = x - this.mInitialMotionX;
                    final float dy = y - this.mInitialMotionY;

                    if (dx * dx + dy * dy < touchSlop * touchSlop) {
                        // Taps close a dimmed open drawer but only if it isn't locked open.
                        child = this.findOpenDrawerView();
                        if (child != null) {
                            peekingOnly = this.checkDrawerViewLockMode(child, LOCK_LOCKED_OPENED);
                        }
                    }
                }
                if (!peekingOnly) {
                    this.closeDrawers();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                this.mChildrenCanceledTouch = false;
                break;
            }
        }
        return wantTouchEvents;
    }

    private boolean canScroll(@NonNull View target,
                              int x, int y, int dx, int dy, boolean check) {
        if (target instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) target;
            final int scrollX = target.getScrollX();
            final int scrollY = target.getScrollY();
            final int N = viewGroup.getChildCount();
            for (int index = N - 1; index >= 0; index--) {
                final View child = viewGroup.getChildAt(index);
                if (x + scrollX >= child.getLeft()
                        && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop()
                        && y + scrollY < child.getBottom()
                        && this.canScroll(child,
                        x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop(), dx, dy, true)) {
                    return true;
                }
            }
        }
        return check && ((dx != 0 && target.canScrollHorizontally(dx))
                || (dy != 0 && target.canScrollVertically(dy)));
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public void computeScroll() {
        float scrimOpacity = 0;
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            final float screen;
            screen = this.getDrawerViewScreen(child);
            scrimOpacity = Math.max(scrimOpacity, screen);
        }
        this.mScrimOpacity = scrimOpacity;

        boolean needsInvalidate = false;
        if (this.mTopDragged.continueSettling(true)) {
            needsInvalidate = true;
        }
        if (this.mLeftDragged.continueSettling(true)) {
            needsInvalidate = true;
        }
        if (this.mRightDragged.continueSettling(true)) {
            needsInvalidate = true;
        }
        if (this.mBottomDragged.continueSettling(true)) {
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.hasVisibleDrawer()) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final View child = this.findVisibleDrawerView();
            if (child != null && this.checkDrawerViewLockMode(child, LOCK_UNLOCKED)) {
                this.closeDrawers();
            }
            return child != null;
        }
        return super.onKeyUp(keyCode, event);
    }

    @NonNull
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState;
        if (superState == null) {
            savedState = new SavedState();
        } else {
            savedState = new SavedState(superState);
        }
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            // Is the current child fully opened (that is, not closing)?
            // Is the current child opening?
            final int openStateFlags = FLAG_OPENED | FLAG_OPENING;
            if (this.checkDrawerViewOpenState(child, openStateFlags)) {
                // If one of the conditions above holds, save the child's gravity
                // so that we open that child during state restore.
                savedState.mOpenEdgeMode = this.getDrawerViewEdgeMode(child);
                break;
            }
        }
        savedState.mLockModeTop = this.mLockModeTop;
        savedState.mLockModeLeft = this.mLockModeLeft;
        savedState.mLockModeRight = this.mLockModeRight;
        savedState.mLockModeBottom = this.mLockModeBottom;
        savedState.mNestedLockModeTop = this.mNestedLockModeTop;
        savedState.mNestedLockModeLeft = this.mNestedLockModeLeft;
        savedState.mNestedLockModeRight = this.mNestedLockModeRight;
        savedState.mNestedLockModeBottom = this.mNestedLockModeBottom;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            final Parcelable superState = savedState.getSuperState();
            super.onRestoreInstanceState(superState);
            final int openEdgeMode = savedState.mOpenEdgeMode;
            if (openEdgeMode != EDGE_NODE) {
                final View child = this.findDrawerByEdgeMode(openEdgeMode);
                if (child != null) {
                    this.openDrawer(child);
                }
            }
            if (savedState.mLockModeLeft != LOCK_UNDEFINED) {
                this.setDrawerLockMode(EDGE_LEFT, savedState.mLockModeLeft);
            }
            if (savedState.mLockModeRight != LOCK_UNDEFINED) {
                this.setDrawerLockMode(EDGE_RIGHT, savedState.mLockModeRight);
            }
            if (savedState.mLockModeTop != LOCK_UNDEFINED) {
                this.setDrawerLockMode(EDGE_TOP, savedState.mLockModeTop);
            }
            if (savedState.mLockModeBottom != LOCK_UNDEFINED) {
                this.setDrawerLockMode(EDGE_BOTTOM, savedState.mLockModeBottom);
            }
            if (savedState.mNestedLockModeLeft != LOCK_UNDEFINED) {
                this.setDrawerNestedLockMode(EDGE_LEFT, savedState.mNestedLockModeLeft);
            }
            if (savedState.mNestedLockModeRight != LOCK_UNDEFINED) {
                this.setDrawerNestedLockMode(EDGE_RIGHT, savedState.mNestedLockModeRight);
            }
            if (savedState.mNestedLockModeTop != LOCK_UNDEFINED) {
                this.setDrawerNestedLockMode(EDGE_TOP, savedState.mNestedLockModeTop);
            }
            if (savedState.mNestedLockModeBottom != LOCK_UNDEFINED) {
                this.setDrawerNestedLockMode(EDGE_BOTTOM, savedState.mNestedLockModeBottom);
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }

    // NestedScrollParent

    @Nullable
    private DrawerNestedHelper mDrawerNestedHelper;
    @Nullable
    private ScrollingAxesHelper mScrollingAxesHelper;

    @Override
    public boolean onStartNestedScroll(@NonNull View child,
                                       @NonNull View target, int scrollAxes, int scrollType) {
        return this.getDrawerNestedHelper().startNestedScroll(child, target, scrollAxes, scrollType);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child,
                                       @NonNull View target, int scrollAxes, int scrollType) {
        this.getScrollingAxesHelper().save(scrollType, scrollAxes);
        this.getDrawerNestedHelper().dispatchOnNestedScrollAccepted(child, target, scrollAxes, scrollType);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy,
                                  @NonNull int[] consumed, int scrollType) {
        this.getDrawerNestedHelper().dispatchOnNestedPreScroll(target, dx, dy, consumed, scrollType);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               @NonNull int[] consumed, int scrollType) {
        this.getDrawerNestedHelper().dispatchOnNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        if (this.getDrawerNestedHelper().dispatchOnNestedPreFling(target, velocityX, velocityY)) {
            return true;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (this.getDrawerNestedHelper().dispatchOnNestedFling(target, velocityX, velocityY, consumed)) {
            return true;
        }
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int scrollType) {
        this.getDrawerNestedHelper().dispatchOnStopNestedScroll(target, scrollType);
        this.getScrollingAxesHelper().clear(scrollType);
    }

    @Override
    public int getNestedScrollAxes() {
        return this.getScrollingAxesHelper().getScrollAxes();
    }

    @NonNull
    public DrawerNestedHelper getDrawerNestedHelper() {
        if (this.mDrawerNestedHelper == null) {
            this.mDrawerNestedHelper = new DrawerNestedHelper(this);
        }
        return this.mDrawerNestedHelper;
    }

    @Nullable
    public ScrollingAxesHelper getScrollingAxesHelper() {
        if (this.mScrollingAxesHelper == null) {
            this.mScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mScrollingAxesHelper;
    }

    public void setChildInsets(@NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final boolean draw;
            draw = insets.getSystemWindowInsetTop() > 0;
            if (draw) {
                this.setWillNotDraw(false);
            } else {
                this.setWillNotDraw(this.getBackground() == null);
            }
            this.mStatusBarInsets = insets;
            this.mStatusBarBackgroundEnabled = draw;
            this.requestLayout();
        }
    }

    public void addDrawerListener(@NonNull DrawerListener listener) {
        this.mDrawerListeners.add(listener);
    }

    public void removeDrawerListener(@NonNull DrawerListener listener) {
        this.mDrawerListeners.remove(listener);
    }

    public void clearDrawerListeners() {
        this.mDrawerListeners.clear();
    }

    @ColorInt
    public int getScrimColor() {
        return this.mScrimColor;
    }

    public void setScrimColor(@ColorInt int colorInt) {
        if (this.mScrimColor != colorInt) {
            this.mScrimColor = colorInt;
            this.invalidate();
        }
    }

    public void setScrimColorResource(@ColorRes int resId) {
        final Context context = this.getContext();
        this.setScrimColor(ContextCompat.getColor(context, resId));
    }

    public float getDrawerElevation() {
        return this.mDrawerElevation;
    }

    public void setDrawerElevation(float drawerElevation) {
        this.mDrawerElevation = drawerElevation;
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (this.isDrawerView(child)) {
                ViewCompat.setElevation(child, drawerElevation);
            }
        }
    }

    @Nullable
    public Drawable getDrawerShadow(@EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                return this.mShadowTop;
            case EDGE_LEFT:
                return this.mShadowLeft;
            case EDGE_RIGHT:
                return this.mShadowRight;
            case EDGE_BOTTOM:
                return this.mShadowBottom;
            default:
                return null;
        }
    }

    public void setDrawerShadow(@EdgeMode int edgeMode,
                                @Nullable Drawable drawable) {
        switch (edgeMode) {
            case EDGE_TOP:
                this.mShadowTop = drawable;
                break;
            case EDGE_LEFT:
                this.mShadowLeft = drawable;
                break;
            case EDGE_RIGHT:
                this.mShadowRight = drawable;
                break;
            case EDGE_BOTTOM:
                this.mShadowBottom = drawable;
                break;
            default:
                return;
        }
        this.invalidate();
    }

    public void setDrawerShadowResource(@EdgeMode int edgeMode,
                                        @DrawableRes int resId) {
        final Context context = this.getContext();
        final Drawable drawable;
        drawable = ContextCompat.getDrawable(context, resId);
        this.setDrawerShadow(edgeMode, drawable);
    }

    @Nullable
    public Drawable getStatusBarBackground() {
        return this.mStatusBarBackground;
    }

    public void setStatusBarBackground(@Nullable Drawable background) {
        this.mStatusBarBackground = background;
        this.invalidate();
    }

    public void setStatusBarBackgroundResource(@DrawableRes int resId) {
        final Context context = this.getContext();
        final Drawable background;
        background = ContextCompat.getDrawable(context, resId);
        this.setStatusBarBackground(background);
    }

    public void setStatusBarBackgroundColor(@ColorInt int colorInt) {
        this.setStatusBarBackground(new ColorDrawable(colorInt));
    }

    public void setStatusBarBackgroundColorResource(@ColorRes int resId) {
        final Context context = this.getContext();
        final int colorInt;
        colorInt = ContextCompat.getColor(context, resId);
        this.setStatusBarBackgroundColor(colorInt);
    }

    public void stopScroll() {
        this.stopScroll(EDGE_LEFT);
        this.stopScroll(EDGE_RIGHT);
        this.stopScroll(EDGE_TOP);
        this.stopScroll(EDGE_BOTTOM);
    }

    public void stopScroll(@EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                this.mTopDragged.abort();
                break;
            case EDGE_LEFT:
                this.mLeftDragged.abort();
                break;
            case EDGE_RIGHT:
                this.mRightDragged.abort();
                break;
            case EDGE_BOTTOM:
                this.mBottomDragged.abort();
                break;
            default:
                break;
        }
    }

    public void openDrawer(@EdgeMode int edgeMode) {
        this.openDrawer(edgeMode, true);
    }

    public void openDrawer(@EdgeMode int edgeMode, boolean animate) {
        final View child = this.findDrawerByEdgeMode(edgeMode);
        if (child == null) {
            throw new IllegalArgumentException(
                    "No drawer view found with edge " + edgeMode);
        }
        this.openDrawer(child, animate);
    }

    public void openDrawer(@NonNull View child) {
        this.openDrawer(child, true);
    }

    public void openDrawer(@Nullable View child, boolean animate) {
        if (child == null) {
            return;
        }
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a sliding drawer");
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        if (this.mFirstLayout) {
            layoutParams.screen = 1.f;
            layoutParams.openState = FLAG_OPENED;
            this.updateChildrenImportantForAccessibility(child, true);
        } else if (animate) {
            layoutParams.openState |= FLAG_OPENING;

            int finalTop = child.getTop();
            int finalLeft = child.getLeft();

            if (this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                finalLeft = 0;
                this.mLeftDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                finalLeft = this.getWidth() - child.getWidth();
                this.mRightDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                finalTop = 0;
                this.mTopDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                finalTop = this.getHeight() - child.getHeight();
                this.mBottomDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            }
        } else {
            this.moveDrawerToScreen(child, 1.f);
            this.setDrawerViewState(child, STATE_IDLE);
            child.setVisibility(View.VISIBLE);
        }
        this.invalidate();
    }

    public void closeDrawer(@EdgeMode int edgeMode) {
        this.closeDrawer(edgeMode, true);
    }

    public void closeDrawer(@EdgeMode int edgeMode, boolean animate) {
        final View child = this.findDrawerByEdgeMode(edgeMode);
        if (child == null) {
            throw new IllegalArgumentException(
                    "No drawer view found with edge " + edgeMode);
        }
        this.closeDrawer(child, animate);
    }

    public void closeDrawer(@Nullable View child) {
        this.closeDrawer(child, true);
    }

    public void closeDrawer(@Nullable View child, boolean animate) {
        if (child == null) {
            return;
        }
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a sliding drawer");
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        if (this.mFirstLayout) {
            layoutParams.screen = 0.f;
            layoutParams.openState = FLAG_CLOSED;
            this.updateChildrenImportantForAccessibility(child, false);
        } else if (animate) {
            layoutParams.openState |= FLAG_CLOSING;

            int finalTop = child.getTop();
            int finalLeft = child.getLeft();

            if (this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                finalLeft = -child.getWidth();
                this.mLeftDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                finalLeft = this.getWidth();
                this.mRightDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                finalTop = -child.getHeight();
                this.mTopDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                finalTop = this.getHeight();
                this.mBottomDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            }
        } else {
            this.moveDrawerToScreen(child, 0.f);
            this.setDrawerViewState(child, STATE_IDLE);
            child.setVisibility(View.INVISIBLE);
        }
        this.invalidate();
    }

    public void closeDrawers() {
        boolean needsInvalidate = false;
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (!this.isDrawerView(child)) {
                continue;
            }
            int finalTop = child.getTop();
            int finalLeft = child.getLeft();

            if (this.checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                finalLeft = -child.getWidth();
                needsInvalidate |= this.mLeftDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                finalLeft = this.getWidth();
                needsInvalidate |= this.mRightDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                finalTop = -child.getHeight();
                needsInvalidate |= this.mTopDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            } else if (this.checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                finalTop = this.getHeight();
                needsInvalidate |= this.mBottomDragged.smoothSlideViewTo(child, finalLeft, finalTop);
            }
        }
        if (needsInvalidate) {
            this.invalidate();
        }
    }

    @Px
    public int getDraggedEdgeSize(@EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                return this.mTopDragged.getEdgeSize();
            case EDGE_LEFT:
                return this.mLeftDragged.getEdgeSize();
            case EDGE_RIGHT:
                return this.mRightDragged.getEdgeSize();
            case EDGE_BOTTOM:
                return this.mBottomDragged.getEdgeSize();
            default:
                break;
        }
        return 0;
    }

    public void setDraggedEdgeSize(@Px int edgeSize) {
        this.setDraggedEdgeSize(EDGE_LEFT, edgeSize);
        this.setDraggedEdgeSize(EDGE_RIGHT, edgeSize);
        this.setDraggedEdgeSize(EDGE_TOP, edgeSize);
        this.setDraggedEdgeSize(EDGE_BOTTOM, edgeSize);
    }

    public void setDraggedEdgeSize(@EdgeMode int edgeMode,
                                   @Px int edgeSize) {
        switch (edgeMode) {
            case EDGE_TOP:
                this.mTopDragged.setEdgeSize(edgeSize);
                break;
            case EDGE_LEFT:
                this.mLeftDragged.setEdgeSize(edgeSize);
                break;
            case EDGE_RIGHT:
                this.mRightDragged.setEdgeSize(edgeSize);
                break;
            case EDGE_BOTTOM:
                this.mBottomDragged.setEdgeSize(edgeSize);
                break;
            default:
                break;
        }
    }

    @LockMode
    public int getDrawerLockMode(@NonNull View child) {
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        final int edgeMode = this.getDrawerViewEdgeMode(child);
        return this.getDrawerLockMode(edgeMode);
    }

    @LockMode
    public int getDrawerLockMode(@EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                if (this.mLockModeTop != LOCK_UNDEFINED) {
                    return this.mLockModeTop;
                }
                break;
            case EDGE_LEFT:
                if (this.mLockModeLeft != LOCK_UNDEFINED) {
                    return this.mLockModeLeft;
                }
                break;
            case EDGE_RIGHT:
                if (this.mLockModeRight != LOCK_UNDEFINED) {
                    return this.mLockModeRight;
                }
                break;
            case EDGE_BOTTOM:
                if (this.mLockModeBottom != LOCK_UNDEFINED) {
                    return this.mLockModeBottom;
                }
                break;
            default:
                break;
        }
        return LOCK_UNLOCKED;
    }

    public void setDrawerLockMode(@NonNull View child,
                                  @LockMode int lockMode) {
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        final int edgeMode = this.getDrawerViewEdgeMode(child);
        this.setDrawerLockMode(edgeMode, lockMode);
    }

    public void setDrawerLockMode(@LockMode int lockMode) {
        this.setDrawerLockMode(EDGE_LEFT, lockMode);
        this.setDrawerLockMode(EDGE_RIGHT, lockMode);
        this.setDrawerLockMode(EDGE_TOP, lockMode);
        this.setDrawerLockMode(EDGE_BOTTOM, lockMode);
    }

    public void setDrawerLockMode(@EdgeMode int edgeMode,
                                  @LockMode int lockMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                this.mLockModeTop = lockMode;
                break;
            case EDGE_LEFT:
                this.mLockModeLeft = lockMode;
                break;
            case EDGE_RIGHT:
                this.mLockModeRight = lockMode;
                break;
            case EDGE_BOTTOM:
                this.mLockModeBottom = lockMode;
                break;
            default:
                break;
        }

        if (lockMode != LOCK_UNLOCKED) {
            // Cancel interaction in progress
            if (edgeMode == EDGE_TOP) {
                this.mTopDragged.cancel();
            }
            if (edgeMode == EDGE_LEFT) {
                this.mLeftDragged.cancel();
            }
            if (edgeMode == EDGE_RIGHT) {
                this.mRightDragged.cancel();
            }
            if (edgeMode == EDGE_BOTTOM) {
                this.mBottomDragged.cancel();
            }
        }

        switch (lockMode) {
            case LOCK_LOCKED_OPENED: {
                final View child = this.findDrawerByEdgeMode(edgeMode);
                if (child != null) {
                    this.openDrawer(child);
                }
                break;
            }
            case LOCK_LOCKED_CLOSED: {
                final View child = this.findDrawerByEdgeMode(edgeMode);
                if (child != null) {
                    this.closeDrawer(child);
                }
                break;
            }
            default:
                // nothing done.
                break;
        }
    }

    @LockMode
    public int getDrawerNestedLockMode(@NonNull View child) {
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        final int edgeMode = this.getDrawerViewEdgeMode(child);
        return this.getDrawerNestedLockMode(edgeMode);
    }

    @LockMode
    public int getDrawerNestedLockMode(@EdgeMode int edgeMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                if (this.mNestedLockModeTop != LOCK_UNDEFINED) {
                    return this.mNestedLockModeTop;
                }
                break;
            case EDGE_LEFT:
                if (this.mNestedLockModeLeft != LOCK_UNDEFINED) {
                    return this.mNestedLockModeLeft;
                }
                break;
            case EDGE_RIGHT:
                if (this.mNestedLockModeRight != LOCK_UNDEFINED) {
                    return this.mNestedLockModeRight;
                }
                break;
            case EDGE_BOTTOM:
                if (this.mNestedLockModeBottom != LOCK_UNDEFINED) {
                    return this.mNestedLockModeBottom;
                }
                break;
            default:
                break;
        }
        return LOCK_UNLOCKED;
    }

    public void setDrawerNestedLockMode(@NonNull View child,
                                        @LockMode int lockMode) {
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        final int edgeMode = this.getDrawerViewEdgeMode(child);
        this.setDrawerNestedLockMode(edgeMode, lockMode);
    }

    public void setDrawerNestedLockMode(@LockMode int lockMode) {
        this.setDrawerNestedLockMode(EDGE_LEFT, lockMode);
        this.setDrawerNestedLockMode(EDGE_RIGHT, lockMode);
        this.setDrawerNestedLockMode(EDGE_TOP, lockMode);
        this.setDrawerNestedLockMode(EDGE_BOTTOM, lockMode);
    }

    public void setDrawerNestedLockMode(@EdgeMode int edgeMode,
                                        @LockMode int lockMode) {
        switch (edgeMode) {
            case EDGE_TOP:
                this.mNestedLockModeTop = lockMode;
                break;
            case EDGE_LEFT:
                this.mNestedLockModeLeft = lockMode;
                break;
            case EDGE_RIGHT:
                this.mNestedLockModeRight = lockMode;
                break;
            case EDGE_BOTTOM:
                this.mNestedLockModeBottom = lockMode;
                break;
            default:
                break;
        }

        if (lockMode != LOCK_UNLOCKED) {
            // We need to stopNestedScroll().
            this.getDrawerNestedHelper().stopTargetNestedScroll(edgeMode);
        }
    }

    public final boolean hasVisibleDrawer() {
        return this.findVisibleDrawerView() != null;
    }

    @Nullable
    public final View findVisibleDrawerView() {
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (this.isDrawerView(child)
                    && this.isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public final View findDrawerByEdgeMode(@EdgeMode int edgeMode) {
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (this.checkDrawerViewEdgeMode(child, edgeMode)) {
                return child;
            }
        }
        return null;
    }

    @Nullable
    public final View findOpenDrawerView() {
        return this.findDrawerByOpenState(FLAG_OPENED);
    }

    @Nullable
    public final View findDrawerByOpenState(@OpenState int openState) {
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (this.checkDrawerViewOpenState(child, openState)) {
                return child;
            }
        }
        return null;
    }

    public final boolean isDrawerVisible(@EdgeMode int edgeMode) {
        final View child = this.findDrawerByEdgeMode(edgeMode);
        if (child == null) {
            return false;
        }
        return this.isDrawerVisible(child);
    }

    public final boolean isDrawerVisible(@Nullable View child) {
        if (child == null) {
            return false;
        }
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        return this.getDrawerViewScreen(child) > 0.f;
    }

    public final boolean isDrawerOpen(@EdgeMode int edgeMode) {
        final View child = this.findDrawerByEdgeMode(edgeMode);
        if (child == null) {
            return false;
        }
        return this.isDrawerOpen(child);
    }

    public final boolean isDrawerOpen(@Nullable View child) {
        if (child == null) {
            return false;
        }
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        return this.checkDrawerViewOpenState(child, FLAG_OPENED);
    }

    public final boolean isDrawerView(@Nullable View child) {
        if (child == null) {
            return false;
        }
        return !this.checkDrawerViewEdgeMode(child, EDGE_NODE);
    }

    public final boolean isContentView(@Nullable View child) {
        if (child == null) {
            return false;
        }
        return this.checkDrawerViewEdgeMode(child, EDGE_NODE);
    }

    @EdgeMode
    public final int getDrawerViewEdgeMode(@Nullable View child) {
        if (child == null) {
            return EDGE_NODE;
        }
        return ((LayoutParams) child.getLayoutParams()).edgeMode;
    }

    @OpenState
    public final int getDrawerViewOpenState(@Nullable View child) {
        if (child == null) {
            return FLAG_CLOSED;
        }
        return ((LayoutParams) child.getLayoutParams()).openState;
    }

    public final boolean checkDrawerViewHorOri(@NonNull View child) {
        final int edgeMode = this.getDrawerViewEdgeMode(child);
        return edgeMode == EDGE_LEFT || edgeMode == EDGE_RIGHT;
    }

    public final boolean checkDrawerViewLockMode(@NonNull View child,
                                                 @LockMode int lockMode) {
        return this.getDrawerLockMode(child) == lockMode;
    }

    public final boolean checkDrawerViewNestedLockMode(@NonNull View child,
                                                       @LockMode int lockMode) {
        return this.getDrawerNestedLockMode(child) == lockMode;
    }

    public final boolean checkDrawerViewEdgeMode(@NonNull View child,
                                                 @EdgeMode int edgeMode) {
        return this.getDrawerViewEdgeMode(child) == edgeMode;
    }

    public final boolean checkDrawerViewOpenState(@NonNull View child,
                                                  @OpenState int openState) {
        return (this.getDrawerViewOpenState(child) & openState) != 0;
    }

    public final float getDrawerViewScreen(@Nullable View child) {
        if (child == null) {
            return 0.f;
        }
        return ((LayoutParams) child.getLayoutParams()).screen;
    }

    private void setDrawerViewScreen(@Nullable View child,
                                     float screen) {
        if (child == null) {
            return;
        }
        if (Float.isNaN(screen)) {
            return;
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        if (layoutParams.screen == screen) {
            return;
        }
        layoutParams.screen = screen;
        this.dispatchOnDrawerSlide(child, screen);
    }

    public final void moveDrawerToScreen(@NonNull View child, float screen) {
        if (!this.isDrawerView(child)) {
            throw new IllegalArgumentException(
                    "View " + child + " is not a drawer");
        }
        final float oldScreen = this.getDrawerViewScreen(child);
        if (this.checkDrawerViewHorOri(child)) {
            final int width = child.getWidth();
            final int oldPos = (int) (width * oldScreen);
            final int newPos = (int) (width * screen);
            int dx = newPos - oldPos;
            dx = this.checkDrawerViewEdgeMode(child, EDGE_LEFT) ? dx : -dx;
            ViewCompat.offsetLeftAndRight(child, dx);
        } else {
            final int height = child.getHeight();
            final int oldPos = (int) (height * oldScreen);
            final int newPos = (int) (height * screen);
            int dy = newPos - oldPos;
            dy = this.checkDrawerViewEdgeMode(child, EDGE_TOP) ? dy : -dy;
            ViewCompat.offsetTopAndBottom(child, dy);
        }
        this.setDrawerViewScreen(child, screen);
    }

    @DrawerState
    public final int getDrawerViewState() {
        return this.mDrawerViewState;
    }

    final void setDrawerViewState(@Nullable View child,
                                  @DrawerState int activeState) {
        final int topState = this.mTopDragged.getViewDragState();
        final int leftState = this.mLeftDragged.getViewDragState();
        final int rightState = this.mRightDragged.getViewDragState();
        final int bottomState = this.mBottomDragged.getViewDragState();
        final int drawerViewState;
        if (activeState == STATE_DRAGGING
                || leftState == STATE_DRAGGING || rightState == STATE_DRAGGING
                || topState == STATE_DRAGGING || bottomState == STATE_DRAGGING) {
            drawerViewState = STATE_DRAGGING;
        } else if (activeState == STATE_SETTLING
                || leftState == STATE_SETTLING || rightState == STATE_SETTLING
                || topState == STATE_SETTLING || bottomState == STATE_SETTLING) {
            drawerViewState = STATE_SETTLING;
        } else {
            drawerViewState = STATE_IDLE;
        }

        if (activeState == STATE_IDLE) {
            if (child != null) {
                final float screen = this.getDrawerViewScreen(child);
                if (screen == 0.f) {
                    this.dispatchOnDrawerClosed(child);
                } else if (screen == 1.f) {
                    this.dispatchOnDrawerOpened(child);
                }
            }
        }

        if (this.mDrawerViewState != drawerViewState) {
            this.mDrawerViewState = drawerViewState;
            // Notify the listeners. Do that from the end of the list so that if a listener
            // removes itself as the result of being called, it won't mess up with our iteration
            this.dispatchOnDrawerStateChanged(drawerViewState);
        }
    }

    private void dispatchOnDrawerSlide(@NonNull View child, float screen) {
        final int N = this.mDrawerListeners.size();
        for (int index = N - 1; index >= 0; index--) {
            this.mDrawerListeners.get(index).onDrawerSlide(this, child, screen);
        }
    }

    private void dispatchOnDrawerOpened(@NonNull View child) {
        if (!this.checkDrawerViewOpenState(child, FLAG_OPENED)) {
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.screen = 1.f;
            layoutParams.openState = FLAG_OPENED;

            final int N = this.mDrawerListeners.size();
            for (int index = N - 1; index >= 0; index--) {
                this.mDrawerListeners.get(index).onDrawerOpened(this, child);
            }
            this.updateChildrenImportantForAccessibility(child, true);
            // Only send WINDOW_STATE_CHANGE if the host has window focus.
            if (this.hasWindowFocus()) {
                this.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            }
        }
    }

    private void dispatchOnDrawerClosed(@NonNull View child) {
        if (!this.checkDrawerViewOpenState(child, FLAG_CLOSED)) {
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.screen = 0.f;
            layoutParams.openState = FLAG_CLOSED;

            final int N = this.mDrawerListeners.size();
            for (int index = N - 1; index >= 0; index--) {
                this.mDrawerListeners.get(index).onDrawerClosed(this, child);
            }
            this.updateChildrenImportantForAccessibility(child, false);
            // Only send WINDOW_STATE_CHANGE if the host has window focus. This
            // may change if support for multiple foreground windows (e.g. IME)
            // improves.
            if (this.hasWindowFocus()) {
                final View rootView = this.getRootView();
                if (rootView != null) {
                    rootView.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                }
            }
        }
    }

    private void dispatchOnDrawerStateChanged(@DrawerState int drawerState) {
        final int N = this.mDrawerListeners.size();
        for (int index = N - 1; index >= 0; index--) {
            this.mDrawerListeners.get(index).onDrawerStateChanged(this, drawerState);
        }
    }

    private boolean hasViewOpaqueBackground(@Nullable View child) {
        if (child == null) {
            return false;
        }
        final Drawable background = child.getBackground();
        if (background != null) {
            return background.getOpacity() == PixelFormat.OPAQUE;
        }
        return false;
    }

    private void updateChildrenImportantForAccessibility(@NonNull View drawerView,
                                                         boolean isDrawerOpen) {
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if ((isDrawerOpen && child == drawerView)
                    || (!isDrawerOpen && !this.isDrawerView(child))) {
                // Drawer is closed and this is a content view or this is an
                // open drawer view, so it should be visible.
                ViewCompat.setImportantForAccessibility(child,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            } else {
                ViewCompat.setImportantForAccessibility(child,
                        ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            }
        }
    }

    /*
     * Quick close drawer.
     * */
    public static boolean closeDrawerView(@Nullable View target) {
        if (target == null) {
            return false;
        }
        final ViewGroup parent = (ViewGroup) target.getParent();
        if (parent instanceof DrawerLayout) {
            final DrawerLayout drawerLayout = (DrawerLayout) parent;
            if (drawerLayout.isDrawerView(target)
                    && drawerLayout.isDrawerOpen(target)) {
                drawerLayout.closeDrawer(target);
                return true;
            } else return false;
        }
        return closeDrawerView(parent);
    }

    private final class ViewDragCallback extends ViewDragHelper.Callback {
        @EdgeMode
        private final int mEdgeMode;
        private ViewDragHelper mDragged;

        public ViewDragCallback(@EdgeMode int edgeMode) {
            this.mEdgeMode = edgeMode;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            final View child = findDrawerByEdgeMode(edgeFlags);
            if (child == null) {
                return;
            }
            if (checkDrawerViewLockMode(child, LOCK_UNLOCKED)) {
                invalidate();
                this.closeOtherDrawer(child);
                this.cancelChildViewTouch();
            }
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            final View child = findDrawerByEdgeMode(edgeFlags);
            if (child == null) {
                return;
            }
            if (checkDrawerViewLockMode(child, LOCK_UNLOCKED)) {
                this.mDragged.captureChildView(child, pointerId);
            }
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            // Only capture views where the gravity matches what we're looking for.
            // This lets us use two ViewDragHelpers, one for each side drawer.
            return isDrawerView(child)
                    && checkDrawerViewEdgeMode(child, this.mEdgeMode)
                    && checkDrawerViewLockMode(child, LOCK_UNLOCKED);
        }

        @Override
        public void onViewCaptured(@NonNull View child, int activePointerId) {
            this.closeOtherDrawer(child);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            final View child = this.mDragged.getCapturedView();
            setDrawerViewState(child, state);
        }

        @Override
        public void onViewPositionChanged(@NonNull View child, int left, int top, int dx, int dy) {
            final int width = getWidth();
            final int height = getHeight();
            final int childWidth = child.getWidth();
            final int childHeight = child.getHeight();
            float screen = 0.f;
            // This reverses the positioning shown in onLayout.
            if (checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                screen = (float) (childWidth + left) / childWidth;
            } else if (checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                screen = (float) (width - left) / childWidth;
            } else if (checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                screen = (float) (childHeight + top) / childHeight;
            } else if (checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                screen = (float) (height - top) / childHeight;
            }
            setDrawerViewScreen(child, screen);
            final int newVisibility = screen == 0 ? View.INVISIBLE : View.VISIBLE;
            if (child.getVisibility() != newVisibility) {
                child.setVisibility(newVisibility);
            }
            invalidate();
        }

        @Override
        public void onViewReleased(@NonNull View child, float velocityX, float velocityY) {
            final float screen = getDrawerViewScreen(child);
            final int width = getWidth();
            final int height = getHeight();
            final int childWidth = child.getWidth();
            final int childHeight = child.getHeight();
            int finalTop = child.getTop();
            int finalLeft = child.getLeft();
            // Offset is how open the drawer is, therefore edge values
            // are reversed from one another.
            if (checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                finalLeft = velocityX > 0 || (velocityX == 0 && screen > 0.5f)
                        ? 0 : -childWidth;
            } else if (checkDrawerViewEdgeMode(child, EDGE_RIGHT)) {
                finalLeft = velocityX < 0 || (velocityX == 0 && screen > 0.5f)
                        ? width - childWidth : width;
            } else if (checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                finalTop = velocityY > 0 || (velocityY == 0 && screen > 0.5f)
                        ? 0 : -childHeight;
            } else if (checkDrawerViewEdgeMode(child, EDGE_BOTTOM)) {
                finalTop = velocityY < 0 || (velocityY == 0 && screen > 0.5f)
                        ? height - childHeight : height;
            }
            this.mDragged.settleCapturedViewAt(finalLeft, finalTop);
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (checkDrawerViewHorOri(child)) {
                final int width = getWidth();
                final int childWidth = child.getWidth();

                if (checkDrawerViewEdgeMode(child, EDGE_LEFT)) {
                    return Math.max(-childWidth, Math.min(left, 0));
                } else {
                    return Math.max(width - childWidth, Math.min(left, width));
                }
            } else return child.getLeft();
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (checkDrawerViewHorOri(child)) {
                return child.getTop();
            } else {
                final int height = getHeight();
                final int childHeight = child.getHeight();

                if (checkDrawerViewEdgeMode(child, EDGE_TOP)) {
                    return Math.max(-childHeight, Math.min(top, 0));
                } else {
                    return Math.max(height - childHeight, Math.min(top, height));
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if (!isDrawerView(child)) return 0;
            return checkDrawerViewHorOri(child) ? child.getWidth() : 0;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if (!isDrawerView(child)) return 0;
            return checkDrawerViewHorOri(child) ? 0 : child.getHeight();
        }

        public void setDragged(@NonNull ViewDragHelper dragged) {
            this.mDragged = dragged;
        }

        private void closeOtherDrawer(@NonNull View drawerChild) {
            final int N = getChildCount();
            for (int index = 0; index < N; index++) {
                final View child = getChildAt(index);
                if (child == null) {
                    continue;
                }
                if (child == drawerChild || !isDrawerView(child)) {
                    continue;
                }
                closeDrawer(child);
            }
        }

        private void cancelChildViewTouch() {
            // Cancel child touches
            if (mChildrenCanceledTouch) {
                return;
            }
            mChildrenCanceledTouch = true;
            final long now = SystemClock.uptimeMillis();
            final MotionEvent cancelEvent = MotionEvent.obtain(now, now,
                    MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
            final int N = getChildCount();
            for (int index = 0; index < N; index++) {
                final View child = getChildAt(index);
                if (child == null) {
                    continue;
                }
                child.dispatchTouchEvent(cancelEvent);
            }
            cancelEvent.recycle();
        }
    }

    public static class SavedState extends AbsSavedState {
        @EdgeMode
        private int mOpenEdgeMode;
        @LockMode
        private int mLockModeTop;
        @LockMode
        private int mLockModeLeft;
        @LockMode
        private int mLockModeRight;
        @LockMode
        private int mLockModeBottom;
        @LockMode
        private int mNestedLockModeTop;
        @LockMode
        private int mNestedLockModeLeft;
        @LockMode
        private int mNestedLockModeRight;
        @LockMode
        private int mNestedLockModeBottom;

        public SavedState() {
            this(EMPTY_STATE);
        }

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mOpenEdgeMode = EDGE_NODE;
            this.mLockModeTop = LOCK_UNLOCKED;
            this.mLockModeLeft = LOCK_UNLOCKED;
            this.mLockModeRight = LOCK_UNLOCKED;
            this.mLockModeBottom = LOCK_UNLOCKED;
            this.mNestedLockModeTop = LOCK_UNLOCKED;
            this.mNestedLockModeLeft = LOCK_UNLOCKED;
            this.mNestedLockModeRight = LOCK_UNLOCKED;
            this.mNestedLockModeBottom = LOCK_UNLOCKED;
        }

        public SavedState(@NonNull Parcel in, @Nullable ClassLoader loader) {
            super(in, loader);
            this.mOpenEdgeMode = in.readInt();
            this.mLockModeTop = in.readInt();
            this.mLockModeLeft = in.readInt();
            this.mLockModeRight = in.readInt();
            this.mLockModeBottom = in.readInt();
            this.mNestedLockModeTop = in.readInt();
            this.mNestedLockModeLeft = in.readInt();
            this.mNestedLockModeRight = in.readInt();
            this.mNestedLockModeBottom = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mOpenEdgeMode);
            dest.writeInt(this.mLockModeTop);
            dest.writeInt(this.mLockModeLeft);
            dest.writeInt(this.mLockModeRight);
            dest.writeInt(this.mLockModeBottom);
            dest.writeInt(this.mNestedLockModeTop);
            dest.writeInt(this.mNestedLockModeLeft);
            dest.writeInt(this.mNestedLockModeRight);
            dest.writeInt(this.mNestedLockModeBottom);
        }

        @NonNull
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @NonNull
            @Override
            public SavedState createFromParcel(@NonNull Parcel in, @Nullable ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @NonNull
            @Override
            public SavedState createFromParcel(@NonNull Parcel in) {
                return new SavedState(in, null);
            }

            @NonNull
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public static class LayoutParams extends MarginLayoutParams {
        private float screen = 0.f;
        private int edgeMode = EDGE_NODE;
        private int openState = FLAG_CLOSED;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull LayoutParams source) {
            super(source);
            this.edgeMode = source.edgeMode;
        }

        public LayoutParams(@NonNull MarginLayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            final TypedArray typedArray;
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawerLayout_Layout);
            this.edgeMode = typedArray.getInt(R.styleable.DrawerLayout_Layout_edgeMode, EDGE_NODE);
            typedArray.recycle();
        }
    }

    public interface DrawerListener {
        /**
         * Called when a drawer's position changes.
         *
         * @param drawerView The child view that was moved
         * @param screen     The new screen of this drawer within its range, from 0-1
         */
        default void onDrawerSlide(@NonNull DrawerLayout parent,
                                   @NonNull View drawerView, float screen) {
            // nothing
        }

        /**
         * Called when a drawer has settled in a completely open state.
         * The drawer is interactive at this point.
         *
         * @param drawerView Drawer view that is now open
         */
        default void onDrawerOpened(@NonNull DrawerLayout parent,
                                    @NonNull View drawerView) {
            // nothing
        }

        /**
         * Called when a drawer has settled in a completely closed state.
         *
         * @param drawerView Drawer view that is now closed
         */
        default void onDrawerClosed(@NonNull DrawerLayout parent,
                                    @NonNull View drawerView) {
            // nothing
        }

        /**
         * Called when the drawer motion state changes. The new state will
         * be one of {@link #STATE_IDLE},
         * or {@link #STATE_DRAGGING},
         * or {@link #STATE_SETTLING}.
         *
         * @param drawerState The new drawer motion state
         */
        default void onDrawerStateChanged(@NonNull DrawerLayout parent,
                                          @DrawerState int drawerState) {
            // nothing
        }
    }
}
