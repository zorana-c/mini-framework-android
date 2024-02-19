package com.framework.common.ui.picker.factory;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UICountrySourcesFactory {
    @NonNull
    public static UICountrySources get() {
        return new UICountrySourcesImpl();
    }
}
