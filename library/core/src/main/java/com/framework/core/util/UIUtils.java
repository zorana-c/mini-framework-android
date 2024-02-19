package com.framework.core.util;

import androidx.annotation.NonNull;

import com.framework.core.rx.UIObservable;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @Author create by Zhengzelong on 2023-12-28
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIUtils {
    private UIUtils() {
        throw new IllegalStateException("No instances!");
    }

    /**
     * 仅执行一次回调
     *
     * @param timeCount 总时长(秒)
     */
    @NonNull
    public static Disposable setTimeout(@NonNull Action action, int timeCount) {
        return UIObservable.intervalRange(
                        // 起始时间
                        0L,
                        // 时间数量
                        timeCount,
                        // 延迟时间
                        0L,
                        // 间隔时间
                        1000L,
                        // 时间单位
                        TimeUnit.MILLISECONDS)
                .buffer(timeCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> action.run());
    }

    /**
     * 间隔1秒时间执行一次回调
     *
     * @param timeCount 总时长(秒)
     */
    @NonNull
    public static Disposable setInterval(@NonNull Consumer<Integer> consumer, int timeCount) {
        return UIObservable.intervalRange(
                        // 起始时间
                        0L,
                        // 时间数量
                        timeCount,
                        // 延迟时间
                        0L,
                        // 间隔时间
                        1000L,
                        // 时间单位
                        TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> consumer.accept(it.intValue()));
    }
}
