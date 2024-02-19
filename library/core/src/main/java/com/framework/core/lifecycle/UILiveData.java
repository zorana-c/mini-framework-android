package com.framework.core.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.framework.core.rx.UILiveInterface;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;

/**
 * @Author create by Zhengzelong on 2022/7/20
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UILiveData<T> extends MutableLiveData<T>
        implements UILiveInterface<T> {
    @NonNull
    volatile Disposable mUpstream = DisposableHelper.DISPOSED;

    public UILiveData() {
        super();
    }

    public UILiveData(@Nullable T value) {
        super(value);
    }

    @Nullable
    @Override
    public T getValue() {
        return super.getValue();
    }

    @NonNull
    public final T requireValue() {
        final T r = this.getValue();
        if (r == null) {
            throw new NullPointerException();
        }
        return r;
    }

    @Override
    public void setValue(@Nullable T value) {
        super.setValue(value);
    }

    @Override
    public void postValue(@Nullable T value) {
        super.postValue(value);
    }

    @Override
    public void setOnce(@NonNull Disposable upstream) {
        this.dispose();
        this.mUpstream = upstream;
    }

    @Override
    public void dispose() {
        if (this.mUpstream != DisposableHelper.DISPOSED) {
            this.mUpstream.dispose();
            this.mUpstream = DisposableHelper.DISPOSED;
        }
    }

    @Override
    public boolean isDisposed() {
        return this.mUpstream.isDisposed();
    }
}
