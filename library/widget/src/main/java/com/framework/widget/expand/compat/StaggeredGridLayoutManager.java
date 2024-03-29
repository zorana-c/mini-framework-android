package com.framework.widget.expand.compat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.expand.PositionType;

/**
 * @Author create by Zhengzelong on 2023-03-23
 * @Email : 171905184@qq.com
 * @Description : StaggeredGridLayoutManager 兼容
 */
public class StaggeredGridLayoutManager extends androidx.recyclerview.widget.StaggeredGridLayoutManager
        implements RecyclerView.OnChildAttachStateChangeListener {
    private RecyclerView recyclerView;

    public StaggeredGridLayoutManager() {
        this(2);
    }

    public StaggeredGridLayoutManager(int spanCount) {
        this(spanCount, RecyclerView.VERTICAL);
    }

    public StaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public StaggeredGridLayoutManager(@NonNull Context context) {
        this(context, null);
    }

    public StaggeredGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaggeredGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public StaggeredGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.addOnChildAttachStateChangeListener(this);
        super.onAttachedToWindow(recyclerView);
    }

    @Override
    public void onDetachedFromWindow(@NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        this.recyclerView.removeOnChildAttachStateChangeListener(this);
        this.recyclerView = null;
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View itemView) {
        final int positionType = this.getPositionType(itemView);
        if (PositionType.TYPE_NONE == positionType) {
            return;
        }
        if (this.hasFullSpanFlags(positionType)) {
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View itemView) {
        // no-op
    }

    // 下面类型默认是 填满状态
    private int positionTypeFlags = PositionType.TYPE_NONE
            | PositionType.TYPE_HEAD
            | PositionType.TYPE_TAIL
            | PositionType.TYPE_EMPTY;

    @NonNull
    public StaggeredGridLayoutManager setFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags = positionTypeFlags;
        return this;
    }

    @NonNull
    public StaggeredGridLayoutManager addFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags |= positionTypeFlags;
        return this;
    }

    @NonNull
    public StaggeredGridLayoutManager delFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags &= ~positionTypeFlags;
        return this;
    }

    @NonNull
    public StaggeredGridLayoutManager clearFullSpanFlags() {
        this.positionTypeFlags = PositionType.TYPE_NONE;
        return this;
    }

    public boolean hasFullSpanFlags(int positionTypeFlags) {
        return (this.positionTypeFlags & positionTypeFlags) != 0;
    }

    public final int getPositionType(@NonNull View itemView) {
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            return PositionType.TYPE_NONE;
        }
        final RecyclerView.ViewHolder holder;
        holder = recyclerView.getChildViewHolder(itemView);
        if (holder instanceof ExpandableRecyclerView.ViewHolder) {
            final ExpandableRecyclerView.ViewHolder expHolder;
            expHolder = (ExpandableRecyclerView.ViewHolder) holder;
            return (expHolder).getPositionType();
        }
        return PositionType.TYPE_NONE;
    }

    public final int getRetItemViewType(@NonNull View itemView) {
        final RecyclerView recyclerView = this.recyclerView;
        if (recyclerView == null) {
            return -1;
        }
        final RecyclerView.ViewHolder holder;
        holder = recyclerView.getChildViewHolder(itemView);
        if (holder instanceof ExpandableRecyclerView.ViewHolder) {
            final ExpandableRecyclerView.ViewHolder expHolder;
            expHolder = (ExpandableRecyclerView.ViewHolder) holder;
            return (expHolder).getRetItemViewType();
        }
        return holder.getItemViewType();
    }
}
