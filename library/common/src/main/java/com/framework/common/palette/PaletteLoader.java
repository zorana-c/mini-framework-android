package com.framework.common.palette;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @Author create by Zhengzelong on 2023-06-02
 * @Email : 171905184@qq.com
 * @Description :
 */
final class PaletteLoader<TranscodeType> {
    @NonNull
    private final Object url;
    @NonNull
    private final LinkedList<PaletteTarget> paletteTargets = new LinkedList<>();
    @NonNull
    private final ArrayList<PaletteListener> paletteListeners = new ArrayList<>();
    private boolean skipCache;

    @Nullable
    private AsyncTask<Bitmap, Void, Palette> paletteTask;

    public PaletteLoader(@NonNull Object url) {
        this.url = url;
    }

    public void skipCache(boolean skipCache) {
        this.skipCache = skipCache;
    }

    @NonNull
    public PaletteTarget lastPaletteTarget() {
        if (this.paletteTargets.isEmpty()) {
            throw new UnsupportedOperationException(
                    "You must specify a palette with use(Profile)");
        }
        return this.paletteTargets.getLast();
    }

    public void addPaletteTarget(@NonNull PaletteTarget target) {
        this.paletteTargets.add(target);
    }

    public void addPaletteListener(@NonNull PaletteListener listener) {
        this.paletteListeners.add(listener);
    }

    @Nullable
    public AsyncTask<Bitmap, Void, Palette> getPaletteTask() {
        return this.paletteTask;
    }

    public void generate(@Nullable TranscodeType resource) {
        Bitmap bitmap = null;
        if (resource instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) resource).getBitmap();
        } else if (resource instanceof GifDrawable) {
            bitmap = ((GifDrawable) resource).getFirstFrame();
        } else if (resource instanceof BitmapOwner) {
            bitmap = ((BitmapOwner) resource).getBitmap();
        }
        // from empty.
        if (bitmap == null) {
            this.apply(null, false);
            return;
        }
        // from cache.
        if (!this.skipCache) {
            @Nullable final Palette it;
            it = PaletteCache.get(this.url);

            if (it != null) {
                this.apply(it, true);
                return;
            }
        }
        // from create.
        @NonNull
        Palette.Builder builder;
        builder = new Palette.Builder(bitmap);
        builder = this.dispatchOnIntercept(builder);
        this.paletteTask = builder.generate(it -> {
            if (!this.skipCache) {
                if (it != null) {
                    PaletteCache.set(this.url, it);
                }
            }
            this.apply(it, false);
        });
    }

    private void apply(@Nullable Palette palette,
            /* from cache */ boolean cacheHit) {
        this.dispatchOnPaletteLoaded(palette);
        if (palette == null) {
            this.clear();
            return;
        }
        @Nullable
        Palette.Swatch ps = null;
        for (PaletteTarget it : this.paletteTargets) {
            switch (it.getPaletteProfile()) {
                case Profile.VIBRANT:
                    ps = palette.getVibrantSwatch();
                    break;
                case Profile.VIBRANT_DARK:
                    ps = palette.getDarkVibrantSwatch();
                    break;
                case Profile.VIBRANT_LIGHT:
                    ps = palette.getLightVibrantSwatch();
                    break;
                case Profile.MUTED:
                    ps = palette.getMutedSwatch();
                    break;
                case Profile.MUTED_DARK:
                    ps = palette.getDarkMutedSwatch();
                    break;
                case Profile.MUTED_LIGHT:
                    ps = palette.getLightMutedSwatch();
                    break;
            }
            if (ps != null) {
                it.apply(ps, cacheHit);
            }
            it.clear();
        }
        this.clear();
    }

    @NonNull
    private Palette.Builder dispatchOnIntercept(
            @NonNull Palette.Builder builder) {
        @Nullable Palette.Builder pb;
        for (PaletteListener listener : this.paletteListeners) {
            pb = listener.onIntercept(builder);

            if (pb != null) {
                return pb;
            }
        }
        return builder;
    }

    private void dispatchOnPaletteLoaded(@Nullable Palette palette) {
        for (PaletteListener listener : this.paletteListeners) {
            listener.onPaletteLoaded(palette);
        }
    }

    private void clear() {
        for (PaletteTarget it : this.paletteTargets) {
            it.clear();
        }
        this.skipCache = false;
        this.paletteTask = null;
        this.paletteTargets.clear();
        this.paletteListeners.clear();
    }
}
