package com.framework.widget.recycler.picker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.framework.widget.R;

/**
 * @Author create by Zhengzelong on 2023-03-31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class AppCompatPickerTextView extends AppCompatTextView
        implements PickerLayoutManager.ClipChildCallback {
    private final Rect mTempRect = new Rect();
    private final Rect mClipRect = new Rect();
    private final Paint mOverPaint = new Paint();

    // 覆盖文本: 是否加粗
    private boolean mOverFakeBoldText;
    // 覆盖文本大小
    private float mOverTextSize = .0f;
    // 覆盖文本颜色
    @ColorInt
    private int mOverTextColor = Color.TRANSPARENT;

    public AppCompatPickerTextView(@NonNull Context context) {
        this(context, null);
    }

    public AppCompatPickerTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatPickerTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppCompatPickerTextView);
        final int overTextColor;
        overTextColor = typedArray.getColor(R.styleable.AppCompatPickerTextView_overTextColor, -1);
        final int overTextSize;
        overTextSize = typedArray.getDimensionPixelSize(R.styleable.AppCompatPickerTextView_overTextSize, -1);
        final boolean overFakeBoldText;
        overFakeBoldText = typedArray.getBoolean(R.styleable.AppCompatPickerTextView_overFakeBoldText, false);
        typedArray.recycle();
        if (overTextColor != -1) {
            this.setOverTextColor(overTextColor);
        }
        if (overTextSize != -1) {
            this.setOverTextSize(TypedValue.COMPLEX_UNIT_PX, overTextSize);
        }
        this.setOverFakeBoldText(overFakeBoldText);
    }

    @Override
    public void dispatchClipChildChanged(@NonNull ViewGroup target, int left, int top, int right, int bottom) {
        final Rect clipRect = this.mClipRect;
        if (top != clipRect.top
                || left != clipRect.left
                || right != clipRect.right
                || bottom != clipRect.bottom) {
            clipRect.set(left, top, right, bottom);
            this.invalidate();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final CharSequence text = this.getText();
        if (text == null) {
            super.onDraw(canvas);
            return;
        }
        final Paint textPaint = this.getPaint();
        final String textString = text.toString();
        this.drawOrigText(canvas, textString, textPaint);
        this.drawOverText(canvas, textString, textPaint);
    }

    private void drawOrigText(@NonNull Canvas canvas, @NonNull String text, @NonNull Paint paint) {
        final Rect outRect = this.mTempRect;
        final Rect clipRect = this.mClipRect;
        paint.setColor(this.getCurrentTextColor());
        paint.getTextBounds(text, 0, text.length(), outRect);
        final int x = (this.getWidth() - (outRect.left + outRect.right)) / 2;
        final int y = (this.getHeight() - (outRect.top + outRect.bottom)) / 2;
        final int restoreCount = canvas.save();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(clipRect);
        } else {
            canvas.clipRect(clipRect, Region.Op.DIFFERENCE);
        }
        canvas.drawText(text, x, y, paint);
        canvas.restoreToCount(restoreCount);
    }

    private void drawOverText(@NonNull Canvas canvas, @NonNull String text, @NonNull Paint paint) {
        final Rect outRect = this.mTempRect;
        final Rect clipRect = this.mClipRect;
        if (clipRect.isEmpty()) {
            return;
        }
        final Paint overPaint = this.mOverPaint;
        overPaint.set(paint);
        overPaint.setColor(this.mOverTextColor);
        overPaint.setFakeBoldText(this.mOverFakeBoldText);
        final float overTextSize = this.mOverTextSize;
        if (overTextSize != .0f) {
            overPaint.setTextSize(overTextSize);
        }
        overPaint.getTextBounds(text, 0, text.length(), outRect);
        final int x = (this.getWidth() - (outRect.left + outRect.right)) / 2;
        final int y = (this.getHeight() - (outRect.top + outRect.bottom)) / 2;
        final int restoreCount = canvas.save();
        canvas.clipRect(clipRect);
        canvas.drawText(text, x, y, overPaint);
        canvas.restoreToCount(restoreCount);
    }

    public boolean isOverFakeBoldText() {
        return this.mOverFakeBoldText;
    }

    public void setOverFakeBoldText(boolean fakeBoldText) {
        if (this.mOverFakeBoldText != fakeBoldText) {
            this.mOverFakeBoldText = fakeBoldText;

            if (this.getLayout() != null) {
                this.requestLayout();
                this.invalidate();
            }
        }
    }

    @ColorInt
    public int getOverTextColor() {
        return this.mOverTextColor;
    }

    public void setOverTextColor(@ColorInt int color) {
        if (this.mOverTextColor != color) {
            this.mOverTextColor = color;
            this.invalidate();
        }
    }

    public void setOverTextColorRes(@ColorRes int resId) {
        final Context context = this.getContext();
        final Resources resources;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        this.setOverTextColor(resources.getColor(resId));
    }

    public float getOverTextSize() {
        final float textSize;
        if (this.mOverTextSize == .0f) {
            textSize = this.getTextSize();
        } else {
            textSize = this.mOverTextSize;
        }
        return textSize;
    }

    public void setOverTextSize(float size /*sp*/) {
        this.setOverTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setOverTextSize(int unit, float size) {
        final Context context = this.getContext();
        final Resources resources;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        final DisplayMetrics dm = resources.getDisplayMetrics();
        final float overTextSize;
        overTextSize = TypedValue.applyDimension(unit, size, dm);
        if (this.mOverTextSize != overTextSize) {
            this.mOverTextSize = overTextSize;

            if (this.getLayout() != null) {
                this.requestLayout();
                this.invalidate();
            }
        }
    }
}
