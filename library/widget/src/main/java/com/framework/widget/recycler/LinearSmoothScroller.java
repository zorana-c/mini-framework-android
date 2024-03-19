package com.framework.widget.recycler;

import android.content.Context;
import android.graphics.PointF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2021/12/30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class LinearSmoothScroller extends androidx.recyclerview.widget.LinearSmoothScroller {
    public static final int SNAP_TO_CENTER = 2;

    public LinearSmoothScroller(@NonNull Context context) {
        super(context);
    }

    public boolean isReverseLayout(@Nullable RecyclerView.LayoutManager layoutManager) {
        final int itemCount = layoutManager.getItemCount() - 1;
        final PointF vectorForEnd = this.computeScrollVectorForPosition(itemCount);
        if (vectorForEnd != null) {
            return vectorForEnd.x < 0 || vectorForEnd.y < 0;
        }
        return false;
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd,
                                int boxStart, int boxEnd, int snapPreference) {
        if (snapPreference == SNAP_TO_CENTER) {
            final int childCenter = viewStart + ((viewEnd - viewStart) / 2);
            final int containerCenter = boxStart + ((boxEnd - boxStart) / 2);
            return containerCenter - childCenter;
        }
        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
    }
}
