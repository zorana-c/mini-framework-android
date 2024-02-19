package com.framework.core.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-05-24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIActivityResultContracts {
    /**
     * 系统拍照
     *
     * @parame 指定 Uri-图片保存路径
     * @return Boolean-保存状态
     */
    public static class TakePicture extends ActivityResultContract<Uri, Boolean> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull Uri input) {
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, input);
        }

        @NonNull
        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return resultCode == Activity.RESULT_OK;
        }
    }

    /**
     * 系统拍照
     *
     * @return Bitmap-原始图片
     */
    public static class TakePicturePreview extends ActivityResultContract<Void, Bitmap> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @Nullable Void input) {
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        @Override
        public Bitmap parseResult(int resultCode, @Nullable Intent intent) {
            if (intent == null || resultCode != Activity.RESULT_OK) {
                return null;
            }
            return intent.getParcelableExtra("data");
        }
    }

    /**
     * 系统图库
     *
     * @return Uri-图片路径
     */
    public static class GalleryPicturePreview extends ActivityResultContract<Void, Uri> {

        @NonNull
        @Override
        @SuppressLint("IntentReset")
        public Intent createIntent(@NonNull Context context, @Nullable Void input) {
            return new Intent(Intent.ACTION_PICK)
                    .setType(MediaStore.Images.Media.CONTENT_TYPE)
                    .setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        @Nullable
        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (intent == null || resultCode != Activity.RESULT_OK) {
                return null;
            }
            return intent.getData();
        }
    }
}
