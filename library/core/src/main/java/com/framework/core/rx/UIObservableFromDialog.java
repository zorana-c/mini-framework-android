package com.framework.core.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;

/**
 * @Author create by Zhengzelong on 2023-06-30
 * @Email : 171905184@qq.com
 * @Description :
 */
final class UIObservableFromDialog<T> extends UIObservable<T> {
    @NonNull
    private final UIDialogCallback<? super T> callback;

    public UIObservableFromDialog(@NonNull ObservableSource<T> source,
                                  @NonNull UIDialogCallback<? super T> callback) {
        super(source);
        this.callback = callback;
    }

    @Override
    protected void subscribeActual(@NonNull Observer<? super T> downstream) {
        this.source.subscribe(new DialogLambdaObserver<>(downstream, this.callback));
    }

    private static final class DialogLambdaObserver<T> implements Observer<T>, Disposable {
        @NonNull
        private final Observer<? super T> downstream;
        @NonNull
        private final UIDialogCallback<? super T> callback;
        private volatile Disposable upstream;

        public DialogLambdaObserver(@NonNull Observer<? super T> downstream,
                                    @NonNull UIDialogCallback<? super T> callback) {
            this.downstream = downstream;
            this.callback = callback;
        }

        @Override
        public void onSubscribe(@NonNull Disposable upstream) {
            if (DisposableHelper.validate(this.upstream, upstream)) {
                this.upstream = upstream;
                this.callback.onSubscribe(this);
                this.downstream.onSubscribe(this);
            }
        }

        @Override
        public void onError(@NonNull Throwable throwable) {
            this.callback.onError(throwable);
            this.downstream.onError(throwable);
        }

        @Override
        public void onNext(@NonNull T t) {
            this.callback.accept(t);
            this.downstream.onNext(t);
        }

        @Override
        public void onComplete() {
            this.callback.onComplete();
            this.downstream.onComplete();
        }

        @Override
        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        @Override
        public void dispose() {
            if (this.upstream != DisposableHelper.DISPOSED) {
                this.callback.onDispose();
                this.upstream.dispose();
                this.upstream = DisposableHelper.DISPOSED;
            }
        }
    }
}
