package com.framework.common.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-05-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIBlur {
    private static volatile UIBlur sInstance;

    @NonNull
    public static UIBlur with(@NonNull Context context) {
        if (sInstance != null) return sInstance;
        synchronized (UIBlur.class) {
            sInstance = new UIBlur(context);
        }
        return sInstance;
    }

    public enum Policy {
        /**
         * 模糊策略: RenderScript
         */
        BLUR_RS,
        /**
         * 模糊策略: FastBlur
         */
        BLUR_FAST,
    }

    @NonNull
    private final Context context;
    private int blurRadius = 12;
    private float blurScale = 0.3f;
    @NonNull
    private Policy blurPolicy = Policy.BLUR_RS;

    private UIBlur(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 模糊半径: (0, 25]
     */
    @NonNull
    public UIBlur setRadius(int blurRadius) {
        this.blurRadius = blurRadius;
        return this;
    }

    /**
     * 模糊缩放比例: [0, 1]
     */
    @NonNull
    public UIBlur setScale(float blurScale) {
        this.blurScale = blurScale;
        return this;
    }

    /**
     * 模糊策略(默认: RS)
     */
    @NonNull
    public UIBlur setPolicy(@NonNull Policy policy) {
        this.blurPolicy = policy;
        return this;
    }

    /**
     * @return Bitmap 模糊结果
     */
    @Nullable
    public Bitmap blur(@Nullable Bitmap target) {
        return this.blurInternal(target);
    }

    @Nullable
    private Bitmap blurInternal(@Nullable Bitmap target) {
        if (target == null) {
            return null;
        }
        final int blurRadius = this.blurRadius;
        if (blurRadius <= 0) {
            return null;
        }
        final Bitmap sb = this.createScaledBitmap(target);
        if (Policy.BLUR_FAST == this.blurPolicy) {
            return this.blurByFast(sb, blurRadius);
        }
        return this.blurByRS(sb, blurRadius);
    }

    @NonNull
    private Bitmap createScaledBitmap(@NonNull Bitmap target) {
        final float scale = this.blurScale;
        final int w = Math.round(target.getWidth() * scale);
        final int h = Math.round(target.getHeight() * scale);
        return Bitmap.createScaledBitmap(target, w, h, false);
    }

    @NonNull
    private Bitmap blurByRS(@NonNull Bitmap target, int radius) {
        final RenderScript rs;
        rs = RenderScript.create(this.context, RenderScript.ContextType.NORMAL);

        // Allocate memory for Renderscript to work with
        final Allocation input = Allocation.createFromBitmap(rs, target);
        final Allocation output = Allocation.createTyped(rs, input.getType());

        // Load up an instance of the specific script that we want to use.
        final ScriptIntrinsicBlur sib;
        sib = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        sib.setInput(input);
        // Set the blur radius
        sib.setRadius(radius);
        // Start the ScriptIntrinsicBlur
        sib.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(target);
        // destroy it
        rs.destroy();
        return target;
    }

    /**
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p>
     */
    @Nullable
    private Bitmap blurByFast(@NonNull Bitmap target, int radius) {
        if (radius < 1) {
            return (null);
        }
        final Bitmap bitmap;
        bitmap = target.copy(target.getConfig(), true);

        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        final int wm = w - 1;
        final int hm = h - 1;
        final int wh = w * h;
        final int div = radius + radius + 1;

        final int[] r = new int[wh];
        final int[] g = new int[wh];
        final int[] b = new int[wh];
        final int[] vMin = new int[Math.max(w, h)];
        int divSum, rSum, gSum, bSum, x, y, i, p, yp, yi, yw;

        divSum = (div + 1) >> 1;
        divSum *= divSum;
        final int[] dv = new int[256 * divSum];
        for (i = 0; i < 256 * divSum; i++) {
            dv[i] = (i / divSum);
        }

        yw = yi = 0;

        final int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int rinSum, ginSum, binSum;
        int routSum, goutSum, boutSum;

        for (y = 0; y < h; y++) {
            rSum = gSum = bSum = 0;
            rinSum = ginSum = binSum = 0;
            routSum = goutSum = boutSum = 0;

            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rbs = r1 - Math.abs(i);
                rSum += sir[0] * rbs;
                gSum += sir[1] * rbs;
                bSum += sir[2] * rbs;

                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rSum];
                g[yi] = dv[gSum];
                b[yi] = dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi++;
            }
            yw += w;
        }

        for (x = 0; x < w; x++) {
            rSum = gSum = bSum = 0;
            rinSum = ginSum = binSum = 0;
            routSum = goutSum = boutSum = 0;
            yp = -radius * w;

            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);
                rSum += r[yi] * rbs;
                gSum += g[yi] * rbs;
                bSum += b[yi] * rbs;

                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;

            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi])
                        | (dv[rSum] << 16)
                        | (dv[gSum] << 8)
                        | dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
