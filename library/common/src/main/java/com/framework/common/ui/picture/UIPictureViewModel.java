package com.framework.common.ui.picture;

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
public class UIPictureViewModel extends UIViewModel {
    @NonNull
    private final UILiveData<Uri> pictureLiveData = new UILiveData<>();

    public void observePicture(@NonNull Observer<Uri> it) {
        this.pictureLiveData.observe(this, it);
    }

    public void observePicture(@NonNull LifecycleOwner owner,
                               @NonNull Observer<Uri> it) {
        this.pictureLiveData.observe(owner, it);
    }

    @Nullable
    public Uri getPicture() {
        return this.pictureLiveData.getValue();
    }

    public void setPicture(@NonNull Uri it) {
        this.pictureLiveData.setValue(it);
    }
}
