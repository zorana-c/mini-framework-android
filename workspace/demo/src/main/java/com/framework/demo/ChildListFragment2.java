package com.framework.demo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.compat.UIRes;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIListController;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.ui.adapter.UIExpandableAdapter;
import com.framework.core.util.UITestUtils;
import com.framework.widget.sliver.SliverContainer;

/**
 * @Author create by Zhengzelong on 2023-06-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ChildListFragment2 extends UIListFragment<String> {

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);

        final Context c = this.requireContext();
        final UIListController<?> lc = this.getUIPageController();
        lc.setBounceLocate(SliverContainer.SCROLL_LOCATE_TAIL);
        lc.setRefreshLocate(SliverContainer.SCROLL_LOCATE_TAIL);

        final RecyclerView rv = lc.getExpandableRecyclerView();
        rv.setClipToPadding(false);
        rv.setPadding(
                UIRes.dip2px(c, 10),
                UIRes.dip2px(c, 5),
                UIRes.dip2px(c, 10),
                UIRes.dip2px(c, 5));

        final UIActionBarController abc = lc.getUIActionBarController();
        abc.setEnabled(false);

        final SliverContainer sc;
        sc = this.requireViewById(R.id.uiDecorLoadingContainer);
        sc.setBounceLocate(SliverContainer.SCROLL_LOCATE_TAIL);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        final UIListController<?> lc = this.getUIPageController();
        lc.postDelayed(() -> {
            lc.putAll(UITestUtils.obtain(page, limit));
        }, 2000);
    }

    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(R.layout.item_child_list_2_layout, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder holder, int position) {
        final RecyclerView rv;
        rv = holder.requireViewById(R.id.list1);

        HorListAdapter ad = (HorListAdapter) rv.getAdapter();
        if (ad == null) {
            ad = new HorListAdapter();

            rv.setHasFixedSize(true);
            rv.setAdapter(ad);
        }
        ad.getUIListDataController().setAll(UITestUtils.obtain(1, 10));
    }

    private static final class HorListAdapter extends UIExpandableAdapter<String> {
        @NonNull
        @Override
        public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
            return this.inflate(R.layout.item_child_list_hor_layout);
        }
    }
}
