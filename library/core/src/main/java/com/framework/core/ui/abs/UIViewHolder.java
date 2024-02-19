package com.framework.core.ui.abs;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIListController;
import com.framework.widget.expand.compat.ViewHolderCompat;

/**
 * @Author create by Zhengzelong on 2023-03-17
 * @Email : 171905184@qq.com
 * @Description :
 *
 * </p>
 * 边界处理 {@link ViewHolderCompat}
 */
public abstract class UIViewHolder<T> extends UIListController.ViewHolder<T> {

    public UIViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Nullable
    public final <R extends T> R getCurrentDataSource() {
        final int groupPosition = this.getGroupPosition();
        if (groupPosition == RecyclerView.NO_POSITION) {
            return null;
        }
        return this.findDataSourceBy(groupPosition);
    }

    @NonNull
    public final <R extends T> R requireCurrentDataSource() {
        final R dataSource = this.getCurrentDataSource();
        if (dataSource == null) {
            throw new NullPointerException("ERROR");
        }
        return dataSource;
    }
}
