package com.framework.demo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.common.route.ChatRoute;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIDecorDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

/**
 * @Author create by Zhengzelong on 2022/7/19
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PushDialogFragment extends UIDecorDialogFragment {
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

        UIDialogFragmentCompat.with(this)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setHeight(0.70f);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AppCompatDialog(this.requireContext(), this.getDialogTheme()) {
            @Override
            public void onBackPressed() {
                PushDialogFragment.this.getUINavigatorController().navigateUp();
            }
        };
    }

    private void navChatFragment() {
        ChatRoute.get().startChat(this, ChatRoute.obtain()
                .setChatId(String.valueOf(System.currentTimeMillis()))
                .setChatType(ChatRoute.TYPE_CHAT));
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
