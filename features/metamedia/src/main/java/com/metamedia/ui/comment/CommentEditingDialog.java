package com.metamedia.ui.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.common.widget.EditTextContainer;
import com.metamedia.R;
import com.navigation.floating.UIDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

/**
 * @Author create by Zhengzelong on 2024-01-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommentEditingDialog extends UIDialogFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.dialog_comment_editing_layout;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final EditTextContainer editTextContainer;
        editTextContainer = this.requireViewById(R.id.editTextContainer);
        editTextContainer.setDismissEnforcement(() -> {
            this.getUINavigatorController().navigateUp();
        });
        UIDialogFragmentCompat.with(this)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT)
                .setHeight(WindowManager.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    @SuppressLint("ServiceCast")
    @Override
    public void onResume() {
        super.onResume();
        final Context context = this.requireContext();
        final InputMethodManager imm;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
