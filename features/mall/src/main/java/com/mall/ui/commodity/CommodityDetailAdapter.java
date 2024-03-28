package com.mall.ui.commodity;

import androidx.annotation.NonNull;

import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageControllerOwner;
import com.mall.bean.Commodity;

/**
 * @Author create by Zhengzelong on 2024-03-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityDetailAdapter<T extends Commodity> extends UIListController.LazyAdapter<T> {
    public CommodityDetailAdapter(@NonNull UIPageControllerOwner owner) {
        super(owner);
    }
}
