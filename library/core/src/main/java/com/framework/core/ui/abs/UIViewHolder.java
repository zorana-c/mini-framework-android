package com.framework.core.ui.abs;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.widget.expand.compat.ViewHolderCompat;

/**
 * @Author create by Zhengzelong on 2023-03-17
 * @Email : 171905184@qq.com
 * @Description :
 *
 * </p>
 * 边界处理 {@link ViewHolderCompat}
 */
public abstract class UIViewHolder extends UIListController.ViewHolder {
    @Nullable
    private UIPageControllerOwner mUIPageControllerOwner;

    public UIViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onRecycled() {
        super.onRecycled();
        this.mUIPageControllerOwner = null;
    }

    @NonNull
    public final UIPageControllerOwner owner() {
        if (this.mUIPageControllerOwner == null) {
            this.mUIPageControllerOwner = new UIPageControllerOwnerImpl();
        }
        return this.mUIPageControllerOwner;
    }

    private final class UIPageControllerOwnerImpl implements UIPageControllerOwner {
        @NonNull
        @Override
        public <T extends UIPageController> T getUIPageController() {
            return (T) UIViewHolder.this.getUIPageController();
        }
    }
}
