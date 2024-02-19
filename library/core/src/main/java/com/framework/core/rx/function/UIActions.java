package com.framework.core.rx.function;

import android.os.Looper;

import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIDecorOptions;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Action;

/**
 * @Author create by Zhengzelong on 2023-06-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIActions {

    private UIActions() {
        throw new IllegalStateException("No instances!");
    }

    static final class LayoutOnComplete implements Action {
        @NonNull
        final UIPageController uiPageController;

        LayoutOnComplete(@NonNull UIPageController uiPageController) {
            this.uiPageController = uiPageController;
        }

        @Override
        public void run() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                AndroidSchedulers.mainThread().scheduleDirect(this::run);
                return;
            }
            final UIPageController pc = this.uiPageController;
            if (!pc.isCreatedView()) {
                return;
            }
            if (pc instanceof UIDecorController) {
                ((UIDecorController) pc).postContentOnAnimation();
            }
            if (pc instanceof UIListController) {
                ((UIListController<?>) pc).completeRefreshed(UIDecorOptions.MS_ANIM);
            }
        }
    }

    @NonNull
    public static Action layoutOnComplete(@NonNull UIPageControllerOwner it) {
        return layoutOnComplete(it.<UIPageController>getUIPageController());
    }

    @NonNull
    public static Action layoutOnComplete(@NonNull UIPageController it) {
        return new LayoutOnComplete(it);
    }
}
