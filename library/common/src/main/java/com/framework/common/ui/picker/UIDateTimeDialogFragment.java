package com.framework.common.ui.picker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.common.R;
import com.framework.common.ui.picker.adapter.UIDateTimePickerAdapter;
import com.framework.common.util.DateUtils;
import com.framework.widget.recycler.picker.AppCompatPickerView;
import com.framework.core.compat.UIToast;
import com.framework.core.content.UIViewModelProviders;
import com.framework.core.ui.abs.UIDecorDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

import java.util.Calendar;

/**
 * @Author create by Zhengzelong on 2023-04-06
 * @Email : 171905184@qq.com
 * @Description :日期选择器对话框
 */
public class UIDateTimeDialogFragment extends UIDecorDialogFragment {
    private int timestampType = 0;

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

        this.getUIPageController()
                .postLoadingOnAnimation();

        UIDialogFragmentCompat.with(this)
                .setWidth(1.00f)
                .setHeight(0.40f)
                .setGravity(Gravity.BOTTOM)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        this.init(UIViewModelProviders
                .ofParent(this)
                .get(UIDateTimeViewModel.class)
                .getTimestamp(this.timestampType));
    }

    @NonNull
    public UIDateTimeDialogFragment setTimestampType(int timestampType) {
        this.timestampType = timestampType;
        return this;
    }

    private void init(long timestamp) {
        if (timestamp == -1L) {
            timestamp = System.currentTimeMillis();
        }
        AppCompatPickerView upstream;
        // set adapter
        UIDateTimePickerAdapter upstreamAd;
        // set year
        upstreamAd = new UIDateTimePickerAdapter();
        upstreamAd.setField(Calendar.YEAR);
        upstreamAd.setTimestamp(timestamp);
        upstreamAd.setItemCount(9999); // 9999 year.
        upstream = this.requireViewById(R.id.lPickerView);
        upstream.setAdapter(upstreamAd);
        // set month
        upstreamAd = new UIDateTimePickerAdapter(upstream);
        upstreamAd.setField(Calendar.MONTH);
        upstreamAd.setTimestamp(timestamp);
        upstream = this.requireViewById(R.id.cPickerView);
        upstream.setLoopScrollEnabled(true);
        upstream.setAdapter(upstreamAd);
        // set dayOfMonth
        upstreamAd = new UIDateTimePickerAdapter(upstream);
        upstreamAd.setField(Calendar.DAY_OF_MONTH);
        upstreamAd.setTimestamp(timestamp);
        upstream = this.requireViewById(R.id.rPickerView);
        upstream.setLoopScrollEnabled(true);
        upstream.setAdapter(upstreamAd);
        // done set
        this.getUIPageController().postContentOnAnimation(720L);
    }

    private void submit() {
        final AppCompatPickerView upstream;
        upstream = this.requireViewById(R.id.rPickerView);
        final long timestamp = this.getTimestamp(upstream);
        if (timestamp == -1L) {
            UIToast.asyncToast("请选择");
            return;
        }
        UIViewModelProviders
                .ofParent(this)
                .get(UIDateTimeViewModel.class)
                .setTimestamp(this.timestampType, timestamp);
        // done finish.
        this.getUINavigatorController().navigateUp(true);
    }

    private long getTimestamp(@NonNull AppCompatPickerView upstream) {
        return DateUtils.parse(this.getDateTimeValueStr(upstream));
    }

    @Nullable
    private String getDateTimeValueStr(@NonNull AppCompatPickerView upstream) {
        final int scrollState = upstream.getScrollState();
        if (upstream.isAnimating()
                || scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            return null;
        }
        final UIDateTimePickerAdapter upstreamAd;
        upstreamAd = (UIDateTimePickerAdapter) upstream.getAdapter();
        if (upstreamAd == null) {
            return null;
        }
        final int currentValueInt = upstreamAd.getCurrentValueInt();
        if (currentValueInt == RecyclerView.NO_POSITION) {
            return null;
        }
        final AppCompatPickerView superUpstream = upstreamAd.getUpstream();
        if (superUpstream == null) {
            return String.valueOf(currentValueInt);
        }
        final String upstreamValueStr = this.getDateTimeValueStr(superUpstream);
        if (TextUtils.isEmpty(upstreamValueStr)) {
            return null;
        }
        return String.format("%s-%s", upstreamValueStr, currentValueInt);
    }
}
