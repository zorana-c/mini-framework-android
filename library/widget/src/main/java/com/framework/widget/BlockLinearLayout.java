package com.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2021/4/30
 * @Email : 171905184@qq.com
 * @Description :处理手势(仅此对一个视图进行手势)
 */
public class BlockLinearLayout extends LinearLayoutCompat {
    private final ViewElevationComparator mComparator = new ViewElevationComparator();
    private final ArrayList<View> mTempDecoratedChildren = new ArrayList<>(1);
    private final float[] mTempTouchPoint = new float[2];

    private View mTouchDecoratedChild;

    public BlockLinearLayout(@NonNull Context context) {
        this(context, null);
    }

    public BlockLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlockLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (MotionEvent.ACTION_DOWN == actionMasked) {
            this.resetTouchIntercept();
            this.cancelMotionEvent(event);
        }
        final boolean intercepted = this.performIntercept(event);
        if (MotionEvent.ACTION_UP == actionMasked
                || MotionEvent.ACTION_CANCEL == actionMasked) {
            this.resetTouchIntercept();
        }
        return intercepted || super.onInterceptTouchEvent(event);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final boolean handled;
        boolean intercepted = false;
        MotionEvent motionEvent = null;

        final int actionMasked = event.getActionMasked();
        if (MotionEvent.ACTION_DOWN == actionMasked) {
            this.cancelMotionEvent(event);
        }
        if (this.mTouchDecoratedChild == null) {
            intercepted = this.performIntercept(event);
        }
        if (this.mTouchDecoratedChild == null) {
            handled = super.onTouchEvent(event);
        } else {
            handled = this.dispatchTransformedTouchEvent(event, this.mTouchDecoratedChild);

            if (intercepted) {
                final long nowTime = SystemClock.uptimeMillis();
                motionEvent = MotionEvent.obtain(nowTime, nowTime,
                        MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
                super.onTouchEvent(motionEvent);
            }
        }
        if (motionEvent != null) {
            motionEvent.recycle();
        }
        if (MotionEvent.ACTION_UP == actionMasked
                || MotionEvent.ACTION_CANCEL == actionMasked) {
            this.resetTouchIntercept();
            this.cancelMotionEvent(event);
        }
        return handled;
    }

    private void cancelMotionEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (MotionEvent.ACTION_DOWN == actionMasked) {
            final long nowTime = SystemClock.uptimeMillis();
            final MotionEvent motionEvent = MotionEvent.obtain(nowTime, nowTime,
                    MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
            for (int index = 0; index < this.getChildCount(); index++) {
                final View child = this.getChildAt(index);
                if (child == null) {
                    continue;
                }
                this.dispatchTransformedTouchEvent(motionEvent, child);
            }
            motionEvent.recycle();
        }
    }

    private boolean performIntercept(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        boolean intercepted = false;
        MotionEvent motionEvent = null;

        final List<View> decoratedChildren = this.mTempDecoratedChildren;
        this.getTopSortedDecoratedChildren(decoratedChildren);

        for (int index = 0; index < decoratedChildren.size(); index++) {
            final View decoratedChild = decoratedChildren.get(index);
            if (intercepted && MotionEvent.ACTION_DOWN != actionMasked) {
                if (motionEvent == null) {
                    final long nowTime = SystemClock.uptimeMillis();
                    motionEvent = MotionEvent.obtain(nowTime, nowTime,
                            MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
                }
                this.dispatchTransformedTouchEvent(motionEvent, decoratedChild);
                continue;
            }
            if (!intercepted) {
                final int actionIndex = event.getActionIndex();
                final float x = event.getX(actionIndex);
                final float y = event.getY(actionIndex);
                if (this.isTransformedTouchPointInView(decoratedChild, x, y)) {
                    intercepted = this.dispatchTransformedTouchEvent(event, decoratedChild);
                }
                if (intercepted) {
                    this.mTouchDecoratedChild = decoratedChild;
                }
            }
        }
        if (motionEvent != null) {
            motionEvent.recycle();
        }
        // Done.
        decoratedChildren.clear();
        return intercepted;
    }

    private void resetTouchIntercept() {
        this.mTouchDecoratedChild = null;
    }

    private boolean dispatchTransformedTouchEvent(@NonNull MotionEvent event,
                                                  @Nullable View decoratedChild) {
        boolean handled;
        if (MotionEvent.ACTION_CANCEL == event.getAction()) {
            if (decoratedChild == null) {
                handled = super.dispatchTouchEvent(event);
            } else {
                handled = decoratedChild.dispatchTouchEvent(event);
            }
            return handled;
        }

        final int pointerIdBits = this.getPointerIdBits(event);
        if (pointerIdBits == 0) {
            return false;
        }

        final MotionEvent transformedEvent = MotionEvent.obtain(event);
        if (decoratedChild == null) {
            handled = super.dispatchTouchEvent(transformedEvent);
        } else {
            final float offsetX = this.getScrollX() - decoratedChild.getLeft();
            final float offsetY = this.getScrollY() - decoratedChild.getTop();
            transformedEvent.offsetLocation(offsetX, offsetY);
            handled = decoratedChild.dispatchTouchEvent(transformedEvent);
        }
        // Done.
        transformedEvent.recycle();
        return handled;
    }

    private int getPointerIdBits(@NonNull MotionEvent event) {
        int pointerIdBits = 0;
        for (int index = 0; index < event.getPointerCount(); index++) {
            pointerIdBits |= 1 << event.getPointerId(index);
        }
        return pointerIdBits;
    }

    private boolean isTransformedTouchPointInView(@NonNull View decoratedChild, float x, float y) {
        final float[] tempPoint = this.mTempTouchPoint;
        tempPoint[0] = x + this.getScrollX() - decoratedChild.getLeft();
        tempPoint[1] = y + this.getScrollY() - decoratedChild.getTop();
        return this.pointInView(decoratedChild, tempPoint[0], tempPoint[1]);
    }

    private boolean pointInView(@NonNull View decoratedChild, float localX, float localY) {
        return localX >= 0 && localY >= 0 &&
                localX < (decoratedChild.getRight() - decoratedChild.getLeft()) &&
                localY < (decoratedChild.getBottom() - decoratedChild.getTop());
    }

    private void getTopSortedDecoratedChildren(@NonNull List<View> outDecoratedChildren) {
        outDecoratedChildren.clear();
        final int childCount = this.getChildCount();
        for (int index = childCount - 1; index >= 0; index--) {
            final int childDrawingOrder = this.isChildrenDrawingOrderEnabled()
                    ? this.getChildDrawingOrder(childCount, index)
                    : index;
            outDecoratedChildren.add(this.getChildAt(childDrawingOrder));
        }
        Collections.sort(outDecoratedChildren, this.mComparator);
    }

    private static class ViewElevationComparator implements Comparator<View> {
        @Override
        public int compare(@NonNull View lhs, @NonNull View rhs) {
            return Float.compare(ViewCompat.getZ(rhs), ViewCompat.getZ(lhs));
        }
    }
}
