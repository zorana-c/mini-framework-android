package com.framework.widget.expand;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-08-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class ExpandMetadata implements Parcelable, Comparable<ExpandMetadata> {
    private static final int MAX_POOL_SIZE = 5;
    @NonNull
    private static final ArrayList<ExpandMetadata> sPool = new ArrayList<>(MAX_POOL_SIZE);

    @NonNull
    public static ExpandMetadata obtain(long groupId,
                                        int groupPosition,
                                        int adapterPosition,
                                        int lastChildAdapterPosition) {
        final ExpandMetadata expandMetadata = ExpandMetadata.get();
        expandMetadata.mGroupId = groupId;
        expandMetadata.mGroupPosition = groupPosition;
        expandMetadata.mAdapterPosition = adapterPosition;
        expandMetadata.mLastChildAdapterPosition = lastChildAdapterPosition;
        return expandMetadata;
    }

    @NonNull
    private static ExpandMetadata get() {
        final ExpandMetadata expandMetadata;
        synchronized (sPool) {
            if (sPool.size() > 0) {
                expandMetadata = sPool.remove(0);
            } else {
                expandMetadata = new ExpandMetadata();
            }
        }
        expandMetadata.reset();
        return expandMetadata;
    }

    private long mGroupId;
    private int mGroupPosition;
    private int mAdapterPosition;
    private int mLastChildAdapterPosition;

    private ExpandMetadata() {
        // nothing
    }

    public long getGroupId() {
        return this.mGroupId;
    }

    public void setGroupId(long groupId) {
        this.mGroupId = groupId;
    }

    public int getGroupPosition() {
        return this.mGroupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.mGroupPosition = groupPosition;
    }

    public int getAdapterPosition() {
        return this.mAdapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.mAdapterPosition = adapterPosition;
    }

    public int getLastChildAdapterPosition() {
        return this.mLastChildAdapterPosition;
    }

    public void setLastChildAdapterPosition(int adapterPosition) {
        this.mLastChildAdapterPosition = adapterPosition;
    }

    public int getChildItemCount() {
        final int l = this.mLastChildAdapterPosition;
        final int f = this.mAdapterPosition;
        return Math.max(0, l - f);
    }

    public void recycle() {
        synchronized (sPool) {
            if (sPool.size() < MAX_POOL_SIZE) {
                sPool.add(this);
            }
        }
    }

    private void reset() {
        this.mGroupId = RecyclerView.NO_ID;
        this.mGroupPosition = RecyclerView.NO_POSITION;
        this.mAdapterPosition = RecyclerView.NO_POSITION;
        this.mLastChildAdapterPosition = RecyclerView.NO_POSITION;
    }

    @Override
    public int compareTo(@Nullable ExpandMetadata expandMetadata) {
        if (expandMetadata == null) {
            throw new IllegalArgumentException("ERROR");
        }
        return this.mGroupPosition - expandMetadata.mGroupPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.mGroupId);
        dest.writeInt(this.mGroupPosition);
        dest.writeInt(this.mAdapterPosition);
        dest.writeInt(this.mLastChildAdapterPosition);
    }

    @Override
    public String toString() {
        return "ExpandMetadata{" +
                "mGroupId=" + this.mGroupId +
                ", mGroupPosition=" + this.mGroupPosition +
                ", mAdapterPosition=" + this.mAdapterPosition +
                ", mLastChildAdapterPosition=" + this.mLastChildAdapterPosition +
                '}';
    }

    @NonNull
    public static final Creator<ExpandMetadata> CREATOR = new Creator<ExpandMetadata>() {
        @NonNull
        @Override
        public ExpandMetadata createFromParcel(@NonNull Parcel in) {
            return ExpandMetadata.obtain(in.readLong(),
                    in.readInt(),
                    in.readInt(),
                    in.readInt());
        }

        @NonNull
        @Override
        public ExpandMetadata[] newArray(int size) {
            return new ExpandMetadata[size];
        }
    };
}
