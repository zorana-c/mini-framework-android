package com.framework.demo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.expand.letters.LettersRecyclerView;
import com.framework.demo.R;

/**
 * @Author create by Zhengzelong on 2022/7/18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class LettersRecyclerAdapter extends LettersRecyclerView.Adapter<LettersRecyclerAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateHeadViewHolder(@NonNull ViewGroup parent, int headItemViewType) {
        return new ViewHolder(this.inflate(R.layout.item_letters_layout));
    }

    @NonNull
    @Override
    public ViewHolder onCreateTailViewHolder(@NonNull ViewGroup parent, int tailItemViewType) {
        return new ViewHolder(this.inflate(R.layout.item_letters_layout));
    }

    @NonNull
    @Override
    public ViewHolder onCreateEmptyViewHolder(@NonNull ViewGroup parent, int emptyItemViewType) {
        return new ViewHolder(this.inflate(R.layout.item_letters_layout));
    }

    @NonNull
    @Override
    public ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int groupItemViewType) {
        return new ViewHolder(this.inflate(R.layout.item_letters_layout));
    }

    @Override
    public void onBindHeadViewHolder(@NonNull ViewHolder holder, int position) {
        final TextView titleTextView = holder.requireViewById(R.id.titleTextView);
        titleTextView.setText(holder.getItemLetter());
        titleTextView.setSelected(holder.hasSelected());
    }

    @Override
    public void onBindTailViewHolder(@NonNull ViewHolder holder, int position) {
        final TextView titleTextView = holder.requireViewById(R.id.titleTextView);
        titleTextView.setText(holder.getItemLetter());
        titleTextView.setSelected(holder.hasSelected());
    }

    @Override
    public void onBindEmptyViewHolder(@NonNull ViewHolder holder, int position) {
        final TextView titleTextView = holder.requireViewById(R.id.titleTextView);
        titleTextView.setText(holder.getItemLetter());
        titleTextView.setSelected(holder.hasSelected());
    }

    @Override
    public void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition) {
        final TextView titleTextView = holder.requireViewById(R.id.titleTextView);
        titleTextView.setText(holder.getItemLetter());
        titleTextView.setSelected(holder.hasSelected());
    }

    @Nullable
    @Override
    public CharSequence getHeadItemLetter(int position) {
        return "#";
    }

    @Nullable
    @Override
    public CharSequence getTailItemLetter(int position) {
        return "#";
    }

    @Nullable
    @Override
    public CharSequence getEmptyItemLetter(int position) {
        return "#";
    }

    public static class ViewHolder extends LettersRecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
