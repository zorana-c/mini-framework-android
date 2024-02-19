package com.framework.common.palette;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

/**
 * @Author create by Zhengzelong on 2023-06-01
 * @Email : 171905184@qq.com
 * @Description :
 */
public class GlidePaletteTarget<TranscodeType> extends CustomTarget<TranscodeType> {

    @NonNull
    public static <T> GlidePaletteTarget<T> with(@NonNull Object url) {
        return new GlidePaletteTarget<>(url);
    }

    @NonNull
    private final PaletteLoader<TranscodeType> paletteLoader;

    protected GlidePaletteTarget(@NonNull Object url) {
        this.paletteLoader = new PaletteLoader<>(url);
    }

    @Override
    public void onResourceReady(@NonNull TranscodeType resource,
                                @Nullable Transition<? super TranscodeType> transition) {
        if (transition == null || !transition.transition(resource, null)) {
            this.paletteLoader.generate(resource);
        } else {
            this.paletteLoader.generate(null);
        }
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        this.paletteLoader.generate(null);
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        this.paletteLoader.generate(null);
    }

    @NonNull
    public GlidePaletteTarget<File> asFile() {
        return (GlidePaletteTarget<File>) this;
    }

    @NonNull
    public GlidePaletteTarget<Bitmap> asBitmap() {
        return (GlidePaletteTarget<Bitmap>) this;
    }

    @NonNull
    public GlidePaletteTarget<GifDrawable> asGif() {
        return (GlidePaletteTarget<GifDrawable>) this;
    }

    @NonNull
    public GlidePaletteTarget<Drawable> asDrawable() {
        return (GlidePaletteTarget<Drawable>) this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> use(@Profile int paletteProfile) {
        this.paletteLoader.addPaletteTarget(new PaletteTarget(paletteProfile));
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> skipCache() {
        return this.skipCache(true);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> skipCache(boolean skipCache) {
        this.paletteLoader.skipCache(skipCache);
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> crossFade() {
        return this.crossFade(true);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> crossFade(boolean crossFade) {
        return this.crossFade(crossFade, -1);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> crossFade(int crossFadeSpeed) {
        return this.crossFade(true, crossFadeSpeed);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> crossFade(boolean crossFade, int crossFadeSpeed) {
        final PaletteTarget pt = this.lastPaletteTarget();
        pt.setCrossFade(crossFade);
        pt.setCrossFadeSpeed(crossFadeSpeed);
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> backgroundAlpha(
            @FloatRange(from = 0.f, to = 1.f) float backgroundAlpha) {
        this.lastPaletteTarget().setBackgroundAlpha(backgroundAlpha);
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> addListener(@NonNull PaletteListener listener) {
        this.paletteLoader.addPaletteListener(listener);
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> intoBackground(@NonNull View view) {
        return this.intoBackground(view, Swatch.RGB);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> intoBackground(@NonNull View view,
                                                            @Swatch int paletteSwatch) {
        this.lastPaletteTarget().addBackground(view, paletteSwatch);
        return this;
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> intoTextColor(@NonNull TextView view) {
        return this.intoTextColor(view, Swatch.TITLE_TEXT_COLOR);
    }

    @NonNull
    public GlidePaletteTarget<TranscodeType> intoTextColor(@NonNull TextView view,
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
