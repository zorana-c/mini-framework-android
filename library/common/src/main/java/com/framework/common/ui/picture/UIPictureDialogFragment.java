package com.framework.common.ui.picture;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.framework.common.R;
import com.framework.core.compat.UIFile;
import com.framework.core.compat.UIToast;
import com.framework.core.content.UIViewModelProviders;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.UIActivityResultContracts;
import com.navigation.floating.UIDialogFragment;
import com.navigation.floating.UIDialogFragmentCompat;

import java.io.File;
import java.util.Map;

/**
 * @Author create by Zhengzelong on 2023-05-26
 * @Email : 171905184@qq.com
 * @Description : 选择系统图片
 */
public class UIPictureDialogFragment extends UIDialogFragment {
    @Nullable
    private ActivityResultLauncher<Uri> pictureByTake;
    @Nullable
    private ActivityResultLauncher<Void> pictureByGallery;
    @Nullable
    private ActivityResultLauncher<String[]> permissionsByTake;
    @Nullable
    private ActivityResultLauncher<String[]> permissionsByGallery;
    @Nullable
    private Uri rUri;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.ui_dialog_picture_layout;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        UIDialogFragmentCompat.with(this)
                .setGravity(Gravity.BOTTOM)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.UIAnimation_Dialog_InBottomOutBottom);
        // Sets views click.
        RxView.of(this).click(this::onClick,
                R.id.cancelTextView,
                R.id.pictureTakeTextView,
                R.id.pictureGalleryTextView);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        // no-op
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册系统拍照回调
        this.pictureByTake = this.registerForActivityResult(
                new UIActivityResultContracts.TakePicture(),
                it -> {
                    if (null == it || !it) {
                        return;
                    }
                    this.handlerUri(this.rUri);
                });
        // 注册系统图库回调
        this.pictureByGallery = this.registerForActivityResult(
                new UIActivityResultContracts.GalleryPicturePreview(),
                it -> {
                    if (it != null) {
                        this.handlerUri(it);
                    }
                });
        // 注册系统拍照权限回调
        this.permissionsByTake = this.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                it -> {
                    if (this.getGranted(it)) {
                        this.pictureByTake();
                    } else {
                        UIToast.asyncToast("获取系统权限失败");
                    }
                });
        // 注册系统图库权限回调
        this.permissionsByGallery = this.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                it -> {
                    if (this.getGranted(it)) {
                        this.pictureByGallery();
                    } else {
                        UIToast.asyncToast("获取系统权限失败");
                    }
                });
    }

    private void onClick(@NonNull View it) {
        if (R.id.cancelTextView == it.getId()) {
            this.dismiss();
        }
        if (R.id.pictureTakeTextView == it.getId()) {
            this.permissionsByTake();
        }
        if (R.id.pictureGalleryTextView == it.getId()) {
            this.permissionsByGallery();
        }
    }

    private void pictureByTake() {
        this.rUri = this.createTempPictureUri();
        if (this.pictureByTake != null) {
            this.pictureByTake.launch(this.rUri);
        }
    }

    private void pictureByGallery() {
        if (this.pictureByGallery != null) {
            this.pictureByGallery.launch(null);
        }
    }

    private void permissionsByTake() {
        final String[] permissions = new String[]{
                Manifest.permission.CAMERA,
        };
        if (this.permissionsByTake != null) {
            this.permissionsByTake.launch(permissions);
        }
    }

    private void permissionsByGallery() {
        final String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        if (this.permissionsByGallery != null) {
            this.permissionsByGallery.launch(permissions);
        }
    }

    private boolean getGranted(Map<String, Boolean> it) {
        if (it == null || it.isEmpty()) {
            return false;
        }
        for (final String permission : it.keySet()) {
            final Boolean granted = it.get(permission);
            if (null == granted || !granted) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    private Uri createTempPictureUri() {
        // 生成图片名称
        final String fName;
        fName = String.format("pic%s.jpg", System.currentTimeMillis());
        // 生成图片文件
        final File picFile;
        picFile = new File(UIFile.getImagePath(), fName);
        // 生成图片Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Context context = this.requireContext();
            final String pName = context.getPackageName();
            final String authority;
            authority = String.format("%s.FileProvider", pName);
            return FileProvider.getUriForFile(context, authority, picFile);
        }
        return Uri.fromFile(picFile);
    }

    private void handlerUri(@Nullable Uri it) {
        if (it == null) {
            UIToast.asyncToast("获取失败");
            return;
        }
        // Sets result call.
        UIViewModelProviders
                .ofParent(this)
                .get(UIPictureViewModel.class)
                .setPicture(it);
        // Done and finish it.
        this.getUINavigatorController().navigateUp();
    }
}
