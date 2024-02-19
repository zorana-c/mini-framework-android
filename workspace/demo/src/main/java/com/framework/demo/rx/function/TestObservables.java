package com.framework.demo.rx.function;

import com.framework.demo.rx.TestObservable;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableConverter;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class TestObservables {

    private TestObservables() {
        throw new IllegalStateException("No instances!");
    }

    static final class ObservableTo<T> implements ObservableConverter<T, TestObservable<T>> {

        @Override
        public TestObservable<T> apply(@NonNull Observable<T> upstream) {
            return TestObservable.wrap(upstream);
        }
    }

    @NonNull
    public static <T> ObservableConverter<T, TestObservable<T>> asObservable() {
        return new ObservableTo<>();
    }
}
