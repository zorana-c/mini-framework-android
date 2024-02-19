package com.framework.core.rx;

import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.rx.function.UIActions;
import com.framework.core.rx.function.UIConsumers;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @Author create by Zhengzelong on 2022/1/25
 * @Email : 171905184@qq.com
 * @Description :常用函数扩展
 */
public class UIObservable<@NonNull T> extends AbstractObservableWithUpstream<T, T> {

    @NonNull
    public static <T> UIObservable<T> empty() {
        return UIObservable.wrap(Observable.empty());
    }

    @NonNull
    public static <T> UIObservable<T> wrap(@NonNull ObservableSource<T> source) {
        if (source instanceof UIObservable) {
            return (UIObservable<T>) RxJavaPlugins.onAssembly((UIObservable<T>) source);
        }
        return (UIObservable<T>) RxJavaPlugins.onAssembly(new UIObservable<>(source));
    }

    public UIObservable(@NonNull ObservableSource<T> source) {
        super(source);
    }

    @Override
    protected void subscribeActual(@NonNull Observer<? super T> downstream) {
        this.source.subscribe(downstream);
    }

    @NonNull
    public final UIObservable<T> subscribeOnIO() {
        return UIObservable.wrap(this.subscribeOn(Schedulers.io()));
    }

    @NonNull
    public final UIObservable<T> subscribeOnNewThread() {
        return UIObservable.wrap(this.subscribeOn(Schedulers.newThread()));
    }

    @NonNull
    public final UIObservable<T> observeOnMainThread() {
        return UIObservable.wrap(this.observeOn(AndroidSchedulers.mainThread()));
    }

    @NonNull
    public final UIObservable<T> delay(long timeMs) {
        return UIObservable.wrap(this.delay(timeMs, TimeUnit.MILLISECONDS));
    }

    @NonNull
    public final UIObservable<T> throttleLast() {
        return this.throttleLast(250L);
    }

    @NonNull
    public final UIObservable<T> throttleLast(long windowDurationMs) {
        return UIObservable.wrap(this.throttleLast(windowDurationMs, TimeUnit.MILLISECONDS));
    }

    @NonNull
    public final UIObservable<T> throttleFirst() {
        return this.throttleFirst(250L);
    }

    @NonNull
    public final UIObservable<T> throttleFirst(long windowDurationMs) {
        return UIObservable.wrap(this.throttleFirst(windowDurationMs, TimeUnit.MILLISECONDS));
    }

    @NonNull
    public final Disposable subscribeSet(@NonNull UILiveInterface<? super T> source) {
        return this.subscribeSet(source, Functions.ON_ERROR_MISSING);
    }

    @NonNull
    public final Disposable subscribeSet(@NonNull UILiveInterface<? super T> source,
                                         @NonNull Consumer<? super Throwable> onError) {
        return this.subscribeSet(source, onError, Functions.EMPTY_ACTION);
    }

    @NonNull
    public final Disposable subscribeSet(@NonNull UILiveInterface<? super T> source,
                                         @NonNull Consumer<? super Throwable> onError,
                                         @NonNull Action onComplete) {
        return this.doOnSubscribe(UIConsumers.setOnceOnSubscribe(source))
                .subscribe(UIConsumers.setValueOnSubscribe(source), onError, onComplete);
    }

    @NonNull
    public final Disposable subscribePost(@NonNull UILiveInterface<? super T> source) {
        return this.subscribePost(source, Functions.ON_ERROR_MISSING);
    }

    @NonNull
    public final Disposable subscribePost(@NonNull UILiveInterface<? super T> source,
                                          @NonNull Consumer<? super Throwable> onError) {
        return this.subscribePost(source, onError, Functions.EMPTY_ACTION);
    }

    @NonNull
    public final Disposable subscribePost(@NonNull UILiveInterface<? super T> source,
                                          @NonNull Consumer<? super Throwable> onError,
                                          @NonNull Action onComplete) {
        return this.doOnSubscribe(UIConsumers.setOnceOnSubscribe(source))
                .subscribe(UIConsumers.postValueOnSubscribe(source), onError, onComplete);
    }

    @NonNull
    public final UIObservable<T> unsubscribe(@NonNull Disposable upstream) {
        return UIObservable.wrap(this.doOnSubscribe(UIConsumers.unsubscribeOnSubscribe(upstream)));
    }

    @NonNull
    public final UIObservable<T> doOnSubscribeWithLayout(@NonNull UIPageControllerOwner owner) {
        return this.doOnSubscribeWithLayout(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> doOnSubscribeWithLayout(@NonNull UIPageController uiPageController) {
        return UIObservable.wrap(this.doOnSubscribe(UIConsumers.layoutOnSubscribe(uiPageController)));
    }

    @NonNull
    public final UIObservable<T> doOnErrorWithLayout(@NonNull UIPageControllerOwner owner) {
        return this.doOnErrorWithLayout(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> doOnErrorWithLayout(@NonNull UIPageController uiPageController) {
        return UIObservable.wrap(this.doOnError(UIConsumers.layoutOnError(uiPageController)));
    }

    @NonNull
    public final UIObservable<T> doOnCompleteWithLayout(@NonNull UIPageControllerOwner owner) {
        return this.doOnCompleteWithLayout(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> doOnCompleteWithLayout(@NonNull UIPageController uiPageController) {
        return UIObservable.wrap(this.doOnComplete(UIActions.layoutOnComplete(uiPageController)));
    }

    @NonNull
    public final UIObservable<T> doOnDisposeWithLayout(@NonNull UIPageControllerOwner owner) {
        return this.doOnDisposeWithLayout(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> doOnDisposeWithLayout(@NonNull UIPageController uiPageController) {
        return UIObservable.wrap(this.doOnDispose(UIActions.layoutOnComplete(uiPageController)));
    }

    @NonNull
    public final UIObservable<T> subscribeWithLayout(@NonNull UIPageControllerOwner owner) {
        return this.subscribeWithLayout(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> subscribeWithLayout(@NonNull UIPageController uiPageController) {
        return UIObservable.wrap(new UIObservableFromLayout<>(this, uiPageController));
    }

    @NonNull
    public final UIObservable<T> subscribeWithDialog(@NonNull UIPageControllerOwner owner) {
        return this.subscribeWithDialog(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public final UIObservable<T> subscribeWithDialog(@NonNull UIPageController uiPageController) {
        return this.subscribeWithDialog(new UILambdaDialog.Builder(uiPageController).build());
    }

    @NonNull
    public final UIObservable<T> subscribeWithDialog(@NonNull UIDialogCallback<? super T> callback) {
        return UIObservable.wrap(new UIObservableFromDialog<>(this, callback));
    }
}
