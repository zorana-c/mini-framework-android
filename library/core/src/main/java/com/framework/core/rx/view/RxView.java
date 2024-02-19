package com.framework.core.rx.view;

import android.os.Looper;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.rx.UIObservable;
import com.framework.core.rx.function.UIObservers;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class RxView {

    @NonNull
    public static RxView of(@NonNull UIPageControllerOwner owner) {
        return of(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static RxView of(@NonNull UIPageController uiPageController) {
        return new RxView(uiPageController);
    }

    @NonNull
    private final UIPageController uiPageController;

    /* package */ RxView(@NonNull UIPageController uiPageController) {
        this.uiPageController = uiPageController;
    }

    @NonNull
    public <T extends View> UIObservable<T> clickWith(@IdRes int... ids) {
        return (UIObservable<T>) this.clickWith(this.findViewById(ids));
    }

    @NonNull
    public <T extends View> UIObservable<T> clickWith(@NonNull T... items) {
        return new UIObservableFromClick<>(items);
    }

    @NonNull
    public <T extends View> void click(@NonNull View.OnClickListener listener,
                                       @IdRes int... ids) {
        this.click(listener, this.findViewById(ids));
    }

    @NonNull
    public <T extends View> void click(@NonNull View.OnClickListener listener,
                                       @NonNull T... items) {
        this.clickWith(items).subscribe(UIObservers.clickSubscribe(listener));
    }

    @NonNull
    public View[] findViewById(@NonNull @IdRes int... ids) {
        final View[] items = new View[ids.length];
        for (int index = 0; index < items.length; index++) {
            items[index] = this.uiPageController.findViewById(ids[index]);
        }
        return items;
    }

    static boolean checkMainThread(@NonNull Observer<?> observer) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return true;
        }
        observer.onSubscribe(Disposable.empty());
        observer.onError(new IllegalStateException("Expected to be "
                + "called on the main thread but was "
                + Thread.currentThread().getName()));
        return false;
    }
}
