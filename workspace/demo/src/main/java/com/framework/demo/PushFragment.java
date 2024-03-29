package com.framework.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.route.IChatRoute;
import com.framework.core.compat.UIToast;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIDecorFragment;
import com.navigation.UINavigatorController;

/**
 * @Author create by Zhengzelong on 2022/7/19
 * @Email : 171905184@qq.com
 * @Description :
 */
@UINavigatorController.FragmentRoute(
        tag = "Hi fragment tag",
        hostClass = CustomNavigatorAbility.class,
        launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
)
public class PushFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_push;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        this.getUIActionBarController().setTitleText("Push Fragment");

        RxView.of(this).click(it -> {
                    if (it.getId() == R.id.navChatFragment) {
                        this.navChatFragment();
                    }
                    if (it.getId() == R.id.navChildFragment) {
                        this.navChildFragment();
                    }
                    if (it.getId() == R.id.pushChildFragment) {
                        this.pushChildFragment();
                    }
                },
                R.id.navChatFragment,
                R.id.navChildFragment,
                R.id.pushChildFragment);

        UIToast.asyncToast(this.getTag());
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
//        try {
//            UILayoutException.fail("初始化数据失败, 显示错误界面");
//        } catch (@NonNull Exception e) {
//            this.getUIPageController().postErrorOnAnimation(e);
//        }
    }

    private void navChatFragment() {
        IChatRoute.navigator().pushChat(this);
    }

    private void navChildFragment() {
        final Bundle args = new Bundle();
        args.putString("key", "navChildFragment");
        this.getUINavigatorController()
                .startFragment(PushFragment.class, args);
    }

    private void pushChildFragment() {
        this.getUINavigatorController()
                .pushFragment(R.id.container, ChildFragment.class);
    }
}
