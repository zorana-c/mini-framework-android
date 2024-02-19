package com.framework.core.content;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-07-05
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIDataControllers {

    private UIDataControllers() {
        throw new IllegalStateException("No instances!");
    }

    static final class RecyclerDataObserver implements UIDataController.DataObserver {
        @NonNull
        final RecyclerView.Adapter<?> adapter;

        RecyclerDataObserver(@NonNull RecyclerView.Adapter<?> adapter) {
            this.adapter = adapter;
        }

        @Override
        @SuppressLint("NotifyDataSetChanged")
        public boolean onItemRangeInserted(int positionStart, int itemCount) {
            this.adapter.notifyDataSetChanged();
            return true;
        }

        @Override
        @SuppressLint("NotifyDataSetChanged")
        public boolean onItemRangeRemoved(int positionStart, int itemCount) {
            this.adapter.notifyDataSetChanged();
            return true;
        }

        @Override
        @SuppressLint("NotifyDataSetChanged")
        public boolean onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            this.adapter.notifyDataSetChanged();
            return true;
        }
    }

    @NonNull
    public static UIDataController.DataObserver observer(@NonNull RecyclerView.Adapter<?> adapter) {
        return new RecyclerDataObserver(adapter);
    }
}
