package com.framework.core.lifecycle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/**
 * @Author create by Zhengzelong on 2022/5/18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIWeakLiveData<T> extends UILiveData<T> {

    @NonNull
    public static <T> UILiveData<T> create() {
        return new UIWeakLiveData<>();
    }

    @NonNull
    public static <T> UILiveData<T> create(@Nullable T value) {
        return new UIWeakLiveData<>(value);
    }

    public UIWeakLiveData() {
        super();
    }

    public UIWeakLiveData(@Nullable T value) {
        super(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner,
                        @NonNull Observer<? super T> observer) {
        super.observe(owner, new UILifecycleObserver<>(this, observer, owner));
    }

    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        super.observeForever(new UIObserver<>(this, observer));
    }

    private static class UIObserver<T> implements Observer<T> {
        @NonNull
        final LiveData<? extends T> liveData;
        @NonNull
        final Observer<? super T> downstream;
        volatile boolean done;

        public UIObserver(@NonNull LiveData<? extends T> liveData,
                          @NonNull Observer<? super T> downstream) {
            this.liveData = liveData;
            this.downstream = downstream;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (this.done) {
                return;
            }
            this.done = true;
            this.downstream.onChanged(t);
            this.gc(t);
        }

        @CallSuper
        public void gc(@Nullable T t) {
            this.liveData.removeObserver(this);
        }
    }

    private static class UILifecycleObserver<T> extends UIObserver<T> {
        @NonNull
        private final LifecycleOwner owner;

        public UILifecycleObserver(@NonNull LiveData<? extends T> liveData,
                                   @NonNull Observer<? super T> upstream,
                                   @NonNull LifecycleOwner owner) {
            super(liveData, upstream);
            this.owner = owner;
        }

        @Override
        public void gc(@Nullable T t) {
            super.gc(t);
            this.liveData.removeObservers(this.owner);
        }
    }
}
