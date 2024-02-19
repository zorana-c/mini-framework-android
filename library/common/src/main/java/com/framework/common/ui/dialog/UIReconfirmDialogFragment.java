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
 * @Description : 确认对话框
 */
public class UIReconfirmDialogFragment extends UIDialogFragment {
    private int titleGravity = Gravity.NO_GRAVITY;
    private int contentGravity = Gravity.NO_GRAVITY;
    @Nullable
    private CharSequence titleText;
    @Nullable
    private CharSequence contentText;
    @Nullable
    private CharSequence cancelText;
    @Nullable
    private CharSequence confirmText;
    @Nullable
    private OnClickComponent cancelClickComponent;
    @Nullable
    private OnClickComponent confirmClickComponent;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.ui_dialog_reconfirm_layout;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        TextView it;
        // Sets title text.
        it = this.requireViewById(R.id.titleTextView);
        final int titleGravity = this.titleGravity;
        if (titleGravity != Gravity.NO_GRAVITY) {
            it.setGravity(titleGravity);
        }
        final CharSequence titleText = this.titleText;
        if (!TextUtils.isEmpty(titleText)) {
            it.setText(titleText);
        }
        // Sets content text.
        it = this.requireViewById(R.id.contentTextView);
        final int contentGravity = this.contentGravity;
        if (contentGravity != Gravity.NO_GRAVITY) {
            it.setGravity(contentGravity);
        }
        final CharSequence contentText = this.contentText;
        if (!TextUtils.isEmpty(contentText)) {
            it.setText(contentText);
        }
        // Sets cancel text.
        it = this.requireViewById(R.id.cancelTextView);
        it.setOnClickListener(this::onClick);
        final CharSequence cancelText = this.cancelText;
        if (!TextUtils.isEmpty(cancelText)) {
            it.setText(cancelText);
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

    public void setTitleGravity(int titleGravity) {
        this.titleGravity = titleGravity;
    }

    public void setContentGravity(int contentGravity) {
        this.contentGravity = contentGravity;
    }

    public void setTitleText(@Nullable CharSequence text) {
        this.titleText = text;
    }

    public void setContentText(@Nullable CharSequence text) {
        this.contentText = text;
    }

    public void setCancelText(@Nullable CharSequence text) {
        this.cancelText = text;
    }

    public void setCancelClickComponent(@Nullable OnClickComponent c) {
        this.cancelClickComponent = c;
    }

    public void setConfirmText(@Nullable CharSequence text) {
        this.confirmText = text;
    }

    public void setConfirmClickComponent(@Nullable OnClickComponent c) {
        this.confirmClickComponent = c;
    }

    private void onClick(@NonNull View it) {
        if (R.id.cancelTextView == it.getId()) {
            if (this.cancelClickComponent != null) {
                this.cancelClickComponent.onClick(this);
            } else {
                this.getUINavigatorController().navigateUp(true);
            }
        }
        if (R.id.confirmTextView == it.getId()) {
            if (this.confirmClickComponent != null) {
                this.confirmClickComponent.onClick(this);
            } else {
                this.getUINavigatorController().navigateUp(true);
            }
        }
    }

    public interface OnClickComponent {

        void onClick(@NonNull UIReconfirmDialogFragment it);
    }
}
