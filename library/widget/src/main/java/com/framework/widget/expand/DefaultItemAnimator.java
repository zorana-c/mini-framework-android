package com.framework.widget.expand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-22
 * @Email : 171905184@qq.com
 * @Description :
 * </pre>
 * 作用: (建议展开/收起时使用)
 * 1.展开/收起Group组时, 重置Item的透明度
 */
public class DefaultItemAnimator extends androidx.recyclerview.widget.DefaultItemAnimator {
    // 下面类型默认是 不透明(状态改变时)
    private int positionTypeFlags = PositionType.TYPE_NONE
            | PositionType.TYPE_HEAD
            | PositionType.TYPE_TAIL
            | PositionType.TYPE_EMPTY
            | PositionType.TYPE_GROUP;

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull RecyclerView.ViewHolder newHolder,
                                 int fromX,
                                 int fromY,
                                 int toX,
                                 int toY) {
        final boolean handled;
        handled = super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
        this.resetItemAlpha(oldHolder);
        this.resetItemAlpha(newHolder);
        return handled;
    }

    private void resetItemAlpha(@Nullable RecyclerView.ViewHolder holder) {
        if (holder instanceof ExpandableRecyclerView.ViewHolder) {
            final ExpandableRecyclerView.ViewHolder expHolder;
            expHolder = (ExpandableRecyclerView.ViewHolder) holder;
            final int positionType = expHolder.getPositionType();
            if (PositionType.TYPE_NONE == positionType) {
                return;
            }
            if (this.hasFullSpanFlags(positionType)) {
                expHolder.itemView.setAlpha(1.f);
            }
        }
    }

    @NonNull
    public DefaultItemAnimator setFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags = positionTypeFlags;
        return this;
    }

    @NonNull
    public DefaultItemAnimator addFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags |= positionTypeFlags;
        return this;
    }

    @NonNull
    public DefaultItemAnimator delFullSpanFlags(int positionTypeFlags) {
        this.positionTypeFlags &= ~positionTypeFlags;
        return this;
    }

    @NonNull
    public DefaultItemAnimator clearFullSpanFlags() {
        this.positionTypeFlags = PositionType.TYPE_NONE;
        return this;
    }

    public boolean hasFullSpanFlags(int positionTypeFlags) {
        return (this.positionTypeFlags & positionTypeFlags) != 0;
    }
}
