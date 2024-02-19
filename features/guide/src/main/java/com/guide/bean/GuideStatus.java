package com.guide.bean;

import com.framework.core.bean.UIModelInterface;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class GuideStatus implements UIModelInterface {
    private long versionCode;

    public long getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public boolean pass(long versionCode) {
        return this.versionCode < versionCode;
    }
}
