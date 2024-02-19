package com.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

/**
 * @Author create by Zhengzelong on 2022/1/11
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ClearInputEditText extends TextInputEditText {
    private int mExtentSpace = 24;
    @Nullable
    private Drawable mDrawable;
    @Nullable
    private OnClearClickListener mListener;

    public ClearInputEditText(@NonNull Context context) {
        this(context, null);
    }

    public ClearInputEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.editTextStyle);
    }

    public ClearInputEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.dispatchUpdateDrawableStatus();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_UP) {
            final int x = (int) (event.getX() + 0.5f);
            final int y = (int) (event.getY() + 0.5f);

            if (this.pointInDrawable(x, y)) {
                if (this.performClearClick()) {
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean pointInDrawable(int x, int y) {
        final Drawable[] drawables = this.getCompoundDrawablesRelative();
        final Drawable drawable = drawables[2];
        if (drawable == null) {
            return false;
        }
        final int extentSpace = this.mExtentSpace;
        final int width = this.getMeasuredWidth()
                - this.getPaddingRight();
        final int height = this.getMeasuredHeight()
                - this.getPaddingBottom();

        final Rect bounds = drawable.getBounds();
        final int left = width - bounds.width() - extentSpace;
        final int right = width + extentSpace;
        final int top = height - bounds.height() - extentSpace;
        final int bottom = height + extentSpace;
        return x >= left && x < right && y >= top && y < bottom;
    }

    public boolean performClearClick() {
        if (this.mListener != null) {
            this.mListener.onClearClick(this);
        } else this.setText(null);
        return true;
    }

    @Override
    protected void onTextChanged(@NonNull CharSequence text,
                                 int start,
                                 int lengthBefore,
                                 int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.dispatchUpdateDrawableStatus();
    }

    @Override
    protected void onFocusChanged(boolean focused,
                                  int direction,
                                  @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        this.dispatchUpdateDrawableStatus();
    }

    /*
     * attr
     * ref android.R.styleable#TextView_drawableStart
     * attr
     * ref android.R.styleable#TextView_drawableTop
     * attr
     * ref android.R.styleable#TextView_drawableEnd
     * attr
     * ref android.R.styleable#TextView_drawableBottom
     */
    protected void dispatchUpdateDrawableStatus() {
        final int length = this.length();

        boolean showing = false;
        if (this.hasFocus()) {
            showing = length != 0;
        }

        final Drawable[] drawables = this.getCompoundDrawablesRelative();
        final Drawable oldDrawable = drawables[2];
        if (showing) {
            if (this.mDrawable == null) {
                this.mDrawable = ContextCompat.getDrawable(this.getContext(),
                        R.mipmap.ic_clear_editor);
            }
            if (oldDrawable == null) {
                drawables[2] = this.mDrawable;
            }
        } else {
            if (oldDrawable != null) {
                this.mDrawable = oldDrawable;
                drawables[2] = null;
            }
        }
        if (oldDrawable != drawables[2]) {
            this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    drawables[0], drawables[1], drawables[2], drawables[3]);
        }
        this.setSelection(length);
    }

    public int getExtentSpace() {
        return this.mExtentSpace;
    }

    public void setExtentSpace(int extentSpace /* px */) {
        this.mExtentSpace = extentSpace;
    }

    public void setOnClearClickListener(@Nullable OnClearClickListener listener) {
        this.mListener = listener;
    }

    public interface OnClearClickListener {
        void onClearClick(@NonNull View width);
    }
}
