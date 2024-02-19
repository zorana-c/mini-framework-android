package com.framework.common.ui.picker.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.bean.UIModelInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UINode<T extends UINode<?>> implements UIModelInterface {
    @NonNull
    private final ArrayList<T> downstream = new ArrayList<>();
    @Nullable
    private UINode<?> upstream;

    public void setUpstream(@Nullable UINode<?> upstream) {
        this.upstream = upstream;
    }

    @Nullable
    public final <R extends UINode<?>> R getUpstream() {
        return (R) this.upstream;
    }

    public void setDownstream(@NonNull List<T> downstream) {
        this.downstream.clear();
        this.downstream.addAll(downstream);
    }

    @NonNull
    public final <R extends T> ArrayList<R> getDownstream() {
        return (ArrayList<R>) this.downstream;
    }
}
