package com.framework.core.rx;

import android.os.Looper;

import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIDecorOptions;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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
final class UIObservableFromLayout<T> extends UIObservable<T> {
    @NonNull
    private final UIPageController uiPageController;

    public UIObservableFromLayout(@NonNull ObservableSource<T> source,
                                  @NonNull UIPageController uiPageController) {
        super(source);
        this.uiPageController = uiPageController;
    }

    @Override
    protected void subscribeActual(@NonNull Observer<? super T> downstream) {
        this.source.subscribe(new LayoutLambdaObserver<>(downstream, this.uiPageController));
    }

    private static final class LayoutLambdaObserver<T> implements Observer<T>, Disposable {
        @NonNull
        private final Observer<? super T> downstream;
        @NonNull
        private final UIPageController uiPageController;
        private volatile Disposable upstream;

        private LayoutLambdaObserver(@NonNull Observer<? super T> downstream,
                                     @NonNull UIPageController uiPageController) {
            this.downstream = downstream;
            this.uiPageController = uiPageController;
        }

        @Override
        public void onSubscribe(@NonNull Disposable upstream) {
            if (DisposableHelper.validate(this.upstream, upstream)) {
                this.layoutOnSubscribe(upstream);
                this.upstream = upstream;
                this.downstream.onSubscribe(this);
            }
        }

        @Override
        public void onError(@NonNull Throwable throwable) {
            this.layoutOnError(throwable);
            this.downstream.onError(throwable);
        }

        @Override
        public void onNext(@NonNull T t) {
            this.downstream.onNext(t);
        }

        @Override
        public void onComplete() {
            this.layoutOnComplete();
            this.downstream.onComplete();
        }

        @Override
        public boolean isDisposed() {
            return this.upstream.isDisposed();
        }

        @Override
        public void dispose() {
            if (this.upstream != DisposableHelper.DISPOSED) {
                this.upstream.dispose();
                this.upstream = DisposableHelper.DISPOSED;
            }
        }

        private void layoutOnSubscribe(@NonNull Disposable upstream) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    this.layoutOnSubscribe(upstream);
                });
                return;
            }
            if (upstream.isDisposed()) {
                return;
            }
            final UIPageController pc = this.uiPageController;
            if (!pc.isCreatedView()) {
                return;
            }
            if (pc instanceof UIDecorController) {
                ((UIDecorController) pc).layoutLoadingOnAnimation();
            }
        }

        private void layoutOnError(@NonNull Throwable throwable) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    this.layoutOnError(throwable);
                });
                return;
            }
            final UIPageController pc = this.uiPageController;
            if (!pc.isCreatedView()) {
                return;
            }
            if (pc instanceof UIDecorController) {
                ((UIDecorController) pc).postErrorOnAnimation(throwable);
            } else {
                return;
            }
            if (pc instanceof UIListController) {
                ((UIListController<?>) pc).completeRefreshed(UIDecorOptions.MS_ANIM);
            }
        }

        private void layoutOnComplete() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(this::layoutOnComplete);
                return;
            }
            final UIPageController pc = this.uiPageController;
            if (!pc.isCreatedView()) {
                return;
            }
            if (pc instanceof UIDecorController) {
                ((UIDecorController) pc).postContentOnAnimation();
            }
            if (pc instanceof UIListController) {
                ((UIListController<?>) pc).completeRefreshed(UIDecorOptions.MS_ANIM);
            }
        }
    }
}
