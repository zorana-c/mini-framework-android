package com.framework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * @Author create by Zhengzelong on 2021/7/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CircleProgressView extends AppCompatImageView {

    public CircleProgressView(@NonNull Context context) {
        this(context, null);
    }

    public CircleProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setImageResource(R.mipmap.ic_progress_bar);
    }

    public void startAnimation() {
        Animation animation = this.getAnimation();
        if (animation == null) {
            animation = this.getRotateAnimation();
        }
        if (animation.hasStarted()) {
            return;
        }
        this.startAnimation(animation);
    }

    @NonNull
    protected RotateAnimation getRotateAnimation() {
        final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return rotateAnimation;
    }
}
