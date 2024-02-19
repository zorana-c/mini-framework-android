package com.framework.common.ui.picker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.framework.core.content.UIViewModel;
import com.framework.core.content.UIViewModelProviders;

/**
 * @Author create by Zhengzelong on 2023-04-07
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDateTimeViewModel extends UIViewModel {

    public void withTimestampObserve(@NonNull Observer<Long> observer) {
        this.withTimestampObserve(0, observer);
    }

    public void withTimestampObserve(int timestampType,
                                     @NonNull Observer<Long> observer) {
        UIViewModelProviders
                .of(this)
                .get(keyBy(timestampType), UIDateTimeTypeViewModel.class)
                .withTimestampObserve(observer);
    }

    public long getTimestamp() {
        return this.getTimestamp(0);
    }

    public long getTimestamp(int timestampType) {
        return UIViewModelProviders
                .of(this)
                .get(keyBy(timestampType), UIDateTimeTypeViewModel.class)
                .getTimestamp();
    }

    public void setTimestamp(long timestamp) {
        this.setTimestamp(0, timestamp);
    }

    public void setTimestamp(int timestampType, long timestamp) {
        UIViewModelProviders
                .of(this)
                .get(keyBy(timestampType), UIDateTimeTypeViewModel.class)
                .setTimestamp(timestamp);
    }

    @NonNull
    private static String keyBy(int timestampType) {
        return "DatePickerType: " + timestampType;
    }
}
