package com.framework.core.content;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.expand.ExpandableAdapter;

/**
 * @Author create by Zhengzelong on 2024-02-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIExpandableObserver implements UIDataController.Observer {
    @Nullable
    private final ExpandableAdapter<?> mExpandableAdapter;
    private boolean mIsGroupExpanded;

    public UIExpandableObserver() {
        this(null);
    }

    public UIExpandableObserver(@Nullable ExpandableAdapter<?> adapter) {
        this.mExpandableAdapter = adapter;
    }

    @Override
    public boolean onChanged() {
        this.getExpandableAdapter().notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onRangeInserted(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.getExpandableAdapter();
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupItemCount == itemCount) {
            adapter.notifyDataSetChanged();
        } else {
            final int cItemCount = groupItemCount - positionStart;
            adapter.notifyGroupItemRangeInserted(positionStart, itemCount);
            adapter.notifyGroupItemRangeChanged(positionStart, cItemCount);

            final int tItemCount = adapter.getTailItemCount();
            adapter.notifyTailItemRangeChanged(0, tItemCount);
        }
        if (this.mIsGroupExpanded) {
            adapter.expandGroup(positionStart, itemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeRemoved(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.getExpandableAdapter();
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupItemCount == 0) {
            adapter.notifyDataSetChanged();
        } else {
            final int cItemCount = groupItemCount - positionStart;
            adapter.notifyGroupItemRangeRemoved(positionStart, itemCount);
            adapter.notifyGroupItemRangeChanged(positionStart, cItemCount);

            final int tItemCount = adapter.getTailItemCount();
            adapter.notifyTailItemRangeChanged(0, tItemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeChanged(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.getExpandableAdapter();
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupItemCount == 0) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyGroupItemRangeChanged(positionStart, itemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeMoved(int fromPosition, int toPosition, int itemCount) {
        final ExpandableAdapter<?> adapter = this.getExpandableAdapter();
        final int indexStart = Math.min(fromPosition, toPosition);
        final int cItemCount = Math.abs(fromPosition - toPosition) + 1;
        adapter.notifyGroupItemRangeMoved(fromPosition, toPosition, itemCount);
        adapter.notifyGroupItemRangeChanged(indexStart, cItemCount);
        return true;
    }

    @NonNull
    public ExpandableAdapter<?> getExpandableAdapter() {
        if (this.mExpandableAdapter == null) {
            throw new NullPointerException("ERROR");
        }
        return this.mExpandableAdapter;
    }

    public final boolean getGroupExpanded() {
        return this.mIsGroupExpanded;
    }

    public final void setGroupExpanded(boolean groupExpanded) {
        this.mIsGroupExpanded = groupExpanded;
    }
}
