package com.framework.widget.expand;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @Author create by Zhengzelong on 2022/7/29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DefaultItemDecoration extends ExpandableRecyclerView.ItemDecoration {
    private int padding;
    private int paddingOther;
    private int spanGapSize;
    private int spanGapSizeOther;
    private boolean border;
    private boolean borderOther;

    @Override
    public void getGroupItemOffsets(@NonNull Rect outRect,
                                    @NonNull View itemView,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.State state) {
        final int spanCount = this.getSpanCount(itemView, recyclerView, state);
        final int spanIndex = this.getSpanIndex(itemView, recyclerView, state);
        final int spanCountOther = this.getGroupSpanCountOther(itemView, recyclerView, state);
        final int spanIndexOther = this.getGroupSpanIndexOther(itemView, recyclerView, state);
        this.offsetLeftAndRight(outRect, spanCount, spanIndex, this.spanGapSize, this.border);
        this.offsetTopAndBottom(outRect, spanCountOther, spanIndexOther, this.spanGapSizeOther, this.borderOther);

        if (spanCount <= 2) {
            if (spanIndex == 0) {
                outRect.left += this.padding;
            } else if (spanIndex == spanCount - 1) {
                outRect.right += this.padding;
            }
            if (spanIndexOther == 0) {
                outRect.top += this.paddingOther;
            } else if (spanIndexOther == spanCountOther - 1) {
                outRect.bottom += this.paddingOther;
            }
        }
    }

    public int getSpanCount(@NonNull View itemView,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.State state) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    public int getSpanIndex(@NonNull View itemView,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.State state) {
        final ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (layoutParams instanceof GridLayoutManager.LayoutParams) {
            return ((GridLayoutManager.LayoutParams) layoutParams).getSpanIndex();
        }
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return ((StaggeredGridLayoutManager.LayoutParams) layoutParams).getSpanIndex();
        }
        return 0;
    }

    public int getGroupSpanCountOther(@NonNull View itemView,
                                      @NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.State state) {
        final int spanCount = this.getSpanCount(itemView, recyclerView, state);
        int itemCount = state.getItemCount() + (spanCount - 1);
        itemCount -= this.getHeadItemCount(recyclerView);
        itemCount -= this.getTailItemCount(recyclerView);
        return itemCount / spanCount;
    }

    public int getGroupSpanIndexOther(@NonNull View itemView,
                                      @NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.State state) {
        final int spanCount = this.getSpanCount(itemView, recyclerView, state);
        int position = recyclerView.getChildLayoutPosition(itemView);
        position -= this.getHeadItemCount(recyclerView);
        return position / spanCount;
    }

    @NonNull
    public DefaultItemDecoration setBorder(boolean border) {
        this.border = border;
        return this;
    }

    @NonNull
    public DefaultItemDecoration setBorderOther(boolean borderOther) {
        this.borderOther = borderOther;
        return this;
    }

    @NonNull
    public DefaultItemDecoration setPadding(int padding /*px*/) {
        this.padding = padding;
        return this;
    }

    @NonNull
    public DefaultItemDecoration setPaddingOther(int paddingOther /*px*/) {
        this.paddingOther = paddingOther;
        return this;
    }

    @NonNull
    public DefaultItemDecoration setSpanGapSize(int spanGapSize /*px*/) {
        this.spanGapSize = spanGapSize;
        return this;
    }

    @NonNull
    public DefaultItemDecoration setSpanGapSizeOther(int spanGapSizeOther /*px*/) {
        this.spanGapSizeOther = spanGapSizeOther;
        return this;
    }
}
