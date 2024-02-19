package com.framework.common.ui.picker.factory;

import androidx.annotation.NonNull;

import com.framework.common.ui.picker.bean.UICountryNode;
import com.framework.common.ui.picker.bean.UILetterNode;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2022-11-16
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UICountrySources {
    /**
     * 获取城市或地区列表
     */
    @NonNull
    List<UILetterNode> queryLetterList();

    /**
     * 获取国家地区列表
     */
    @NonNull
    List<UICountryNode> queryCountryList();
}
