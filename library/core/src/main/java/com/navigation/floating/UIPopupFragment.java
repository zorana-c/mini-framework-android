package com.navigation.floating;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.PopupWindowCompat;

import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.ui.abs.UIFragment;
import com.navigation.widget.UIPopupWindow;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2021/12/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIPopupFragment extends UIFragment {
    private static final String KEY_POPUP_SHOW = "androidx:fragment:popup:show";
    public static final int POPUP_AS_DROPDOWN = 1;
    public static final int POPUP_AT_LOCATION = 2;
    public static final int DEFAULT_GRAVITY = Gravity.TOP | Gravity.START;

    private final PopupWindow.OnDismissListener mOnDismissListener = this::dispatchOnDismiss;
    private final ArrayList<OnDismissListener> mOnDismissListeners = new ArrayList<>();

    private int mType;
    private int mGravity;
    private int mOffsetX;
    private int mOffsetY;
    private View mAnchorView;
    private PopupWindow mPopupWindow;
    private boolean mIsShowPopup = true;
    private boolean mIsPopupDismiss = false;
    private boolean mIsPopupCreating = false;
    private boolean mIsViewDestroyed = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mIsPopupDismiss = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mIsPopupDismiss = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsShowPopup = this.getId() == 0;
        this.mIsShowPopup &= this.mAnchorView != null;
        if (savedInstanceState != null) {
            this.mIsShowPopup = savedInstanceState.getBoolean(KEY_POPUP_SHOW, this.mIsShowPopup);
        }
    }

    @NonNull
    public PopupWindow onCreatePopupWindow(@Nullable Bundle savedInstanceState) {
        return new UIPopupWindow(this.requireContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!this.mIsShowPopup) {
            return;
        }
        final PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow == null) {
            return;
        }
        final View contentView = this.getView();
        if (contentView != null) {
            if (contentView.getParent() != null) {
                throw new IllegalStateException("UIPopupFragment "
                        + this + "can not be attached to a container view");
            }
            popupWindow.setContentView(contentView);
        }
        popupWindow.setOnDismissListener(this.mOnDismissListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outInstanceState) {
        super.onSaveInstanceState(outInstanceState);
        if (!this.mIsShowPopup) {
            outInstanceState.putBoolean(KEY_POPUP_SHOW, false);
        }
    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        final LayoutInflater layoutInflater = super.onGetLayoutInflater(savedInstanceState);
        if (!this.mIsShowPopup || this.mIsPopupCreating) {
            return layoutInflater;
        }
        try {
            this.mIsPopupCreating = true;
            this.mPopupWindow = this.onCreatePopupWindow(savedInstanceState);
            this.onSetupPopupWindow(this.mPopupWindow);
        } finally {
            this.mIsPopupCreating = false;
        }
        return layoutInflater.cloneInContext(this.requireContext());
    }

    @CallSuper
    public void onSetupPopupWindow(@NonNull PopupWindow popupWindow) {
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
    }

    @CallSuper
    public void onDismiss(@NonNull PopupWindow popupWindow) {
        // no-op
    }

    public final void setShowAtLocation(@NonNull View parent) {
        this.setShowAtLocation(parent, DEFAULT_GRAVITY);
    }

    public final void setShowAtLocation(@NonNull View parent, int gravity) {
        this.setShowAtLocation(parent, gravity, 0, 0);
    }

    public final void setShowAtLocation(@NonNull View parent, int x, int y) {
        this.setShowAtLocation(parent, DEFAULT_GRAVITY, x, y);
    }

    public final void setShowAtLocation(@NonNull View parent, int gravity, int x, int y) {
        this.mType = POPUP_AT_LOCATION;
        this.mGravity = gravity;
        this.mOffsetX = x;
        this.mOffsetY = y;
        this.mAnchorView = parent;
    }

    public final void setShowAsDropDown(@NonNull View anchorView) {
        this.setShowAsDropDown(anchorView, DEFAULT_GRAVITY);
    }

    public final void setShowAsDropDown(@NonNull View anchorView, int gravity) {
        this.setShowAsDropDown(anchorView, gravity, 0, 0);
    }

    public final void setShowAsDropDown(@NonNull View anchorView, int offsetX, int offsetY) {
        this.setShowAsDropDown(anchorView, DEFAULT_GRAVITY, offsetX, offsetY);
    }

    public final void setShowAsDropDown(@NonNull View anchorView, int gravity, int offsetX, int offsetY) {
        this.mType = POPUP_AS_DROPDOWN;
        this.mGravity = gravity;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        this.mAnchorView = anchorView;
    }

    public final void show(@NonNull UIPageControllerOwner owner) {
        this.show(owner.<UIPageController>getUIPageController());
    }

    public void show(@NonNull UIPageController uiPageController) {
        if (this.mAnchorView == null) {
            throw new NullPointerException("NOT ANCHOR SET");
        }
        uiPageController.getUINavigatorController().showFragment(this);
    }

    public final void showNow(@NonNull UIPageControllerOwner owner) {
        this.showNow(owner.<UIPageController>getUIPageController());
    }

    public void showNow(@NonNull UIPageController uiPageController) {
        if (this.mAnchorView == null) {
            throw new NullPointerException("NOT ANCHOR SET");
        }
        uiPageController.getUINavigatorController().showFragmentNow(this);
    }

    public void addOnDismissListener(@NonNull OnDismissListener listener) {
        this.mOnDismissListeners.add(listener);
    }

    public void removeOnDismissListener(@NonNull OnDismissListener listener) {
        this.mOnDismissListeners.remove(listener);
    }

    public final boolean isShowPopup() {
        return this.mIsShowPopup;
    }

    /**
     * @return type
     * @see UIPopupFragment#POPUP_AT_LOCATION
     * @see UIPopupFragment#POPUP_AS_DROPDOWN
     */
    public final int getType() {
        return this.mType;
    }

    public final int getGravity() {
        return this.mGravity;
    }

    /**
     * @return offsetX
     * @see UIPopupFragment#getType()
     * @see UIPopupFragment#POPUP_AT_LOCATION by x
     * @see UIPopupFragment#POPUP_AS_DROPDOWN by offsetX
     */
    public final int getOffsetX() {
        return this.mOffsetX;
    }

    /**
     * @return offsetY
     * @see UIPopupFragment#getType()
     * @see UIPopupFragment#POPUP_AT_LOCATION by y
     * @see UIPopupFragment#POPUP_AS_DROPDOWN by offsetY
     */
    public final int getOffsetY() {
        return this.mOffsetY;
    }

    /**
     * @return view
     * @see UIPopupFragment#getType()
     * @see UIPopupFragment#POPUP_AT_LOCATION by parent
     * @see UIPopupFragment#POPUP_AS_DROPDOWN by anchorView
     */
    @Nullable
    public final <T extends View> T getAnchorView() {
        return (T) this.mAnchorView;
    }

    /**
     * @return view
     * @see UIPopupFragment#getType()
     * @see UIPopupFragment#POPUP_AT_LOCATION by parent
     * @see UIPopupFragment#POPUP_AS_DROPDOWN by anchorView
     */
    @NonNull
    public final <T extends View> T requireAnchorView() {
        final T anchorView = this.getAnchorView();
        if (anchorView == null) {
            throw new NullPointerException("NOT ANCHOR SET");
        }
        return anchorView;
    }

    @Nullable
    public <T extends PopupWindow> T getPopupWindow() {
        return (T) this.mPopupWindow;
    }

    @NonNull
    public final <T extends PopupWindow> T requirePopupWindow() {
        final T popupWindow = this.getPopupWindow();
        if (popupWindow == null) {
            throw new NullPointerException("NOT POPUP WINDOW SET");
        }
        return popupWindow;
    }

    public boolean isShowing() {
        if (this.mPopupWindow != null) {
            return this.mPopupWindow.isShowing();
        }
        return false;
    }

    public boolean dismiss() {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mIsViewDestroyed = false;
        final PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            this.show(popupWindow);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mIsViewDestroyed = true;
        final PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(null);
            popupWindow.dismiss();
            this.dispatchOnDismiss();
            this.mPopupWindow = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mType = 0;
        this.mGravity = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mAnchorView = null;
        this.mOnDismissListeners.clear();
    }

    final void show(@NonNull PopupWindow popupWindow) {
        if (POPUP_AS_DROPDOWN == this.mType) {
            PopupWindowCompat.showAsDropDown(popupWindow,
                    this.mAnchorView,
                    this.mOffsetX,
                    this.mOffsetY,
                    this.mGravity);
        } else if (POPUP_AT_LOCATION == this.mType) {
            popupWindow.showAtLocation(this.mAnchorView,
                    this.mGravity,
                    this.mOffsetX,
                    this.mOffsetY);
        }
    }

    final void dispatchOnDismiss() {
        if (this.mIsPopupDismiss) {
            return;
        }
        this.mIsPopupDismiss = true;
        final PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            this.onDismiss(popupWindow);

            for (OnDismissListener listener : this.mOnDismissListeners) {
                listener.onDismiss(this);
            }
        }
        if (!this.mIsViewDestroyed) {
            this.getUINavigatorController().navigateUp(true);
        }
    }

    public interface OnDismissListener {

        void onDismiss(@NonNull UIPopupFragment uiPopupFragment);
    }
}
