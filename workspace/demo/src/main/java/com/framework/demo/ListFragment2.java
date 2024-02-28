package com.framework.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.compat.UIToast;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDataController;
import com.framework.core.content.UIListController;
import com.framework.core.bean.UIModelImpl;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.widget.expand.DefaultItemAnimator;
import com.framework.widget.expand.ExpandableAdapter;
import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.expand.PositionType;
import com.framework.widget.expand.compat.GridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2023-08-18
 * @Email : 171905184@qq.com
 * @Description : ExpandableRecyclerView 展开/收起 用法
 */
public class ListFragment2 extends UIListFragment<ListFragment2.Entry> {
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        final UIActionBarController abc = this.getUIActionBarController();
        abc.setMenuClickListener(it -> {
            final ExpandableAdapter<?> adapter = this.getUIPageController().requireAdapter();
            if (adapter.isGroupExpanded(0)) {
                adapter.collapseGroup(0, adapter.getGroupItemCount());
            } else {
                adapter.expandGroup(0, adapter.getGroupItemCount());
            }
        });
        abc.setMenuText("All Switch");
        abc.setTitleText("List Fragment2");

        final UIListController.Adapter<UIListController.ViewHolder<Entry>> adapter;
        adapter = new UIListController.LazyAdapter<Entry>(this) {
        };
        // 1.移除/删除数据(具有动画, 前提: 每一个Item赋予Id)
        adapter.setHasStableIds(true);

        final Context c = this.requireContext();
        final UIListController<Entry> lc = this.getUIPageController();
        lc
                .setAdapter(adapter)
                .addHeadComponent(R.layout.item_head_layout)
                .addTailComponent(R.layout.item_tail_layout)
                .setEmptyComponent(R.layout.ui_decor_list_empty_layout)
                // 2.移除/删除数据(具有动画, 默认为: 展开状态)
                .setGroupExpanded(true)
                .setItemAnimator(new DefaultItemAnimator())
                .setLayoutManager(new GridLayoutManager(c)
                        .addFullSpanFlags(PositionType.TYPE_GROUP))
                .setOnHeadItemClickListener(this::onHeadItemClick)
                .setOnTailItemClickListener(this::onTailItemClick)
        ;
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        this.getUIPageController().postDelayed(() -> {
            this.getUIPageController().putAll(Entry.queryBy(page, 1));
        }, 2000);
    }

    @NonNull
    @Override
    public UIViewHolder<Entry> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return new CustomViewHolder(this.onCreateItemView(inflater, parent, itemViewType));
    }

    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder<Entry> holder, int position) {
        final Entry group = holder.requireData();

        final CustomViewHolder cvh = (CustomViewHolder) holder;
        final TextView text1 = cvh.text1;
        text1.setText(String.format("Group(%s) %s - %s - %s", group.nanoId(), position, holder.getLayoutPosition(), holder.getItemId()));
        text1.setTextColor(Color.GRAY);

        final View itemView = holder.itemView;
        itemView.setBackgroundResource(R.color.decorBackground);
    }

    @NonNull
    @Override
    public View onCreateChildItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
    }

    @Override
    public void onBindChildViewHolder(@NonNull UIViewHolder<Entry> holder, int groupPosition, int childPosition) {
        final Entry group = holder.requireData();
        final Entry child = group.findChildBy(childPosition);

        final TextView text1 = holder.requireViewById(android.R.id.text1);
        text1.setText(String.format("Child %s - %s - %s", groupPosition, childPosition, holder.getLayoutPosition()));
        text1.setTextColor(Color.GRAY);
        final TextView text2 = holder.requireViewById(android.R.id.text2);
        text2.setText(child.text);
        text2.setTextColor(Color.GRAY);
    }

    @Override
    public int getChildItemCount(int groupPosition) {
        return this.requireDataBy(groupPosition).getChildItemCount();
    }

    @Override
    public void onItemClick(@NonNull UIViewHolder<Entry> holder, @NonNull View target, int position) {
        final ExpandableRecyclerView.Adapter<UIViewHolder<Entry>> adapter = holder.requireAdapter();
        if (holder.getItemExpanded()) {
            adapter.collapseGroup(position);
        } else {
            adapter.expandGroup(position);
        }

        // final UIDataController<Entry> dc = holder.getUIDataController();
        // Move
        // dc.moveToHead(position);
    }

    @Override
    public void onChildItemClick(@NonNull UIViewHolder<Entry> holder, @NonNull View target, int groupPosition, int childPosition) {
        final Entry group = holder.requireData();
        final Entry child = group.findChildBy(childPosition);

        final UIDataController<Entry> dc = holder.getUIDataController();
        // Remove
        dc.removeAt(groupPosition);
    }

    public void onHeadItemClick(@NonNull UIListController.ViewHolder<Entry> holder, @NonNull View target, int position) {
        final UIDataController<Entry> dc = holder.getUIDataController();
        // Add
        dc.addAll(Entry.queryBy(1, 1));
    }

    public void onTailItemClick(@NonNull UIListController.ViewHolder<Entry> holder, @NonNull View target, int position) {
        UIToast.asyncToast(String.format("TAIL - %s", holder.getLayoutPosition()));
    }

    @Override
    public long getItemId(int position) {
//        final Entry group = this.requireDataBy(position);
//        return group.nanoId();
        return position;
    }

    @Override
    public long getChildItemId(int groupPosition, int childPosition) {
//        final Entry group = this.requireDataBy(groupPosition);
//        final Entry child = group.findChildBy(childPosition);
//        return child.nanoId();
        return childPosition;
    }

    public static class CustomViewHolder extends UIViewHolder<Entry> {
        public final TextView text1;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text1 = this.findViewById(android.R.id.text1);
        }
    }

    protected static class Entry extends UIModelImpl {
        @NonNull
        private final String text;
        @NonNull
        private final List<Entry> next;

        public Entry(@NonNull String text) {
            this.text = text;
            this.next = Collections.emptyList();
        }

        public Entry(@NonNull String text, @NonNull List<Entry> next) {
            this.text = text;
            this.next = next;
        }

        public int getChildItemCount() {
            return this.next.size();
        }

        @NonNull
        public Entry findChildBy(int position) {
            return this.next.get(position);
        }

        @NonNull
        private static List<Entry> queryBy(int page, int limit) {
            final List<Entry> list = new ArrayList<>();
            for (int i = ((page - 1) * limit); i < (page * limit); i++) {
                list.add(new Entry("This is group text.", nextList(1, 4)));
            }
            return list;
        }

        @NonNull
        private static List<Entry> nextList(int page, int limit) {
            final List<Entry> list = new ArrayList<>();
            for (int i = ((page - 1) * limit); i < (page * limit); i++) {
                list.add(new Entry("This is child text."));
            }
            return list;
        }
    }
}
