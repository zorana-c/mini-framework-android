package com.framework.core.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.cache.UISharedCaches;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @Author create by Zhengzelong on 2021/3/24
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIModelInterface extends Serializable, Cloneable {

    /**
     * 从本地获取缓存数据
     *
     * @return T
     */
    @Nullable
    static <T extends UIModelInterface> T get(@NonNull Class<T> tClass) {
        return get(tClass, null);
    }

    /**
     * 从本地获取缓存数据
     *
     * @return T
     */
    @Nullable
    static <T extends UIModelInterface> T get(@NonNull Class<T> tClass,
                                              @Nullable T defValue) {
        final UISharedCaches uiSharedCaches;
        uiSharedCaches = UISharedCaches.get();
        return uiSharedCaches.getModel(tClass, defValue);
    }

    /**
     * 从本地删除缓存数据
     *
     * @return T
     */
    @Nullable
    static <T extends UIModelInterface> T del(@NonNull Class<T> tClass) {
        final T t = get(tClass);
        if (t != null) {
            t.deleteFromShared();
        }
        return t;
    }

    /**
     * 持久性保存数据到本地
     */
    default void saveToShared() {
        final UISharedCaches uiSharedCaches;
        uiSharedCaches = UISharedCaches.get();
        uiSharedCaches.putModel(this);
    }

    /**
     * 从本地删除缓存数据
     */
    default void deleteFromShared() {
        final UISharedCaches uiSharedCaches;
        uiSharedCaches = UISharedCaches.get();
        uiSharedCaches.remove(this);
    }

    /**
     * 转换成JSON字符串
     */
    @NonNull
    default String toJsonString() {
        return new Gson().toJson(this);
    }
}
