package com.framework.widget.expand;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2023-08-21
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface ExpandableAdapter<VH extends RecyclerView.ViewHolder> {

    @NonNull
    default VH onCreateHeadViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        throw new IllegalArgumentException("ERROR");
    }

    @NonNull
    default VH onCreateTailViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        throw new IllegalArgumentException("ERROR");
    }

    @NonNull
    default VH onCreateEmptyViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        throw new IllegalArgumentException("ERROR");
    }

    @NonNull
    default VH onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        throw new IllegalArgumentException("ERROR");
    }

    @NonNull
    default VH onCreateChildViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        throw new IllegalArgumentException("ERROR");
    }

    default void onBindHeadViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        this.onBindHeadViewHolder(holder, position);
    }

    default void onBindHeadViewHolder(@NonNull VH holder, int position) {
        // nothing
    }

    default void onBindTailViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        this.onBindTailViewHolder(holder, position);
    }

    default void onBindTailViewHolder(@NonNull VH holder, int position) {
        // nothing
    }

    default void onBindEmptyViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        this.onBindEmptyViewHolder(holder, position);
    }

    default void onBindEmptyViewHolder(@NonNull VH holder, int position) {
        // nothing
    }

    default void onBindGroupViewHolder(@NonNull VH holder, int groupPosition, @NonNull List<Object> payloads) {
        this.onBindGroupViewHolder(holder, groupPosition);
    }

    default void onBindGroupViewHolder(@NonNull VH holder, int groupPosition) {
        // nothing
    }

    default void onBindChildViewHolder(@NonNull VH holder, int groupPosition, int childPosition, @NonNull List<Object> payloads) {
        this.onBindChildViewHolder(holder, groupPosition, childPosition);
    }

    default void onBindChildViewHolder(@NonNull VH holder, int groupPosition, int childPosition) {
        // nothing
    }

    default long getHeadItemId(int position) {
        // nothing
        return 0L;
    }

    default long getTailItemId(int position) {
        // nothing
        return 0L;
    }

    default long getEmptyItemId(int position) {
        // nothing
        return 0L;
    }

    default long getGroupItemId(int groupPosition) {
        // nothing
        return 0L;
    }

    default long getChildItemId(int groupPosition, int childPosition) {
        // nothing
        return 0L;
    }

    default int getHeadItemViewType(int position) {
        // nothing
        return 0;
    }

    default int getTailItemViewType(int position) {
        // nothing
        return 0;
    }

    default int getEmptyItemViewType(int position) {
        // nothing
        return 0;
    }

    default int getGroupItemViewType(int groupPosition) {
        // nothing
        return 0;
    }

    default int getChildItemViewType(int groupPosition, int childPosition) {
        // nothing
        return 0;
    }

    default int getHeadItemCount() {
        // nothing
        return 0;
    }

    default int getTailItemCount() {
        // nothing
        return 0;
    }

    default int getEmptyItemCount() {
        // nothing
        return 0;
    }

    int getGroupItemCount();

    default int getChildItemCount(int groupPosition) {
        // nothing
        return 0;
    }

    @Nullable
    default CharSequence getHeadItemLetter(int position) {
        // nothing
        return null;
    }

    @Nullable
    default CharSequence getTailItemLetter(int position) {
        // nothing
        return null;
    }

    @Nullable
    default CharSequence getEmptyItemLetter(int position) {
        // nothing
        return null;
    }

    @Nullable
    default CharSequence getGroupItemLetter(int groupPosition) {
        // nothing
        return null;
    }

    @Nullable
    default CharSequence getChildItemLetter(int groupPosition, int childPosition) {
        // nothing
        return null;
    }

    default void onGroupExpand(int groupPosition) {
        // nothing
    }

    default void onGroupCollapse(int groupPosition) {
        // nothing
    }

    void registerAdapterDataObserver(@NonNull AdapterDataObserver observer);

    void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer);

    int getMaxExpandItemCount();

    void setMaxExpandItemCount(int maxExpandItemCount);

    @PositionType
    int getPositionTypeByAdapterPosition(int adapterPosition);

    int getGroupPositionByAdapterPosition(int adapterPosition);

    int getAdapterPositionByPositionType(@PositionType int positionType, int groupPosition);

    int getChildAdapterPosition(int groupPosition, int childPosition);

    boolean expandGroup(int groupPosition);

    default boolean expandGroup(int groupPositionStart, int itemCount) {
        final int N = groupPositionStart + itemCount;
        boolean positionsChanged = false;
        for (int groupPosition = groupPositionStart; groupPosition < N; groupPosition++) {
            positionsChanged |= this.expandGroup(groupPosition);
        }
        return positionsChanged;
    }

    boolean collapseGroup(int groupPosition);

    default boolean collapseGroup(int groupPositionStart, int itemCount) {
        final int N = groupPositionStart + itemCount;
        boolean positionsChanged = false;
        for (int groupPosition = groupPositionStart; groupPosition < N; groupPosition++) {
            positionsChanged |= this.collapseGroup(groupPosition);
        }
        return positionsChanged;
    }

    boolean isGroupExpanded(int groupPosition);

    // Adapter

    void notifyDataSetChanged();

    // Head

    default void notifyHeadItemInserted(int position) {
        this.notifyHeadItemRangeInserted(position, 1);
    }

    void notifyHeadItemRangeInserted(int positionStart, int itemCount);

    default void notifyHeadItemRemoved(int position) {
        this.notifyHeadItemRangeRemoved(position, 1);
    }

    void notifyHeadItemRangeRemoved(int positionStart, int itemCount);

    default void notifyHeadItemChanged(int position) {
        this.notifyHeadItemChanged(position, null);
    }

    default void notifyHeadItemChanged(int position, @Nullable Object payload) {
        this.notifyHeadItemRangeChanged(position, 1, payload);
    }

    default void notifyHeadItemRangeChanged(int positionStart, int itemCount) {
        this.notifyHeadItemRangeChanged(positionStart, itemCount, null);
    }

    void notifyHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload);

    // Tail

    default void notifyTailItemInserted(int position) {
        this.notifyTailItemRangeInserted(position, 1);
    }

    void notifyTailItemRangeInserted(int positionStart, int itemCount);

    default void notifyTailItemRemoved(int position) {
        this.notifyTailItemRangeRemoved(position, 1);
    }

    void notifyTailItemRangeRemoved(int positionStart, int itemCount);

    default void notifyTailItemChanged(int position) {
        this.notifyTailItemChanged(position, null);
    }

    default void notifyTailItemChanged(int position, @Nullable Object payload) {
        this.notifyTailItemRangeChanged(position, 1, payload);
    }

    default void notifyTailItemRangeChanged(int positionStart, int itemCount) {
        this.notifyTailItemRangeChanged(positionStart, itemCount, null);
    }

    void notifyTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload);

    // Empty

    default void notifyEmptyItemInserted(int position) {
        this.notifyEmptyItemRangeInserted(position, 1);
    }

    void notifyEmptyItemRangeInserted(int positionStart, int itemCount);

    default void notifyEmptyItemRemoved(int position) {
        this.notifyEmptyItemRangeRemoved(position, 1);
    }

    void notifyEmptyItemRangeRemoved(int positionStart, int itemCount);

    default void notifyEmptyItemChanged(int position) {
        this.notifyEmptyItemChanged(position, null);
    }

    default void notifyEmptyItemChanged(int position, @Nullable Object payload) {
        this.notifyEmptyItemRangeChanged(position, 1, payload);
    }

    default void notifyEmptyItemRangeChanged(int positionStart, int itemCount) {
        this.notifyEmptyItemRangeChanged(positionStart, itemCount, null);
    }

    void notifyEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload);

    // Group

    default void notifyGroupItemExpanded(int groupPosition) {
        this.notifyGroupItemRangeExpanded(groupPosition, 1);
    }

    void notifyGroupItemRangeExpanded(int groupPositionStart, int itemCount);

    default void notifyGroupItemInserted(int groupPosition) {
        this.notifyGroupItemRangeInserted(groupPosition, 1);
    }

    void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount);

    default void notifyGroupItemRemoved(int groupPosition) {
        this.notifyGroupItemRangeRemoved(groupPosition, 1);
    }

    void notifyGroupItemRangeRemoved(int groupPositionStart, int itemCount);

    default void notifyGroupItemChanged(int groupPosition) {
        this.notifyGroupItemChanged(groupPosition, null);
    }

    default void notifyGroupItemChanged(int groupPosition, @Nullable Object payload) {
        this.notifyGroupItemRangeChanged(groupPosition, 1, payload);
    }

    default void notifyGroupItemRangeChanged(int groupPositionStart, int itemCount) {
        this.notifyGroupItemRangeChanged(groupPositionStart, itemCount, null);
    }

    void notifyGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload);

    default void notifyGroupItemMoved(int fromGroupPosition, int toGroupPosition) {
        this.notifyGroupItemRangeMoved(fromGroupPosition, toGroupPosition, 1);
    }

    void notifyGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount);

    // Child

    default void notifyChildItemInserted(int groupPosition, int childPosition) {
        this.notifyChildItemRangeInserted(groupPosition, childPosition, 1);
    }

    void notifyChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount);

    default void notifyChildItemRemoved(int groupPosition, int childPosition) {
        this.notifyChildItemRangeRemoved(groupPosition, childPosition, 1);
    }

    void notifyChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount);

    default void notifyChildItemChanged(int groupPosition, int childPosition) {
        this.notifyChildItemChanged(groupPosition, childPosition, null);
    }

    default void notifyChildItemChanged(int groupPosition, int childPosition, @Nullable Object payload) {
        this.notifyChildItemRangeChanged(groupPosition, childPosition, 1, payload);
    }

    default void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount) {
        this.notifyChildItemRangeChanged(groupPosition, childPositionStart, itemCount, null);
    }

    void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload);

    default void notifyChildItemMoved(int groupPosition, int fromChildPosition, int toChildPosition) {
        this.notifyChildItemMoved(groupPosition, fromChildPosition, groupPosition, toChildPosition);
    }

    default void notifyChildItemMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        this.notifyChildItemRangeMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition, 1);
    }

    default void notifyChildItemRangeMoved(int groupPosition, int fromChildPosition, int toChildPosition, int itemCount) {
        this.notifyChildItemRangeMoved(groupPosition, fromChildPosition, groupPosition, toChildPosition, itemCount);
    }

    void notifyChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount);

    // Combined Item

    default long getCombinedGroupId(@PositionType long positionType, long groupId) {
        return ((positionType & Integer.MAX_VALUE) << 32) | groupId;
    }

    default long getCombinedChildId(@PositionType long positionType, long groupId, long childId) {
        return Long.MIN_VALUE | ((groupId & Integer.MAX_VALUE) << 32) | childId;
    }

    default int getPositionType(int combinedItemViewType) {
        return Math.toIntExact(combinedItemViewType >> 16);
    }

    default int getCombinedItemViewType(@PositionType int positionType, int itemViewType) {
        return ((positionType & Short.MAX_VALUE) << 16) | itemViewType;
    }

    default int getUnCombinedItemViewType(int combinedItemViewType) {
        final int positionType = this.getPositionType(combinedItemViewType);
        return ((positionType & Short.MAX_VALUE) << 16) ^ combinedItemViewType;
    }

    interface AdapterDataObserver {

        // Head

        default void onHeadItemRangeChanged(int positionStart, int itemCount) {
            // nothing
        }

        default void onHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.onHeadItemRangeChanged(positionStart, itemCount);
        }

        default void onHeadItemRangeInserted(int positionStart, int itemCount) {
            // nothing
        }

        default void onHeadItemRangeRemoved(int positionStart, int itemCount) {
            // nothing
        }

        // Tail

        default void onTailItemRangeChanged(int positionStart, int itemCount) {
            // nothing
        }

        default void onTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.onTailItemRangeChanged(positionStart, itemCount);
        }

        default void onTailItemRangeInserted(int positionStart, int itemCount) {
            // nothing
        }

        default void onTailItemRangeRemoved(int positionStart, int itemCount) {
            // nothing
        }

        // Empty

        default void onEmptyItemRangeChanged(int positionStart, int itemCount) {
            // nothing
        }

        default void onEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.onEmptyItemRangeChanged(positionStart, itemCount);
        }

        default void onEmptyItemRangeInserted(int positionStart, int itemCount) {
            // nothing
        }

        default void onEmptyItemRangeRemoved(int positionStart, int itemCount) {
            // nothing
        }

        // Group

        default void onGroupItemRangeChanged(int groupPositionStart, int itemCount) {
            // nothing
        }

        default void onGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
            this.onGroupItemRangeChanged(groupPositionStart, itemCount);
        }

        default void onGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
            // nothing
        }

        default void onGroupItemRangeInserted(int groupPositionStart, int itemCount) {
            // nothing
        }

        default void onGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
            // nothing
        }

        default void onGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
            // nothing
        }

        // Child

        default void onChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount) {
            // nothing
        }

        default void onChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
            this.onChildItemRangeChanged(groupPosition, childPositionStart, itemCount);
        }

        default void onChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
            // nothing
        }

        default void onChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
            // nothing
        }

        default void onChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
            // nothing
        }
    }
}
