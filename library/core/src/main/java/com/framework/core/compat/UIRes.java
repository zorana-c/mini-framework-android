package com.framework.core.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.framework.core.UIFramework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIRes {
    @SuppressLint("InlinedApi")
    public static final int ID_NULL = Resources.ID_NULL;

    /**
     * dp 转成px
     */
    public static int dip2px(float dpValue) {
        return dip2px(UIFramework.getApplicationContext(), dpValue);
    }

    /**
     * dp 转成px
     */
    public static int dip2px(@NonNull Context context, float dpValue) {
        final float scale = getResources(context).getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }

    /**
     * px转成dp
     */
    public static int px2dip(float pxValue) {
        return px2dip(UIFramework.getApplicationContext(), pxValue);
    }

    /**
     * px转成dp
     */
    public static int px2dip(@NonNull Context context, float pxValue) {
        final float scale = getResources(context).getDisplayMetrics().density;
        return Math.round(pxValue / scale);
    }

    @NonNull
    public static Resources getResources() {
        return getResources(UIFramework.getApplicationContext());
    }

    @NonNull
    public static Resources getResources(@NonNull Context context) {
        return context.getResources();
    }

    public static float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    public static float getDimension(@NonNull Context context, @DimenRes int id) {
        return getResources(context).getDimension(id);
    }

    public static int getDimensionPixelSize(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public static int getDimensionPixelSize(@NonNull Context context, @DimenRes int id) {
        return getResources(context).getDimensionPixelSize(id);
    }

    public static int getDimensionPixelOffset(@DimenRes int id) {
        return getResources().getDimensionPixelOffset(id);
    }

    public static int getDimensionPixelOffset(@NonNull Context context, @DimenRes int id) {
        return getResources(context).getDimensionPixelOffset(id);
    }

    @NonNull
    public static String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int resId) {
        return getResources(context).getString(resId);
    }

    @NonNull
    public static String getString(@StringRes int resId, @NonNull Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int resId, @NonNull Object... formatArgs) {
        return getResources(context).getString(resId, formatArgs);
    }

    @ColorInt
    public static int getColor(@ColorRes int resId) {
        return getColor(UIFramework.getApplicationContext(), resId);
    }

    @ColorInt
    public static int getColor(@NonNull Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    @Nullable
    public static Drawable getDrawable(@DrawableRes int resId) {
        return getDrawable(UIFramework.getApplicationContext(), resId);
    }

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    @Nullable
    public static ColorStateList getColorStateList(@ColorRes int resId) {
        return getColorStateList(UIFramework.getApplicationContext(), resId);
    }

    @Nullable
    public static ColorStateList getColorStateList(@NonNull Context context, @ColorRes int resId) {
        return ContextCompat.getColorStateList(context, resId);
    }

    @Nullable
    public static ColorDrawable getColorDrawable(@ColorRes int resId) {
        return getColorDrawable(UIFramework.getApplicationContext(), resId);
    }

    @Nullable
    public static ColorDrawable getColorDrawable(@NonNull Context context, @ColorRes int resId) {
        if (resId == ID_NULL) {
            return null;
        }
        return new ColorDrawable(getColor(context, resId));
    }

    @NonNull
    public static Drawable getRoundedDrawable(@ColorInt int colorInt,
                                              float radius) {
        final float[] outerRadii = new float[]{
                radius, radius,
                radius, radius,
                radius, radius,
                radius, radius,
        };
        return getRoundedDrawable(colorInt, outerRadii);
    }

    /**
     * <pre>
     * final float[] outerRadii = new float[]{
     *         topLeftRadius, topLeftRadius,
     *         topRightRadius, topRightRadius,
     *         bottomRightRadius, bottomRightRadius,
     *         bottomLeftRadius, bottomLeftRadius
     * };
     * </pre>
     *
     * @param outerRadii 圆角数组
     */
    @NonNull
    public static Drawable getRoundedDrawable(@ColorInt int colorInt,
                                              @Nullable @Size(8) float[] outerRadii) {
        final RoundRectShape shape;
        shape = new RoundRectShape(outerRadii, null, null);
        // 生成Drawable对象
        final ShapeDrawable drawable = new ShapeDrawable(shape);
        // 设置颜色为填充模式
        final Paint paint = drawable.getPaint();
        paint.setColor(colorInt);
        paint.setStyle(Paint.Style.FILL);
        return drawable;
    }

    @NonNull
    public static Drawable getRoundedDrawableResource(@ColorRes int resId,
                                                      float radius) {
        return getRoundedDrawableResource(UIFramework.getApplicationContext(), resId, radius);
    }

    @NonNull
    public static Drawable getRoundedDrawableResource(@ColorRes int resId,
                                                      @Nullable @Size(8) float[] outerRadii) {
        return getRoundedDrawableResource(UIFramework.getApplicationContext(), resId, outerRadii);
    }

    @NonNull
    public static Drawable getRoundedDrawableResource(@NonNull Context context,
                                                      @ColorRes int resId,
                                                      float radius) {
        return getRoundedDrawable(getColor(context, resId), radius);
    }

    @NonNull
    public static Drawable getRoundedDrawableResource(@NonNull Context context,
                                                      @ColorRes int resId,
                                                      @Nullable @Size(8) float[] outerRadii) {
        return getRoundedDrawable(getColor(context, resId), outerRadii);
    }

    @NonNull
    public static Bitmap drawable2Bitmap(@NonNull Drawable drawable) {
        final Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565
        );
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @NonNull
    public static Drawable bitmap2Drawable(@NonNull Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    @NonNull
    public static InputStream bitmap2InputStream(@NonNull Bitmap bitmap) {
        return bitmap2InputStream(bitmap, Bitmap.CompressFormat.PNG);
    }

    @NonNull
    public static InputStream bitmap2InputStream(@NonNull Bitmap bitmap,
                                                 @NonNull Bitmap.CompressFormat format) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @NonNull
    public static Bitmap inputStream2Bitmap(@NonNull InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 图片按质量压缩
     */
    @NonNull
    public static Bitmap compressImageFromQualityBy(@NonNull Bitmap bitmap,
                                                    long size /*单位: kb*/) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 质量压缩方法, 这里100表示不压缩, 把压缩后的数据存放到byteArrayOutputStream中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int options = 90;
        // 循环判断如果压缩后图片是否大于sizeKB, 大于继续压缩
        while (byteArrayOutputStream.toByteArray().length / 1024 > size) {
            // 重置byteArrayOutputStream, 即清空byteArrayOutputStream
            byteArrayOutputStream.reset();
            // 这里压缩options%, 把压缩后的数据存放到byteArrayOutputStream中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据byteArrayOutputStream存放到ByteArrayInputStream中
        final ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(byteArrayInputStream, null, null);
    }

    /**
     * 图片按比例压缩
     */
    @NonNull
    public static Bitmap compressImageFromSizeBy(@NonNull Bitmap bitmap,
                                                 int width,
                                                 int height) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (byteArrayOutputStream.toByteArray().length / 1024 > 1024) {
            // 重置byteArrayOutputStream, 即清空byteArrayOutputStream
            byteArrayOutputStream.reset();
            // 这里压缩50%，把压缩后的数据存放到byteArrayOutputStream中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        }
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(byteArrayInputStream, null, options);
        options.inJustDecodeBounds = false;
        final int outWidth = options.outWidth;
        final int outHeight = options.outHeight;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; // be = 1 表示不缩放
        if (outWidth > outHeight && outWidth > width) {
            // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / width);
        } else if (outWidth < outHeight && outHeight > height) {
            // 如果高度高的话根据高度固定大小缩放
            be = (int) (options.outHeight / height);
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565; // 降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return BitmapFactory.decodeStream(byteArrayInputStream, null, options);
    }

    /**
     * 图片按比例大小压缩
     */
    @NonNull
    public static Bitmap compressImageFromQualityAndSizeBy(@NonNull Bitmap bitmap,
                                                           int width,
                                                           int height,
                                                           long size /*单位: kb*/) {
        return compressImageFromQualityBy(compressImageFromSizeBy(bitmap, width, height), size);
    }
}
