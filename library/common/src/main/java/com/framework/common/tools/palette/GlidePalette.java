package com.framework.common.tools.palette;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * @Author create by Zhengzelong on 2023-06-01
 * @Email : 171905184@qq.com
 * @Description :
 */
public class GlidePalette<TranscodeType> implements RequestListener<TranscodeType> {

    @NonNull
    public static <T> GlidePalette<T> with(@NonNull Object url) {
        return new GlidePalette<>(url);
    }

    @NonNull
    private final PaletteLoader<TranscodeType> paletteLoader;

    protected GlidePalette(@NonNull Object url) {
        this.paletteLoader = new PaletteLoader<>(url);
    }

    @Override
    public boolean onResourceReady(@Nullable TranscodeType r,
                                   @Nullable Object model,
                                   @Nullable Target<TranscodeType> target,
                                   @Nullable DataSource ds,
                                   boolean isFirstResource) {
        this.paletteLoader.generate(r);
        return false;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e,
                                @Nullable Object model,
                                @Nullable Target<TranscodeType> target,
                                boolean isFirstResource) {
        this.paletteLoader.generate(null);
        return false;
    }

    @NonNull
    public GlidePalette<File> asFile() {
        return (GlidePalette<File>) this;
    }

    @NonNull
    public GlidePalette<Bitmap> asBitmap() {
        return (GlidePalette<Bitmap>) this;
    }

    @NonNull
    public GlidePalette<GifDrawable> asGif() {
        return (GlidePalette<GifDrawable>) this;
    }

    @NonNull
    public GlidePalette<Drawable> asDrawable() {
        return (GlidePalette<Drawable>) this;
    }

    @NonNull
    public GlidePalette<TranscodeType> use(@Profile int paletteProfile) {
        this.paletteLoader.addPaletteTarget(new PaletteTarget(paletteProfile));
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> skipCache() {
        return this.skipCache(true);
    }

    @NonNull
    public GlidePalette<TranscodeType> skipCache(boolean skipCache) {
        this.paletteLoader.skipCache(skipCache);
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> crossFade() {
        return this.crossFade(true);
    }

    @NonNull
    public GlidePalette<TranscodeType> crossFade(boolean crossFade) {
        return this.crossFade(crossFade, -1);
    }

    @NonNull
    public GlidePalette<TranscodeType> crossFade(int crossFadeSpeed) {
        return this.crossFade(true, crossFadeSpeed);
    }

    @NonNull
    public GlidePalette<TranscodeType> crossFade(boolean crossFade, int crossFadeSpeed) {
        final PaletteTarget pt = this.lastPaletteTarget();
        pt.setCrossFade(crossFade);
        pt.setCrossFadeSpeed(crossFadeSpeed);
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> backgroundAlpha(
            @FloatRange(from = 0.f, to = 1.f) float backgroundAlpha) {
        this.lastPaletteTarget().setBackgroundAlpha(backgroundAlpha);
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> addListener(@NonNull PaletteListener listener) {
        this.paletteLoader.addPaletteListener(listener);
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> intoBackground(@NonNull View view) {
        return this.intoBackground(view, Swatch.RGB);
    }

    @NonNull
    public GlidePalette<TranscodeType> intoBackground(@NonNull View view,
                                                      @Swatch int paletteSwatch) {
        this.lastPaletteTarget().addBackground(view, paletteSwatch);
        return this;
    }

    @NonNull
    public GlidePalette<TranscodeType> intoTextColor(@NonNull TextView view) {
        return this.intoTextColor(view, Swatch.TITLE_TEXT_COLOR);
    }

    @NonNull
    public GlidePalette<TranscodeType> intoTextColor(@NonNull TextView view,
                                                     @Swatch int paletteSwatch) {
        this.lastPaletteTarget().addTextColor(view, paletteSwatch);
        return this;
    }

    @Nullable
    public AsyncTask<Bitmap, Void, Palette> getPaletteTask() {
        return this.paletteLoader.getPaletteTask();
    }

    @NonNull
    private PaletteTarget lastPaletteTarget() {
        return this.paletteLoader.lastPaletteTarget();
    }
}
