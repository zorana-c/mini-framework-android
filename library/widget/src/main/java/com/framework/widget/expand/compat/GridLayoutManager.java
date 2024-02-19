package com.framework.widget.expand.compat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.expand.PositionType;

/**
 * @Author create by Zhengzelong on 2022/8/2
 * @Email : 171905184@qq.com
 * @Description : GridLayoutManager 兼容
 */
public class GridLayoutManager extends androidx.recyclerview.widget.GridLayoutManager {
    private RecyclerView recyclerView;

    public GridLayoutManager(@NonNull Context context) {
        this(context, 2);
    }

    public GridLayoutManager(@NonNull Context context, int spanCount) {
        this(context, spanCount, RecyclerView.VERTICAL);
    }

    public GridLayoutManager(@NonNull Context context, int spanCount, int orientation) {
        this(context, spanCount, orientation, false);
    }

    public GridLayoutManager(@NonNull Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        this.setSpanSizeLookup(new SpanSizeLookupInner(this));
    }

    public GridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setSpanSizeLookup(new SpanSizeLookupInner(this));
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToWindow(recyclerView);
    }

    @Override
    public void onDetachedFromWindow(@NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        this.recyclerView = null;
    }

    // 下面类型默认是 填满状态
    private int positionTypeFlags = PositionType.TYPE_NONE
            | PositionType.TYPE_HEAD
            | PositionType.TYPE_TAIL
            | PositionType.TYPE_EMPTY;

    @NonNull
    public GridLayoutManager setFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags = positionTypeFlags;
        return this;
    }

    @NonNull
    public GridLayoutManager addFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags |= positionTypeFlags;
        return this;
    }

    @NonNull
    public GridLayoutManager delFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags &= ~positionTypeFlags;
        return this;
    }

    @NonNull
    public GridLayoutManager clearFullSpanFlags() {
        this.positionTypeFlags = PositionType.TYPE_NONE;
        return this;
    }

    public boolean hasFullSpanFlags(int positionTypeFlags) {
        return (this.positionTypeFlags & positionTypeFlags) != 0;
    }

    public final int getPositionType(int position) {
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            return PositionType.TYPE_NONE;
        }
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof ExpandableRecyclerView.Adapter) {
            final ExpandableRecyclerView.Adapter<?> expAdapter;
            expAdapter = (ExpandableRecyclerView.Adapter<?>) adapter;
            return expAdapter.getPositionTypeByAdapterPosition(position);
        }
        return PositionType.TYPE_NONE;
    }

    private int getSpanSizeInner(int position) {
        final int positionType = this.getPositionType(position);
        if (PositionType.TYPE_NONE == positionType) {
            return 1;
        }
        if (this.hasFullSpanFlags(positionType)) {
            return this.getSpanCount();
        }
        return 1;
    }

    private static final class SpanSizeLookupInner extends SpanSizeLookup {
        private final GridLayoutManager layoutManager;

        private SpanSizeLookupInner(@NonNull GridLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public int getSpanSize(int position) {
            return this.layoutManager.getSpanSizeInner(position);
        }
    }
}
