package com.tiktok.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.common.route.IAppRoute;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;
import com.tiktok.app.constant.Constants;
import com.tiktok.app.ui.MainActivity;

/**
 * @Author create by Zhengzelong on 2024-03-29
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class AppRouteImpl implements IAppRoute {
    @NonNull
    private final INavigator mNavigator = new NavigatorImpl();

    @NonNull
    @Override
    public INavigator getNavigator() {
        return this.mNavigator;
    }

    @NonNull
    @Override
    public IDrawerController getDrawerController(@NonNull UIPageControllerOwner owner) {
        final UIPageController uiPageController = owner.getUIPageController();
        final FragmentActivity fragmentActivity = uiPageController.getFragmentActivity();
        if (fragmentActivity instanceof MainActivity) {
            return ((MainActivity) fragmentActivity).getMainController();
        }
        throw new IllegalStateException("ERROR");
    }

    private static final class NavigatorImpl implements INavigator {
        @Override
        public void pushMain(@NonNull UIPageControllerOwner owner, int position) {
            final Bundle args = new Bundle();
            args.putInt(Constants.KEY_POSITION, position);

            owner.getUIPageController()
                    .getUINavigatorController()
                    .startActivity(MainActivity.class, args);
        }
    }
}
