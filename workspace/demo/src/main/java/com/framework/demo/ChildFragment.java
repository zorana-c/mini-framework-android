package com.framework.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIDecorFragment;

/**
 * @Author create by Zhengzelong on 2022/7/19
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ChildFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_child;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        this.getUIActionBarController().setEnabled(false);
        RxView.of(this).click(it -> {
            this.pushChildFragment();
        }, R.id.pushChildFragment);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    private void pushChildFragment() {
        this.getUIPageController()
                .getUIParentController()
                .getUINavigatorController()
                .pushFragment(R.id.container, ChildFragment.class);
    }
}
