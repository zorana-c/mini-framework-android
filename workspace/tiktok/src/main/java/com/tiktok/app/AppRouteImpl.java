package com.tiktok.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.common.route.AppRoute;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;
import com.tiktok.app.constant.Constants;
import com.tiktok.app.ui.MainActivity;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class AppRouteImpl implements AppRoute {
    @Override
    public void startMain(@NonNull UIPageControllerOwner owner, int position) {
        final Bundle args = new Bundle();
        args.putInt(Constants.KEY_POSITION, position);

        owner.getUIPageController()
                .getUINavigatorController()
                .startActivity(MainActivity.class, args);
    }

    @NonNull
    @Override
    public DrawerController getDrawerController(@NonNull UIPageControllerOwner owner) {
        final UIPageController uiPageController = owner.getUIPageController();
        final FragmentActivity fragmentActivity = uiPageController.getFragmentActivity();
        if (fragmentActivity instanceof MainActivity) {
            return ((MainActivity) fragmentActivity).getMainController();
        }
        throw new IllegalStateException("ERROR");
    }
}
