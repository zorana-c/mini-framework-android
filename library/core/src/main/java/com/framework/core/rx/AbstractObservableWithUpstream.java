package com.framework.core.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.internal.fuseable.HasUpstreamObservableSource;

/**
 * @Author create by Zhengzelong on 2022/5/20
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class AbstractObservableWithUpstream<T, R> extends Observable<R>
        implements HasUpstreamObservableSource<T> {
    @NonNull
    protected final ObservableSource<T> source;

    public AbstractObservableWithUpstream(@NonNull ObservableSource<T> source) {
        this.source = source;
    }

    @NonNull
    @Override
    public final ObservableSource<T> source() {
        return this.source;
    }
}
