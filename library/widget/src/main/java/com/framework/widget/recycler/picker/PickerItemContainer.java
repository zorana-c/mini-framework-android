package com.framework.widget.recycler.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @Author create by Zhengzelong on 2023-04-13
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PickerItemContainer extends ConstraintLayout
        implements PickerLayoutManager.ClipChildCallback {
    public PickerItemContainer(@NonNull Context context) {
        super(context);
    }

    public PickerItemContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerItemContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PickerItemContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void dispatchClipChildChanged(@NonNull ViewGroup target, int left, int top, int right, int bottom) {
        final int N = this.getChildCount();
        if (N == 0) {
            return;
        }
        final int scrollX = this.getScrollX();
        final int scrollY = this.getScrollY();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                return;
            }
            if (child instanceof PickerLayoutManager.ClipChildCallback) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final PickerLayoutManager.ClipChildCallback clipChild;
                clipChild = (PickerLayoutManager.ClipChildCallback) child;
                clipChild.dispatchClipChildChanged(target,
                        left - lp.leftMargin + scrollX,
                        top - lp.topMargin + scrollY,
                        right - lp.leftMargin + scrollX,
                        bottom - lp.topMargin + scrollY);
            }
        }
    }
}
