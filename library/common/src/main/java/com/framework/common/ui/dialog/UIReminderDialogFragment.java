package com.framework.common.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.common.R;
import com.navigation.floating.UIDialogFragment;

/**
 * @Author create by Zhengzelong on 2023-05-26
 * @Email : 171905184@qq.com
 * @Description : 提醒对话框
 */
public class UIReminderDialogFragment extends UIDialogFragment {
    private int gravity = Gravity.NO_GRAVITY;
    @Nullable
    private CharSequence contentText;
    @Nullable
    private CharSequence confirmText;
    @Nullable
    private OnClickComponent confirmClickComponent;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.ui_dialog_reminder_layout;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        TextView it;
        // Sets content text.
        it = this.requireViewById(R.id.contentTextView);
        final int gravity = this.gravity;
        if (gravity != Gravity.NO_GRAVITY) {
            it.setGravity(gravity);
        }
        final CharSequence contentText = this.contentText;
        if (!TextUtils.isEmpty(contentText)) {
            it.setText(contentText);
        }
        // Sets confirm text.
        it = this.requireViewById(R.id.confirmTextView);
        it.setOnClickListener(this::onClick);
        final CharSequence confirmText = this.confirmText;
        if (!TextUtils.isEmpty(confirmText)) {
            it.setText(confirmText);
        }
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        // no-op
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setContentText(@Nullable CharSequence text) {
        this.contentText = text;
    }

    public void setConfirmText(@Nullable CharSequence text) {
        this.confirmText = text;
    }

    public void setConfirmClickComponent(@Nullable OnClickComponent c) {
        this.confirmClickComponent = c;
    }

    private void onClick(@NonNull View it) {
        if (R.id.confirmTextView == it.getId()) {
            if (this.confirmClickComponent != null) {
                this.confirmClickComponent.onClick(this);
            } else {
                this.getUINavigatorController().navigateUp(true);
            }
        }
    }

    public interface OnClickComponent {

        void onClick(@NonNull UIReminderDialogFragment it);
    }
}
