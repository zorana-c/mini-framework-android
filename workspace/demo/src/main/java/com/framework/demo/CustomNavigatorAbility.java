package com.framework.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.framework.core.compat.UILog;
import com.navigation.UINavigatorAbility;

/**
 * @Author create by Zhengzelong on 2023-09-21
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CustomNavigatorAbility extends UINavigatorAbility {
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        UILog.e("==> CustomNavigatorAbility onUICreated: " + savedInstanceState
                + ", Arguments: " + this.getUIPageController().getArguments());

        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        if (fragment != null) {
            UILog.e("==> CustomNavigatorAbility MainRoute: " + fragment.getTag()
                    + ", ClassName: " + fragment.getClass().getName()
                    + ", Arguments: " + fragment.getArguments());
        }
    }
}
