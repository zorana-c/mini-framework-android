package com.framework.widget.expand.compat;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.framework.widget.expand.ExpandableAdapter;
import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.expand.PositionType;

/**
 * @Author create by Zhengzelong on 2023-08-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class ViewHolderCompat {

    /**
     * 是否在 列表(Group)顶部位置
     */
    public static boolean inTop(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final int groupPosition = holder.getGroupPosition();
        return groupPosition == 0;
    }

    /**
     * 是否在 列表(Group)底部位置
     */
    public static boolean inBottom(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final ExpandableAdapter<?> adapter = holder.requireAdapter();
        final int positionType = holder.getPositionType();
        final int groupPosition = holder.getGroupPosition();
        final int itemCount;

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                itemCount = adapter.getHeadItemCount();
                break;
            case PositionType.TYPE_TAIL:
                itemCount = adapter.getTailItemCount();
                break;
            case PositionType.TYPE_EMPTY:
                itemCount = adapter.getEmptyItemCount();
                break;
            case PositionType.TYPE_GROUP:
            case PositionType.TYPE_CHILD:
                itemCount = adapter.getGroupItemCount();
                break;
            default:
                return false;
        }
        return groupPosition == itemCount - 1;
    }

    /**
     * 是否在 列表(Group)头部位置
     */
    public static boolean inHead(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final int positionType = holder.getPositionType();
        final int groupPosition = holder.getGroupPosition();
        final int childPosition = holder.getChildPosition();
        return PositionType.TYPE_CHILD == positionType
                ? childPosition == 0
                : groupPosition == 0;
    }

    /**
     * 是否在 列表(Group)尾部位置
     */
    @SuppressLint("SwitchIntDef")
    public static boolean inTail(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final ExpandableAdapter<?> adapter = holder.requireAdapter();
        final int positionType = holder.getPositionType();
        final int groupPosition = holder.getGroupPosition();
        final int childPosition = holder.getChildPosition();
        final int itemCount;

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                itemCount = adapter.getHeadItemCount();
                break;
            case PositionType.TYPE_TAIL:
                itemCount = adapter.getTailItemCount();
                break;
            case PositionType.TYPE_EMPTY:
                itemCount = adapter.getEmptyItemCount();
                break;
            case PositionType.TYPE_GROUP:
                itemCount = adapter.getGroupItemCount();
                break;
            case PositionType.TYPE_CHILD:
                itemCount = adapter.getChildItemCount(groupPosition);
                break;
            default:
                return false;
        }
        return PositionType.TYPE_CHILD == positionType
                ? childPosition == itemCount - 1
                : groupPosition == itemCount - 1;
    }

    /**
     * 是否在 列表(Group)头部和顶部位置
     */
    public static boolean inHeadAndTop(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        return ViewHolderCompat.inHead(holder) && ViewHolderCompat.inTop(holder);
    }

    /**
     * 是否在 列表(Group)头部和底部位置
     */
    public static boolean inHeadAndBottom(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        return ViewHolderCompat.inHead(holder) && ViewHolderCompat.inBottom(holder);
    }

    /**
     * 是否在 列表(Group)尾部和顶部位置
     */
    public static boolean inTailAndTop(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        return ViewHolderCompat.inTail(holder) && ViewHolderCompat.inTop(holder);
    }

    /**
     * 是否在 列表(Group)尾部和底部位置
     */
    public static boolean inTailAndBottom(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        return ViewHolderCompat.inTail(holder) && ViewHolderCompat.inBottom(holder);
    }
}
