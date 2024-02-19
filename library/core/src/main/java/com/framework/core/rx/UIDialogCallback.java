package com.framework.core.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @Author create by Zhengzelong on 2023-06-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIDialogCallback<T> {

    default void onSubscribe(@NonNull Disposable d) {
        // no-op
    }

    default void onError(@NonNull Throwable e) {
        // no-op
    }

    default void accept(@NonNull T t) {
        // no-op
    }

    default void onComplete() {
        // no-op
    }

    default void onDispose() {
        // no-op
    }
}
