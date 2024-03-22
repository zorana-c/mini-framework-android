package com.common.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.framework.widget.sliver.SliverContainer;

/**
 * @Author create by Zhengzelong on 2024-01-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class EditTextContainer extends SliverContainer {
    @NonNull
    private final ViewGroup mContainer;
    @Nullable
    private DismissEnforcement mDismissEnforcement;

    public EditTextContainer(@NonNull Context context) {
        this(context, null);
    }

    public EditTextContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutParams layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;

        final LinearLayoutCompat linearLayout = new LinearLayoutCompat(context);
        linearLayout.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);
        this.attachViewToParent(linearLayout, 0, layoutParams);
        this.mContainer = linearLayout;

        final LinearLayoutCompat.LayoutParams cLayoutParam = new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        cLayoutParam.weight = 1.f;
        final View occupyingView = new View(context);
        occupyingView.setLayoutParams(cLayoutParam);
        occupyingView.setOnClickListener(widget -> this.dismiss());
        this.addView(occupyingView);
        this.setUserScrollEnabled(false);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void addView(@NonNull View child) {
        if (this.mContainer.getChildCount() > 1) {
            throw new IllegalStateException("Container can host only one direct child");
        }
        this.mContainer.addView(child);
    }

    @Override
    public void addView(@NonNull View child, @NonNull ViewGroup.LayoutParams params) {
        if (this.mContainer.getChildCount() > 1) {
            throw new IllegalStateException("Container can host only one direct child");
        }
        this.mContainer.addView(child, params);
    }

    @Override
    public void addView(@NonNull View child, int index, @NonNull ViewGroup.LayoutParams params) {
        if (this.mContainer.getChildCount() > 1) {
            throw new IllegalStateException("Container can host only one direct child");
        }
        this.mContainer.addView(child, index, params);
    }

    @Nullable
    public DismissEnforcement getDismissEnforcement() {
        return this.mDismissEnforcement;
    }

    public void setDismissEnforcement(@Nullable DismissEnforcement enforcement) {
        this.mDismissEnforcement = enforcement;
    }

    public final void dismiss() {
        if (this.mDismissEnforcement != null) {
            this.mDismissEnforcement.dismiss();
        }
    }

    public interface DismissEnforcement {
        void dismiss();
    }
}
