package com.framework.widget.expand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-08-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class PositionMetadata {
    private static final int MAX_POOL_SIZE = 5;
    @NonNull
    private static final ArrayList<PositionMetadata> sPool = new ArrayList<>(MAX_POOL_SIZE);

    @NonNull
    public static PositionMetadata obtain(@PositionType int type,
                                          int groupIndex,
                                          int groupPosition,
                                          int childPosition,
                                          int adapterPosition,
                                          @Nullable ExpandMetadata expandMetadata) {
        final PositionMetadata positionMetadata = PositionMetadata.get();
        positionMetadata.mType = type;
        positionMetadata.mGroupIndex = groupIndex;
        positionMetadata.mGroupPosition = groupPosition;
        positionMetadata.mChildPosition = childPosition;
        positionMetadata.mAdapterPosition = adapterPosition;
        positionMetadata.mExpandMetadata = expandMetadata;
        return positionMetadata;
    }

    @NonNull
    private static PositionMetadata get() {
        final PositionMetadata positionMetadata;
        synchronized (sPool) {
            if (sPool.size() > 0) {
                positionMetadata = sPool.remove(0);
            } else {
                positionMetadata = new PositionMetadata();
            }
        }
        positionMetadata.reset();
        return positionMetadata;
    }

    @PositionType
    private int mType;
    private int mGroupIndex;
    private int mGroupPosition;
    private int mChildPosition;
    private int mAdapterPosition;
    @Nullable
    private ExpandMetadata mExpandMetadata;

    private PositionMetadata() {
        // nothing
    }

    @PositionType
    public int getType() {
        return this.mType;
    }

    public int getGroupIndex() {
        return this.mGroupIndex;
    }

    public int getGroupPosition() {
        return this.mGroupPosition;
    }

    public int getChildPosition() {
        return this.mChildPosition;
    }

    public int getAdapterPosition() {
        return this.mAdapterPosition;
    }

    @Nullable
    public ExpandMetadata getExpandMetadata() {
        return this.mExpandMetadata;
    }

    public boolean isExpanded() {
        return this.mExpandMetadata != null;
    }

    public int getChildItemCount() {
        final ExpandMetadata expandMetadata;
        expandMetadata = this.mExpandMetadata;
        if (expandMetadata == null) return 0;
        return expandMetadata.getChildItemCount();
    }

    public void recycle() {
        synchronized (sPool) {
            if (sPool.size() < MAX_POOL_SIZE) {
                sPool.add(this);
            }
        }
    }

    private void reset() {
        this.mType = PositionType.TYPE_NONE;
        this.mGroupIndex = -1;
        this.mGroupPosition = RecyclerView.NO_POSITION;
        this.mChildPosition = RecyclerView.NO_POSITION;
        this.mAdapterPosition = RecyclerView.NO_POSITION;
        this.mExpandMetadata = null;
    }
}
