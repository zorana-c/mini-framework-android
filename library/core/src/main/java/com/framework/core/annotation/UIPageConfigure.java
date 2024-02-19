package com.framework.core.annotation;

import androidx.annotation.NonNull;

import com.framework.core.content.UIDecorOptions;
import com.framework.core.content.UIPageOptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author create by Zhengzelong on 2021/12/1
 * @Email : 171905184@qq.com
 * @Description :
 * <pre>
 * @UIPageConfigure(uiPageOptionsClass = UIDemoOptions.class)
 * public class Foo extends UIFragment {
 *     // TODO
 * }
 * </pre>
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIPageConfigure {

    @NonNull
    Class<? extends UIPageOptions> uiPageOptionsClass()
            default UIDecorOptions.class;
}
