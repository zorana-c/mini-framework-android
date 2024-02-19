package com.framework.common.ui.dialog;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.framework.core.content.UIViewModel;
import com.framework.core.lifecycle.UILiveData;

/**
 * @Author create by Zhengzelong on 2023-05-26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UISysPictureViewModel extends UIViewModel {
    @NonNull
    private final UILiveData<Uri> sysPictureLiveData = new UILiveData<>();

    public void sysPictureObserve(@NonNull Observer<Uri> it) {
        this.sysPictureLiveData.observe(this, it);
    }

    public void sysPictureObserve(@NonNull LifecycleOwner owner,
                                  @NonNull Observer<Uri> it) {
        this.sysPictureLiveData.observe(owner, it);
    }

    @Nullable
    public Uri getSysPicture() {
        return this.sysPictureLiveData.getValue();
    }

    public void setSysPicture(@NonNull Uri it) {
        this.sysPictureLiveData.setValue(it);
    }
}
