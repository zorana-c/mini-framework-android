package com.framework.widget.expand;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Observable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2023-08-22
 * @Email : 171905184@qq.com
 * @Description :
 */
final class ExpandableController<VH extends ExpandableRecyclerView.ViewHolder> {
    private static final int SYNC_MAX_DURATION_MILLIS = 100;

    @NonNull
    private final ExpandableDataObserver mExpandableDataObserver;
    @NonNull
    private final ExpandableDataObservable mExpandableDataObservable;
    @NonNull
    private final ExpandableRecyclerView.Adapter<VH> mExpandableAdapter;
    @NonNull
    private final ArrayList<ExpandMetadata> mExpandMetadataList = new ArrayList<>();

    private int mMaxExpandItemCount = Integer.MAX_VALUE;
    private int mExpandChildItemCount;
    @Nullable
    private RecyclerView mRecyclerView;
    @Nullable
    private LayoutInflater mLayoutInflater;

    ExpandableController(@NonNull ExpandableRecyclerView.Adapter<VH> adapter) {
        this.mExpandableAdapter = adapter;
        this.mExpandableDataObserver = new ExpandableDataObserver();
        this.mExpandableDataObservable = new ExpandableDataObservable();
        this.mExpandableDataObservable.registerObserver(this.mExpandableDataObserver);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mExpandableDataObserver);
            adapter.notifyDataSetChanged();
        }
        final Context context = recyclerView.getContext();
        this.mRecyclerView = recyclerView;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(this.mExpandableDataObserver);
        }
        this.mRecyclerView = null;
        this.mLayoutInflater = null;
    }

    @NonNull
    @SuppressLint("SwitchIntDef")
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int combinedItemViewType) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int positionType = adapter.getPositionType(combinedItemViewType);
        final int itemViewType = adapter.getUnCombinedItemViewType(combinedItemViewType);
        VH holder = null;

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                holder = adapter.onCreateHeadViewHolder(parent, itemViewType);
                break;
            case PositionType.TYPE_TAIL:
                holder = adapter.onCreateTailViewHolder(parent, itemViewType);
                break;
            case PositionType.TYPE_EMPTY:
                holder = adapter.onCreateEmptyViewHolder(parent, itemViewType);
                break;
            case PositionType.TYPE_GROUP:
                holder = adapter.onCreateGroupViewHolder(parent, itemViewType);
                break;
            case PositionType.TYPE_CHILD:
                holder = adapter.onCreateChildViewHolder(parent, itemViewType);
                break;
        }
        if (holder == null) {
            throw new IllegalArgumentException(
                    "ERROR ITEM CREATE(" + positionType + "): " + itemViewType);
        }
        holder.mPositionType = positionType;
        holder.mItemViewType = itemViewType;
        return holder;
    }

    @SuppressLint("SwitchIntDef")
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findUnFlattenedItemMetadata(position);
        try {
            final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
            final int positionType = positionMetadata.getType();
            final int groupPosition = positionMetadata.getGroupPosition();
            final int childPosition = positionMetadata.getChildPosition();

            holder.addChangePayloads(payloads);
            holder.setRecyclerView(this.mRecyclerView);
            holder.mItemExpanded = positionMetadata.isExpanded();
            holder.mGroupPosition = groupPosition;
            holder.mChildPosition = childPosition;
            holder.mItemLetter = this.getItemLetter(positionMetadata);
            holder.onInit(payloads);

            switch (positionType) {
                case PositionType.TYPE_HEAD:
                    adapter.onBindHeadViewHolder(holder, groupPosition, payloads);
                    break;
                case PositionType.TYPE_TAIL:
                    adapter.onBindTailViewHolder(holder, groupPosition, payloads);
                    break;
                case PositionType.TYPE_EMPTY:
                    adapter.onBindEmptyViewHolder(holder, groupPosition, payloads);
                    break;
                case PositionType.TYPE_GROUP:
                    adapter.onBindGroupViewHolder(holder, groupPosition, payloads);
                    break;
                case PositionType.TYPE_CHILD:
                    adapter.onBindChildViewHolder(holder, groupPosition, childPosition, payloads);
                    break;
            }
            holder.clearPayloads();
        } finally {
            positionMetadata.recycle();
        }
    }

    @SuppressLint("SwitchIntDef")
    public long getItemId(int position) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findUnFlattenedItemMetadata(position);
        try {
            final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
            final int positionType = positionMetadata.getType();
            final int groupPosition = positionMetadata.getGroupPosition();
            final int childPosition = positionMetadata.getChildPosition();
            long groupId = 0L;
            long childId = 0L;

            switch (positionType) {
                case PositionType.TYPE_HEAD:
                    groupId = adapter.getHeadItemId(groupPosition);
                    break;
                case PositionType.TYPE_TAIL:
                    groupId = adapter.getTailItemId(groupPosition);
                    break;
                case PositionType.TYPE_EMPTY:
                    groupId = adapter.getEmptyItemId(groupPosition);
                    break;
                case PositionType.TYPE_GROUP:
                    groupId = adapter.getGroupItemId(groupPosition);
                    break;
                case PositionType.TYPE_CHILD:
                    groupId = adapter.getGroupItemId(groupPosition);
                    childId = adapter.getChildItemId(groupPosition, childPosition);
                    break;
            }
            if (groupId < 0) {
                throw new IllegalArgumentException(
                        "ERROR GROUP ID(" + positionType + "): " + groupId);
            }
            if (childId < 0) {
                throw new IllegalArgumentException(
                        "ERROR CHILD ID(" + positionType + "): " + childId);
            }
            if (positionType != PositionType.TYPE_CHILD) {
                return adapter.getCombinedGroupId(positionType, groupId);
            }
            return adapter.getCombinedChildId(positionType, groupId, childId);
        } finally {
            positionMetadata.recycle();
        }
    }

    @SuppressLint("SwitchIntDef")
    public int getItemViewType(int position) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findUnFlattenedItemMetadata(position);
        try {
            final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
            final int positionType = positionMetadata.getType();
            final int groupPosition = positionMetadata.getGroupPosition();
            final int childPosition = positionMetadata.getChildPosition();
            int itemViewType = 0;

            switch (positionType) {
                case PositionType.TYPE_HEAD:
                    itemViewType = adapter.getHeadItemViewType(groupPosition);
                    break;
                case PositionType.TYPE_TAIL:
                    itemViewType = adapter.getTailItemViewType(groupPosition);
                    break;
                case PositionType.TYPE_EMPTY:
                    itemViewType = adapter.getEmptyItemViewType(groupPosition);
                    break;
                case PositionType.TYPE_GROUP:
                    itemViewType = adapter.getGroupItemViewType(groupPosition);
                    break;
                case PositionType.TYPE_CHILD:
                    itemViewType = adapter.getChildItemViewType(groupPosition, childPosition);
                    break;
            }
            if (itemViewType < 0) {
                throw new IllegalArgumentException(
                        "ERROR ITEM VIEW TYPE(" + positionType + "): " + itemViewType);
            }
            return adapter.getCombinedItemViewType(positionType, itemViewType);
        } finally {
            positionMetadata.recycle();
        }
    }

    @Nullable
    @SuppressLint("SwitchIntDef")
    private CharSequence getItemLetter(@NonNull PositionMetadata positionMetadata) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int positionType = positionMetadata.getType();
        final int groupPosition = positionMetadata.getGroupPosition();
        final int childPosition = positionMetadata.getChildPosition();
        CharSequence text = null;

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                text = adapter.getHeadItemLetter(groupPosition);
                break;
            case PositionType.TYPE_TAIL:
                text = adapter.getTailItemLetter(groupPosition);
                break;
            case PositionType.TYPE_EMPTY:
                text = adapter.getEmptyItemLetter(groupPosition);
                break;
            case PositionType.TYPE_GROUP:
                text = adapter.getGroupItemLetter(groupPosition);
                break;
            case PositionType.TYPE_CHILD:
                text = adapter.getChildItemLetter(groupPosition, childPosition);
                break;
        }
        return text;
    }

    public int getItemCount() {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        int itemCount = adapter.getGroupItemCount();
        if (itemCount > 0) {
            itemCount += this.mExpandChildItemCount;
        } else {
            itemCount += adapter.getEmptyItemCount();
        }
        itemCount += adapter.getHeadItemCount();
        itemCount += adapter.getTailItemCount();
        return itemCount;
    }

    @NonNull
    public Parcelable onSaveInstanceState() {
        return new SavedState(this.mExpandMetadataList);
    }

    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            this.setExpandMetadataList(savedState.mExpandMetadataList);
        }
    }

    public void registerAdapterDataObserver(@NonNull ExpandableAdapter.AdapterDataObserver observer) {
        this.mExpandableDataObservable.registerObserver(observer);
    }

    public void unregisterAdapterDataObserver(@NonNull ExpandableAdapter.AdapterDataObserver observer) {
        this.mExpandableDataObservable.unregisterObserver(observer);
    }

    public void notifyHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        this.mExpandableDataObservable.notifyHeadItemRangeChanged(positionStart, itemCount, payload);
    }

    public void notifyHeadItemRangeInserted(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyHeadItemRangeInserted(positionStart, itemCount);
    }

    public void notifyHeadItemRangeRemoved(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyHeadItemRangeRemoved(positionStart, itemCount);
    }

    public void notifyTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        this.mExpandableDataObservable.notifyTailItemRangeChanged(positionStart, itemCount, payload);
    }

    public void notifyTailItemRangeInserted(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyTailItemRangeInserted(positionStart, itemCount);
    }

    public void notifyTailItemRangeRemoved(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyTailItemRangeRemoved(positionStart, itemCount);
    }

    public void notifyEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        this.mExpandableDataObservable.notifyEmptyItemRangeChanged(positionStart, itemCount, payload);
    }

    public void notifyEmptyItemRangeInserted(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyEmptyItemRangeInserted(positionStart, itemCount);
    }

    public void notifyEmptyItemRangeRemoved(int positionStart, int itemCount) {
        this.mExpandableDataObservable.notifyEmptyItemRangeRemoved(positionStart, itemCount);
    }

    public void notifyGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
        this.mExpandableDataObservable.notifyGroupItemRangeChanged(groupPositionStart, itemCount, payload);
    }

    public void notifyGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
        this.mExpandableDataObservable.notifyGroupItemRangeExpanded(groupPositionStart, itemCount);
    }

    public void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount) {
        this.mExpandableDataObservable.notifyGroupItemRangeInserted(groupPositionStart, itemCount);
    }

    public void notifyGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
        this.mExpandableDataObservable.notifyGroupItemRangeRemoved(groupPositionStart, itemCount);
    }

    public void notifyGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
        this.mExpandableDataObservable.notifyGroupItemRangeMoved(fromGroupPosition, toGroupPosition, itemCount);
    }

    public void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
        this.mExpandableDataObservable.notifyChildItemRangeChanged(groupPosition, childPositionStart, itemCount, payload);
    }

    public void notifyChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
        this.mExpandableDataObservable.notifyChildItemRangeInserted(groupPosition, childPositionStart, itemCount);
    }

    public void notifyChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
        this.mExpandableDataObservable.notifyChildItemRangeRemoved(groupPosition, childPositionStart, itemCount);
    }

    public void notifyChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
        this.mExpandableDataObservable.notifyChildItemRangeMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition, itemCount);
    }

    public int getMaxExpandItemCount() {
        return this.mMaxExpandItemCount;
    }

    public void setMaxExpandItemCount(int maxExpandItemCount) {
        this.mMaxExpandItemCount = maxExpandItemCount;
    }

    @Nullable
    public LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    @NonNull
    public LayoutInflater requireLayoutInflater() {
        final LayoutInflater inflater = this.getLayoutInflater();
        if (inflater == null) {
            throw new IllegalArgumentException("ERROR");
        }
        return inflater;
    }

    @Nullable
    public <V extends RecyclerView> V getRecyclerView() {
        return (V) this.mRecyclerView;
    }

    @NonNull
    public <V extends RecyclerView> V requireRecyclerView() {
        final V recyclerView = this.getRecyclerView();
        if (recyclerView == null) {
            throw new IllegalArgumentException("ERROR");
        }
        return recyclerView;
    }

    @NonNull
    public <V extends View> V inflate(@LayoutRes int layoutId) {
        return this.inflate(layoutId, this.mRecyclerView);
    }

    @NonNull
    public <V extends View> V inflate(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
        if (this.mLayoutInflater == null) {
            throw new IllegalArgumentException("ERROR");
        }
        return (V) this.mLayoutInflater.inflate(layoutId, parent, false);
    }

    // ExpandableAdapter

    public int getPositionTypeByAdapterPosition(int adapterPosition) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findUnFlattenedItemMetadata(adapterPosition);
        try {
            return positionMetadata.getType();
        } finally {
            positionMetadata.recycle();
        }
    }

    public int getGroupPositionByAdapterPosition(int adapterPosition) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findUnFlattenedItemMetadata(adapterPosition);
        try {
            return positionMetadata.getGroupPosition();
        } finally {
            positionMetadata.recycle();
        }
    }

    public int getAdapterPositionByPositionType(int positionType, int groupPosition) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(positionType, groupPosition);
        try {
            return positionMetadata.getAdapterPosition();
        } finally {
            positionMetadata.recycle();
        }
    }

    public int getChildAdapterPosition(int groupPosition, int childPosition) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            if (!positionMetadata.isExpanded()) {
                return RecyclerView.NO_POSITION;
            }
            int adapterPosition = 1;
            adapterPosition += childPosition;
            adapterPosition += positionMetadata.getAdapterPosition();
            return adapterPosition;
        } finally {
            positionMetadata.recycle();
        }
    }

    public boolean expandGroup(int groupPosition) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupPosition >= groupItemCount) {
            return false;
        }
        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        if (expandMetadataList.size() >= this.mMaxExpandItemCount) {
            final ExpandMetadata expandMetadata;
            expandMetadata = expandMetadataList.get(0);
            this.collapseGroup(expandMetadata.getGroupPosition());
        }
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            if (positionMetadata.isExpanded()) {
                return true;
            }
            final int childItemCount = adapter.getChildItemCount(groupPosition);
            final int adapterPosition = positionMetadata.getAdapterPosition();
            final ExpandMetadata expandMetadata;
            expandMetadata = ExpandMetadata.obtain(adapter.getGroupItemId(groupPosition),
                    groupPosition,
                    adapterPosition,
                    adapterPosition + childItemCount);
            expandMetadataList.add(positionMetadata.getGroupIndex(), expandMetadata);

            final int cItemCount = childItemCount;
            final int gItemCount = groupItemCount - groupPosition;
            final int tItemCount = adapter.getTailItemCount();
            this.notifyChildItemRangeInserted(groupPosition, 0, cItemCount);
            this.notifyGroupItemRangeChanged(groupPosition, gItemCount, null);
            this.notifyTailItemRangeChanged(0, tItemCount, null);
            this.dispatchOnGroupExpandChanged(groupPosition, true);
            return true;
        } finally {
            positionMetadata.recycle();
        }
    }

    public boolean collapseGroup(int groupPosition) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupPosition >= groupItemCount) {
            return false;
        }
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            final ExpandMetadata expandMetadata;
            expandMetadata = positionMetadata.getExpandMetadata();
            if (expandMetadata != null) {
                expandMetadata.setGroupId(RecyclerView.NO_ID);
            } else return true;

            final int cItemCount = expandMetadata.getChildItemCount();
            final int gItemCount = groupItemCount - groupPosition;
            final int tItemCount = adapter.getTailItemCount();
            this.notifyChildItemRangeRemoved(groupPosition, 0, cItemCount);
            this.notifyGroupItemRangeChanged(groupPosition, gItemCount, null);
            this.notifyTailItemRangeChanged(0, tItemCount, null);
            this.dispatchOnGroupExpandChanged(groupPosition, false);
            return true;
        } finally {
            positionMetadata.recycle();
        }
    }

    public boolean isGroupExpanded(int groupPosition) {
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            return positionMetadata.isExpanded();
        } finally {
            positionMetadata.recycle();
        }
    }

    // PositionMetadata

    @NonNull
    public PositionMetadata findFlattenedItemMetadata(int positionType, int groupPosition) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        int adapterPosition;

        if (PositionType.TYPE_CHILD == positionType) {
            throw new IllegalArgumentException("Unsupported parameter types");
        }

        if (PositionType.TYPE_HEAD == positionType) {
            adapterPosition = groupPosition;
            return PositionMetadata.obtain(positionType, 0, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
        }

        if (PositionType.TYPE_TAIL == positionType) {
            final int itemCount = this.getItemCount();
            final int tailItemCount = adapter.getTailItemCount();
            adapterPosition = groupPosition + (itemCount - tailItemCount);
            return PositionMetadata.obtain(positionType, 0, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
        }

        if (PositionType.TYPE_EMPTY == positionType) {
            adapterPosition = groupPosition + adapter.getHeadItemCount();
            return PositionMetadata.obtain(positionType, 0, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
        }

        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        final int N = expandMetadataList.size();
        if (N == 0) {
            adapterPosition = groupPosition + adapter.getHeadItemCount();
            return PositionMetadata.obtain(positionType, 0, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
        }
        return this.findFlattenedGroupMetadata(groupPosition);
    }

    @NonNull
    public PositionMetadata findUnFlattenedItemMetadata(int adapterPosition) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        int groupPosition;
        int childPosition;

        final int headItemCount = adapter.getHeadItemCount();
        if (adapterPosition < headItemCount) {
            groupPosition = adapterPosition;
            childPosition = RecyclerView.NO_POSITION;
            return PositionMetadata.obtain(PositionType.TYPE_HEAD, 0, groupPosition, childPosition, adapterPosition, null);
        }

        final int itemCount = this.getItemCount();
        final int tailItemCount = adapter.getTailItemCount();
        final int lastItemCount = itemCount - tailItemCount;
        if (adapterPosition >= lastItemCount) {
            groupPosition = adapterPosition - lastItemCount;
            childPosition = RecyclerView.NO_POSITION;
            return PositionMetadata.obtain(PositionType.TYPE_TAIL, 0, groupPosition, childPosition, adapterPosition, null);
        }

        final int groupItemCount = adapter.getGroupItemCount();
        if (groupItemCount <= 0) {
            groupPosition = adapterPosition - headItemCount;
            childPosition = RecyclerView.NO_POSITION;
            return PositionMetadata.obtain(PositionType.TYPE_EMPTY, 0, groupPosition, childPosition, adapterPosition, null);
        }

        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        final int N = expandMetadataList.size();
        if (N == 0) {
            groupPosition = adapterPosition - headItemCount;
            childPosition = RecyclerView.NO_POSITION;
            return PositionMetadata.obtain(PositionType.TYPE_GROUP, 0, groupPosition, childPosition, adapterPosition, null);
        }
        return this.findUnFlattenedGroupMetadata(adapterPosition);
    }

    @NonNull
    private PositionMetadata findFlattenedGroupMetadata(int groupPosition) {
        int adapterPosition;

        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        final int N = expandMetadataList.size();
        if (N == 0) {
            adapterPosition = groupPosition;
            return PositionMetadata.obtain(PositionType.TYPE_GROUP, 0, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
        }
        int mIndex = 0;
        int lIndex = 0;
        int rIndex = N - 1;
        ExpandMetadata midMetadata;

        while (lIndex <= rIndex) {
            mIndex = (rIndex - lIndex) / 2 + lIndex;
            midMetadata = expandMetadataList.get(mIndex);

            if (groupPosition > midMetadata.getGroupPosition()) {
                lIndex = mIndex + 1;
            } else if (groupPosition < midMetadata.getGroupPosition()) {
                rIndex = mIndex - 1;
            } else {
                adapterPosition = midMetadata.getAdapterPosition();
                return PositionMetadata.obtain(PositionType.TYPE_GROUP, mIndex, groupPosition, RecyclerView.NO_POSITION, adapterPosition, midMetadata);
            }
        }

        if (mIndex < lIndex) {
            mIndex = lIndex;
            midMetadata = expandMetadataList.get(mIndex - 1);
            adapterPosition = midMetadata.getLastChildAdapterPosition() + (groupPosition - midMetadata.getGroupPosition());
        } else {
            mIndex = rIndex + 1;
            midMetadata = expandMetadataList.get(mIndex);
            adapterPosition = midMetadata.getAdapterPosition() - (midMetadata.getGroupPosition() - groupPosition);
        }
        return PositionMetadata.obtain(PositionType.TYPE_GROUP, mIndex, groupPosition, RecyclerView.NO_POSITION, adapterPosition, null);
    }

    @NonNull
    private PositionMetadata findUnFlattenedGroupMetadata(int adapterPosition) {
        int groupPosition;
        int childPosition;

        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        final int N = expandMetadataList.size();
        if (N == 0) {
            groupPosition = adapterPosition;
            childPosition = RecyclerView.NO_POSITION;
            return PositionMetadata.obtain(PositionType.TYPE_GROUP, 0, groupPosition, childPosition, adapterPosition, null);
        }
        int mIndex = 0;
        int lIndex = 0;
        int rIndex = N - 1;
        ExpandMetadata midMetadata;

        while (lIndex <= rIndex) {
            mIndex = (rIndex - lIndex) / 2 + lIndex;
            midMetadata = expandMetadataList.get(mIndex);

            if (adapterPosition > midMetadata.getLastChildAdapterPosition()) {
                lIndex = mIndex + 1;
            } else if (adapterPosition < midMetadata.getAdapterPosition()) {
                rIndex = mIndex - 1;
            } else if (adapterPosition == midMetadata.getAdapterPosition()) {
                groupPosition = midMetadata.getGroupPosition();
                childPosition = RecyclerView.NO_POSITION;
                return PositionMetadata.obtain(PositionType.TYPE_GROUP, mIndex, groupPosition, childPosition, adapterPosition, midMetadata);
            } else if (adapterPosition <= midMetadata.getLastChildAdapterPosition()) {
                groupPosition = midMetadata.getGroupPosition();
                childPosition = adapterPosition - (midMetadata.getAdapterPosition() + 1);
                return PositionMetadata.obtain(PositionType.TYPE_CHILD, mIndex, groupPosition, childPosition, adapterPosition, midMetadata);
            }
        }

        if (mIndex < lIndex) {
            mIndex = lIndex;
            midMetadata = expandMetadataList.get(mIndex - 1);
            groupPosition = (adapterPosition - midMetadata.getLastChildAdapterPosition()) + midMetadata.getGroupPosition();
        } else {
            mIndex = rIndex + 1;
            midMetadata = expandMetadataList.get(mIndex);
            groupPosition = midMetadata.getGroupPosition() - (midMetadata.getAdapterPosition() - adapterPosition);
        }
        childPosition = RecyclerView.NO_POSITION;
        return PositionMetadata.obtain(PositionType.TYPE_GROUP, mIndex, groupPosition, childPosition, adapterPosition, null);
    }

    private void setExpandMetadataList(@NonNull List<ExpandMetadata> expandedGroupMetadataList) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int groupItemCount = adapter.getGroupItemCount();

        final int N = expandedGroupMetadataList.size();
        for (int index = N - 1; index >= 0; index--) {
            final ExpandMetadata expandMetadata;
            expandMetadata = expandedGroupMetadataList.get(index);

            if (expandMetadata.getGroupPosition() >= groupItemCount) {
                return;
            }
        }
        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        expandMetadataList.clear();
        expandMetadataList.addAll(expandedGroupMetadataList);
        this.refreshExpandMetadataList(false);
    }

    // RecyclerView.AdapterDataObserver

    private void refreshExpandMetadataList(boolean syncGroupPosition) {
        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        if (syncGroupPosition) {
            final int N = expandMetadataList.size();
            boolean positionsChanged = false;

            for (int index = N - 1; index >= 0; index--) {
                final ExpandMetadata expandMetadata;
                expandMetadata = expandMetadataList.get(index);
                final int groupPosition;
                groupPosition = this.findGroupPosition(expandMetadata);

                if (groupPosition == expandMetadata.getGroupPosition()) {
                    continue;
                }
                if (groupPosition == RecyclerView.NO_POSITION) {
                    expandMetadataList.remove(index);
                    expandMetadata.recycle();
                } else {
                    expandMetadata.setGroupPosition(groupPosition);
                }
                if (!positionsChanged) {
                    positionsChanged = true;
                }
            }
            if (positionsChanged) {
                Collections.sort(expandMetadataList);
            }
        }
        this.mExpandChildItemCount = 0;

        if (expandMetadataList.isEmpty()) {
            return;
        }
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        int lastGroupPosition = 0;
        int nextAdapterPosition = adapter.getHeadItemCount();

        for (final ExpandMetadata expandMetadata : expandMetadataList) {
            final int groupPosition = expandMetadata.getGroupPosition();
            final int childItemCount = adapter.getChildItemCount(groupPosition);
            this.mExpandChildItemCount += childItemCount;

            // Reset Group Adapter Position.
            nextAdapterPosition += groupPosition;
            nextAdapterPosition -= lastGroupPosition;
            expandMetadata.setAdapterPosition(nextAdapterPosition);

            // Reset Child Last Adapter Position.
            nextAdapterPosition += childItemCount;
            expandMetadata.setLastChildAdapterPosition(nextAdapterPosition);

            // Last Group Position.
            lastGroupPosition = groupPosition;
        }
    }

    private int findGroupPosition(@NonNull ExpandMetadata expandMetadata) {
        final long oldGroupItemId = expandMetadata.getGroupId();
        if (oldGroupItemId == RecyclerView.NO_ID) {
            return RecyclerView.NO_POSITION;
        }
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int N = adapter.getGroupItemCount();
        if (N == 0) {
            return RecyclerView.NO_POSITION;
        }
        int groupPosition;
        groupPosition = expandMetadata.getGroupPosition();
        groupPosition = Math.max(0, groupPosition);
        groupPosition = Math.min(groupPosition, N - 1);

        final long endTime;
        endTime = SystemClock.uptimeMillis() + SYNC_MAX_DURATION_MILLIS;

        long groupItemId;
        int headIndex = groupPosition;
        int tailIndex = groupPosition;
        boolean headHit;
        boolean tailHit;
        boolean next = false;

        while (SystemClock.uptimeMillis() <= endTime) {
            groupItemId = adapter.getGroupItemId(groupPosition);
            if (groupItemId == oldGroupItemId) {
                return groupPosition;
            }
            headHit = headIndex == 0;
            tailHit = tailIndex == N - 1;

            if (headHit && tailHit) {
                break;
            }

            if (headHit || (next && !tailHit)) {
                groupPosition = (++tailIndex);
                next = false;
            } else {
                groupPosition = (--headIndex);
                next = true;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    // ExpandableAdapter.AdapterDataObserver

    // Head

    private void onHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_HEAD, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onHeadItemRangeInserted(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_HEAD, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onHeadItemRangeRemoved(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_HEAD, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    // Tail

    private void onTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_TAIL, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onTailItemRangeInserted(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_TAIL, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onTailItemRangeRemoved(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_TAIL, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    // Empty

    private void onEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_EMPTY, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onEmptyItemRangeInserted(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_EMPTY, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    public void onEmptyItemRangeRemoved(int positionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_EMPTY, positionStart);
        try {
            positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    // Group

    private void onGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionStart);
        try {
            final int positionStart = positionMetadata.getAdapterPosition();
            final int groupPositionEnd = groupPositionStart + itemCount - 1;
            positionMetadata.recycle();
            positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionEnd);

            final ExpandMetadata expandMetadata;
            expandMetadata = positionMetadata.getExpandMetadata();
            final int adjustedItemCount = (expandMetadata == null
                    ? positionMetadata.getAdapterPosition()
                    : expandMetadata.getLastChildAdapterPosition()) + 1 - positionStart;
            adapter.notifyItemRangeChanged(positionStart, adjustedItemCount, payload);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
        final ExpandableAdapter<VH> adapter = this.mExpandableAdapter;
        final int groupItemCount = adapter.getGroupItemCount();
        if (groupPositionStart >= groupItemCount) {
            return;
        }
        final List<ExpandMetadata> expandMetadataList = this.mExpandMetadataList;
        if (expandMetadataList.size() >= this.mMaxExpandItemCount) {
            final ExpandMetadata expandMetadata;
            expandMetadata = expandMetadataList.get(0);
            this.collapseGroup(expandMetadata.getGroupPosition());
        }
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionStart);
        try {
            final List<Integer> expandPositionList = new ArrayList<>();
            int groupPosition = positionMetadata.getGroupPosition();
            int adapterPosition = positionMetadata.getAdapterPosition();
            int positionMetadataIndex = positionMetadata.getGroupIndex();
            int lastChildAdapterPosition = -1;

            for (int index = itemCount; index > 0; index--) {
                if (groupPosition >= groupItemCount) {
                    break;
                }
                if (expandMetadataList.size() >= this.mMaxExpandItemCount) {
                    final ExpandMetadata expandMetadata;
                    expandMetadata = expandMetadataList.remove(--positionMetadataIndex);
                    final int collapseGroupPos = expandMetadata.getGroupPosition();
                    expandPositionList.remove((Integer) collapseGroupPos);

                    adapterPosition -= expandMetadata.getChildItemCount();
                    expandMetadata.recycle();
                }
                lastChildAdapterPosition = adapterPosition;
                lastChildAdapterPosition += adapter.getChildItemCount(groupPosition);

                final ExpandMetadata expandMetadata;
                expandMetadata = ExpandMetadata.obtain(adapter.getGroupItemId(groupPosition),
                        groupPosition,
                        adapterPosition,
                        lastChildAdapterPosition);
                expandMetadataList.add(positionMetadataIndex, expandMetadata);
                expandPositionList.add(groupPosition);

                positionMetadataIndex++;
                groupPosition++;
                adapterPosition = lastChildAdapterPosition + 1;
            }
            if (lastChildAdapterPosition == -1) {
                return;
            }
            final int positionStart = positionMetadata.getAdapterPosition();
            final int adjustedItemCount = lastChildAdapterPosition + 1 - positionStart;
            this.mExpandableAdapter.notifyItemRangeInserted(positionStart, adjustedItemCount);

            if (expandPositionList.isEmpty()) {
                return;
            }
            for (int groupPositionInt : expandPositionList) {
                this.dispatchOnGroupExpandChanged(groupPositionInt, true);
            }
        } finally {
            positionMetadata.recycle();
        }
    }

    public void onGroupItemRangeInserted(int groupPositionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionStart);
        try {
            final int positionStart = positionMetadata.getAdapterPosition();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    public void onGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionStart);
        try {
            final int positionStart = positionMetadata.getAdapterPosition();
            final int groupPositionEnd = groupPositionStart + itemCount - 1;
            positionMetadata.recycle();
            positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPositionEnd);

            final ExpandMetadata expandMetadata;
            expandMetadata = positionMetadata.getExpandMetadata();
            final int adjustedItemCount = (expandMetadata == null
                    ? positionMetadata.getAdapterPosition()
                    : expandMetadata.getLastChildAdapterPosition()) + 1 - positionStart;
            adapter.notifyItemRangeRemoved(positionStart, adjustedItemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
        if (fromGroupPosition == toGroupPosition) {
            return;
        }
        if (itemCount != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata fromPositionMetadata;
        fromPositionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, fromGroupPosition);
        final PositionMetadata toPositionMetadata;
        toPositionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, toGroupPosition);
        try {
            if (fromPositionMetadata.isExpanded()) {
                throw new IllegalArgumentException("Moving been expanded group position " +
                        fromGroupPosition + " is not supported yet");
            }
            if (toPositionMetadata.isExpanded()) {
                throw new IllegalArgumentException("Moving been expanded group position " +
                        toGroupPosition + " is not supported yet");
            }
            final int fromPosition = fromPositionMetadata.getAdapterPosition();
            final int toPosition = toPositionMetadata.getAdapterPosition();
            adapter.notifyItemMoved(fromPosition, toPosition);
        } finally {
            fromPositionMetadata.recycle();
            toPositionMetadata.recycle();
        }
    }

    // Child

    private void onChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            if (!positionMetadata.isExpanded()) {
                return;
            }
            final int positionStart = positionMetadata.getAdapterPosition() + childPositionStart + 1;
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            if (!positionMetadata.isExpanded()) {
                return;
            }
            final int positionStart = positionMetadata.getAdapterPosition() + childPositionStart + 1;
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata positionMetadata;
        positionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        try {
            if (!positionMetadata.isExpanded()) {
                return;
            }
            final int positionStart = positionMetadata.getAdapterPosition() + childPositionStart + 1;
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        } finally {
            positionMetadata.recycle();
        }
    }

    private void onChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
        if (fromGroupPosition == toGroupPosition && fromChildPosition == toChildPosition) {
            return;
        }
        if (itemCount != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        final RecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        final PositionMetadata fromPositionMetadata;
        fromPositionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, fromGroupPosition);
        final PositionMetadata toPositionMetadata;
        toPositionMetadata = this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, toGroupPosition);
        try {
            if (!fromPositionMetadata.isExpanded()) {
                throw new IllegalArgumentException("Moving not been expanded group position " +
                        fromGroupPosition + " is not supported yet");
            }
            if (!toPositionMetadata.isExpanded()) {
                throw new IllegalArgumentException("Moving not been expanded group position " +
                        toGroupPosition + " is not supported yet");
            }
            final int fromPosition = fromPositionMetadata.getAdapterPosition()
                    + fromChildPosition + 1;
            final int toPosition = toPositionMetadata.getAdapterPosition()
                    + toChildPosition + 1;
            adapter.notifyItemMoved(fromPosition, toPosition);
        } finally {
            fromPositionMetadata.recycle();
            toPositionMetadata.recycle();
        }
    }

    private void dispatchOnGroupExpandChanged(int groupPosition, boolean expand) {
        final ExpandableRecyclerView.Adapter<VH> adapter = this.mExpandableAdapter;
        if (expand) {
            adapter.onGroupExpand(groupPosition);
            adapter.dispatchOnGroupExpand(groupPosition);
        } else {
            adapter.onGroupCollapse(groupPosition);
            adapter.dispatchOnGroupCollapse(groupPosition);
        }
    }

    private final class ExpandableDataObserver extends RecyclerView.AdapterDataObserver
            implements ExpandableAdapter.AdapterDataObserver {
        @Override
        public void onChanged() {
            ExpandableController.this.refreshExpandMetadataList(true);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            ExpandableController.this.refreshExpandMetadataList(true);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            ExpandableController.this.refreshExpandMetadataList(true);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            ExpandableController.this.refreshExpandMetadataList(true);
        }

        // Head

        @Override
        public void onHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            ExpandableController.this.onHeadItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onHeadItemRangeInserted(int positionStart, int itemCount) {
            ExpandableController.this.onHeadItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onHeadItemRangeRemoved(int positionStart, int itemCount) {
            ExpandableController.this.onHeadItemRangeRemoved(positionStart, itemCount);
        }

        // Tail

        @Override
        public void onTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            ExpandableController.this.onTailItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onTailItemRangeInserted(int positionStart, int itemCount) {
            ExpandableController.this.onTailItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onTailItemRangeRemoved(int positionStart, int itemCount) {
            ExpandableController.this.onTailItemRangeRemoved(positionStart, itemCount);
        }

        // Empty

        @Override
        public void onEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            ExpandableController.this.onEmptyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onEmptyItemRangeInserted(int positionStart, int itemCount) {
            ExpandableController.this.onEmptyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onEmptyItemRangeRemoved(int positionStart, int itemCount) {
            ExpandableController.this.onEmptyItemRangeRemoved(positionStart, itemCount);
        }

        // Group

        @Override
        public void onGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
            ExpandableController.this.onGroupItemRangeChanged(groupPositionStart, itemCount, payload);
        }

        @Override
        public void onGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
            ExpandableController.this.onGroupItemRangeExpanded(groupPositionStart, itemCount);
        }

        @Override
        public void onGroupItemRangeInserted(int groupPositionStart, int itemCount) {
            ExpandableController.this.onGroupItemRangeInserted(groupPositionStart, itemCount);
        }

        @Override
        public void onGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
            ExpandableController.this.onGroupItemRangeRemoved(groupPositionStart, itemCount);
        }

        @Override
        public void onGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
            ExpandableController.this.onGroupItemRangeMoved(fromGroupPosition, toGroupPosition, itemCount);
        }

        // Child

        @Override
        public void onChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
            ExpandableController.this.onChildItemRangeChanged(groupPosition, childPositionStart, itemCount, payload);
        }

        @Override
        public void onChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
            ExpandableController.this.onChildItemRangeInserted(groupPosition, childPositionStart, itemCount);
        }

        @Override
        public void onChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
            ExpandableController.this.onChildItemRangeRemoved(groupPosition, childPositionStart, itemCount);
        }

        @Override
        public void onChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
            ExpandableController.this.onChildItemRangeMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition, itemCount);
        }
    }

    private static final class ExpandableDataObservable extends Observable<ExpandableAdapter.AdapterDataObserver> {

        // Head

        public void notifyHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onHeadItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyHeadItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onHeadItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyHeadItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onHeadItemRangeRemoved(positionStart, itemCount);
            }
        }

        // Tail

        public void notifyTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onTailItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyTailItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onTailItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyTailItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onTailItemRangeRemoved(positionStart, itemCount);
            }
        }

        // Empty

        public void notifyEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onEmptyItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyEmptyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onEmptyItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyEmptyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onEmptyItemRangeRemoved(positionStart, itemCount);
            }
        }

        // Group

        public void notifyGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onGroupItemRangeChanged(groupPositionStart, itemCount, payload);
            }
        }

        public void notifyGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onGroupItemRangeExpanded(groupPositionStart, itemCount);
            }
        }

        public void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onGroupItemRangeInserted(groupPositionStart, itemCount);
            }
        }

        public void notifyGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onGroupItemRangeRemoved(groupPositionStart, itemCount);
            }
        }

        public void notifyGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onGroupItemRangeMoved(fromGroupPosition, toGroupPosition, itemCount);
            }
        }

        // Child

        public void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onChildItemRangeChanged(groupPosition, childPositionStart, itemCount, payload);
            }
        }

        public void notifyChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onChildItemRangeInserted(groupPosition, childPositionStart, itemCount);
            }
        }

        public void notifyChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onChildItemRangeRemoved(groupPosition, childPositionStart, itemCount);
            }
        }

        public void notifyChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                this.mObservers.get(i).onChildItemRangeMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition, itemCount);
            }
        }
    }

    private static final class SavedState implements Parcelable {
        @NonNull
        private final ArrayList<ExpandMetadata> mExpandMetadataList;

        public SavedState(@NonNull ArrayList<ExpandMetadata> expandMetadataList) {
            this.mExpandMetadataList = new ArrayList<>();
            this.mExpandMetadataList.addAll(expandMetadataList);
        }

        public SavedState(@NonNull Parcel source) {
            final ClassLoader classLoader = ExpandableController.class.getClassLoader();
            this.mExpandMetadataList = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                source.readList(this.mExpandMetadataList, classLoader, ExpandMetadata.class);
            } else {
                source.readList(this.mExpandMetadataList, classLoader);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeList(this.mExpandMetadataList);
        }

        @NonNull
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(@NonNull Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
