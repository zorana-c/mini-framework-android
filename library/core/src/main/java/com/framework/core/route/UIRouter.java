package com.framework.core.route;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ServiceLoader;

/**
 * @Author create by Zhengzelong on 2023-12-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIRouter {
    @Nullable
    private static UIRouter sRouter;

    public static void init(@NonNull Context context) {
        if (sRouter == null) {
            sRouter = new UIRouter(context);
        }
    }

    @NonNull
    static <T extends UIRoute> T get(@NonNull Class<T> tClass) {
        if (sRouter == null) {
            throw new IllegalStateException("Not init");
        }
        return sRouter.getRoute(tClass);
    }

    @NonNull
    private final ServiceLoader<UIRoute> mServiceLoader;

    private UIRouter(@NonNull Context context) {
        this.mServiceLoader = ServiceLoader.load(UIRoute.class);
        this.mServiceLoader.reload();

        for (@NonNull UIRoute route : this.mServiceLoader) {
            route.init(context);
        }
    }

    @NonNull
    private <T extends UIRoute> T getRoute(@NonNull Class<T> tClass) {
        for (@NonNull UIRoute route : this.mServiceLoader) {
            if (tClass.isAssignableFrom(UIRoute.class)) {
                continue;
            }
            if (tClass.isInstance(route)) {
                return (T) route;
            }
        }
        throw new IllegalStateException(
                "Not find route: " + tClass.getName());
    }
}
