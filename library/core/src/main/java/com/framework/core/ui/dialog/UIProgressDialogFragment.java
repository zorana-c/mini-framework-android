package com.framework.core.ui.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.framework.core.R;
import com.navigation.floating.UIDialogFragment;

/**
 * @Author create by Zhengzelong on 2022/1/27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIProgressDialogFragment extends UIDialogFragment {

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.ui_dialog_progress_layout;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
