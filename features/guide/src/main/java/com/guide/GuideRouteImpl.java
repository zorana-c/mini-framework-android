package com.guide;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.common.route.GuideRoute;
import com.framework.core.UIFramework;
import com.framework.core.bean.UIModelInterface;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;
import com.guide.bean.GuideStatus;
import com.guide.ui.GuideFragment;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class GuideRouteImpl implements GuideRoute {
    @Override
    public void init(@NonNull Context context) {
        GuideInit.init(context);
    }

    @Override
    public boolean startGuide(@NonNull UIPageControllerOwner owner, @NonNull int[] resIds) {
        final GuideStatus guideStatus;
        guideStatus = UIModelInterface.get(GuideStatus.class);
        if (guideStatus == null
                || guideStatus.pass(UIFramework.getVersionCode())) {
            final Bundle args = new Bundle();
            args.putIntArray("Data", resIds);

            owner.getUIPageController()
                    .getUINavigatorController()
                    .startFragment(GuideFragment.class, args);
            return true;
        }
        return false;
    }
}
