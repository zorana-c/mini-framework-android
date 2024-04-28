package com.mall.ui.commodity;

import androidx.annotation.NonNull;

import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageControllerOwner;

/**
 * @Author create by Zhengzelong on 2024-03-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityDetailAdapter extends UIListController.LazyAdapter<UIListController.ViewHolder> {
    public CommodityDetailAdapter(@NonNull UIPageControllerOwner owner) {
        super(owner);
    }
}
