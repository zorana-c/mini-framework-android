package com.framework.core.rx.function;

import com.framework.core.rx.UIObservable;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableConverter;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIObservables {

    private UIObservables() {
        throw new IllegalStateException("No instances!");
    }

    static final class ObservableTo<T> implements ObservableConverter<T, UIObservable<T>> {

        @Override
        public UIObservable<T> apply(@NonNull Observable<T> upstream) {
            return UIObservable.wrap(upstream);
        }
    }

    @NonNull
    public static <T> ObservableConverter<T, UIObservable<T>> asObservable() {
        return new ObservableTo<>();
    }
}
