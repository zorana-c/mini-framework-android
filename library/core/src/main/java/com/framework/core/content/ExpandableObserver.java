package com.framework.core.content;

import androidx.annotation.NonNull;

import com.framework.widget.expand.ExpandableAdapter;

/**
 * @Author create by Zhengzelong on 2024-02-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ExpandableObserver implements UIDataController.Observer {
    @NonNull
    protected final ExpandableAdapter<?> mExpandableAdapter;
    private boolean mGroupExpanded;

    public ExpandableObserver(@NonNull ExpandableAdapter<?> adapter) {
        this.mExpandableAdapter = adapter;
    }

    public boolean getGroupExpanded() {
        return this.mGroupExpanded;
    }

    @NonNull
    public ExpandableObserver setGroupExpanded(boolean groupExpanded) {
        this.mGroupExpanded = groupExpanded;
        return this;
    }

    @Override
    public boolean onRangeInserted(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.mExpandableAdapter;
        if (adapter.getGroupItemCount() == itemCount) {
            // Ignore notify.
        } else {
            adapter.notifyGroupItemRangeInserted(positionStart, itemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeRemoved(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.mExpandableAdapter;
        if (adapter.getGroupItemCount() == 0) {
            // Ignore notify.
        } else {
            adapter.notifyGroupItemRangeRemoved(positionStart, itemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeChanged(int positionStart, int itemCount) {
        final ExpandableAdapter<?> adapter = this.mExpandableAdapter;
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupItemCount == 0 || groupItemCount == itemCount) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyGroupItemRangeChanged(positionStart, itemCount);
            adapter.notifyTailItemRangeChanged(0, adapter.getTailItemCount());
        }
        if (this.mGroupExpanded) {
            adapter.expandGroup(positionStart, itemCount);
        }
        return true;
    }

    @Override
    public boolean onRangeMoved(int fromPosition, int toPosition, int itemCount) {
        this.mExpandableAdapter.notifyGroupItemRangeMoved(fromPosition, toPosition, itemCount);
        return true;
    }
}
