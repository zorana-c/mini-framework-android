package com.framework.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.expand.DefaultItemAnimator;
import com.framework.widget.expand.PositionType;
import com.framework.widget.expand.compat.GridLayoutManager;
import com.framework.widget.expand.letters.LettersEnlargeView;
import com.framework.widget.expand.letters.LettersRecyclerView;
import com.framework.core.compat.UIToast;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.util.UITestUtils;
import com.framework.demo.adapter.LettersRecyclerAdapter;
import com.framework.widget.sliver.SliverContainer;

/**
 * @Author create by Zhengzelong on 2022/7/15
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ListFragment extends UIListFragment<String> {
    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @NonNull
    @Override
    public View onCreateChildItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder holder, int position) {
        holder.<TextView>requireViewById(android.R.id.text1).setText(String.format("%s", holder.getItemLetter()));
        holder.<TextView>requireViewById(android.R.id.text1).setTextColor(Color.GRAY);
        holder.itemView.setBackgroundResource(R.color.decorBackground);
    }

    @Override
    public void onBindChildViewHolder(@NonNull UIViewHolder holder, int groupPosition, int childPosition) {
        holder.<TextView>requireViewById(android.R.id.text1).setText(String.format("Item %s - %s - %s", holder.getLayoutPosition(), groupPosition, childPosition));
        holder.<TextView>requireViewById(android.R.id.text1).setTextColor(Color.GRAY);
    }

    @Override
    public int getChildItemCount(int position) {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getItemLetter(int position) {
        return String.valueOf((char) (65 + position));
    }

    @Override
    public void onItemClick(@NonNull UIViewHolder holder, @NonNull View target, int position) {
        if (holder.getItemExpanded()) {
            this.getUIPageController().collapseGroup(position);
        } else {
            this.getUIPageController().expandGroup(position);
        }
    }

    @Override
    public void onChildItemClick(@NonNull UIViewHolder holder, @NonNull View target, int groupPosition, int childPosition) {
        UIToast.asyncToast("Item " + holder.getLayoutPosition() + " => " + groupPosition + " " + childPosition);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        this.getUIPageController().postDelayed(() -> {
            this.getUIPageController().putAll(UITestUtils.obtain(page, 26));
        }, 2000);
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        this.getUIActionBarController()
                .setTitleText("List Fragment");

        this.getUIPageController()
                .setRefreshLocate(SliverContainer.SCROLL_LOCATE_HEAD)
                .setGroupExpanded(true)
                .addHeadComponent(R.layout.item_head_layout)
                .addHeadComponent(R.layout.item_head_2_layout)
                .addTailComponent(R.layout.item_tail_layout)
                .addTailComponent(R.layout.item_tail_2_layout)
                .setItemAnimator(new DefaultItemAnimator())
//                .setLayoutManager(new UIStaggeredGridLayoutManager()
//                        .addFullSpanFlags(PositionType.TYPE_GROUP))
                .setLayoutManager(new GridLayoutManager(this.requireContext())
                        .addFullSpanFlags(PositionType.TYPE_GROUP))
        ;

        final LettersRecyclerAdapter lettersRecyclerAdapter = new LettersRecyclerAdapter();
        lettersRecyclerAdapter.attachedToTarget(this.getUIPageController().getExpandableRecyclerView());

        final LettersRecyclerView lettersRecyclerView = this.requireViewById(R.id.lettersRecyclerView);
        lettersRecyclerView.setAdapter(lettersRecyclerAdapter);

        final LettersEnlargeView lettersEnlargeView = this.requireViewById(R.id.lettersEnlargeView);
        lettersEnlargeView.attachedToLetters(lettersRecyclerView);
    }

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_list;
    }
}
