package com.common.http;

import androidx.annotation.NonNull;

import com.framework.core.bean.UIModelInterface;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public class Req implements UIModelInterface {

    @NonNull
    @Override
    public String toString() {
        return this.toJsonString();
    }
}
