package com.metamedia.bean;

import androidx.annotation.RawRes;

import com.framework.core.bean.UIModelImpl;

/**
 * @Author create by Zhengzelong on 2024-01-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public class Video extends UIModelImpl {
    @RawRes
    private final int rawId;

    public Video(@RawRes int rawId) {
        this.rawId = rawId;
    }

    @RawRes
    public int getRawId() {
        return this.rawId;
    }
}
