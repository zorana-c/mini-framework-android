package com.framework.widget.expand.compat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-23
 * @Email : 171905184@qq.com
 * @Description : LinearLayoutManager 兼容
 */
public class LinearLayoutManager extends androidx.recyclerview.widget.LinearLayoutManager {
    private RecyclerView recyclerView;

    public LinearLayoutManager(@NonNull Context context) {
        this(context, RecyclerView.VERTICAL);
    }

    public LinearLayoutManager(@NonNull Context context, int orientation) {
        this(context, orientation, false);
    }

    public LinearLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManager(@NonNull Context context, @NonNull AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutManager(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LinearLayoutManager(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToWindow(recyclerView);
    }

    @Override
    public void onDetachedFromWindow(@NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        this.recyclerView = null;
    }
}
