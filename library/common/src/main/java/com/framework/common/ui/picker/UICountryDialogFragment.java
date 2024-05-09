package com.framework.common.ui.picker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.common.R;
import com.framework.common.ui.picker.adapter.UINodePickerAdapter;
import com.framework.common.bean.UICountryNode;
import com.framework.common.bean.UINode;
import com.framework.widget.recycler.picker.AppCompatPickerView;
import com.framework.core.compat.UIToast;
import com.framework.core.content.UIViewModelProviders;
import com.framework.core.ui.abs.UIDecorDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

/**
 * @Author create by Zhengzelong on 2022/5/31
 * @Email : 171905184@qq.com
 * @Description :城市选择器对话框
 */
public class UICountryDialogFragment extends UIDecorDialogFragment {
    private boolean allowUnlimitedEnabled = true;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.ui_dialog_picker_layout;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        this.getUIActionBarController()
                .setMenuText("确认")
                .setMenuClickListener(view -> this.submit());

        UIDialogFragmentCompat.with(this)
                .setWidth(1.00f)
                .setHeight(0.40f)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom);

        UIViewModelProviders
                .ofParent(this)
                .get(UICountryViewModel.class)
                .observeCountryList(this, this::init);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        UIViewModelProviders
                .ofParent(this)
                .get(UICountryViewModel.class)
                .queryCountryList(this);
    }

    private void init(@Nullable UICountryNode dataSources) {
        if (dataSources == null) {
            this.getUINavigatorController().navigateUp();
            return;
        }
        final boolean allowUnlimitedEnabled = this.allowUnlimitedEnabled;
        AppCompatPickerView upstream;
        // set adapter
        UINodePickerAdapter<UICountryNode> upstreamAd;
        // set province
        upstreamAd = new UINodePickerAdapter<>();
        upstreamAd.setAllowUnlimitedEnabled(allowUnlimitedEnabled);
        upstreamAd.setDataSources(dataSources);
        upstream = this.requireViewById(R.id.lPickerView);
        upstream.setAdapter(upstreamAd);
        // set city
        upstreamAd = new UINodePickerAdapter<>(upstream);
        upstreamAd.setAllowUnlimitedEnabled(allowUnlimitedEnabled);
        upstream = this.requireViewById(R.id.cPickerView);
        upstream.setAdapter(upstreamAd);
        // set area
        upstreamAd = new UINodePickerAdapter<>(upstream);
        upstreamAd.setAllowUnlimitedEnabled(allowUnlimitedEnabled);
        upstream = this.requireViewById(R.id.rPickerView);
        upstream.setAdapter(upstreamAd);
    }

    private void submit() {
        final AppCompatPickerView upstream;
        upstream = this.requireViewById(R.id.rPickerView);
        final String countryStr = this.getCountryStr(upstream);
        if (TextUtils.isEmpty(countryStr)) {
            UIToast.asyncToast("请选择选项");
            return;
        }
        UIViewModelProviders
                .ofParent(this)
                .get(UICountryViewModel.class)
                .setCountryString(countryStr);
        // done finish.
        this.getUINavigatorController().navigateUp(true);
    }

    @Nullable
    private String getCountryStr(@NonNull AppCompatPickerView upstream) {
        final int scrollState = upstream.getScrollState();
        if (upstream.isAnimating()
                || scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            return null;
        }
        final UINodePickerAdapter<?> upstreamAd;
        upstreamAd = (UINodePickerAdapter<?>) upstream.getAdapter();
        if (upstreamAd == null) {
            return null;
        }
        int currentPosition;
        currentPosition = upstreamAd.getCurrentPosition();
        currentPosition -= upstreamAd.getHeadItemCount();
        final UINode<?> node = upstreamAd.findDataBy(currentPosition);
        final String countryStr = node == null ? "不限" : node.toString();
        final AppCompatPickerView superUpstream = upstreamAd.getUpstream();
        if (superUpstream == null) {
            return countryStr;
        }
        final String upstreamCountryStr = this.getCountryStr(superUpstream);
        if (TextUtils.isEmpty(upstreamCountryStr)) {
            return null;
        }
        return String.format("%s-%s", upstreamCountryStr, countryStr);
    }

    public void setAllowUnlimitedEnabled(boolean allowUnlimitedEnabled) {
        this.allowUnlimitedEnabled = allowUnlimitedEnabled;
    }
}
