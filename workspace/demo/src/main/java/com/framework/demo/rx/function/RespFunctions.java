package com.framework.demo.rx.function;

import androidx.lifecycle.LiveData;

import com.common.http.Resp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.functions.Function;

/**
 * @Author create by Zhengzelong on 2023-07-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class RespFunctions {

    private RespFunctions() {
        throw new IllegalStateException("No instances!");
    }

    static final class ToBody<T> implements Function<Resp<T>, T> {
        @Nullable
        final T defValue;

        private ToBody(@Nullable T defValue) {
            this.defValue = defValue;
        }

        @NonNull
        @Override
        public T apply(@NonNull Resp<T> resp) throws Throwable {
            T res = resp.body();
            if (res == null) {
                res = this.defValue;
            }
            if (res == null) {
                throw new NullPointerException("ERROR");
            }
            return res;
        }
    }

    /**
     * Resp<T> 转换 T
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<Resp<T>, T> toBody() {
        return toBody(null);
    }

    /**
     * Resp<T> 转换 T
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<Resp<T>, T> toBody(@Nullable T defValue) {
        return new ToBody<>(defValue);
    }

    static final class ToResp<T> implements Function<T, Resp<T>> {
        private ToResp() {
        }

        @NonNull
        @Override
        public Resp<T> apply(@NonNull T res) throws Throwable {
            return Resp.success(res);
        }
    }

    /**
     * T 转换 Resp<T>
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<T, Resp<T>> toResp() {
        return new ToResp<>();
    }

    static final class MergeList<T> implements Function<Resp<? extends List<T>>, Resp<List<T>>> {
        @Nullable
        final Resp<? extends List<T>> oldResp;

        private MergeList(@Nullable Resp<? extends List<T>> oldResp) {
            this.oldResp = oldResp;
        }

        @NonNull
        @Override
        public Resp<List<T>> apply(@NonNull Resp<? extends List<T>> resp) throws Throwable {
            final Resp<? extends List<T>> oldResp = this.oldResp;
            final Resp<? extends List<T>> newResp = resp;
            final List<T> res = new ArrayList<>();
            List<T> oldRes = null;
            List<T> newRes;

            if (oldResp != null) {
                oldRes = oldResp.body();
            }
            newRes = newResp.body();

            if (oldRes != null) {
                res.addAll(oldRes);
            }
            if (newRes != null) {
                res.addAll(newRes);
            }
            return Resp.with(newResp.code(), res, newResp.message());
        }
    }

    /**
     * Resp<List<T>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<Resp<? extends List<T>>, Resp<List<T>>> mergeList(@NonNull LiveData<Resp<List<T>>> ld) {
        return mergeList(ld.getValue());
    }

    /**
     * Resp<List<T>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<Resp<? extends List<T>>, Resp<List<T>>> mergeList(@NonNull List<T> oldBody) {
        return mergeList(Resp.success(oldBody));
    }

    /**
     * Resp<List<T>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <T> Function<Resp<? extends List<T>>, Resp<List<T>>> mergeList(@Nullable Resp<List<T>> oldResp) {
        return new MergeList<>(oldResp);
    }

    static final class MergeMap<K, V> implements Function<Resp<? extends Map<K, V>>, Resp<Map<K, V>>> {
        @Nullable
        final Resp<? extends Map<K, V>> oldResp;

        private MergeMap(@Nullable Resp<? extends Map<K, V>> oldResp) {
            this.oldResp = oldResp;
        }

        @NonNull
        @Override
        public Resp<Map<K, V>> apply(@NonNull Resp<? extends Map<K, V>> resp) throws Throwable {
            final Resp<? extends Map<K, V>> oldResp = this.oldResp;
            final Resp<? extends Map<K, V>> newResp = resp;
            final Map<K, V> res = new HashMap<>();
            Map<K, V> oldRes = null;
            Map<K, V> newRes;

            if (oldResp != null) {
                oldRes = oldResp.body();
            }
            newRes = newResp.body();

            if (oldRes != null) {
                res.putAll(oldRes);
            }
            if (newRes != null) {
                res.putAll(newRes);
            }
            return Resp.with(newResp.code(), res, newResp.message());
        }
    }

    /**
     * Resp<Map<K, V>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <K, V> Function<Resp<? extends Map<K, V>>, Resp<Map<K, V>>> mergeMap(@NonNull LiveData<Resp<Map<K, V>>> ld) {
        return mergeMap(ld.getValue());
    }

    /**
     * Resp<Map<K, V>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <K, V> Function<Resp<? extends Map<K, V>>, Resp<Map<K, V>>> mergeMap(@Nullable Map<K, V> oldBody) {
        return mergeMap(Resp.success(oldBody));
    }

    /**
     * Resp<Map<K, V>> 合并
     * {@link io.reactivex.rxjava3.core.Observable#map(Function)}
     */
    @NonNull
    public static <K, V> Function<Resp<? extends Map<K, V>>, Resp<Map<K, V>>> mergeMap(@Nullable Resp<Map<K, V>> oldResp) {
        return new MergeMap<>(oldResp);
    }
}
