package com.framework.core.ui.abs;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.framework.core.content.UIActivityDispatcher;
import com.framework.core.content.UIActivityDispatcherOwner;
import com.framework.core.content.UIPageController;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIFragmentActivity extends AppCompatActivity
        implements UIPageController.UIComponent, UIActivityDispatcherOwner {
    @NonNull
    private final UIPageController
            mUIPageController = new UIPageController(this);
    @NonNull
    private final UIActivityDispatcher
            mUIActivityDispatcher = new UIActivityDispatcher();

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        this.mUIActivityDispatcher.onNewIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (this.mUIActivityDispatcher.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (this.mUIActivityDispatcher.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (this.mUIActivityDispatcher.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        T view = super.findViewById(id);
        if (view == null) {
            view = this.getUIPageController().findViewById(id);
        }
        return view;
    }

    @NonNull
    @Override
    public <T extends UIPageController> T getUIPageController() {
        return (T) this.mUIPageController;
    }

    @NonNull
    @Override
    public final UIActivityDispatcher getUIActivityDispatcher() {
        return this.mUIActivityDispatcher;
    }
}
