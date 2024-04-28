package com.framework.demo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.compat.UILog;
import com.framework.core.compat.UIRes;
import com.framework.core.compat.UIToast;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIViewModelProviders;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.util.UITestUtils;
import com.framework.demo.helper.UIListToppingHelper;
import com.framework.widget.expand.compat.GridLayoutManager;
import com.framework.widget.sliver.SliverContainer;

/**
 * @Author create by Zhengzelong on 2023-06-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ChildListFragment extends UIListFragment<String> {
    private static final int TYPE_BANNER = 1;

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);

        final Context c = this.requireContext();
        final GridLayoutManager lm;
        lm = new GridLayoutManager(c);
        lm.setOrientation(GridLayoutManager.VERTICAL);

        final UIListController<String> lc = this.getUIPageController();
        lc.setLayoutManager(lm);
        lc.setBounceLocate(SliverContainer.SCROLL_LOCATE_TAIL);
        lc.setRefreshLocate(SliverContainer.SCROLL_LOCATE_TAIL);
        lc.addTailComponent(R.layout.item_tail_layout);
        lc.addTailComponent(R.layout.item_tail_2_layout);

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

        UIViewModelProviders
                .of(this)
                .get(TestViewModel.class)
                .testMapObserve(it -> {
                    UIToast.asyncToastDebug(it.toString());
                });

        UIListToppingHelper.with(this);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        UILog.e("============ " + page + " ==> Limit: " + limit);
        final UIListController<?> lc = this.getUIPageController();
        lc.postDelayed(() -> {
            lc.putAll(UITestUtils.obtain(page, limit));
        }, 2000);
    }

    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(R.layout.item_child_list_layout, parent, false);
    }

    @Override
    public void onItemClick(@NonNull UIViewHolder holder, @NonNull View target, int position) {
        UIViewModelProviders
                .of(this)
                .get(TestViewModel.class)
                .insertText(
                        String.format("Key %s", position),
                        String.format("Val %s", position));
    }
}
