package com.framework.core.rx.function;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIFunctions {

    private UIFunctions() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static final Consumer<Throwable> EMPTY_ERROR_CONSUMER = new EmptyErrorConsumer();

    static final class EmptyErrorConsumer implements Consumer<Throwable> {
        EmptyErrorConsumer() {
            // nothing
        }

        @Override
        public void accept(@NonNull Throwable throwable) {
            // nothing
        }
    }
}
