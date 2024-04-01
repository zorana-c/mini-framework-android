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
 * @Author create by Zhengzelong on 2024-03-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface IAppRoute extends UIRoute {
    @NonNull
    static IAppRoute get() {
        return UIRoute.get(IAppRoute.class);
    }

    @NonNull
    INavigator getNavigator();

    @NonNull
    IDrawerController getDrawerController(@NonNull UIPageControllerOwner owner);

    interface INavigator {
        default void pushMain(@NonNull UIPageControllerOwner owner) {
            this.pushMain(owner, 0);
        }

        void pushMain(@NonNull UIPageControllerOwner owner, int position);
    }

    interface IDrawerController {
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

    interface IDrawerCallback {
        void onNewArguments(@NonNull Bundle arguments);
    }
}
