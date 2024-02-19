package com.framework.core.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @Author create by Zhengzelong on 2023-12-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UILiveInterface<T> extends Disposable {

    void setOnce(@NonNull Disposable upstream);

    void setValue(@Nullable T value);

    void postValue(@Nullable T value);
}
