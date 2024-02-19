package com.common;

import com.framework.core.UIApplication;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommonApplication extends UIApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CommonInit.init(this);
    }
}
