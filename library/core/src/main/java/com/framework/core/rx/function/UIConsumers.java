package com.framework.core.rx.function;

import android.os.Looper;

import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIDecorOptions;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.rx.UILiveInterface;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @Author create by Zhengzelong on 2023-06-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIConsumers {

    private UIConsumers() {
        throw new IllegalStateException("No instances!");
    }

    static final class LayoutOnError<T extends Throwable> implements Consumer<T> {
        @NonNull
        final UIPageController uiPageController;

        LayoutOnError(@NonNull UIPageController uiPageController) {
            this.uiPageController = uiPageController;
        }

        @Override
        public void accept(@NonNull T t) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    this.accept(t);
                });
                return;
            }
            final UIPageController pc = this.uiPageController;
            if (!pc.isCreatedView()) {
                return;
            }
            if (pc instanceof UIDecorController) {
                ((UIDecorController) pc).postErrorOnAnimation(t);
            } else {
                return;
            }
            if (pc instanceof UIListController) {
                ((UIListController<?>) pc).completeRefreshed(UIDecorOptions.MS_ANIM);
            }
        }
    }

    @NonNull
    public static <T extends Throwable> Consumer<T> layoutOnError(@NonNull UIPageControllerOwner it) {
        return layoutOnError(it.<UIPageController>getUIPageController());
    }

    @NonNull
    public static <T extends Throwable> Consumer<T> layoutOnError(@NonNull UIPageController it) {
        return new LayoutOnError<>(it);
    }

    static final class LayoutOnSubscribe<T extends Disposable> implements Consumer<T> {
        @NonNull
        final UIPageController uiPageController;

        LayoutOnSubscribe(@NonNull UIPageController uiPageController) {
            this.uiPageController = uiPageController;
        }

        @Override
        public void accept(@NonNull T t) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    this.accept(t);
                });
                return;
            }
            if (t.isDisposed()) {
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
    }

    @NonNull
    public static <T extends Disposable> Consumer<T> layoutOnSubscribe(@NonNull UIPageControllerOwner it) {
        return layoutOnSubscribe(it.<UIPageController>getUIPageController());
    }

    @NonNull
    public static <T extends Disposable> Consumer<T> layoutOnSubscribe(@NonNull UIPageController it) {
        return new LayoutOnSubscribe<>(it);
    }

    static final class SetOnceOnSubscribe<T extends Disposable> implements Consumer<T> {
        @NonNull
        final UILiveInterface<?> uiLiveInterface;

        SetOnceOnSubscribe(@NonNull UILiveInterface<?> uiLiveInterface) {
            this.uiLiveInterface = uiLiveInterface;
        }

        @Override
        public void accept(@NonNull T t) throws Throwable {
            this.uiLiveInterface.setOnce(t);
        }
    }

    @NonNull
    public static <T extends Disposable> Consumer<T> setOnceOnSubscribe(@NonNull UILiveInterface<?> it) {
        return new SetOnceOnSubscribe<>(it);
    }

    static final class SetValueOnSubscribe<T> implements Consumer<T> {
        @NonNull
        final UILiveInterface<? super T> uiLiveInterface;
        final boolean async;

        SetValueOnSubscribe(@NonNull UILiveInterface<? super T> uiLiveInterface, boolean async) {
            this.uiLiveInterface = uiLiveInterface;
            this.async = async;
        }

        @Override
        public void accept(@Nullable T t) throws Throwable {
            if (this.async) {
                this.uiLiveInterface.postValue(t);
            } else {
                this.uiLiveInterface.setValue(t);
            }
        }
    }

    @NonNull
    public static <T> Consumer<T> setValueOnSubscribe(@NonNull UILiveInterface<? super T> it) {
        return new SetValueOnSubscribe<>(it, false);
    }

    @NonNull
    public static <T> Consumer<T> postValueOnSubscribe(@NonNull UILiveInterface<? super T> it) {
        return new SetValueOnSubscribe<>(it, true);
    }

    static final class UnsubscribeOnSubscribe<T extends Disposable> implements Consumer<T> {
        @NonNull
        final Disposable upstream;

        UnsubscribeOnSubscribe(@NonNull Disposable upstream) {
            this.upstream = upstream;
        }

        @Override
        public void accept(@NonNull T t) throws Throwable {
            this.upstream.dispose();
        }
    }

    @NonNull
    public static <T extends Disposable> Consumer<T> unsubscribeOnSubscribe(@NonNull Disposable it) {
        return new UnsubscribeOnSubscribe<>(it);
    }
}
