package com.common.route;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.framework.widget.drawer.DrawerLayout;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface AppRoute extends UIRoute {
    @NonNull
    static AppRoute get() {
        return UIRoute.get(AppRoute.class);
    }

    default void startMain(@NonNull UIPageControllerOwner owner) {
        this.startMain(owner, 0);
    }

    void startMain(@NonNull UIPageControllerOwner owner, int position);

    @NonNull
    DrawerController getDrawerController(@NonNull UIPageControllerOwner owner);

    interface DrawerController {
        void setSubtleComponent(@NonNull Class<? extends Fragment> tClass);

        void setSimpleComponent(@NonNull Class<? extends Fragment> tClass);

        void setPersonComponent(@NonNull Class<? extends Fragment> tClass,
                                @Nullable Bundle arguments);

        void setCommentComponent(@NonNull Class<? extends Fragment> tClass,
                                 @Nullable Bundle arguments);

        void openSubtleComponent();

        void openSimpleComponent();

        void openPersonComponent();

        void openCommentComponent();

        void closeDrawerComponent(@NonNull UIPageControllerOwner owner);

        void setDrawerListener(@NonNull LifecycleOwner owner,
                               @NonNull DrawerLayout.DrawerListener listener);
    }

    interface DrawerCallback {
        void onNewArguments(@NonNull Bundle arguments);
    }
}
