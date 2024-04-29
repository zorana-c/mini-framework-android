package com.common.constant;

import com.common.R;
import com.framework.core.compat.UIRes;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class Constants {
    private Constants() {
        throw new IllegalStateException("No instances!");
    }

    public static int ITEM_RADIUS;
    public static int SPLIT_VER;
    public static int SPLIT_HOR;
    public static int SPLIT_VER_HALF;
    public static int SPLIT_HOR_HALF;

    static {
        ITEM_RADIUS = UIRes.getDimensionPixelSize(R.dimen.item_radius);
        // SPLIT_VER = UIRes.getDimensionPixelSize(R.dimen.split_ver);
        // SPLIT_HOR = UIRes.getDimensionPixelSize(R.dimen.split_hor);
        // SPLIT_VER_HALF = UIRes.getDimensionPixelSize(R.dimen.split_ver_half);
        // SPLIT_HOR_HALF = UIRes.getDimensionPixelSize(R.dimen.split_hor_half);
    }
}
