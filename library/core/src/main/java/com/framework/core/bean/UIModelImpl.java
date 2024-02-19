package com.framework.core.bean;

/**
 * @Author create by Zhengzelong on 2023-08-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIModelImpl implements UIModelInterface {
    private final long _ID = System.nanoTime();

    public final long nanoId() {
        return this._ID;
    }
}
