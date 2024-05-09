package com.framework.common.ui.picker;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.framework.core.content.UIViewModel;
import com.framework.core.lifecycle.UILiveData;

/**
 * @Author create by Zhengzelong on 2023-04-07
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDateTimeTypeViewModel extends UIViewModel {
    @NonNull
    private final UILiveData<Long> mTimestampLiveData = new UILiveData<>();

    public void observeTimestamp(@NonNull Observer<Long> observer) {
        this.mTimestampLiveData.observe(this, observer);
    }

    public long getTimestamp() {
        final Long valueLong = this.mTimestampLiveData.getValue();
        return valueLong == null ? -1L : valueLong;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestampLiveData.postValue(timestamp);
    }
}
