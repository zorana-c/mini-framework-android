package com.framework.widget.sliver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.framework.widget.R;

/**
 * @Author create by Zhengzelong on 2023-06-28
 * @Email : 171905184@qq.com
 * @Description : 兼容 ViewPager2 (不可被继承原因)
 * <p>
 * {@link androidx.viewpager2.widget.ViewPager2}
 */
public class SliverViewPager2 extends FrameLayout implements
        InheritScroll,
        InheritScrollProvider {
    private ViewPager2 mViewPager;
    private RecyclerView mRecyclerView;

    public SliverViewPager2(@NonNull Context context) {
        this(context, null);
    }

    public SliverViewPager2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverViewPager2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context, attrs);
        this.setInheritScrollingEnabled(true);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        this.mViewPager = new ViewPager2(context);
        this.mViewPager.setId(ViewCompat.generateViewId());
        this.mViewPager.setLayoutParams(this.generateDefaultLayoutParams());
        this.mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int scrollState) {
                stopTargetInheritScroll();
            }
        });
        final View target = this.mViewPager.getChildAt(0);
        if (target instanceof RecyclerView) {
            this.mRecyclerView = (RecyclerView) target;
        }
        this.setAttributeData(context, attrs);
        this.attachViewToParent(this.mViewPager, 0, this.mViewPager.getLayoutParams());
    }

    @SuppressLint("CustomViewStyleable")
    private void setAttributeData(@NonNull Context context, @Nullable AttributeSet attrs) {
        final TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ViewPager2);
        if (Build.VERSION.SDK_INT >= 29) {
            this.saveAttributeDataForStyleable(context, R.styleable.ViewPager2, attrs, t, 0, 0);
        }
        try {
            this.setOrientation(t.getInt(R.styleable.ViewPager2_android_orientation, ViewPager2.ORIENTATION_HORIZONTAL));
        } finally {
            t.recycle();
        }
    }

    @NonNull
    public final ViewPager2 getViewPager() {
        return this.mViewPager;
    }

    @NonNull
    public final RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    @Nullable
    public RecyclerView.Adapter<?> getAdapter() {
        return this.mViewPager.getAdapter();
    }

    public void setAdapter(@Nullable RecyclerView.Adapter<?> adapter) {
        this.mViewPager.setAdapter(adapter);
    }

    public void registerOnPageChangeCallback(@NonNull ViewPager2.OnPageChangeCallback callback) {
        this.mViewPager.registerOnPageChangeCallback(callback);
    }

    public void unregisterOnPageChangeCallback(@NonNull ViewPager2.OnPageChangeCallback callback) {
        this.mViewPager.unregisterOnPageChangeCallback(callback);
    }

    public int getCurrentItem() {
        return this.mViewPager.getCurrentItem();
    }

    public void setCurrentItem(int item) {
        this.mViewPager.setCurrentItem(item);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        this.mViewPager.setCurrentItem(item, smoothScroll);
    }

    @ViewPager2.ScrollState
    public int getScrollState() {
        return this.mViewPager.getScrollState();
    }

    @ViewPager2.Orientation
    public int getOrientation() {
        return this.mViewPager.getOrientation();
    }

    public void setOrientation(@ViewPager2.Orientation int orientation) {
        this.mViewPager.setOrientation(orientation);
    }

    @ViewPager2.OffscreenPageLimit
    public int getOffscreenPageLimit() {
        return this.mViewPager.getOffscreenPageLimit();
    }

    public void setOffscreenPageLimit(@ViewPager2.OffscreenPageLimit int limit) {
        this.mViewPager.setOffscreenPageLimit(limit);
    }

    public boolean isUserScrollEnabled() {
        return this.mViewPager.isUserInputEnabled();
    }

    public void setUserScrollEnabled(boolean enabled) {
        this.mViewPager.setUserInputEnabled(enabled);
    }

    public void setPageTransformer(@Nullable ViewPager2.PageTransformer transformer) {
        this.mViewPager.setPageTransformer(transformer);
    }

    @Nullable
    public View findCurrentView() {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return null;
        }
        final int position = this.getCurrentItem();
        if (RecyclerView.NO_POSITION == position) {
            return null;
        }
        final RecyclerView.ViewHolder holder;
        holder = recyclerView.findViewHolderForLayoutPosition(position);
        if (holder == null) {
            return null;
        }
        return holder.itemView;
    }

    // ########## InheritScroll ##########

    private ScrollingAxesHelper mScrollingAxesHelper;
    private int mInheritScrollType;
    private ViewParent mInheritScrollParent;
    private ViewParent mInheritScrollTarget;

    @Override
    public boolean onStartInheritScroll(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        boolean needsInheritScroll = false;

        if ((SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0) {
            needsInheritScroll = true;
        }
        if ((SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0) {
            needsInheritScroll = true;
        }
        return needsInheritScroll;
    }

    @Override
    public void onInheritScrollAccepted(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        this.getScrollingAxesHelper().save(scrollType, scrollAxes);
        this.mInheritScrollType = scrollType;
        this.mInheritScrollParent = parent;
        this.mInheritScrollTarget = target;

        final View currentView = this.findCurrentView();
        if (currentView == null) {
            return;
        }
        this.getInheritScrollHelper().startInheritScroll(currentView, scrollAxes, scrollType);
    }

    @Override
    public void onInheritPreScroll(@NonNull ViewParent target, int dx, int dy, @NonNull int[] consumed, int scrollType) {
        this.dispatchInheritPreScroll(dx, dy, consumed, null, scrollType);
    }

    @Override
    public void onInheritScroll(@NonNull ViewParent target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, null, scrollType);
    }

    @Override
    public boolean onInheritPreFling(@NonNull ViewParent target, float velocityX, float velocityY) {
        return this.dispatchInheritPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onInheritFling(@NonNull ViewParent target, float velocityX, float velocityY, boolean consumed) {
        return this.dispatchInheritFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopInheritScroll(@NonNull ViewParent target, int scrollType) {
        this.getScrollingAxesHelper().clear(scrollType);
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
        // no-op
    }

    @Override
    public int getInheritScrollAxes() {
        return this.getScrollingAxesHelper().getScrollAxes();
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

    public void stopTargetInheritScroll(int scrollType) {
        final View inheritScrollTarget = (View) this.mInheritScrollTarget;
        if (inheritScrollTarget != null) {
            SliverCompat.stopInheritScroll(inheritScrollTarget, scrollType);
        }
    }

    @NonNull
    public ScrollingAxesHelper getScrollingAxesHelper() {
        if (this.mScrollingAxesHelper == null) {
            this.mScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mScrollingAxesHelper;
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
            this.mInheritScrollHelper = new InheritScrollHelper(this.mRecyclerView);
        }
        return this.mInheritScrollHelper;
    }
}
