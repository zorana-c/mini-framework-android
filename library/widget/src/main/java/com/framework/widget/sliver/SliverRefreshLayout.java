package com.framework.widget.sliver;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.customview.view.AbsSavedState;

import com.framework.widget.R;
import com.framework.widget.compat.UIViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-02-28
 * @Email : 171905184@qq.com
 * @Description : 上下拉刷新控件
 */
public class SliverRefreshLayout extends SliverContainer {
    private static final boolean DEBUG = false;
    private static final String TAG = "SliverRefreshLayout";
    /**
     * 刷新状态: 闲置
     */
    public static final int REFRESH_STATE_NONE = 0;
    /**
     * 刷新状态: 拖拽
     */
    public static final int REFRESH_STATE_DRAG = 1;
    /**
     * 刷新状态: 准备
     */
    public static final int REFRESH_STATE_READY = 2;
    /**
     * 刷新状态: 刷新
     */
    public static final int REFRESH_STATE_PROGRESS = 3;
    /**
     * 刷新状态: 完成
     */
    public static final int REFRESH_STATE_COMPLETE = 4;

    @IntDef({REFRESH_STATE_NONE,
            REFRESH_STATE_DRAG,
            REFRESH_STATE_READY,
            REFRESH_STATE_PROGRESS,
            REFRESH_STATE_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface RefreshState {
    }

    /**
     * 临时存放重新排序的视图
     */
    @NonNull
    private final ArrayList<View> mDrawingOrderedChildren = new ArrayList<>();
    /**
     * 缓存刷新监听事件对象
     */
    @NonNull
    private final ArrayList<OnRefreshListener> mOnRefreshListeners = new ArrayList<>();

    public SliverRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public SliverRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMeasureAllChildren(true);
        this.setChildrenDrawingOrderEnabled(true);

        SliverRefreshLoadLayout loadView;
        loadView = new DefRefreshLoadView(context, attrs, defStyleAttr);
        loadView.setScrollStyle(DefRefreshLoadView.SCROLL_STYLE_FAST);
        this.setHeadLoadView(loadView);

        loadView = new DefRefreshLoadView(context, attrs, defStyleAttr);
        loadView.setScrollStyle(DefRefreshLoadView.SCROLL_STYLE_FOLLOW);
        this.setTailLoadView(loadView);

        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliverRefreshLayout);
        final int refreshLocate;
        refreshLocate = typedArray.getInt(R.styleable.SliverRefreshLayout_sliverRefreshLocate, SCROLL_LOCATE_NONE);
        typedArray.recycle();

        this.setRefreshLocate(refreshLocate);
    }

    // ########## Layout ##########

    @NonNull
    @Override
    public LayoutParams generateDefaultLayoutParams() {
        if (this.canScrollVertically()) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @NonNull
    @Override
    public LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof SliverContainer.LayoutParams) {
            return new LayoutParams((SliverContainer.LayoutParams) layoutParams);
        } else if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    @Override
    public boolean checkLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.sortChildDrawingOrder();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int drawingPosition) {
        final ArrayList<View> drawingOrderedChildren;
        drawingOrderedChildren = this.mDrawingOrderedChildren;
        if (drawingOrderedChildren.size() == childCount) {
            final View child = drawingOrderedChildren.get(drawingPosition);
            return ((LayoutParams) child.getLayoutParams()).childIndex;
        }
        return super.getChildDrawingOrder(childCount, drawingPosition);
    }

    private void sortChildDrawingOrder() {
        final ArrayList<View> drawingOrderedChildren;
        drawingOrderedChildren = this.mDrawingOrderedChildren;
        drawingOrderedChildren.clear();

        final int N = this.getChildCount();
        if (N <= 0) {
            return;
        }
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.childIndex = index;

            if (SCROLL_LOCATE_NONE == layoutParams.childLocate) {
                drawingOrderedChildren.add(child);
            } else {
                drawingOrderedChildren.add(0, child);
            }
        }
    }

    // ########## InstanceState ##########

    @NonNull
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mRefreshState = this.mRefreshState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setRefreshState(savedState.mRefreshState);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    // ########## LoadView ##########

    @Nullable
    public <V extends View> V findLoadViewBy(@ScrollLocate int locate) {
        if (SCROLL_LOCATE_NONE == locate) {
            return null;
        }
        final int N = this.getChildCount();
        if (N <= 0) {
            return null;
        }
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);

            if (locate == this.getChildLocate(child)) {
                return (V) child;
            }
        }
        return null;
    }

    @Nullable
    public <V extends View> V getHeadLoadView() {
        return this.findLoadViewBy(SCROLL_LOCATE_HEAD);
    }

    @NonNull
    public final <V extends View> V requireHeadLoadView() {
        final V child = this.getHeadLoadView();
        if (child == null) {
            throw new NullPointerException("ERROR");
        }
        return child;
    }

    @Nullable
    public <V extends View> V getTailLoadView() {
        return this.findLoadViewBy(SCROLL_LOCATE_TAIL);
    }

    @NonNull
    public final <V extends View> V requireTailLoadView() {
        final V child = this.getTailLoadView();
        if (child == null) {
            throw new NullPointerException("ERROR");
        }
        return child;
    }

    @ScrollLocate
    public int getChildLocate(@Nullable View child) {
        if (child == null) {
            return SCROLL_LOCATE_NONE;
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        return layoutParams.childLocate;
    }

    public void setHeadLoadView(@LayoutRes int layoutId) {
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(this.getContext());
        final View child;
        child = inflater.inflate(layoutId, this, false);
        this.setHeadLoadView(child);
    }

    public void setHeadLoadView(@NonNull View child) {
        this.setLoadView(SCROLL_LOCATE_HEAD, child);
    }

    public void setTailLoadView(@LayoutRes int layoutId) {
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(this.getContext());
        final View child;
        child = inflater.inflate(layoutId, this, false);
        this.setTailLoadView(child);
    }

    public void setTailLoadView(@NonNull View child) {
        this.setLoadView(SCROLL_LOCATE_TAIL, child);
    }

    private void setLoadView(int locate, @NonNull View child) {
        final View oldChild = this.findLoadViewBy(locate);
        if (oldChild == child) {
            return;
        }
        if (oldChild != null) {
            this.removeView(oldChild);
        }
        final LayoutParams layoutParams;
        layoutParams = this.generateLayoutParams(child);
        layoutParams.scrolling = false;
        layoutParams.childLocate = locate;

        if (this.canScrollVertically()) {
            layoutParams.layoutTTB(locate);
        } else {
            layoutParams.layoutLTR(locate);
        }
        this.addView(child, layoutParams);
        this.measureChild(child,
                MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        this.dispatchOnRefreshScrolled(locate, 0, 0);
        this.dispatchOnRefreshStateChanged(locate, REFRESH_STATE_NONE);
    }

    public final int getDecorSize(@ScrollLocate int locate) {
        if (!this.canRefreshScroll(locate)) {
            return 0;
        }
        final View child = this.findLoadViewBy(locate);
        if (child == null) {
            return 0;
        }
        int decorSize;
        if (this.canScrollVertically()) {
            decorSize = this.getDecoratedMeasuredHeight(child);
        } else {
            decorSize = this.getDecoratedMeasuredWidth(child);
        }
        if (child instanceof RefreshInterfaces) {
            final RefreshInterfaces<SliverRefreshLayout> interfaces;
            interfaces = (RefreshInterfaces<SliverRefreshLayout>) child;
            decorSize = interfaces.getRefreshDecorSize(this);
        }
        return decorSize * locate;
    }

    @RefreshState
    public int getRefreshState() {
        return this.mRefreshState;
    }

    @ScrollLocate
    public int getRefreshLocate() {
        return this.mRefreshLocate;
    }

    /**
     * 注意两者调用的顺序, 可能互相影响.
     * <p>
     * 调用之前, 注意是否已开启刷新滑动{@link #setRefreshLocate(int)}
     *
     * @see SliverRefreshLayout#setBounceLocate(int)
     * @see SliverRefreshLayout#setRefreshLocate(int)
     */
    @Override
    public void setBounceLocate(@ScrollLocate int bounceLocate) {
        super.setBounceLocate(bounceLocate);
    }

    /**
     * 注意两者调用的顺序, 可能互相影响.
     * <p>
     * 调用之前, 注意是否已开启阻尼滑动{@link #setBounceLocate(int)}
     *
     * @see SliverRefreshLayout#setBounceLocate(int)
     * @see SliverRefreshLayout#setRefreshLocate(int)
     */
    public void setRefreshLocate(@ScrollLocate int refreshLocate) {
        if (this.mRefreshLocate != refreshLocate) {
            this.mRefreshLocate = refreshLocate;
        }
    }

    public boolean canRefreshScroll(@ScrollLocate int scrollLocate) {
        final int refreshLocate = this.mRefreshLocate;
        if (SCROLL_LOCATE_NONE == refreshLocate) {
            return false;
        }
        return refreshLocate == scrollLocate
                || refreshLocate == SCROLL_LOCATE_ALL;
    }

    public boolean isRefreshing() {
        final int refreshState = this.mRefreshState;
        return REFRESH_STATE_PROGRESS == refreshState;
    }

    public final void forcedRefreshing() {
        this.forcedRefreshing(0L);
    }

    public final void forcedRefreshing(int duration) {
        this.forcedRefreshing(duration, 0L);
    }

    public final void forcedRefreshing(long delayMillis) {
        this.forcedRefreshing(UNDEFINED_DURATION, delayMillis);
    }

    /**
     * @param duration    滑动持续时间(ms)
     * @param delayMillis 滑动延迟时间(ms)
     */
    public void forcedRefreshing(int duration, long delayMillis) {
        this.mRefreshAction.startRefreshed(duration, delayMillis);
    }

    public final void completeRefreshed() {
        this.completeRefreshed(0L);
    }

    public final void completeRefreshed(int duration) {
        this.completeRefreshed(duration, 0L);
    }

    public final void completeRefreshed(long delayMillis) {
        this.completeRefreshed(UNDEFINED_DURATION, delayMillis);
    }

    /**
     * @param duration    滑动持续时间(ms)
     * @param delayMillis 滑动延迟时间(ms)
     */
    public void completeRefreshed(int duration, long delayMillis) {
        final int refreshState = this.mRefreshState;
        if (REFRESH_STATE_PROGRESS == refreshState) {
            this.setRefreshState(REFRESH_STATE_COMPLETE);
        }
        this.mRefreshAction.startCompleted(duration, delayMillis);
    }

    public void addOnRefreshListener(@NonNull OnRefreshListener listener) {
        this.mOnRefreshListeners.add(listener);
    }

    public void removeOnRefreshListener(@NonNull OnRefreshListener listener) {
        this.mOnRefreshListeners.remove(listener);
    }

    public void setRefreshCallback(@Nullable RefreshCallback refreshCallback) {
        this.mRefreshCallback = refreshCallback;
    }

    // ########## SliverScroll ##########

    @NonNull
    private final RefreshAction mRefreshAction = new RefreshAction() {
        @Override
        public void apply(int opAction, int duration) {
            if (OP_REFRESHED == opAction) {
                handleRefreshed(duration);
            } else if (OP_COMPLETED == opAction) {
                handleCompleted(duration);
            }
        }
    };

    @RefreshState
    private int mRefreshState;
    @ScrollLocate
    private int mRefreshLocate;
    @ScrollLocate
    private int mLastScrollLocate;
    private int mHeadDecorSize = 0;
    private int mTailDecorSize = 0;
    private int mLastExtraOffsetX = 0;
    private int mLastExtraOffsetY = 0;
    @Nullable
    private RefreshCallback mRefreshCallback;

    @Override
    public void onSliverScrollAccepted(int scrollAxes,
                                       int scrollType) {
        super.onSliverScrollAccepted(scrollAxes, scrollType);
        this.mRefreshAction.stop();
        this.mHeadDecorSize = this.getDecorSize(SCROLL_LOCATE_HEAD);
        this.mTailDecorSize = this.getDecorSize(SCROLL_LOCATE_TAIL);
        this.mLastExtraOffsetX = this.getExtraOffsetX();
        this.mLastExtraOffsetY = this.getExtraOffsetY();
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        final int scrollLocate;
        final int scrollX = this.mLastExtraOffsetX;
        final int scrollY = this.mLastExtraOffsetY;
        this.mLastExtraOffsetX = this.getExtraOffsetX();
        this.mLastExtraOffsetY = this.getExtraOffsetY();
        scrollLocate = this.computeScrollLocate(scrollX, scrollY);

        if (SCROLL_LOCATE_NONE == scrollLocate) {
            return;
        }
        final int absScroll;
        if (this.canScrollVertically()) {
            absScroll = Math.abs(this.mLastExtraOffsetY);
        } else {
            absScroll = Math.abs(this.mLastExtraOffsetX);
        }

        final int oldRefreshState = this.mRefreshState;
        int refreshState = oldRefreshState;
        if (absScroll > 0
                && REFRESH_STATE_PROGRESS != refreshState
                && REFRESH_STATE_COMPLETE != refreshState) {
            refreshState = REFRESH_STATE_DRAG;

            final int absDecorSize;
            if (SCROLL_LOCATE_TAIL == scrollLocate) {
                absDecorSize = Math.abs(this.mTailDecorSize);
            } else {
                absDecorSize = Math.abs(this.mHeadDecorSize);
            }
            if (absDecorSize > 0 && absScroll >= absDecorSize) {
                refreshState = REFRESH_STATE_READY;
            }
        } else if (absScroll == 0) {
            refreshState = REFRESH_STATE_NONE;
        }
        boolean needsRefreshScrolled;
        needsRefreshScrolled = dx != 0 || dy != 0;

        if (needsRefreshScrolled) {
            needsRefreshScrolled = refreshState != oldRefreshState;
            needsRefreshScrolled |= refreshState != REFRESH_STATE_NONE;
        }

        if (REFRESH_STATE_NONE == refreshState
                || REFRESH_STATE_READY == refreshState) {
            if (needsRefreshScrolled) {
                needsRefreshScrolled = false;
                this.dispatchOnRefreshScrolled(scrollLocate, dx, dy);
            }
        }
        this.setRefreshState(refreshState);

        if (needsRefreshScrolled) {
            this.dispatchOnRefreshScrolled(scrollLocate, dx, dy);
        }
    }

    @Override
    public void onSliverScrollFinished(int scrollType) {
        final int refreshState = this.mRefreshState;
        if (REFRESH_STATE_READY == refreshState
                || REFRESH_STATE_PROGRESS == refreshState) {
            this.mRefreshAction.startRefreshed(0L);
        } else {
            this.mRefreshAction.startCompleted(0L);
        }
    }

    public void onRefreshScrolled(@ScrollLocate int locate,
                                  int dx,
                                  int dy) {
        // no-op
        printLog("onRefreshScrolled [" + this + "]"
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
                + "\n[Locate=" + locate + "]"
                + "\n[Dx=" + dx + "]"
                + "\n[Dy=" + dy + "]"
                + "\n[ExtraOffsetX=" + this.getExtraOffsetX() + "]"
                + "\n[ExtraOffsetY=" + this.getExtraOffsetY() + "]"
        );
    }

    public void onRefreshStateChanged(@ScrollLocate int locate,
                                      @RefreshState int refreshState) {
        // no-op
        printLog("onRefreshStateChanged [" + this + "]"
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
                + "\n[Locate=" + locate + "]"
                + "\n[RefreshState=" + refreshState + "]"
                + "\n[ExtraOffsetX=" + this.getExtraOffsetX() + "]"
                + "\n[ExtraOffsetY=" + this.getExtraOffsetY() + "]"
        );
    }

    private void handleRefreshed(int duration) {
        if (this.disallowRefreshAction()) {
            return;
        }
        int scrollLocate;
        scrollLocate = this.computeScrollLocate(0, 0);
        if (scrollLocate == SCROLL_LOCATE_NONE) {
            scrollLocate = SCROLL_LOCATE_HEAD;
        }
        final int decorSize = this.getDecorSize(scrollLocate);

        if (Math.abs(decorSize) > 0) {
            final int scrollRangeX = this.getScrollRangeX();
            final int scrollRangeY = this.getScrollRangeY();
            int scrollOffsetX = this.getScrollOffsetX();
            int scrollOffsetY = this.getScrollOffsetY();
            scrollOffsetX = Math.min(scrollOffsetX, scrollRangeX);
            scrollOffsetY = Math.min(scrollOffsetY, scrollRangeY);
            scrollOffsetX = Math.max(0, scrollOffsetX);
            scrollOffsetY = Math.max(0, scrollOffsetY);

            if (scrollLocate == SCROLL_LOCATE_TAIL) {
                scrollOffsetX -= scrollRangeX;
                scrollOffsetY -= scrollRangeY;
            }
            int amountX = this.getExtraOffsetX();
            int amountY = this.getExtraOffsetY();

            if (this.canScrollVertically()) {
                amountY -= (decorSize - scrollOffsetY);
            } else {
                amountX -= (decorSize - scrollOffsetX);
            }
            if (this.smoothScrollBy(-amountX, -amountY, duration)) {
                return;
            }
            final int refreshState = this.mRefreshState;

            if (REFRESH_STATE_READY == refreshState
                    || REFRESH_STATE_PROGRESS == refreshState
                    || REFRESH_STATE_COMPLETE == refreshState) {
                this.setRefreshState(REFRESH_STATE_PROGRESS);
            }
        } else {
            this.handleCompleted(duration);
        }
    }

    private void handleCompleted(int duration) {
        if (this.disallowRefreshAction()) {
            return;
        }
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();

        if (this.smoothScrollBy(-scrollX, -scrollY, duration)) {
            return;
        }
        this.setRefreshState(REFRESH_STATE_NONE);
    }

    private int computeScrollLocate(int dx, int dy) {
        final int delta;
        final int scroll;

        if (this.canScrollVertically()) {
            delta = dy;
            scroll = this.getExtraOffsetY();
        } else {
            delta = dx;
            scroll = this.getExtraOffsetX();
        }
        if (scroll == 0) {
            return Integer.compare(delta, 0);
        } else {
            return Integer.compare(scroll, 0);
        }
    }

    private void setRefreshState(int refreshState) {
        if (this.mRefreshState == refreshState) {
            return;
        }
        this.mRefreshState = refreshState;
        int scrollLocate;
        scrollLocate = this.computeScrollLocate(0, 0);
        if (scrollLocate == SCROLL_LOCATE_NONE) {
            scrollLocate = this.mLastScrollLocate;
        }
        if (this.mRefreshState == REFRESH_STATE_PROGRESS) {
            this.dispatchOnRefresh(scrollLocate);
        }
        if (this.mRefreshState != refreshState) {
            return;
        }
        this.dispatchOnRefreshStateChanged(scrollLocate, refreshState);
        this.mLastScrollLocate = scrollLocate;
    }

    private boolean disallowRefreshAction() {
        printLog("disallowRefreshAction [" + this + "]"
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
        return SCROLL_STATE_DRAGGING == this.getScrollState()
                || SliverCompat.SCROLL_AXIS_NONE != this.getSliverScrollAxes()
                || SliverCompat.SCROLL_AXIS_NONE != this.getNestedScrollAxes()
                || SliverCompat.SCROLL_AXIS_NONE != this.getInheritScrollAxes();
    }

    private void dispatchOnRefresh(int locate) {
        if (this.mRefreshCallback != null) {
            this.mRefreshCallback.onRefresh(this, locate);
        } else {
            this.completeRefreshed();
        }
    }

    private void dispatchOnRefreshScrolled(int locate, int dx, int dy) {
        final View child = this.findLoadViewBy(locate);
        if (child instanceof RefreshInterfaces) {
            final RefreshInterfaces<SliverRefreshLayout> interfaces;
            interfaces = (RefreshInterfaces<SliverRefreshLayout>) child;
            interfaces.onRefreshScrolled(this, dx, dy);
        }
        this.onRefreshScrolled(locate, dx, dy);

        for (OnRefreshListener listener : this.mOnRefreshListeners) {
            listener.onRefreshScrolled(this, locate, dx, dy);
        }
    }

    private void dispatchOnRefreshStateChanged(int locate, int refreshState) {
        final View child = this.findLoadViewBy(locate);
        if (child instanceof RefreshInterfaces) {
            final RefreshInterfaces<SliverRefreshLayout> interfaces;
            interfaces = (RefreshInterfaces<SliverRefreshLayout>) child;
            interfaces.onRefreshStateChanged(this, refreshState);
        }
        this.onRefreshStateChanged(locate, refreshState);

        for (OnRefreshListener listener : this.mOnRefreshListeners) {
            listener.onRefreshStateChanged(this, locate, refreshState);
        }
    }

    private static void printLog(@NonNull String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    private static abstract class RefreshAction implements Handler.Callback {
        public static final int OP_REFRESHED = 1;
        public static final int OP_COMPLETED = 2;
        private final Handler handler;

        private RefreshAction() {
            this.handler = new Handler(this);
        }

        @Override
        public boolean handleMessage(@NonNull Message message) {
            final int opAction = message.what;
            final int duration = message.arg1;
            this.stop();
            this.apply(opAction, duration);
            return true;
        }

        public abstract void apply(int opAction, int duration);

        public final void stop() {
            synchronized (SliverRefreshLayout.class) {
                this.handler.removeMessages(OP_REFRESHED);
                this.handler.removeMessages(OP_COMPLETED);
            }
        }

        public final void startRefreshed(long delayMillis) {
            this.startRefreshed(UNDEFINED_DURATION, delayMillis);
        }

        public final void startRefreshed(int duration, long delayMillis) {
            synchronized (SliverRefreshLayout.class) {
                this.stop();

                final Handler handler = this.handler;
                final Message message;
                message = handler.obtainMessage(OP_REFRESHED);
                message.arg1 = duration;
                handler.sendMessageDelayed(message, delayMillis);
            }
        }

        public final void startCompleted(long delayMillis) {
            this.startCompleted(UNDEFINED_DURATION, delayMillis);
        }

        public final void startCompleted(int duration, long delayMillis) {
            synchronized (SliverRefreshLayout.class) {
                this.stop();

                final Handler handler = this.handler;
                final Message message;
                message = handler.obtainMessage(OP_COMPLETED);
                message.arg1 = duration;
                handler.sendMessageDelayed(message, delayMillis);
            }
        }
    }

    public static class LayoutParams extends SliverContainer.LayoutParams {
        private int childIndex;
        private int childLocate = SCROLL_LOCATE_NONE;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull LayoutParams layoutParams) {
            super(layoutParams);
            this.childIndex = layoutParams.childIndex;
            this.childLocate = layoutParams.childLocate;
        }

        public LayoutParams(@NonNull MarginLayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull SliverContainer.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull Context context, @NonNull AttributeSet attrs) {
            super(context, attrs);
        }

        private void layoutLTR(int locate) {
            if (SCROLL_LOCATE_HEAD == locate) {
                this.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            }
            if (SCROLL_LOCATE_TAIL == locate) {
                this.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            }
        }

        private void layoutTTB(int locate) {
            if (SCROLL_LOCATE_HEAD == locate) {
                this.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            }
            if (SCROLL_LOCATE_TAIL == locate) {
                this.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            }
        }
    }

    public static class SavedState extends AbsSavedState {
        @RefreshState
        private int mRefreshState;

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mRefreshState = REFRESH_STATE_NONE;
        }

        public SavedState(@NonNull Parcel source) {
            this(source, null);
        }

        public SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            this.mRefreshState = source.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mRefreshState);
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

    public interface RefreshCallback {

        void onRefresh(@NonNull SliverRefreshLayout sliverRefreshLayout, @ScrollLocate int locate);
    }

    public interface RefreshInterfaces<T extends SliverRefreshLayout> {

        int getRefreshDecorSize(@NonNull T parent);

        void onRefreshScrolled(@NonNull T parent, int dx, int dy);

        void onRefreshStateChanged(@NonNull T parent, @RefreshState int refreshState);
    }

    public interface OnRefreshListener {

        void onRefreshScrolled(@NonNull SliverRefreshLayout sliverRefreshLayout, @ScrollLocate int locate, int dx, int dy);

        void onRefreshStateChanged(@NonNull SliverRefreshLayout sliverRefreshLayout, @ScrollLocate int locate, @RefreshState int refreshState);
    }

    public static abstract class SimpleOnRefreshListener implements OnRefreshListener {
        @Override
        public void onRefreshScrolled(@NonNull SliverRefreshLayout sliverRefreshLayout, @ScrollLocate int locate, int dx, int dy) {
        }

        @Override
        public void onRefreshStateChanged(@NonNull SliverRefreshLayout sliverRefreshLayout, @ScrollLocate int locate, @RefreshState int refreshState) {
        }
    }
}
