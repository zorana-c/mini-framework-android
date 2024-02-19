package com.framework.core;

import android.app.Application;

/**
 * @Author create by Zhengzelong on 2021/12/28
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIFramework.init(this);
    }
}
