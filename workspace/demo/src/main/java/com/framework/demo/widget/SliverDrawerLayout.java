package com.framework.demo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

import com.framework.widget.compat.UIViewCompat;
import com.framework.widget.sliver.SliverCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-08-04
 * @Email : 171905184@qq.com
 * @Description : 滑动抽屉效果
 */
public class SliverDrawerLayout extends CustomSliverScrollView {
    public static final int TOP = EdgeGravity.TOP;
    public static final int LEFT = EdgeGravity.LEFT;
    public static final int RIGHT = EdgeGravity.RIGHT;
    public static final int BOTTOM = EdgeGravity.BOTTOM;
    public static final int NO_GRAVITY = EdgeGravity.NO_GRAVITY;

    @SuppressLint("RtlHardcoded")
    @IntDef(flag = true, value = {
            EdgeGravity.TOP,
            EdgeGravity.LEFT,
            EdgeGravity.RIGHT,
            EdgeGravity.BOTTOM,
            EdgeGravity.NO_GRAVITY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeGravity {
        int TOP = Gravity.TOP;
        int LEFT = Gravity.LEFT;
        int RIGHT = Gravity.RIGHT;
        int BOTTOM = Gravity.BOTTOM;
        int NO_GRAVITY = Gravity.NO_GRAVITY;
    }

    @NonNull
    private final SparseArray<View> mDrawerChildrenSpa = new SparseArray<>();
    @NonNull
    private final ArrayList<OnDrawerChangedListener> mOnDrawerChangedListeners = new ArrayList<>();

    public SliverDrawerLayout(@NonNull Context context) {
        this(context, null);
    }

    public SliverDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnDrawerChangedListener(@NonNull OnDrawerChangedListener listener) {
        this.mOnDrawerChangedListeners.add(listener);
    }

    public void removeOnDrawerChangedListener(@NonNull OnDrawerChangedListener listener) {
        this.mOnDrawerChangedListeners.remove(listener);
    }

    public void clearOnDrawerChangedListeners() {
        this.mOnDrawerChangedListeners.clear();
    }

    // ############# SliverScroll #############

    private int mSliverVelocityX;
    private int mSliverVelocityY;
    private int mEdgeGravityFlags;

    @Override
    public void onSliverScrollAccepted(int scrollAxes,
                                       int scrollType) {
        super.onSliverScrollAccepted(scrollAxes, scrollType);
        this.ensureDrawerChildren(scrollAxes);
    }

    @Override
    public void onSliverPreScroll(int dx, int dy,
                                  @NonNull int[] consumed,
                                  int scrollType) {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        super.onSliverPreScroll(dx, dy, consumed, scrollType);
        final int dxUnconsumed = this.getExtraOffsetX() - scrollX;
        final int dyUnconsumed = this.getExtraOffsetY() - scrollY;
        this.scrollDrawerBy(dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onBounceScroll(int dxConsumed,
                               int dyConsumed,
                               int dxUnconsumed,
                               int dyUnconsumed,
                               @NonNull int[] consumed,
                               int scrollType) {
        this.scrollBy(dxUnconsumed, dyUnconsumed);
        consumed[0] += dxUnconsumed;
        consumed[1] += dyUnconsumed;
        this.scrollDrawerBy(dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onSliverPreFling(float velocityX,
                                    float velocityY) {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        if (scrollX != 0 || scrollY != 0) {
            this.mSliverVelocityX = (int) velocityX;
            this.mSliverVelocityY = (int) velocityY;
            return true;
        }
        return super.onSliverPreFling(velocityX, velocityY);
    }

    @Override
    public void onSliverScrollFinished(int scrollType) {
        final int velocityX = this.mSliverVelocityX;
        final int velocityY = this.mSliverVelocityY;
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        final float sxs = Math.signum(scrollX);
        final float sys = Math.signum(scrollY);
        float wr = 0.5f;
        float hr = 0.5f;
        this.mSliverVelocityX = 0;
        this.mSliverVelocityY = 0;

        if (velocityX != 0 || velocityY != 0) {
            final float vxs = Math.signum(velocityX);
            final float vys = Math.signum(velocityY);
            boolean restore = false;
            restore |= (scrollX != 0 && sxs == vxs);
            restore |= (scrollY != 0 && sys == vys);

            if (restore) {
                wr = .0f;
                hr = .0f;
            } else {
                wr = 1.0f;
                hr = 1.0f;
            }
        }

        final int cw = this.getTargetFinishWidth(wr);
        final int ch = this.getTargetFinishHeight(hr);
        final int dx = scrollX - Math.round(cw * sxs);
        final int dy = scrollY - Math.round(ch * sys);

        if (cw > 0) {
            this.mEdgeGravityFlags |= sxs < 0
                    ? EdgeGravity.LEFT
                    : EdgeGravity.RIGHT;
        }
        if (ch > 0) {
            this.mEdgeGravityFlags |= sys < 0
                    ? EdgeGravity.TOP
                    : EdgeGravity.BOTTOM;
        }

        if (this.smoothScrollBy(-dx, -dy)) {
            return;
        }
        this.snapToTargetExistingState();
    }

    public boolean closeDrawer() {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        return this.smoothScrollBy(-scrollX, -scrollY);
    }

    public boolean closeDrawerNow() {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        this.scrollBy(-scrollX, -scrollY);
        return scrollX != 0 || scrollY != 0;
    }

    public boolean isOpen() {
        return this.isOpen(this.mEdgeGravityFlags);
    }

    public boolean isOpen(@EdgeGravity int edgeGravity) {
        return (this.mEdgeGravityFlags & edgeGravity) != 0;
    }

    private void snapToTargetExistingState() {
        final int cw = this.getTargetFinishWidth(1.0f);
        final int ch = this.getTargetFinishHeight(1.0f);
        final int edgeGravityFlags = this.mEdgeGravityFlags;

        if (cw <= 0 && ch <= 0) {
            final int scrollX = this.getExtraOffsetX();
            final int scrollY = this.getExtraOffsetY();

            if (this.smoothScrollBy(-scrollX, -scrollY)) {
                return;
            }
            this.mEdgeGravityFlags = EdgeGravity.NO_GRAVITY;
        }
        if (EdgeGravity.NO_GRAVITY == edgeGravityFlags) {
            return;
        }
        this.dispatchOnDrawerChanged(edgeGravityFlags);
    }

    private int getTargetFinishWidth(float r) {
        final int sx = this.getExtraOffsetX();
        final int eg = Math.signum(sx) < 0
                ? EdgeGravity.LEFT
                : EdgeGravity.RIGHT;
        final View child = this.findDrawerChildBy(eg);
        if (child == null) {
            return 0;
        }
        final int cw;
        cw = UIViewCompat.getMeasuredWidth(child);
        if (cw <= 0) {
            return 0;
        }
        return Math.abs(sx) >= Math.round(cw * r) ? cw : 0;
    }

    private int getTargetFinishHeight(float r) {
        final int sy = this.getExtraOffsetY();
        final int eg = Math.signum(sy) < 0
                ? EdgeGravity.TOP
                : EdgeGravity.BOTTOM;
        final View child = this.findDrawerChildBy(eg);
        if (child == null) {
            return 0;
        }
        final int ch;
        ch = UIViewCompat.getMeasuredHeight(child);
        if (ch <= 0) {
            return 0;
        }
        return Math.abs(sy) >= Math.round(ch * r) ? ch : 0;
    }

    private void ensureDrawerChildren(int scrollAxes) {
        final SparseArray<View> drawerChildrenSpa;
        drawerChildrenSpa = this.mDrawerChildrenSpa;
        drawerChildrenSpa.clear();
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            final int eg;
            eg = this.getChildEdgeGravity(child, scrollAxes);
            if (eg == EdgeGravity.NO_GRAVITY) {
                continue;
            }
            drawerChildrenSpa.put(eg, child);
        }
    }

    @EdgeGravity
    @SuppressLint("RtlHardcoded")
    private int getChildEdgeGravity(@NonNull View child,
                                    int scrollAxes) {
        final LayoutParams lp;
        lp = (LayoutParams) child.getLayoutParams();
        if (lp.scrolling) {
            return EdgeGravity.NO_GRAVITY;
        }
        final int gra = lp.gravity;
        final int dir = ViewCompat.getLayoutDirection(this);
        final int absGravity;
        final int verGravity;
        final int horGravity;
        absGravity = GravityCompat.getAbsoluteGravity(gra, dir);
        verGravity = absGravity & Gravity.VERTICAL_GRAVITY_MASK;
        horGravity = absGravity & Gravity.HORIZONTAL_GRAVITY_MASK;

        final boolean v;
        v = (SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0;
        final boolean h;
        h = (SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0;

        if (v) {
            if (verGravity == Gravity.TOP) {
                return EdgeGravity.TOP;
            }
            if (verGravity == Gravity.BOTTOM) {
                return EdgeGravity.BOTTOM;
            }
        }
        if (h) {
            if (horGravity == Gravity.LEFT) {
                return EdgeGravity.LEFT;
            }
            if (horGravity == Gravity.RIGHT) {
                return EdgeGravity.RIGHT;
            }
        }
        return EdgeGravity.NO_GRAVITY;
    }

    @Nullable
    private View findDrawerChildBy(int edgeGravity) {
        return this.mDrawerChildrenSpa.get(edgeGravity);
    }

    private void scrollDrawerBy(int dx, int dy) {
        final SparseArray<View> drawerChildrenSpa;
        drawerChildrenSpa = this.mDrawerChildrenSpa;
        final int N = drawerChildrenSpa.size();
        for (int index = 0; index < N; index++) {
            final View child;
            child = drawerChildrenSpa.valueAt(index);
            if (child == null) {
                continue;
            }
            final int seg = this.getScrollGravity();
            final int veg = drawerChildrenSpa.keyAt(index);
            if (seg == veg) {
                this.offsetDrawerChild(child, seg);
            } else {
                this.resetDrawerChild(child, seg);
            }
        }
    }

    @EdgeGravity
    private int getScrollGravity() {
        final int scrollX = this.getExtraOffsetX();
        final int scrollY = this.getExtraOffsetY();
        if (scrollX == 0 && scrollY == 0) {
            return EdgeGravity.NO_GRAVITY;
        }
        if (this.canScrollHorizontally()) {
            return scrollX < 0
                    ? EdgeGravity.LEFT
                    : EdgeGravity.RIGHT;
        } else {
            return scrollY < 0
                    ? EdgeGravity.TOP
                    : EdgeGravity.BOTTOM;
        }
    }

    private void resetDrawerChild(@NonNull View child,
                                  int edgeGravity) {
        child.setTranslationX(0);
        child.setTranslationY(0);
        child.setVisibility(View.GONE);
    }

    private void offsetDrawerChild(@NonNull View child,
                                   int edgeGravity) {
        final int scrollLocate;
        scrollLocate = EdgeGravity.LEFT == edgeGravity
                || EdgeGravity.TOP == edgeGravity ? -1 : 1;
        final int cw = this.getDecoratedMeasuredWidth(child);
        final int ch = this.getDecoratedMeasuredHeight(child);
        final float scrollX = this.getExtraOffsetX();
        final float scrollY = this.getExtraOffsetY();
        float x = -scrollX * scrollLocate + cw;
        float y = -scrollY * scrollLocate + ch;
        x = Math.round(Math.min(0, x) * scrollLocate);
        y = Math.round(Math.min(0, y) * scrollLocate);

        if (this.canScrollVertically()) {
            child.setTranslationY(y);
        } else {
            child.setTranslationX(x);
        }
        child.setVisibility(View.VISIBLE);
    }

    private void dispatchOnDrawerChanged(int edgeGravityFlags) {
        for (OnDrawerChangedListener listener : this.mOnDrawerChangedListeners) {
            listener.onDrawerChanged(this, edgeGravityFlags);
        }
    }

    public interface OnDrawerChangedListener {

        /**
         * @param edgeGravityFlags {@link SliverDrawerLayout#isOpen(int)}
         */
        void onDrawerChanged(@NonNull SliverDrawerLayout container, @EdgeGravity int edgeGravityFlags);
    }
}
