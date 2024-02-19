package com.framework.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.demo.R;
import com.framework.widget.compat.UIViewCompat;
import com.framework.widget.sliver.SliverContainer;

import java.lang.ref.WeakReference;

/**
 * @Author create by Zhengzelong on 2023-07-06
 * @Email : 171905184@qq.com
 * @Description : 滑动折叠效果
 */
public class CollapsingBarLayout extends FrameLayout {
    @Nullable
    private SparseArray<View> anchorViewSpa;
    @Nullable
    private ComponentListener componentListener;

    @IdRes
    private int headAnchorId;
    @IdRes
    private int tailAnchorId;

    public CollapsingBarLayout(@NonNull Context context) {
        this(context, null);
    }

    public CollapsingBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray ta;
        ta = context.obtainStyledAttributes(attrs, R.styleable.CollapsingBarLayout);
        try {
            this.headAnchorId = ta.getResourceId(R.styleable.CollapsingBarLayout_headAnchorId, 0);
            this.tailAnchorId = ta.getResourceId(R.styleable.CollapsingBarLayout_tailAnchorId, 0);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final SliverContainer sc = this.findParentBy(this);
        if (sc != null) {
            if (this.componentListener == null) {
                this.componentListener = new ComponentListener();
            }
            this.componentListener.setSliverContainer(sc);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.componentListener != null) {
            this.componentListener.setSliverContainer(null);
        }
    }

    public void setHeadAnchorId(@IdRes int id) {
        this.headAnchorId = id;
    }

    public void setTailAnchorId(@IdRes int id) {
        this.tailAnchorId = id;
    }

    private void offsetSetup(@NonNull SliverContainer sliverContainer) {
        final View hv = this.findAnchorViewBy(this.headAnchorId);
        final View tv = this.findAnchorViewBy(this.tailAnchorId);
        if (hv == null && tv == null) {
            return;
        }
        final int hvw = UIViewCompat.getMeasuredWidth(hv);
        final int tvw = UIViewCompat.getMeasuredWidth(tv);
        final int r = sliverContainer.computeVerticalScrollRange();
        final int e = sliverContainer.computeVerticalScrollExtent();
        final int o = sliverContainer.computeVerticalScrollOffset();
        final float s = (float) Math.abs(o) / Math.max(1.f, (r - e));

        final View child = this.getChildAt(0);
        if (child == null) {
            return;
        }
        final LayoutParams lp;
        lp = (LayoutParams) child.getLayoutParams();

        int cw = this.getWidth();
        cw -= this.getPaddingLeft();
        cw -= this.getPaddingRight();
        cw -= lp.leftMargin;
        cw -= lp.rightMargin;
        int cg = Gravity.NO_GRAVITY;

        if (hv != null) {
            cw -= (int) ((hvw - lp.leftMargin) * s);
            cg = Gravity.END | Gravity.CENTER_VERTICAL;
        }
        if (tv != null) {
            cw -= (int) ((tvw - lp.rightMargin) * s);
            cg = Gravity.START | Gravity.CENTER_VERTICAL;
        }

        if (hv != null && tv != null) {
            cg = Gravity.CENTER;
        }

        if (lp.width != cw) {
            lp.width = cw;
            lp.gravity = cg;
            child.requestLayout();
        }
    }

    @Nullable
    private View findAnchorViewBy(@IdRes int id) {
        @Nullable
        View av = null;
        if (this.anchorViewSpa != null) {
            av = this.anchorViewSpa.get(id);
        }
        if (av != null) {
            return av;
        }
        @Nullable
        ViewParent vp = this.getParent();
        while (av == null && vp instanceof ViewGroup) {
            av = ((ViewGroup) vp).findViewById(id);
            vp = vp.getParent();
        }
        if (av != null) {
            if (this.anchorViewSpa == null) {
                this.anchorViewSpa = new SparseArray<>();
            }
            this.anchorViewSpa.put(id, av);
        }
        return av;
    }

    @Nullable
    private SliverContainer findParentBy(@NonNull View child) {
        final ViewParent parent = child.getParent();
        if (parent instanceof SliverContainer) {
            return (SliverContainer) parent;
        }
        return this.findParentBy((View) parent);
    }

    private final class ComponentListener extends SliverContainer.SimpleOnScrollListener {
        @Nullable
        private WeakReference<SliverContainer> ref;

        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            CollapsingBarLayout.this.offsetSetup(sliverContainer);
        }

        @Override
        public void onScrollStateChanged(@NonNull SliverContainer sliverContainer,
                                         int scrollState) {
            CollapsingBarLayout.this.offsetSetup(sliverContainer);
        }

        @Nullable
        public SliverContainer getSliverContainer() {
            return this.ref == null ? null : this.ref.get();
        }

        public void setSliverContainer(@Nullable SliverContainer sliverContainer) {
            final SliverContainer oldSliverContainer = this.getSliverContainer();
            if (oldSliverContainer == sliverContainer) {
                return;
            }
            if (oldSliverContainer != null) {
                oldSliverContainer.removeOnScrollListener(this);
            }
            if (sliverContainer == null) {
                if (this.ref != null) {
                    this.ref.clear();
                    this.ref = null;
                }
                return;
            }
            this.ref = new WeakReference<>(sliverContainer);
            // Setup listener.
            sliverContainer.addOnScrollListener(this);
        }
    }
}
