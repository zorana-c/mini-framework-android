package com.framework.common.palette;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.palette.graphics.Palette;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-06-01
 * @Email : 171905184@qq.com
 * @Description :
 */
final class PaletteTarget {
    private static final int DEFAULT_CROSS_FADE_SPEED = 300;

    @Profile
    private final int paletteProfile;
    @NonNull
    private final ArrayList<Pair<View, Integer>> backgrounds = new ArrayList<>();
    @NonNull
    private final ArrayList<Pair<TextView, Integer>> textColors = new ArrayList<>();

    @FloatRange(from = 0.f, to = 1.f)
    private float backgroundAlpha = 1.f;
    private int crossFadeSpeed = DEFAULT_CROSS_FADE_SPEED;
    private boolean crossFade = false;

    public PaletteTarget(@Profile int paletteProfile) {
        this.paletteProfile = paletteProfile;
    }

    public void setCrossFade(boolean crossFade) {
        this.crossFade = crossFade;
    }

    public void setCrossFadeSpeed(int crossFadeSpeed) {
        this.crossFadeSpeed = Math.max(0, crossFadeSpeed);
    }

    public void setBackgroundAlpha(float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    @Profile
    public int getPaletteProfile() {
        return this.paletteProfile;
    }

    public void addBackground(@NonNull View view,
                              @Swatch int paletteSwatch) {
        this.backgrounds.add(Pair.create(view, paletteSwatch));
    }

    public void addTextColor(@NonNull TextView view,
                             @Swatch int paletteSwatch) {
        this.textColors.add(Pair.create(view, paletteSwatch));
    }

    public void clear() {
        this.textColors.clear();
        this.backgrounds.clear();
        this.crossFade = false;
        this.crossFadeSpeed = DEFAULT_CROSS_FADE_SPEED;
    }

    public void apply(@NonNull Palette.Swatch swatch,
                      boolean cacheHit) {
        this.applyBackground(swatch, cacheHit);
        this.applyTextColor(swatch, cacheHit);
    }

    private void applyBackground(@NonNull Palette.Swatch swatch,
                                 boolean cacheHit) {
        for (Pair<View, Integer> it : this.backgrounds) {
            @ColorInt final int colorInt;
            colorInt = this.getColorInt(swatch, it.second);

            if (!cacheHit && this.crossFade) {
                this.crossFadeBackground(it, colorInt);
            } else {
                final Drawable bd;
                bd = this.createBackgroundDrawable(colorInt);
                it.first.setBackground(bd);
            }
        }
    }

    private void applyTextColor(@NonNull Palette.Swatch swatch,
                                boolean cacheHit) {
        for (Pair<TextView, Integer> it : this.textColors) {
            @ColorInt final int colorInt;
            colorInt = this.getColorInt(swatch, it.second);
            it.first.setTextColor(colorInt);
        }
    }

    @ColorInt
    private int getColorInt(@NonNull Palette.Swatch swatch,
                            @Swatch int paletteSwatch) {
        switch (paletteSwatch) {
            case Swatch.RGB:
                return swatch.getRgb();
            case Swatch.BODY_TEXT_COLOR:
                return swatch.getBodyTextColor();
            case Swatch.TITLE_TEXT_COLOR:
                return swatch.getTitleTextColor();
        }
        return 0;
    }

    private void crossFadeBackground(@NonNull Pair<View, Integer> pair,
                                     @ColorInt int newColorInt) {
        final View it = pair.first;
        final Drawable[] drawables = new Drawable[2];
        final Drawable oldDrawable = it.getBackground();
        if (oldDrawable == null) {
            drawables[0] = new ColorDrawable(it.getSolidColor());
        } else {
            drawables[0] = oldDrawable;
        }
        drawables[1] = this.createBackgroundDrawable(newColorInt);

        final TransitionDrawable td;
        td = new TransitionDrawable(drawables);
        it.setBackground(td);
        td.startTransition(this.crossFadeSpeed);
    }

    @NonNull
    private Drawable createBackgroundDrawable(@ColorInt int colorInt) {
        final Drawable background = new ColorDrawable(colorInt);
        background.setAlpha((int) (255 * this.backgroundAlpha));
        return background;
    }
}
