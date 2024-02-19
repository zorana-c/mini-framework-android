package com.framework.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.framework.demo.R;
import com.framework.widget.CircleProgressView;
import com.framework.widget.sliver.SliverRefreshLayout;
import com.framework.widget.sliver.SliverRefreshLoadLayout;

/**
 * @Author create by Zhengzelong on 2023-07-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CustomRefreshLoadView extends SliverRefreshLoadLayout {
    @NonNull
    private final AppCompatTextView atv;
    @NonNull
    private final CircleProgressView cpv;

    public CustomRefreshLoadView(@NonNull Context context) {
        this(context, null);
    }

    public CustomRefreshLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_loading_view, this);

        this.atv = this.findViewById(R.id.loadTextView);
        this.cpv = this.findViewById(R.id.loadProgressView);
    }

    @Override
    public void onRefreshStateChanged(@NonNull SliverRefreshLayout parent, int refreshState) {
        super.onRefreshStateChanged(parent, refreshState);
        if (SliverRefreshLayout.REFRESH_STATE_COMPLETE == refreshState) {
            final Context c = this.getContext();
            Animation anim;
            anim = AnimationUtils.loadAnimation(c, R.anim.slide_in_from_bottom);
            this.atv.setVisibility(View.VISIBLE);
            this.atv.startAnimation(anim);

            anim = AnimationUtils.loadAnimation(c, R.anim.anim_alpha_action_inv);
            this.cpv.clearAnimation();
            this.cpv.startAnimation(anim);
            this.cpv.setVisibility(View.GONE);
        } else {
            this.atv.clearAnimation();
            this.atv.setVisibility(View.GONE);

            this.cpv.setVisibility(View.VISIBLE);
            if (SliverRefreshLayout.REFRESH_STATE_PROGRESS == refreshState) {
                this.cpv.startAnimation();
            } else {
                this.cpv.clearAnimation();
            }
        }
    }
}
