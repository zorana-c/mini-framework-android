package com.framework.widget.drawer;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.framework.widget.sliver.SliverCompat;

/**
 * @Author create by Zhengzelong on 2024-01-24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DrawerNestedHelper {
    @NonNull
    protected final DrawerLayout parent;

    public DrawerNestedHelper(@NonNull DrawerLayout parent) {
        this.parent = parent;
    }

    @Nullable
    private View child;
    @Nullable
    private View target;
    @Nullable
    private View drawer;
    private float velocityX;
    private float velocityY;
    private boolean mIsUnableToDrag;

    @CallSuper
    public boolean startNestedScroll(@NonNull View child,
                                     @NonNull View target,
                                     @SliverCompat.ScrollAxis int scrollAxes,
                                     @SliverCompat.ScrollType int scrollType) {
        if (SliverCompat.TYPE_NON_TOUCH == scrollType) {
            // nothing
            return false;
        } else return true;
    }

    @CallSuper
    public void dispatchOnNestedScrollAccepted(@NonNull View child,
                                               @NonNull View target,
                                               @SliverCompat.ScrollAxis int scrollAxes,
                                               @SliverCompat.ScrollType int scrollType) {
        this.child = child;
        this.target = target;
        this.drawer = null;
        this.velocityX = 0;
        this.velocityY = 0;
        this.mIsUnableToDrag = false;
    }

    @CallSuper
    public void dispatchOnNestedPreScroll(@NonNull View target,
                                          int dx,
                                          int dy,
                                          @NonNull int[] consumed,
                                          @SliverCompat.ScrollType int scrollType) {
        final View child = this.child;
        final View drawer = this.drawer;
        if (drawer == null) {
            return;
        }
        final int x = -dx;
        final int y = -dy;
        final int oldX = drawer.getLeft();
        final int oldY = drawer.getTop();
        int newX = oldX + x;
        int newY = oldY + y;
        newX = this.clampViewPositionHorizontal(drawer, newX, x);
        newY = this.clampViewPositionVertical(drawer, newY, y);
        int dxConsumed = newX - oldX;
        int dyConsumed = newY - oldY;
        if (dxConsumed != 0) {
            final DrawerLayout parent = this.parent;
            if (parent.checkDrawerViewEdgeMode(drawer, DrawerLayout.EDGE_LEFT)) {
                if (parent.isContentView(child) && dx < 0) {
                    dxConsumed = 0;
                } else if (parent.isDrawerView(child) && dx > 0) {
                    dxConsumed = 0;
                }
            }
            if (parent.checkDrawerViewEdgeMode(drawer, DrawerLayout.EDGE_RIGHT)) {
                if (parent.isContentView(child) && dx > 0) {
                    dxConsumed = 0;
                } else if (parent.isDrawerView(child) && dx < 0) {
                    dxConsumed = 0;
                }
            }
        }
        if (dyConsumed != 0) {
            final DrawerLayout parent = this.parent;
            if (parent.checkDrawerViewEdgeMode(drawer, DrawerLayout.EDGE_TOP)) {
                if (parent.isContentView(child) && dy < 0) {
                    dyConsumed = 0;
                } else if (parent.isDrawerView(child) && dy > 0) {
                    dyConsumed = 0;
                }
            }
            if (parent.checkDrawerViewEdgeMode(drawer, DrawerLayout.EDGE_BOTTOM)) {
                if (parent.isContentView(child) && dy > 0) {
                    dyConsumed = 0;
                } else if (parent.isDrawerView(child) && dy < 0) {
                    dyConsumed = 0;
                }
            }
        }
        if (dxConsumed != 0 || dyConsumed != 0) {
            this.onViewPositionChanged(drawer, newX, newY, dxConsumed, dyConsumed);
        }
        consumed[0] = -dxConsumed;
        consumed[1] = -dyConsumed;
    }

    @CallSuper
    public void dispatchOnNestedScroll(@NonNull View target,
                                       int dxConsumed,
                                       int dyConsumed,
                                       int dxUnconsumed,
                                       int dyUnconsumed,
                                       @NonNull int[] consumed,
                                       @SliverCompat.ScrollType int scrollType) {
        this.tryCaptureViewForDrag(this.child, dxUnconsumed, dyUnconsumed);
        final View drawer = this.drawer;
        if (drawer == null) {
            return;
        }
        final int x = -dxUnconsumed;
        final int y = -dyUnconsumed;
        final int oldX = drawer.getLeft();
        final int oldY = drawer.getTop();
        int newX = oldX + x;
        int newY = oldY + y;
        newX = this.clampViewPositionHorizontal(drawer, newX, x);
        newY = this.clampViewPositionVertical(drawer, newY, y);
        final int dx = newX - oldX;
        final int dy = newY - oldY;
        if (dx != 0 || dy != 0) {
            this.onViewPositionChanged(drawer, newX, newY, dx, dy);
        }
        consumed[0] = -dx;
        consumed[1] = -dy;
    }

    @CallSuper
    public boolean dispatchOnNestedPreFling(@NonNull View target,
                                            float velocityX,
                                            float velocityY) {
        final View drawer = this.drawer;
        if (drawer == null) {
            return false;
        }
        final float screen = Math.abs(this.parent.getDrawerViewScreen(drawer));
        if (screen <= 0.f || screen >= 1.f) {
            return false;
        }
        this.velocityX = -velocityX;
        this.velocityY = -velocityY;
        return true;
    }

    @CallSuper
    public boolean dispatchOnNestedFling(@NonNull View target,
                                         float velocityX,
                                         float velocityY, boolean consumed) {
        // nothing
        return false;
    }

    @CallSuper
    public void dispatchOnStopNestedScroll(@NonNull View target,
                                           @SliverCompat.ScrollType int scrollType) {
        final View drawer = this.drawer;
        if (drawer != null) {
            this.onViewReleased(drawer, this.velocityX, this.velocityY);
            final DrawerLayout parent = this.parent;
            final int drawerViewState = parent.getDrawerViewState();
            if (drawerViewState == DrawerLayout.STATE_DRAGGING) {
                parent.setDrawerViewState(drawer, DrawerLayout.STATE_IDLE);
            }
            this.child = null;
            this.target = null;
            this.drawer = null;
            this.velocityX = 0;
            this.velocityY = 0;
            this.mIsUnableToDrag = false;
        }
    }

    public void stopTargetNestedScroll(@DrawerLayout.EdgeMode int edgeMode) {
        final View child = this.child;
        if (child == null) {
            return;
        }
        final View target = this.target;
        if (target == null) {
            return;
        }
        final View drawer = this.drawer;
        if (drawer == null) {
            return;
        }
        final DrawerLayout parent = this.parent;
        if (!parent.checkDrawerViewEdgeMode(drawer, edgeMode)) {
            return;
        }
        final int lockMode;
        if (parent.isContentView(child)) {
            lockMode = DrawerLayout.LOCK_LOCKED_OPENED;
        } else {
            lockMode = DrawerLayout.LOCK_LOCKED_CLOSED;
        }
        if (parent.checkDrawerViewNestedLockMode(drawer, lockMode)) {
            ViewCompat.stopNestedScroll(target);
        }
    }

    private void tryCaptureViewForDrag(@Nullable View child, int dx, int dy) {
        if (this.mIsUnableToDrag
                || child == null
                || (dx == 0 && dy == 0)) {
            return;
        }
        View drawer = this.drawer;
        if (drawer != null) {
            return;
        }
        drawer = this.tryCaptureView(child, dx, dy);
        if (drawer == null) {
            this.mIsUnableToDrag = true;
            return;
        }
        final DrawerLayout parent = this.parent;
        final int lockMode;
        if (parent.isContentView(child)) {
            lockMode = DrawerLayout.LOCK_LOCKED_OPENED;
        } else {
            lockMode = DrawerLayout.LOCK_LOCKED_CLOSED;
        }
        if (parent.checkDrawerViewNestedLockMode(drawer, lockMode)) {
            this.mIsUnableToDrag = true;
            return;
        }
        this.drawer = drawer;
        final int drawerViewState = parent.getDrawerViewState();
        if (drawerViewState == DrawerLayout.STATE_IDLE) {
            parent.setDrawerViewState(drawer, DrawerLayout.STATE_DRAGGING);
        }
    }

    @Nullable
    protected View tryCaptureView(@NonNull View child, int dx, int dy) {
        final DrawerLayout parent = this.parent;
        View drawer = null;
        if (parent.isDrawerView(child)) {
            if (dx != 0) {
                if (parent.checkDrawerViewHorOri(child)) {
                    drawer = child;
                }
            }
            if (dy != 0) {
                if (!parent.checkDrawerViewHorOri(child)) {
                    drawer = child;
                }
            }
        } else {
            if (dx != 0) {
                if (dx < 0) {
                    drawer = parent.findDrawerByEdgeMode(DrawerLayout.EDGE_LEFT);
                } else {
                    drawer = parent.findDrawerByEdgeMode(DrawerLayout.EDGE_RIGHT);
                }
            }
            if (dy != 0) {
                if (dy < 0) {
                    drawer = parent.findDrawerByEdgeMode(DrawerLayout.EDGE_TOP);
                } else {
                    drawer = parent.findDrawerByEdgeMode(DrawerLayout.EDGE_BOTTOM);
                }
            }
        }
        if (drawer == null) {
            return null;
        }
        if (parent.checkDrawerViewLockMode(drawer, DrawerLayout.LOCK_UNLOCKED)) {
            return drawer;
        }
        return null;
    }

    protected int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
        final DrawerLayout parent = this.parent;
        if (parent.checkDrawerViewHorOri(child)) {
            final int width = parent.getWidth();
            final int childWidth = child.getWidth();

            if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_LEFT)) {
                return Math.max(-childWidth, Math.min(left, 0));
            } else {
                return Math.max(width - childWidth, Math.min(left, width));
            }
        } else return child.getLeft();
    }

    protected int clampViewPositionVertical(@NonNull View child, int top, int dy) {
        final DrawerLayout parent = this.parent;
        if (parent.checkDrawerViewHorOri(child)) {
            return child.getTop();
        } else {
            final int height = parent.getHeight();
            final int childHeight = child.getHeight();

            if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_TOP)) {
                return Math.max(-childHeight, Math.min(top, 0));
            } else {
                return Math.max(height - childHeight, Math.min(top, height));
            }
        }
    }

    protected void onViewPositionChanged(@NonNull View child, int left, int top, int dx, int dy) {
        final DrawerLayout parent = this.parent;
        final int width = parent.getWidth();
        final int height = parent.getHeight();
        final int childWidth = child.getWidth();
        final int childHeight = child.getHeight();
        float screen = 0.f;
        if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_LEFT)) {
            screen = (float) (childWidth + left) / childWidth;
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_RIGHT)) {
            screen = (float) (width - left) / childWidth;
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_TOP)) {
            screen = (float) (childHeight + top) / childHeight;
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_BOTTOM)) {
            screen = (float) (height - top) / childHeight;
        }
        parent.moveDrawerToScreen(child, screen);
        final int newVisibility = screen == 0 ? View.INVISIBLE : View.VISIBLE;
        if (child.getVisibility() != newVisibility) {
            child.setVisibility(newVisibility);
        }
        parent.invalidate();
    }

    protected void onViewReleased(@NonNull View child, float velocityX, float velocityY) {
        final DrawerLayout parent = this.parent;
        final float screen = parent.getDrawerViewScreen(child);
        final int width = parent.getWidth();
        final int height = parent.getHeight();
        final int childWidth = child.getWidth();
        final int childHeight = child.getHeight();
        boolean openDrawer = false;
        if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_LEFT)) {
            openDrawer = velocityX > 0 || (velocityX == 0 && screen > 0.5f);
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_RIGHT)) {
            openDrawer = velocityX < 0 || (velocityX == 0 && screen > 0.5f);
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_TOP)) {
            openDrawer = velocityY > 0 || (velocityY == 0 && screen > 0.5f);
        } else if (parent.checkDrawerViewEdgeMode(child, DrawerLayout.EDGE_BOTTOM)) {
            openDrawer = velocityY < 0 || (velocityY == 0 && screen > 0.5f);
        }
        if (openDrawer) {
            parent.openDrawer(child);
        } else {
            parent.closeDrawer(child);
        }
        parent.invalidate();
    }
}
