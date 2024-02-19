package com.framework.core.rx.function;

import android.view.View;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIObservers {

    private UIObservers() {
        throw new IllegalStateException("No instances!");
    }

    static final class ClickSubscribe<T extends View> implements Observer<T> {
        @NonNull
        final View.OnClickListener listener;

        ClickSubscribe(@NonNull View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable disposable) {
            // no-op
        }

        @Override
        public void onError(@NonNull Throwable throwable) {
            // no-op
        }

        @Override
        public void onNext(@NonNull T t) {
            this.listener.onClick(t);
        }

        @Override
        public void onComplete() {
            // no-op
        }
    }

    @NonNull
    public static <T extends View> Observer<T> clickSubscribe(@NonNull View.OnClickListener listener) {
        return new ClickSubscribe<>(listener);
    }
}
