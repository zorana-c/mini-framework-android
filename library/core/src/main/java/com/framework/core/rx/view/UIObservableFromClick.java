package com.framework.core.rx.view;

import android.view.View;

import com.framework.core.rx.UIObservable;

import io.reactivex.rxjava3.android.MainThreadDisposable;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;

/**
 * @Author create by Zhengzelong on 2022/3/7
 * @Email : 171905184@qq.com
 * @Description :
 */
final class UIObservableFromClick<@NonNull T extends View> extends UIObservable<T> {
    @NonNull
    private final T[] items;

    @SafeVarargs
    public UIObservableFromClick(@NonNull T... items) {
        super(UIObservable.empty());
        this.items = items;
    }

    @Override
    public void subscribeActual(@NonNull Observer<? super T> downstream) {
        if (!RxView.checkMainThread(downstream)) {
            return;
        }
        final ClickListener<T> listener = new ClickListener<>(downstream, this.items);
        downstream.onSubscribe(listener);
        if (listener.isDisposed()) {
            return;
        }
        for (final T item : this.items) {
            if (item == null) {
                continue;
            }
            item.setOnClickListener(listener);
        }
    }

    private static final class ClickListener<@NonNull T extends View> extends MainThreadDisposable
            implements View.OnClickListener {
        @NonNull
        private final Observer<? super T> downstream;
        @NonNull
        private final T[] items;

        @SafeVarargs
        public ClickListener(@NonNull Observer<? super T> downstream,
                             @NonNull T... items) {
            this.downstream = downstream;
            this.items = items;
        }

        @Override
        public void onClick(@NonNull View view) {
            if (this.isDisposed()) {
                return;
            }
            this.downstream.onNext((T) view);
        }

        @Override
        protected void onDispose() {
            for (final T item : this.items) {
                if (item == null) {
                    continue;
                }
                item.setOnClickListener(null);
            }
        }
    }
}
