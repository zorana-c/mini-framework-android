package com.framework.core.content;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.annotation.UIPageConfigure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @Author create by Zhengzelong on 2022/2/16
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIPagePlugins {
    private static Class<? extends UIPageOptions> sUIPageOptionsClass;

    static {
        sUIPageOptionsClass = UIDecorOptions.class;
    }

    public static <T extends UIPageOptions> void setUIPageOptions(@NonNull Class<T> t) {
        sUIPageOptionsClass = Objects.requireNonNull(t);
    }

    @NonNull
    static <T extends UIPageOptions> T apply(@NonNull UIPageController uiPageController) {
        Class<? extends UIPageOptions> uiPageOptionsClass = sUIPageOptionsClass;
        final UIPageConfigure uiPageConfigure = getUIPageConfigure(uiPageController);
        if (uiPageConfigure != null) {
            uiPageOptionsClass = uiPageConfigure.uiPageOptionsClass();
        }
        final T uiPageOptions = (T) generateInstance(uiPageOptionsClass);
        uiPageOptions.apply(uiPageController);
        return uiPageOptions;
    }

    @Nullable
    static UIPageConfigure getUIPageConfigure(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        return uiComponent.getClass().getAnnotation(UIPageConfigure.class);
    }

    @NonNull
    private static <T> T generateInstance(@NonNull Class<T> objectClass) {
        final Constructor<T> constructor;
        try {
            constructor = objectClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException
                 | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot create an instance of " + objectClass, e);
        }
    }
}
