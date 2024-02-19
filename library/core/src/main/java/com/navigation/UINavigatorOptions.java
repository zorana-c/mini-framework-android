package com.navigation;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;

import com.framework.core.R;

/**
 * @Author create by Zhengzelong on 2021/12/3
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UINavigatorOptions {
    @NonNull
    public static final UINavigatorOptions EMPTY = new UINavigatorOptions.Builder()
            .build();
    @NonNull
    public static final UINavigatorOptions DEFAULT = new UINavigatorOptions.Builder()
            .setExitAnim(R.anim.slide_out_to_left)
            .setEnterAnim(R.anim.slide_in_from_right)
            .setPopExitAnim(R.anim.slide_out_to_right)
            .setPopEnterAnim(R.anim.slide_in_from_left)
            .build();

    @AnimRes
    @AnimatorRes
    private final int mEnterAnim;
    @AnimRes
    @AnimatorRes
    private final int mExitAnim;
    @AnimRes
    @AnimatorRes
    private final int mPopEnterAnim;
    @AnimRes
    @AnimatorRes
    private final int mPopExitAnim;

    UINavigatorOptions(@NonNull Builder builder) {
        this.mEnterAnim = builder.mEnterAnim;
        this.mExitAnim = builder.mExitAnim;
        this.mPopEnterAnim = builder.mPopEnterAnim;
        this.mPopExitAnim = builder.mPopExitAnim;
    }

    @AnimRes
    @AnimatorRes
    public final int getEnterAnim() {
        return this.mEnterAnim;
    }

    @AnimRes
    @AnimatorRes
    public final int getExitAnim() {
        return this.mExitAnim;
    }

    @AnimRes
    @AnimatorRes
    public final int getPopEnterAnim() {
        return this.mPopEnterAnim;
    }

    @AnimRes
    @AnimatorRes
    public final int getPopExitAnim() {
        return this.mPopExitAnim;
    }

    @NonNull
    public final Builder rebuild() {
        return new Builder(this);
    }

    public static class Builder {
        @AnimRes
        @AnimatorRes
        private int mEnterAnim = -1;
        @AnimRes
        @AnimatorRes
        private int mExitAnim = -1;
        @AnimRes
        @AnimatorRes
        private int mPopEnterAnim = -1;
        @AnimRes
        @AnimatorRes
        private int mPopExitAnim = -1;

        public Builder() {
        }

        Builder(@NonNull UINavigatorOptions options) {
            this.mEnterAnim = options.mEnterAnim;
            this.mExitAnim = options.mExitAnim;
            this.mPopEnterAnim = options.mPopEnterAnim;
            this.mPopExitAnim = options.mPopExitAnim;
        }

        @NonNull
        public Builder setEnterAnim(@AnimRes @AnimatorRes int enterAnim) {
            this.mEnterAnim = enterAnim;
            return this;
        }

        @NonNull
        public Builder setExitAnim(@AnimRes @AnimatorRes int exitAnim) {
            this.mExitAnim = exitAnim;
            return this;
        }

        @NonNull
        public Builder setPopEnterAnim(@AnimRes @AnimatorRes int popEnterAnim) {
            this.mPopEnterAnim = popEnterAnim;
            return this;
        }

        @NonNull
        public Builder setPopExitAnim(@AnimRes @AnimatorRes int popExitAnim) {
            this.mPopExitAnim = popExitAnim;
            return this;
        }

        @NonNull
        public UINavigatorOptions build() {
            return new UINavigatorOptions(this);
        }
    }
}
