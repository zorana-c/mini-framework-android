package com.framework.core.ui.abs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.framework.core.content.UIPageController;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIFragment extends Fragment
        implements UIPageController.UIComponent {
    @NonNull
    private final UIPageController
            mUIPageController = new UIPageController(this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return this.createView(inflater, container, savedInstanceState);
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        return this.getUIPageController().findViewById(id);
    }

    @NonNull
    public <T extends View> T requireViewById(@IdRes int id) {
        return this.getUIPageController().requireViewById(id);
    }

    @NonNull
    @Override
    public <T extends UIPageController> T getUIPageController() {
        return (T) this.mUIPageController;
    }
}
