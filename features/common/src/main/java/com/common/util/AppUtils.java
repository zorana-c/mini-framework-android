package com.common.util;

import android.os.Process;

/**
 * @Author create by Zhengzelong on 2024-02-01
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class AppUtils {
    private AppUtils() {
        throw new IllegalStateException("No instances!");
    }
    
    public static void exitApplication() {
        // 通知回收
        System.gc();
        // 退出虚拟机
        System.exit(0);
        // 杀掉当前进程
        Process.killProcess(Process.myPid());
    }
}
