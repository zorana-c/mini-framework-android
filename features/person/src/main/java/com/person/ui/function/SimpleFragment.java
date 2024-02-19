package com.person.ui.function;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.framework.core.ui.abs.UIFragment;
import com.person.R;

/**
 * @Author create by Zhengzelong on 2024-02-01
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SimpleFragment extends UIFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_function_simple;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {

    }
}
