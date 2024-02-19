package com.framework.core.helper;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;

/**
 * @Author create by Zhengzelong on 2021/4/12
 * @Email : 171905184@qq.com
 * @Description : 软键盘帮助类
 */
public final class UIKeyboardHelper {

    @NonNull
    public static UIKeyboardHelper with(@NonNull UIPageControllerOwner owner) {
        return UIKeyboardHelper.with(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static UIKeyboardHelper with(@NonNull UIPageController uiPageController) {
        return UIKeyboardHelper.with(uiPageController.<View>requireContentView());
    }

    @NonNull
    public static UIKeyboardHelper with(@NonNull View anchorView) {
        return new UIKeyboardHelper(anchorView);
    }

    @NonNull
    private final KeyboardComponent component;
    @Nullable
    private Callback callback;

    UIKeyboardHelper(@NonNull View anchor) {
        this.component = new KeyboardComponent(anchor);
    }

    @NonNull
    public UIKeyboardHelper setCallback(@Nullable Callback callback) {
        this.callback = callback;
        return this;
    }

    public void release() {
        this.callback = null;
        this.component.release();
    }

    private void notifyKeyboardChanged(@NonNull View view, int height) {
        if (this.callback != null) {
            this.callback.onKeyboardChanged(view, height);
        }
    }

    private final class KeyboardComponent implements
            ViewTreeObserver.OnGlobalLayoutListener,
            View.OnAttachStateChangeListener {
        @NonNull
        private final Rect tempRect = new Rect();
        @NonNull
        private final View anchor;
        // 偏移量
        private int diff = Integer.MIN_VALUE;

        private KeyboardComponent(@NonNull View anchor) {
            this.anchor = anchor;
            this.anchor.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onGlobalLayout() {
            final View anchor = this.anchor;
            final Rect outRect = this.tempRect;
            final View rootView = anchor.getRootView();
            anchor.getWindowVisibleDisplayFrame(outRect);

            final int oldDiff = this.diff;
            final int nowDiff = rootView.getHeight() - outRect.height();
            if (oldDiff == nowDiff) {
                return;
            }
            if (oldDiff != Integer.MIN_VALUE) {
                if (oldDiff < nowDiff) {
                    notifyKeyboardChanged(anchor, nowDiff);
                } else {
                    notifyKeyboardChanged(anchor, 0);
                }
            }
            this.diff = nowDiff;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull View view) {
            view.getViewTreeObserver()
                    .addOnGlobalLayoutListener(this);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull View view) {
            view.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);
        }

        public void release() {
            final View anchor = this.anchor;
            anchor.removeOnAttachStateChangeListener(this);
            anchor.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);
        }
    }

    public interface Callback {
        void onKeyboardChanged(@NonNull View view, int keyboardHeight);
    }
}
