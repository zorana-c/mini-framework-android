package com.person.ui.works;

import android.graphics.Color;
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
import com.framework.core.util.UITestUtils;
import com.framework.widget.expand.DefaultItemDecoration;
import com.framework.widget.expand.compat.GridLayoutManager;
import com.framework.widget.sliver.SliverContainer;
import com.person.R;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class WorksFragment extends UIListFragment<String> {
    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        this.getUIPageController().postDelayed(() -> {
            this.getUIPageController().putAll(UITestUtils.obtain(page, page > 2 ? 0 : limit * 3));
        }, 1500);
    }

    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int itemViewType) {
        return inflater.inflate(R.layout.item_video_cover_layout, parent, false);
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setEnabled(false);

        final RecyclerView.LayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this.requireContext(), 3);

        final RecyclerView.ItemDecoration itemDecoration;
        itemDecoration = new DefaultItemDecoration()
                .setSpanGapSize(UIRes.dip2px(this.requireContext(), 2))
                .setSpanGapSizeOther(UIRes.dip2px(this.requireContext(), 2));

        final UIListController<?> uiListController;
        uiListController = this.getUIPageController();
        uiListController.setBounceLocate(SliverContainer.SCROLL_LOCATE_TAIL);
        uiListController.setRefreshLocate(SliverContainer.SCROLL_LOCATE_TAIL);
        uiListController.setLayoutManager(layoutManager);
        uiListController.addItemDecoration(itemDecoration);
        uiListController.setLoadingView(R.layout.layout_loading_item);

        View view;
        view = this.getUIPageController().requireContentView();
        view.setBackgroundColor(Color.TRANSPARENT);

        view = this.getUIPageController().getExpandableRecyclerView();
        view.setBackgroundColor(Color.TRANSPARENT);
    }
}
