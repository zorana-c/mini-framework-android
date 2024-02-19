package com.navigation.floating;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;

import com.framework.core.R;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.ui.abs.UIFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2021/12/21
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIDialogFragment extends UIFragment {
    private static final String KEY_DIALOG_SHOW = "androidx:fragment:dialog:show";
    private static final String KEY_DIALOG_STATE = "androidx:fragment:dialog:state";
    private static final String KEY_DIALOG_STYLE = "androidx:fragment:dialog:style";
    private static final String KEY_DIALOG_THEME = "androidx:fragment:dialog:theme";
    private static final String KEY_DIALOG_CANCEL = "androidx:fragment:dialog:cancel";

    /**
     * Style for {@link #setDialogStyle(int, int)}: a basic, normal dialog.
     */
    public static final int STYLE_NORMAL = 0;

    /**
     * Style for {@link #setDialogStyle(int, int)}: don't include a title area.
     */
    public static final int STYLE_NO_TITLE = 1;

    /**
     * Style for {@link #setDialogStyle(int, int)}: don't draw any frame at all;
     * the view hierarchy returned by {@link #onCreateView} is entirely responsible for drawing the dialog.
     */
    public static final int STYLE_NO_FRAME = 2;

    /**
     * Style for {@link #setDialogStyle(int, int)}: like {@link #STYLE_NO_FRAME}, but also disables all input to the dialog.
     * The user can not touch it, and its window will not receive input focus.
     */
    public static final int STYLE_NO_INPUT = 3;

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @IntDef({STYLE_NORMAL,
            STYLE_NO_TITLE,
            STYLE_NO_FRAME,
            STYLE_NO_INPUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogStyle {
    }

    private final DialogInterface.OnCancelListener mOnCancelListener = dialogInterface -> {
        this.dispatchOnCancel();
    };
    private final DialogInterface.OnDismissListener mOnDismissListener = dialogInterface -> {
        this.dispatchOnDismiss();
    };
    private final ArrayList<OnCancelListener> mOnCancelListeners = new ArrayList<>();
    private final ArrayList<OnDismissListener> mOnDismissListeners = new ArrayList<>();

    private int mDialogStyle = STYLE_NORMAL;
    private int mDialogTheme = 0;

    private Dialog mDialog;
    private boolean mIsShowDialog = true;
    private boolean mIsCancelable = true;
    private boolean mIsViewDestroyed = false;
    private boolean mIsDialogDismiss = false;
    private boolean mIsDialogCreating = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mIsDialogDismiss = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mIsDialogDismiss = true;
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsShowDialog = this.getId() == 0;
        if (savedInstanceState == null) {
            this.setDialogStyle(STYLE_NORMAL, R.style.UITheme_Dialog_NoActionBar);
        } else {
            this.mDialogStyle = savedInstanceState.getInt(KEY_DIALOG_STYLE, this.mDialogStyle);
            this.mDialogTheme = savedInstanceState.getInt(KEY_DIALOG_THEME, this.mDialogTheme);
            this.mIsShowDialog = savedInstanceState.getBoolean(KEY_DIALOG_SHOW, this.mIsShowDialog);
            this.mIsCancelable = savedInstanceState.getBoolean(KEY_DIALOG_CANCEL, this.mIsCancelable);
        }
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AppCompatDialog(this.requireContext(), this.mDialogTheme);
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!this.mIsShowDialog) {
            return;
        }
        final Dialog dialog = this.mDialog;
        if (dialog == null) {
            return;
        }
        final View contentView = this.getView();
        if (contentView != null) {
            if (contentView.getParent() != null) {
                throw new IllegalStateException("UIDialogFragment "
                        + this + " can not be attached to a container view");
            }
            dialog.setContentView(contentView);
        }
        final Activity activity = this.getActivity();
        if (activity != null) {
            dialog.setOwnerActivity(activity);
        }
        dialog.setCancelable(this.mIsCancelable);
        dialog.setOnCancelListener(this.mOnCancelListener);
        dialog.setOnDismissListener(this.mOnDismissListener);
        if (savedInstanceState != null) {
            final Bundle dialogInstanceState = savedInstanceState.getBundle(KEY_DIALOG_STATE);
            if (dialogInstanceState != null) {
                dialog.onRestoreInstanceState(dialogInstanceState);
            }
        }
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outInstanceState) {
        super.onSaveInstanceState(outInstanceState);
        final Dialog dialog = this.mDialog;
        if (dialog != null) {
            final Bundle dialogInstanceState = dialog.onSaveInstanceState();
            outInstanceState.putBundle(KEY_DIALOG_STATE, dialogInstanceState);
        }
        if (this.mDialogStyle != STYLE_NORMAL) {
            outInstanceState.putInt(KEY_DIALOG_STYLE, this.mDialogStyle);
        }
        if (this.mDialogTheme != 0) {
            outInstanceState.putInt(KEY_DIALOG_THEME, this.mDialogTheme);
        }
        if (!this.mIsShowDialog) {
            outInstanceState.putBoolean(KEY_DIALOG_SHOW, false);
        }
        if (!this.mIsCancelable) {
            outInstanceState.putBoolean(KEY_DIALOG_CANCEL, false);
        }
    }

    @NonNull
    @Override
    @CallSuper
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        final LayoutInflater layoutInflater = super.onGetLayoutInflater(savedInstanceState);
        if (!this.mIsShowDialog || this.mIsDialogCreating) {
            return layoutInflater;
        }
        try {
            this.mIsDialogCreating = true;
            this.mDialog = this.onCreateDialog(savedInstanceState);
            this.onSetupDialogStyle(this.mDialog, this.mDialogStyle);
        } finally {
            this.mIsDialogCreating = false;
        }
        return layoutInflater.cloneInContext(this.requireDialog().getContext());
    }

    @CallSuper
    @SuppressLint("SwitchIntDef")
    public void onSetupDialogStyle(@NonNull Dialog dialog, @DialogStyle int dialogStyle) {
        switch (dialogStyle) {
            case STYLE_NO_INPUT:
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            case STYLE_NO_FRAME:
            case STYLE_NO_TITLE:
                if (dialog instanceof AppCompatDialog) {
                    final AppCompatDialog appCompatDialog = (AppCompatDialog) dialog;
                    appCompatDialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                } else {
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                }
        }
    }

    @CallSuper
    public void onCancel(@NonNull DialogInterface dialogInterface) {
        // no-op
    }

    @CallSuper
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        // no-op
    }

    public final void show(@NonNull UIPageControllerOwner owner) {
        this.show(owner.<UIPageController>getUIPageController());
    }

    public void show(@NonNull UIPageController uiPageController) {
        uiPageController.getUINavigatorController().showFragment(this);
    }

    public final void showNow(@NonNull UIPageControllerOwner owner) {
        this.showNow(owner.<UIPageController>getUIPageController());
    }

    public void showNow(@NonNull UIPageController uiPageController) {
        uiPageController.getUINavigatorController().showFragmentNow(this);
    }

    public void addOnCancelListener(@NonNull OnCancelListener listener) {
        this.mOnCancelListeners.add(listener);
    }

    public void removeOnCancelListener(@NonNull OnCancelListener listener) {
        this.mOnCancelListeners.remove(listener);
    }

    public void addOnDismissListener(@NonNull OnDismissListener listener) {
        this.mOnDismissListeners.add(listener);
    }

    public void removeOnDismissListener(@NonNull OnDismissListener listener) {
        this.mOnDismissListeners.remove(listener);
    }

    public final boolean isShowDialog() {
        return this.mIsShowDialog;
    }

    public final boolean isCancelable() {
        return this.mIsCancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.mIsCancelable = cancelable;
        if (this.mDialog != null) {
            this.mDialog.setCancelable(cancelable);
        }
    }

    @DialogStyle
    public final int getDialogStyle() {
        return this.mDialogStyle;
    }

    public void setDialogStyle(@DialogStyle int dialogStyle,
                               @StyleRes int dialogTheme) {
        this.mDialogStyle = dialogStyle;
        if (dialogStyle == STYLE_NO_FRAME || dialogStyle == STYLE_NO_INPUT) {
            this.mDialogTheme = android.R.style.Theme_Panel;
        }
        if (dialogTheme != 0) {
            this.mDialogTheme = dialogTheme;
        }
    }

    @StyleRes
    public final int getDialogTheme() {
        return this.mDialogTheme;
    }

    @Nullable
    public <T extends Dialog> T getDialog() {
        return (T) this.mDialog;
    }

    @NonNull
    public final <T extends Dialog> T requireDialog() {
        final T dialog = this.getDialog();
        if (dialog == null) {
            throw new NullPointerException("NOT DIALOG SET");
        }
        return dialog;
    }

    public boolean isShowing() {
        if (this.mDialog != null) {
            return this.mDialog.isShowing();
        }
        return false;
    }

    public boolean dismiss() {
        if (this.mDialog != null) {
            this.mDialog.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mIsViewDestroyed = false;
        if (this.mDialog != null) {
            this.mDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.mDialog != null) {
            this.mDialog.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mIsViewDestroyed = true;
        final Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.setOnDismissListener(null);
            dialog.dismiss();
            this.dispatchOnDismiss();
            this.mDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mOnCancelListeners.clear();
        this.mOnDismissListeners.clear();
    }

    final void dispatchOnCancel() {
        final Dialog dialog = this.mDialog;
        if (dialog != null) {
            this.onCancel(dialog);

            for (OnCancelListener listener : this.mOnCancelListeners) {
                listener.onCancel(this);
            }
        }
    }

    final void dispatchOnDismiss() {
        if (this.mIsDialogDismiss) {
            return;
        }
        this.mIsDialogDismiss = true;
        final Dialog dialog = this.mDialog;
        if (dialog != null) {
            this.onDismiss(dialog);

            for (OnDismissListener listener : this.mOnDismissListeners) {
                listener.onDismiss(this);
            }
        }
        if (!this.mIsViewDestroyed) {
            this.getUINavigatorController().navigateUp(true);
        }
    }

    public interface OnCancelListener {

        void onCancel(@NonNull UIDialogFragment uiDialogFragment);
    }

    public interface OnDismissListener {

        void onDismiss(@NonNull UIDialogFragment uiDialogFragment);
    }
}
