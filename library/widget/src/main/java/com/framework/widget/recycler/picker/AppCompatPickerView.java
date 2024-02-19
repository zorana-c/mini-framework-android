package com.framework.widget.recycler.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.R;
import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.recycler.pager.PagerLayoutManager;

/**
 * @Author create by Zhengzelong on 2023-03-31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class AppCompatPickerView extends ExpandableRecyclerView {
    private int mSoundId;
    private boolean mSoundEnabled;
    private SoundPool mSoundPools;

    public AppCompatPickerView(@NonNull Context context) {
        this(context, null);
    }

    public AppCompatPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceAsColor")
    public AppCompatPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final OnItemClickListener pickerItemClickListener;
        pickerItemClickListener = new OnItemClickListener();

        final PickerItemDecoration pickerItemDecoration;
        pickerItemDecoration = new PickerItemDecoration(context);

        final PickerLayoutManager pickerLayoutManager;
        pickerLayoutManager = new PickerLayoutManager(context);
        pickerLayoutManager.addOnPageChangeListener(new OnPageChangeListener());

        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppCompatPickerView);
        final boolean playSound;
        playSound = typedArray.getBoolean(R.styleable.AppCompatPickerView_pickerPlaySound, true);
        final int lineColor;
        lineColor = typedArray.getColor(R.styleable.AppCompatPickerView_pickerLineColor, -1);
        final int lineWidth;
        lineWidth = typedArray.getDimensionPixelOffset(R.styleable.AppCompatPickerView_pickerLineWidth, -1);
        final Drawable background;
        background = typedArray.getDrawable(R.styleable.AppCompatPickerView_pickerBackground);
        typedArray.recycle();

        if (lineColor != -1) {
            pickerItemDecoration.setLineColor(lineColor);
        }
        if (lineWidth != -1) {
            pickerItemDecoration.setLineWidth(lineWidth);
        }
        pickerItemDecoration.setBackground(background);

        this.setHasFixedSize(true);
        this.setSoundEnabled(playSound);
        this.setLayoutManager(pickerLayoutManager);
        this.addItemDecoration(pickerItemDecoration);
        this.setOnHeadItemClickListener(pickerItemClickListener);
        this.setOnTailItemClickListener(pickerItemClickListener);
        this.setOnGroupItemClickListener(pickerItemClickListener);
    }

    public void setLineWidth(int size) {
        for (int i = 0; i < this.getItemDecorationCount(); i++) {
            final RecyclerView.ItemDecoration itd = this.getItemDecorationAt(i);
            if (itd instanceof PickerItemDecoration) {
                ((PickerItemDecoration) itd).setLineWidth(size);
            }
        }
        this.invalidate();
    }

    public void setLineColor(@ColorInt int color) {
        for (int i = 0; i < this.getItemDecorationCount(); i++) {
            final RecyclerView.ItemDecoration itd = this.getItemDecorationAt(i);
            if (itd instanceof PickerItemDecoration) {
                ((PickerItemDecoration) itd).setLineColor(color);
            }
        }
        this.invalidate();
    }

    public void setDrawLineEnabled(boolean enabled) {
        RecyclerView.ItemDecoration itemDecoration = null;
        for (int i = 0; i < this.getItemDecorationCount(); i++) {
            final RecyclerView.ItemDecoration itd = this.getItemDecorationAt(i);
            if (itd instanceof PickerItemDecoration) {
                itemDecoration = itd;
            }
        }
        if (enabled) {
            if (itemDecoration == null) {
                itemDecoration = new PickerItemDecoration(this.getContext());
                this.addItemDecoration(itemDecoration);
            }
        } else {
            if (itemDecoration != null) {
                this.removeItemDecoration(itemDecoration);
            }
        }
        this.invalidate();
    }

    public void setPickerBackgroundColor(@ColorInt int color) {
        for (int i = 0; i < this.getItemDecorationCount(); i++) {
            final RecyclerView.ItemDecoration itd = this.getItemDecorationAt(i);
            if (itd instanceof PickerItemDecoration) {
                ((PickerItemDecoration) itd).setBackgroundColor(color);
            }
        }
        this.invalidate();
    }

    public void setPickerBackground(@Nullable Drawable drawable) {
        for (int i = 0; i < this.getItemDecorationCount(); i++) {
            final RecyclerView.ItemDecoration itd = this.getItemDecorationAt(i);
            if (itd instanceof PickerItemDecoration) {
                ((PickerItemDecoration) itd).setBackground(drawable);
            }
        }
        this.invalidate();
    }

    public boolean isLoopScrollEnabled() {
        final PickerLayoutManager lm = this.requireLayoutManager();
        return lm.isLoopScrollEnabled();
    }

    public void setLoopScrollEnabled(boolean enabled) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.setLoopScrollEnabled(enabled);
    }

    public int getCurrentPosition() {
        final PickerLayoutManager lm = this.requireLayoutManager();
        return lm.getCurrentPosition();
    }

    public void setCurrentPosition(int position) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.setCurrentPosition(position);
    }

    public void setCurrentPosition(int position, boolean smoothScroll) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.setCurrentPosition(position, smoothScroll);
    }

    public void setCurrentItemView(@NonNull View itemView) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.setCurrentItemView(itemView);
    }

    public void setCurrentItemView(@NonNull View itemView, boolean smoothScroll) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.setCurrentItemView(itemView, smoothScroll);
    }

    public void addOnPageChangeListener(@NonNull PagerLayoutManager.OnPageChangeListener listener) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.addOnPageChangeListener(listener);
    }

    public void removeOnPageChangeListener(@NonNull PagerLayoutManager.OnPageChangeListener listener) {
        final PickerLayoutManager lm = this.requireLayoutManager();
        lm.removeOnPageChangeListener(listener);
    }

    public boolean isSoundEnabled() {
        return this.mSoundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        if (this.mSoundEnabled != enabled) {
            this.mSoundEnabled = enabled;
            if (!enabled) {
                this.releaseSound();
            }
        }
    }

    @SuppressLint("NewApi")
    private void initSound() {
        if (!this.mSoundEnabled) {
            this.releaseSound();
            return;
        }
        try {
            if (this.mSoundPools == null) {
                final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_SYSTEM)
                        .build();
                this.mSoundPools = new SoundPool.Builder()
                        .setAudioAttributes(audioAttributes)
                        .setMaxStreams(50)
                        .build();
                this.mSoundPools.load(getContext(), R.raw.picker_keypress, 1);
            }
        } catch (@NonNull Exception e) {
            e.printStackTrace();
        }
    }

    private void playSound() {
        try {
            this.initSound();
            if (this.mSoundPools != null) {
                this.mSoundPools.stop(this.mSoundId);
                this.mSoundId = this.mSoundPools.play(1, 1, 1, 0, 0, 1);
            }
        } catch (@NonNull Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseSound() {
        if (this.mSoundPools != null) {
            this.mSoundPools.stop(this.mSoundId);
            this.mSoundPools.release();
            this.mSoundPools = null;
        }
    }

    private final class OnPageChangeListener extends PagerLayoutManager.SimpleOnPageChangeListener {
        private int mLastPosition = RecyclerView.NO_POSITION;

        @Override
        public void onPageScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
            if (RecyclerView.SCROLL_STATE_IDLE != recyclerView.getScrollState()) {
                AppCompatPickerView.this.initSound();
            }
        }

        @Override
        public void onPageScrolled(@NonNull RecyclerView recyclerView,
                                   int position, float positionOffset, int positionOffsetPixels) {
            if (RecyclerView.SCROLL_STATE_IDLE != recyclerView.getScrollState()) {
                this.playSoundBy(position);
            }
        }

        @Override
        public void onPageSelected(@NonNull RecyclerView recyclerView, int position) {
            if (RecyclerView.SCROLL_STATE_IDLE != recyclerView.getScrollState()) {
                this.playSoundBy(position);
            }
        }

        private void playSoundBy(int position) {
            if (this.mLastPosition != position) {
                this.mLastPosition = position;
                AppCompatPickerView.this.playSound();
            }
        }
    }

    private final class OnItemClickListener implements ExpandableRecyclerView.OnItemClickListener<ViewHolder> {
        @Override
        public void onItemClick(@NonNull ViewHolder holder, @NonNull View target, int position) {
            AppCompatPickerView.this.setCurrentItemView(holder.itemView, true);
        }
    }
}
