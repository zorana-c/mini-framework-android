package com.framework.core.listener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @Author create by Zhengzelong on 2022/4/13
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class OnEditTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable text) {
    }
}
