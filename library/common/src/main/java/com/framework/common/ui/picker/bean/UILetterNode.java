package com.framework.common.ui.picker.bean;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UILetterNode extends UINode<UICountryNode> {
    @Nullable
    private String name;

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public final String getName() {
        return name;
    }

    @Nullable
    @Override
    public String toString() {
        return this.name;
    }
}
