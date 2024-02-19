package com.framework.demo.rx;

import androidx.annotation.NonNull;

import com.framework.core.rx.UIObservable;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public class TestObservable<T> extends UIObservable<T> {

    @NonNull
    public static <T> TestObservable<T> empty() {
        return TestObservable.wrap(UIObservable.empty());
    }

    @NonNull
    public static <T> TestObservable<T> wrap(@NonNull ObservableSource<T> source) {
        if (source instanceof TestObservable) {
            return (TestObservable<T>) RxJavaPlugins.onAssembly((TestObservable<T>) source);
        }
        return (TestObservable<T>) RxJavaPlugins.onAssembly(new TestObservable<>(source));
    }

    public TestObservable(@NonNull ObservableSource<T> source) {
        super(source);
    }
}
