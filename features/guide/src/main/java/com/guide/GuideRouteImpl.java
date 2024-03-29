package com.guide;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.common.route.IGuideRoute;
import com.framework.core.UIFramework;
import com.framework.core.bean.UIModelInterface;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;
import com.guide.bean.GuideStatus;
import com.guide.constant.Constants;
import com.guide.ui.GuideFragment;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class GuideRouteImpl implements IGuideRoute {
    @NonNull
    private final INavigator mNavigator = new NavigatorImpl();

    @Override
    public void init(@NonNull Context context) {
        GuideInit.init(context);
    }

    @NonNull
    @Override
    public INavigator getNavigator() {
        return this.mNavigator;
    }

    private static final class NavigatorImpl implements INavigator {
        @Override
        public boolean pushGuide(@NonNull UIPageControllerOwner owner, @NonNull int[] resIds) {
            final GuideStatus guideStatus = UIModelInterface.get(GuideStatus.class);
            if (guideStatus == null
                    || guideStatus.pass(UIFramework.getVersionCode())) {
                final Bundle args = new Bundle();
                args.putIntArray(Constants.KEY_PARAM, resIds);

                owner.getUIPageController()
                        .getUINavigatorController()
                        .startFragment(GuideFragment.class, args);
                return true;
            }
            return false;
        }
    }
}
