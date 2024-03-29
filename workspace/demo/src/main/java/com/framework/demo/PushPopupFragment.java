package com.framework.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.route.IChatRoute;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIDecorPopupFragment;
import com.navigation.floating.UIPopupFragmentCompat;
import com.navigation.widget.UIPopupWindow;

/**
 * @Author create by Zhengzelong on 2022/7/19
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PushPopupFragment extends UIDecorPopupFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_push;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
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

        UIPopupFragmentCompat.with(this)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT)
                .setHeight(0.70f)
                .setBackgroundColor(Color.WHITE)
                .setBackgroundAlpha(0.60f);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    @NonNull
    @Override
    public PopupWindow onCreatePopupWindow(@Nullable Bundle savedInstanceState) {
        return new UIPopupWindow(this.requireContext()) {
            @Override
            public void dismiss() {
                if (getUINavigatorController().popBackStack()) {
                    return;
                }
                super.dismiss();
            }
        };
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
