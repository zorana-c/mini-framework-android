package com.common.widget;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * @Author create by Zhengzelong on 2024-01-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class EditTextCompat extends AppCompatEditText {
    public EditTextCompat(@NonNull Context context) {
        this(context, null);
    }

    public EditTextCompat(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.editTextStyle);
    }

    public EditTextCompat(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
