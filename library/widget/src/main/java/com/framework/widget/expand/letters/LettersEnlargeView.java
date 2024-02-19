package com.framework.widget.expand.letters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @Author create by Zhengzelong on 2022/5/25
 * @Email : 171905184@qq.com
 * @Description : 快速索引提示
 */
public class LettersEnlargeView extends AppCompatTextView {
    private OnLettersChangedListener listener;
    private LettersRecyclerView lettersRecyclerView;

    public LettersEnlargeView(@NonNull Context context) {
        this(context, null);
    }

    public LettersEnlargeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LettersEnlargeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachedToLetters(@Nullable LettersRecyclerView lettersRecyclerView) {
        final LettersRecyclerView oldLettersRecyclerView = this.lettersRecyclerView;
        if (oldLettersRecyclerView == lettersRecyclerView) {
            return;
        }
        if (oldLettersRecyclerView != null) {
            oldLettersRecyclerView.removeOnLettersChangedListener(this.listener);
        }
        this.setVisibility(View.VISIBLE);
        this.lettersRecyclerView = lettersRecyclerView;
        if (lettersRecyclerView != null) {
            if (this.listener == null) {
                this.listener = new OnLettersChangedListener();
            }
            lettersRecyclerView.addOnLettersChangedListener(this.listener);
            this.setVisibility(View.GONE);
        }
    }

    public void onLettersChanged(@NonNull LettersRecyclerView lettersRecyclerView,
                                 @Nullable LettersRecyclerView.ViewHolder holder) {
        if (holder == null) {
            this.setVisibility(View.GONE);
        } else {
            this.setText(holder.getItemLetter());
            this.setVisibility(View.VISIBLE);
        }
    }

    public void onLettersScrolled(@NonNull LettersRecyclerView lettersRecyclerView,
                                  @NonNull LettersRecyclerView.ViewHolder holder) {
    }

    private final class OnLettersChangedListener implements LettersRecyclerView.OnLettersChangedListener {
        @Override
        public void onLettersChanged(@NonNull LettersRecyclerView lettersRecyclerView,
                                     @Nullable LettersRecyclerView.ViewHolder holder) {
            LettersEnlargeView.this.onLettersChanged(lettersRecyclerView, holder);

        }

        @Override
        public void onLettersScrolled(@NonNull LettersRecyclerView lettersRecyclerView,
                                      @NonNull LettersRecyclerView.ViewHolder holder) {
            LettersEnlargeView.this.onLettersScrolled(lettersRecyclerView, holder);
        }
    }
}
