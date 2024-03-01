package com.framework.core.content;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.compat.UIRes;
import com.framework.core.widget.UIDecorLayout;
import com.framework.widget.expand.ExpandableAdapter;
import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.expand.compat.LinearLayoutManager;
import com.framework.widget.sliver.DefRefreshLoadView;
import com.framework.widget.sliver.SliverContainer;
import com.framework.widget.sliver.SliverRefreshLayout;
import com.framework.core.R;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2022/7/12
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIListController<T> extends UIDecorController
        implements SliverRefreshLayout.RefreshCallback {
    public static final int LIST_MIN_LIMIT = 12;

    public interface UIComponent<T> extends UIDecorController.UIComponent {

        void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit);

        default void notifyDataSetChanged() {
            this.<UIListController<T>>getUIPageController().notifyDataSetChanged();
        }

        default void notifyDataSetLoadMore() {
            this.<UIListController<T>>getUIPageController().notifyDataSetLoadMore();
        }

        @NonNull
        default UIDataController<T> getUIDataController() {
            return this.<UIListController<T>>getUIPageController().getUIDataController();
        }

        @Nullable
        default <P extends T> P findDataBy(int position) {
            return this.getUIDataController().findBy(position);
        }

        @NonNull
        default <P extends T> P requireDataBy(int position) {
            return this.getUIDataController().requireBy(position);
        }

        default void put(@Nullable T t) {
            this.<UIListController<T>>getUIPageController().put(t);
        }

        default void putAll(@NonNull Collection<? extends T> c) {
            this.<UIListController<T>>getUIPageController().putAll(c);
        }

        default void putAllWithClear(@NonNull Collection<? extends T> c) {
            this.<UIListController<T>>getUIPageController().putAllWithClear(c);
        }
    }

    public interface Callback<VH extends ViewHolder<?>> {

        @NonNull
        VH onCreateViewHolder(@NonNull LayoutInflater inflater,
                              @NonNull ViewGroup parent, int itemViewType);

        @NonNull
        VH onCreateChildViewHolder(@NonNull LayoutInflater inflater,
                                   @NonNull ViewGroup parent, int itemViewType);

        default void onBindViewHolder(@NonNull VH holder,
                                      int position, @NonNull List<Object> payloads) {
            this.onBindViewHolder(holder, position);
        }

        void onBindViewHolder(@NonNull VH holder, int position);

        default void onBindChildViewHolder(@NonNull VH holder,
                                           int groupPosition,
                                           int childPosition, @NonNull List<Object> payloads) {
            this.onBindChildViewHolder(holder, groupPosition, childPosition);
        }

        void onBindChildViewHolder(@NonNull VH holder, int groupPosition, int childPosition);

        @Nullable
        default CharSequence getItemLetter(int position) {
            return null;
        }

        @Nullable
        default CharSequence getChildItemLetter(int groupPosition, int childPosition) {
            return null;
        }

        default long getItemId(int position) {
            return position;
        }

        default long getChildItemId(int groupPosition, int childPosition) {
            return childPosition;
        }

        default int getItemViewType(int position) {
            return 0;
        }

        default int getChildItemViewType(int groupPosition, int childPosition) {
            return 0;
        }

        default int getChildItemCount(int groupPosition) {
            return 0;
        }
    }

    private final UIDataController<T> mUIDataController;
    private final ExpandableObserver mExpandableObserver;
    private int mRefreshPageCount;
    private int mPendingPageCount;
    private Bundle mSavedState;
    private SliverRefreshLayout mSliverRefreshLayout;
    private ExpandableRecyclerView mExpandableRecyclerView;

    public UIListController(@NonNull UIComponent<? super T> uiComponent) {
        super(uiComponent);
        this.mExpandableObserver = new ExpandableObserver(this);
        this.mUIDataController = new UIDataController<>();
        this.mUIDataController.registerObserver(this.mExpandableObserver);
    }

    @Override
    protected void onInit(@NonNull Object target,
                          @Nullable Bundle savedInstanceState) {
        super.onInit(target, savedInstanceState);
        this.mSavedState = savedInstanceState;
    }

    @Nullable
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable View contentView,
                                @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.ui_decor_list_layout, container, false);
        }
        return super.onCreateView(inflater, container, contentView, savedInstanceState);
    }

    @Override
    protected void onViewCreated(@NonNull View contentView,
                                 @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);
        final SliverRefreshLayout sliverRefreshLayout;
        sliverRefreshLayout = this.getSliverRefreshLayout();
        sliverRefreshLayout.setRefreshCallback(this);
        sliverRefreshLayout.completeRefreshed();

        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        // Setup LayoutManager
        final RecyclerView.LayoutManager oldLayoutManager;
        final RecyclerView.LayoutManager newLayoutManager;
        oldLayoutManager = recyclerView.getLayoutManager();
        if (oldLayoutManager == null) {
            newLayoutManager = new LinearLayoutManager(this.requireContext());
        } else {
            newLayoutManager = oldLayoutManager;
        }
        if (oldLayoutManager != newLayoutManager) {
            recyclerView.setLayoutManager(newLayoutManager);
        }
        // Setup Adapter
        final RecyclerView.Adapter<?> oldAdapter = recyclerView.getAdapter();
        final RecyclerView.Adapter<?> newAdapter;
        if (oldAdapter == null) {
            newAdapter = new Adapter<>(this);
        } else {
            newAdapter = oldAdapter;
        }
        if (oldAdapter != newAdapter) {
            if (newAdapter instanceof Adapter) {
                recyclerView.setAdapter(newAdapter);
            } else {
                throw new IllegalStateException("The adapter is illegal.");
            }
        }
        // Setup Background
        if (recyclerView.getBackground() == null) {
            recyclerView.setBackgroundColor(UIRes.getColor(R.color.decorBackground));
        }
    }

    @Override
    protected void onRefresh(@Nullable Bundle savedInstanceState) {
        super.onRefresh(savedInstanceState);
        final int currentDecorKey = this.getCurrentDecorKey();
        if (currentDecorKey != UIDecorLayout.DECOR_LOADING) {
            this.notifyDataSetReset();
            return;
        }
        if (this.isRefreshing()) {
            return;
        }
        if (this.canRefreshScroll(SliverRefreshLayout.SCROLL_LOCATE_HEAD)) {
            this.forcedRefreshing(0);
        } else {
            this.notifyDataSetReset();
        }
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        this.mSliverRefreshLayout = null;
        this.mExpandableRecyclerView = null;
    }

    @Override
    public void onRefresh(@NonNull SliverRefreshLayout sliverRefreshLayout,
                          @SliverContainer.ScrollLocate int scrollLocate) {
        if (SliverRefreshLayout.SCROLL_LOCATE_TAIL == scrollLocate) {
            this.notifyDataSetLoadMore();
        } else {
            this.notifyDataSetReset();
        }
    }

    @NonNull
    public <V extends SliverRefreshLayout> V getSliverRefreshLayout() {
        if (this.mSliverRefreshLayout != null) {
            return (V) this.mSliverRefreshLayout;
        }
        final V sliverRefreshLayout = this.findViewById(R.id.uiDecorRefreshView);
        if (sliverRefreshLayout == null) {
            throw new NullPointerException("Xml not find Id: @id/uiDecorRefreshView");
        }
        return (V) (this.mSliverRefreshLayout = sliverRefreshLayout);
    }

    @NonNull
    public <V extends ExpandableRecyclerView> V getExpandableRecyclerView() {
        if (this.mExpandableRecyclerView != null) {
            return (V) this.mExpandableRecyclerView;
        }
        final V recyclerView = this.findViewById(R.id.uiDecorRecyclerView);
        if (recyclerView == null) {
            throw new NullPointerException("Xml not find Id: @id/uiDecorRecyclerView");
        }
        return (V) (this.mExpandableRecyclerView = recyclerView);
    }

    @NonNull
    public final UIListController<T> setPaddingTop(int padding /* px */) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setPadding(recyclerView.getPaddingLeft(),
                padding,
                recyclerView.getPaddingRight(),
                recyclerView.getPaddingBottom());
        return this;
    }

    @NonNull
    public final UIListController<T> setPaddingLeft(int padding /* px */) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setPadding(padding,
                recyclerView.getPaddingTop(),
                recyclerView.getPaddingRight(),
                recyclerView.getPaddingBottom());
        return this;
    }

    @NonNull
    public final UIListController<T> setPaddingRight(int padding /* px */) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setPadding(recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop(),
                padding,
                recyclerView.getPaddingBottom());
        return this;
    }

    @NonNull
    public final UIListController<T> setPaddingBottom(int padding /* px */) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setPadding(recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop(),
                recyclerView.getPaddingRight(),
                padding);
        return this;
    }

    @NonNull
    public final UIListController<T> setClipToPadding(boolean clipToPadding) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setClipToPadding(clipToPadding);
        return this;
    }

    @NonNull
    public final UIListController<T> setBackground(@Nullable Drawable background) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setBackground(background);
        return this;
    }

    @NonNull
    public final UIListController<T> setBackgroundColor(@ColorInt int colorInt) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setBackgroundColor(colorInt);
        return this;
    }

    @NonNull
    public final UIListController<T> setBackgroundResource(@DrawableRes int resId) {
        final RecyclerView recyclerView = this.getExpandableRecyclerView();
        recyclerView.setBackgroundResource(resId);
        return this;
    }

    @Nullable
    public final <Ad extends Adapter<?>> Ad getAdapter() {
        return (Ad) this.getExpandableRecyclerView().getAdapter();
    }

    @NonNull
    public final <Ad extends Adapter<?>> Ad requireAdapter() {
        return this.getExpandableRecyclerView().requireAdapter();
    }

    @NonNull
    public final <Ad extends Adapter<?>> UIListController<T> setAdapter(@Nullable Ad adapter) {
        this.getExpandableRecyclerView().setAdapter(adapter);
        return this;
    }

    @NonNull
    public final <Ad extends Adapter<?>> UIListController<T> swapAdapter(@Nullable Ad adapter) {
        return this.swapAdapter(adapter, false);
    }

    @NonNull
    public final <Ad extends Adapter<?>> UIListController<T> swapAdapter(@Nullable Ad adapter, boolean removeViews) {
        this.getExpandableRecyclerView().swapAdapter(adapter, removeViews);
        return this;
    }

    @NonNull
    public final UIDataController<ItemComponent<ViewHolder<T>>> getUIHeadComponentController() {
        return this.<Adapter<ViewHolder<T>>>requireAdapter().getUIHeadComponentController();
    }

    @NonNull
    public final UIDataController<ItemComponent<ViewHolder<T>>> getUITailComponentController() {
        return this.<Adapter<ViewHolder<T>>>requireAdapter().getUITailComponentController();
    }

    @NonNull
    public final UIDataController<ItemComponent<ViewHolder<T>>> getUIEmptyComponentController() {
        return this.<Adapter<ViewHolder<T>>>requireAdapter().getUIEmptyComponentController();
    }

    @NonNull
    public final UIListController<T> addHeadComponent(@LayoutRes int layoutId) {
        return this.addHeadComponent(-1, layoutId);
    }

    @NonNull
    public final UIListController<T> addHeadComponent(int index, @LayoutRes int layoutId) {
        return this.addHeadComponent(index, new LayoutItemComponent<>(layoutId));
    }

    @NonNull
    public final UIListController<T> addHeadComponent(@NonNull ItemComponent<ViewHolder<T>> component) {
        return this.addHeadComponent(-1, component);
    }

    @NonNull
    public final UIListController<T> addHeadComponent(int index, @NonNull ItemComponent<ViewHolder<T>> component) {
        this.getUIHeadComponentController().add(index, component);
        return this;
    }

    @NonNull
    public final UIListController<T> removeHeadComponentAt(int index) {
        this.getUIHeadComponentController().removeAt(index);
        return this;
    }

    @NonNull
    public final UIListController<T> removeHeadComponent(@NonNull ItemComponent<ViewHolder<T>> component) {
        this.getUIHeadComponentController().remove(component);
        return this;
    }

    @NonNull
    public final UIListController<T> addTailComponent(@LayoutRes int layoutId) {
        return this.addTailComponent(-1, layoutId);
    }

    @NonNull
    public final UIListController<T> addTailComponent(int index, @LayoutRes int layoutId) {
        return this.addTailComponent(index, new LayoutItemComponent<>(layoutId));
    }

    @NonNull
    public final UIListController<T> addTailComponent(@NonNull ItemComponent<ViewHolder<T>> component) {
        return this.addTailComponent(-1, component);
    }

    @NonNull
    public final UIListController<T> addTailComponent(int index, @NonNull ItemComponent<ViewHolder<T>> component) {
        this.getUITailComponentController().add(index, component);
        return this;
    }

    @NonNull
    public final UIListController<T> removeTailComponentAt(int index) {
        this.getUITailComponentController().removeAt(index);
        return this;
    }

    @NonNull
    public final UIListController<T> removeTailComponent(@NonNull ItemComponent<ViewHolder<T>> component) {
        this.getUITailComponentController().remove(component);
        return this;
    }

    @NonNull
    public final UIListController<T> setEmptyComponent(@LayoutRes int layoutId) {
        return this.setEmptyComponent(new LayoutItemComponent<>(layoutId));
    }

    @NonNull
    public final UIListController<T> setEmptyComponent(@Nullable ItemComponent<ViewHolder<T>> component) {
        if (component == null) {
            this.getUIEmptyComponentController().removeAll();
        } else {
            this.getUIEmptyComponentController().set(component);
        }
        return this;
    }

    @NonNull
    @SuppressLint("NotifyDataSetChanged")
    public final UIListController<T> notifyDataSetChanged() {
        this.requireAdapter().notifyDataSetChanged();
        return this;
    }

    @NonNull
    public final UIListController<T> notifyHeadItemChanged(int position) {
        this.requireAdapter().notifyHeadItemChanged(position);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyHeadItemChanged(int position, @Nullable Object payload) {
        this.requireAdapter().notifyHeadItemChanged(position, payload);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyTailItemChanged(int position) {
        this.requireAdapter().notifyTailItemChanged(position);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyTailItemChanged(int position, @Nullable Object payload) {
        this.requireAdapter().notifyTailItemChanged(position, payload);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyEmptyItemChanged(int position) {
        this.requireAdapter().notifyEmptyItemChanged(position);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyEmptyItemChanged(int position, @Nullable Object payload) {
        this.requireAdapter().notifyEmptyItemChanged(position, payload);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyGroupItemChanged(int groupPosition) {
        this.requireAdapter().notifyGroupItemChanged(groupPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyGroupItemChanged(int groupPosition, @Nullable Object payload) {
        this.requireAdapter().notifyGroupItemChanged(groupPosition, payload);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyChildItemChanged(int groupPosition, int childPosition) {
        this.requireAdapter().notifyChildItemChanged(groupPosition, childPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> notifyChildItemChanged(int groupPosition, int childPosition, @Nullable Object payload) {
        this.requireAdapter().notifyChildItemChanged(groupPosition, childPosition, payload);
        return this;
    }

    @NonNull
    public final UIListController<T> expandGroup(int groupPositionStart, int itemCount) {
        this.requireAdapter().expandGroup(groupPositionStart, itemCount);
        return this;
    }

    @NonNull
    public final UIListController<T> collapseGroup(int groupPositionStart, int itemCount) {
        this.requireAdapter().collapseGroup(groupPositionStart, itemCount);
        return this;
    }

    @NonNull
    public final UIListController<T> registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        this.requireAdapter().registerAdapterDataObserver(observer);
        return this;
    }

    @NonNull
    public final UIListController<T> unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        this.requireAdapter().unregisterAdapterDataObserver(observer);
        return this;
    }

    public boolean getGroupExpanded() {
        return this.mExpandableObserver.getGroupExpanded();
    }

    @NonNull
    public UIListController<T> setGroupExpanded(boolean groupExpanded) {
        this.mExpandableObserver.setGroupExpanded(groupExpanded);
        return this;
    }

    @NonNull
    public final UIListController<T> expandGroup(int groupPosition) {
        this.getExpandableRecyclerView().expandGroup(groupPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> expandGroup(int groupPosition, boolean animate) {
        this.getExpandableRecyclerView().expandGroup(groupPosition, animate);
        return this;
    }

    @NonNull
    public final UIListController<T> collapseGroup(int groupPosition) {
        this.getExpandableRecyclerView().collapseGroup(groupPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> collapseGroup(int groupPosition, boolean animate) {
        this.getExpandableRecyclerView().collapseGroup(groupPosition, animate);
        return this;
    }

    public final boolean isGroupExpanded(int groupPosition) {
        return this.getExpandableRecyclerView().isGroupExpanded(groupPosition);
    }

    public final int getMaxExpandItemCount() {
        return this.getExpandableRecyclerView().getMaxExpandItemCount();
    }

    @NonNull
    public final UIListController<T> setMaxExpandItemCount(int maxExpandItemCount) {
        this.getExpandableRecyclerView().setMaxExpandItemCount(maxExpandItemCount);
        return this;
    }

    @NonNull
    public final UIListController<T> scrollToPosition(int position) {
        this.getExpandableRecyclerView().scrollToPosition(position);
        return this;
    }

    @NonNull
    public final UIListController<T> smoothScrollToPosition(int position) {
        this.getExpandableRecyclerView().smoothScrollToPosition(position);
        return this;
    }

    @NonNull
    public final UIListController<T> scrollToGroupPosition(int groupPosition) {
        this.getExpandableRecyclerView().scrollToGroupPosition(groupPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> scrollToChildPosition(int groupPosition, int childPosition) {
        this.getExpandableRecyclerView().scrollToChildPosition(groupPosition, childPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> smoothScrollToGroupPosition(int groupPosition) {
        this.getExpandableRecyclerView().smoothScrollToGroupPosition(groupPosition);
        return this;
    }

    @NonNull
    public final UIListController<T> smoothScrollToChildPosition(int groupPosition, int childPosition) {
        this.getExpandableRecyclerView().smoothScrollToChildPosition(groupPosition, childPosition);
        return this;
    }

    @Nullable
    public final <P extends RecyclerView.LayoutManager> P getLayoutManager() {
        return (P) this.getExpandableRecyclerView().getLayoutManager();
    }

    @NonNull
    public final <P extends RecyclerView.LayoutManager> P requireLayoutManager() {
        return this.getExpandableRecyclerView().requireLayoutManager();
    }

    @NonNull
    public final UIListController<T> setLayoutManager(@Nullable RecyclerView.LayoutManager layoutManager) {
        this.getExpandableRecyclerView().setLayoutManager(layoutManager);
        return this;
    }

    @NonNull
    public final UIListController<T> setItemAnimator(@Nullable RecyclerView.ItemAnimator itemAnimator) {
        this.getExpandableRecyclerView().setItemAnimator(itemAnimator);
        return this;
    }

    @NonNull
    public final UIListController<T> addItemDecoration(@NonNull RecyclerView.ItemDecoration itemDecoration) {
        return this.addItemDecoration(-1, itemDecoration);
    }

    @NonNull
    public final UIListController<T> addItemDecoration(int index, @NonNull RecyclerView.ItemDecoration itemDecoration) {
        this.getExpandableRecyclerView().addItemDecoration(itemDecoration, index);
        return this;
    }

    @NonNull
    public final UIListController<T> removeItemDecoration(@NonNull RecyclerView.ItemDecoration itemDecoration) {
        this.getExpandableRecyclerView().removeItemDecoration(itemDecoration);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnScrollListener(@NonNull RecyclerView.OnScrollListener listener) {
        this.getExpandableRecyclerView().addOnScrollListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnScrollListener(@NonNull RecyclerView.OnScrollListener listener) {
        this.getExpandableRecyclerView().removeOnScrollListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnAdapterChangedListener(@NonNull ExpandableRecyclerView.OnAdapterChangedListener listener) {
        this.getExpandableRecyclerView().addOnAdapterChangedListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnAdapterChangedListener(@NonNull ExpandableRecyclerView.OnAdapterChangedListener listener) {
        this.getExpandableRecyclerView().removeOnAdapterChangedListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnGroupExpandListener(@NonNull ExpandableRecyclerView.OnGroupExpandListener listener) {
        this.getExpandableRecyclerView().addOnGroupExpandListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnGroupExpandListener(@NonNull ExpandableRecyclerView.OnGroupExpandListener listener) {
        this.getExpandableRecyclerView().removeOnGroupExpandListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnGroupCollapseListener(@NonNull ExpandableRecyclerView.OnGroupCollapseListener listener) {
        this.getExpandableRecyclerView().addOnGroupCollapseListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnGroupCollapseListener(@NonNull ExpandableRecyclerView.OnGroupCollapseListener listener) {
        this.getExpandableRecyclerView().removeOnGroupCollapseListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnHeadItemClickListener(@Nullable ExpandableRecyclerView.OnItemClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnHeadItemClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnHeadItemLongClickListener(@Nullable ExpandableRecyclerView.OnItemLongClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnHeadItemLongClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnTailItemClickListener(@Nullable ExpandableRecyclerView.OnItemClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnTailItemClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnTailItemLongClickListener(@Nullable ExpandableRecyclerView.OnItemLongClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnTailItemLongClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnEmptyItemClickListener(@Nullable ExpandableRecyclerView.OnItemClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnEmptyItemClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnEmptyItemLongClickListener(@Nullable ExpandableRecyclerView.OnItemLongClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnEmptyItemLongClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnItemClickListener(@Nullable ExpandableRecyclerView.OnItemClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnGroupItemClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnItemLongClickListener(@Nullable ExpandableRecyclerView.OnItemLongClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnGroupItemLongClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnChildItemClickListener(@Nullable ExpandableRecyclerView.OnChildItemClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnChildItemClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> setOnChildItemLongClickListener(@Nullable ExpandableRecyclerView.OnChildItemLongClickListener<? extends ViewHolder<T>> listener) {
        this.getExpandableRecyclerView().setOnChildItemLongClickListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> forcedRefreshing() {
        this.getSliverRefreshLayout().forcedRefreshing();
        return this;
    }

    @NonNull
    public final UIListController<T> forcedRefreshing(int duration) {
        this.getSliverRefreshLayout().forcedRefreshing(duration);
        return this;
    }

    @NonNull
    public final UIListController<T> forcedRefreshing(long delayMillis) {
        this.getSliverRefreshLayout().forcedRefreshing(delayMillis);
        return this;
    }

    @NonNull
    public final UIListController<T> forcedRefreshing(int duration, long delayMillis) {
        this.getSliverRefreshLayout().forcedRefreshing(duration, delayMillis);
        return this;
    }

    @NonNull
    public final UIListController<T> completeRefreshed() {
        this.getSliverRefreshLayout().completeRefreshed();
        return this;
    }

    @NonNull
    public final UIListController<T> completeRefreshed(int duration) {
        this.getSliverRefreshLayout().completeRefreshed(duration);
        return this;
    }

    @NonNull
    public final UIListController<T> completeRefreshed(long delayMillis) {
        this.getSliverRefreshLayout().completeRefreshed(delayMillis);
        return this;
    }

    @NonNull
    public final UIListController<T> completeRefreshed(int duration, long delayMillis) {
        this.getSliverRefreshLayout().completeRefreshed(duration, delayMillis);
        return this;
    }

    @NonNull
    public final UIListController<T> setRefreshOri(@SliverRefreshLayout.Orientation int orientation) {
        this.getSliverRefreshLayout().setOrientation(orientation);
        return this;
    }

    @NonNull
    public final UIListController<T> setBounceLocate(@SliverRefreshLayout.ScrollLocate int refreshLocate) {
        this.getSliverRefreshLayout().setBounceLocate(refreshLocate);
        return this;
    }

    @NonNull
    public final UIListController<T> setRefreshLocate(@SliverRefreshLayout.ScrollLocate int refreshLocate) {
        this.getSliverRefreshLayout().setRefreshLocate(refreshLocate);
        return this;
    }

    public final boolean isRefreshing() {
        return this.getSliverRefreshLayout().isRefreshing();
    }

    public final boolean canRefreshScroll(@SliverRefreshLayout.ScrollLocate int refreshLocate) {
        return this.getSliverRefreshLayout().canRefreshScroll(refreshLocate);
    }

    @Nullable
    public final <V extends View> V getHeadLoadView() {
        return this.getSliverRefreshLayout().getHeadLoadView();
    }

    @NonNull
    public final UIListController<T> setHeadLoadView(@NonNull View loadView) {
        this.getSliverRefreshLayout().setHeadLoadView(loadView);
        return this;
    }

    @Nullable
    public final <V extends View> V getTailLoadView() {
        return this.getSliverRefreshLayout().getTailLoadView();
    }

    @NonNull
    public final UIListController<T> setTailLoadView(@NonNull View loadView) {
        this.getSliverRefreshLayout().setTailLoadView(loadView);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnScrollListener(@NonNull SliverContainer.OnScrollListener listener) {
        this.getSliverRefreshLayout().addOnScrollListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnScrollListener(@NonNull SliverContainer.OnScrollListener listener) {
        this.getSliverRefreshLayout().removeOnScrollListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> addOnRefreshListener(@NonNull SliverRefreshLayout.OnRefreshListener listener) {
        this.getSliverRefreshLayout().addOnRefreshListener(listener);
        return this;
    }

    @NonNull
    public final UIListController<T> removeOnRefreshListener(@NonNull SliverRefreshLayout.OnRefreshListener listener) {
        this.getSliverRefreshLayout().removeOnRefreshListener(listener);
        return this;
    }

    // ########## UIDataController ##########

    @NonNull
    public UIDataController<T> getUIDataController() {
        return this.mUIDataController;
    }

    @NonNull
    public final UIListController<T> registerObserver(@NonNull UIDataController.Observer observer) {
        this.getUIDataController().registerObserver(observer);
        return this;
    }

    @NonNull
    public final UIListController<T> unregisterObserver(@NonNull UIDataController.Observer observer) {
        this.getUIDataController().unregisterObserver(observer);
        return this;
    }

    /**
     * 优先使用此方法添加数据
     *
     * @see UIListController#put(Object)
     * @see UIListController#putAll(Collection)
     * @see UIListController#putAllWithClear(Collection)
     */
    @NonNull
    public final UIListController<T> put(@Nullable T t) {
        return this.putAll(Collections.singletonList(t));
    }

    /**
     * 优先使用此方法添加数据
     *
     * @see UIListController#put(Object)
     * @see UIListController#putAll(Collection)
     * @see UIListController#putAllWithClear(Collection)
     */
    @NonNull
    public UIListController<T> putAll(@NonNull Collection<? extends T> c) {
        this.putAllInternal(c);
        return this;
    }

    /**
     * 优先使用此方法添加数据(删除所有旧数据)
     *
     * @see UIListController#put(Object)
     * @see UIListController#putAll(Collection)
     * @see UIListController#putAllWithClear(Collection)
     */
    @NonNull
    public final UIListController<T> putAllWithClear(@NonNull Collection<? extends T> c) {
        this.getUIDataController().removeAll();
        this.putAllInternal(c);
        return this;
    }

    final void putAllInternal(@NonNull Collection<? extends T> collection) {
        final int N = collection.size();
        final int pendingPageCount = this.mPendingPageCount;
        final boolean refreshing;
        refreshing = pendingPageCount > 0;

        boolean needsRemoveAll;
        needsRemoveAll = refreshing;
        needsRemoveAll &= this.mRefreshPageCount == 0;

        this.mRefreshPageCount += (N > 0 ? pendingPageCount : 0);
        this.mPendingPageCount = 0;

        final UIDataController<T> uiDataController;
        uiDataController = this.getUIDataController();
        if (needsRemoveAll) {
            uiDataController.removeAll();
        }
        uiDataController.addAll(collection);
        this.postContentOnAnimation();
        this.completeRefreshed(UIDecorOptions.MS_ANIM);

        if (!refreshing) {
            return;
        }
        final View child = this.getTailLoadView();
        if (child instanceof DefRefreshLoadView) {
            final boolean refreshHintEnabled;
            refreshHintEnabled = N < LIST_MIN_LIMIT;

            final DefRefreshLoadView rlv;
            rlv = (DefRefreshLoadView) child;
            rlv.setRefreshHintEnabled(refreshHintEnabled);
        }
    }

    final void notifyDataSetReset() {
        final int page = 1;
        this.mRefreshPageCount = 0;
        this.mPendingPageCount = 0;
        this.performListRefresh(page);
    }

    /**
     * 刷新列表 OR 加载更多
     *
     * @see UIListController#notifyDataSetRefresh() 通知执行刷新操作
     * @see UIListController#notifyDataSetLoadMore() 通知执行加载操作
     * </p>
     * 填充数据
     * {@link UIListController#put(Object)}
     * {@link UIListController#putAll(Collection)}
     * 执行回调
     * {@link UIComponent#onUIRefresh(Bundle, int, int)}
     */
    public final void notifyDataSetLoadMore() {
        int page = 1;
        page += this.mRefreshPageCount;
        page += this.mPendingPageCount;
        this.performListRefresh(page);
    }

    /*
     * 1.Need to avoid multiple
     * calls to refresh and load more operations.
     * 2.The refresh restriction
     * must be lifted through the putAllInternal() method.
     */
    final void performListRefresh(int page) {
        final Bundle savedInstanceState;
        synchronized (UIListController.class) {
            if (this.mPendingPageCount >= 1) {
                // In refresh.
                return;
            }
            savedInstanceState = this.mSavedState;
            this.mSavedState = null;
            this.mPendingPageCount++;
        }
        final UIComponent<?> uiComponent;
        uiComponent = this.getUIComponent();
        uiComponent.onUIRefresh(savedInstanceState, page, LIST_MIN_LIMIT);
    }

    private final static class ExpandableObserver extends UIExpandableObserver {
        @NonNull
        private final UIListController<?> mUIListController;

        public ExpandableObserver(@NonNull UIListController<?> uiListController) {
            this.mUIListController = uiListController;
        }

        @NonNull
        @Override
        public ExpandableAdapter<?> getExpandableAdapter() {
            return this.mUIListController.requireAdapter();
        }
    }

    public static class ViewHolder<T> extends ExpandableRecyclerView.ViewHolder
            implements UIPageControllerOwner {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Nullable
        public final <P extends T> P findDataBy(int position) {
            final UIDataController<T> uiDataController = this.getUIDataController();
            if (uiDataController == null) {
                return null;
            }
            return uiDataController.findBy(position);
        }

        @NonNull
        public final <P extends T> P requireDataBy(int position) {
            final UIDataController<T> uiDataController = this.getUIDataController();
            if (uiDataController == null) {
                throw new NullPointerException("ERROR");
            }
            return uiDataController.requireBy(position);
        }

        @Nullable
        @Override
        public final UIListController<T> getUIPageController() {
            final Adapter<ViewHolder<T>> adapter = this.getAdapter();
            if (adapter == null) {
                return null;
            }
            return adapter.getUIPageController();
        }

        @NonNull
        public final UIListController<T> requireUIPageController() {
            final UIListController<T> uiListController = this.getUIPageController();
            if (uiListController == null) {
                throw new NullPointerException("ERROR");
            }
            return uiListController;
        }

        @Nullable
        public final UIDataController<T> getUIDataController() {
            final UIListController<T> uiListController = this.getUIPageController();
            if (uiListController == null) {
                return null;
            }
            return uiListController.getUIDataController();
        }

        @NonNull
        public final UIDataController<T> requireUIDataController() {
            final UIDataController<T> uiDataController = this.getUIDataController();
            if (uiDataController == null) {
                throw new NullPointerException("ERROR");
            }
            return uiDataController;
        }
    }

    public static class Adapter<VH extends ViewHolder<?>> extends ExpandableRecyclerView.Adapter<VH>
            implements UIDataController.Adapter {
        @NonNull
        private final UIListController<?> mUIListController;
        @NonNull
        private final UIDataController<ItemComponent<VH>> mUIHeadDataController;
        @NonNull
        private final UIDataController<ItemComponent<VH>> mUITailDataController;
        @NonNull
        private final UIDataController<ItemComponent<VH>> mUIEmptyDataController;

        public <T> Adapter(@NonNull UIPageControllerOwner owner) {
            this(owner.<UIListController<T>>getUIPageController());
        }

        public <T> Adapter(@NonNull UIListController<T> uiListController) {
            this.mUIListController = uiListController;
            this.mUIHeadDataController = new UIDataController<>(this);
            this.mUITailDataController = new UIDataController<>(this);
            this.mUIEmptyDataController = new UIDataController<>(this);
        }

        // Head.

        @Nullable
        @Override
        public VH onCreateHeadViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            final LayoutInflater inflater = this.requireLayoutInflater();
            return this.mUIHeadDataController.requireBy(itemViewType).onCreateViewHolder(inflater, parent, itemViewType);
        }

        @Override
        public void onBindHeadViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            super.onBindHeadViewHolder(holder, position, payloads);
            this.mUIHeadDataController.requireBy(position).onBindViewHolder(holder, position, payloads);
        }

        @Nullable
        @Override
        public CharSequence getHeadItemLetter(int position) {
            return this.mUIHeadDataController.requireBy(position).getItemLetter(position);
        }

        @Override
        public long getHeadItemId(int position) {
            return this.mUIHeadDataController.requireBy(position).getItemId(position);
        }

        @Override
        public int getHeadItemViewType(int position) {
            return this.mUIHeadDataController.requireBy(position).getItemViewType(position);
        }

        @Override
        public int getHeadItemCount() {
            return this.mUIHeadDataController.size();
        }

        // Tail.

        @Nullable
        @Override
        public VH onCreateTailViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            final LayoutInflater inflater = this.requireLayoutInflater();
            return this.mUITailDataController.requireBy(itemViewType).onCreateViewHolder(inflater, parent, itemViewType);
        }

        @Override
        public void onBindTailViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            super.onBindTailViewHolder(holder, position, payloads);
            this.mUITailDataController.requireBy(position).onBindViewHolder(holder, position, payloads);
        }

        @Nullable
        @Override
        public CharSequence getTailItemLetter(int position) {
            return this.mUITailDataController.requireBy(position).getItemLetter(position);
        }

        @Override
        public long getTailItemId(int position) {
            return this.mUITailDataController.requireBy(position).getItemId(position);
        }

        @Override
        public int getTailItemViewType(int position) {
            return this.mUITailDataController.requireBy(position).getItemViewType(position);
        }

        @Override
        public int getTailItemCount() {
            return this.mUITailDataController.size();
        }

        // Empty.

        @Nullable
        @Override
        public VH onCreateEmptyViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            final LayoutInflater inflater = this.requireLayoutInflater();
            return this.mUIEmptyDataController.requireBy(itemViewType).onCreateViewHolder(inflater, parent, itemViewType);
        }

        @Override
        public void onBindEmptyViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            super.onBindEmptyViewHolder(holder, position, payloads);
            this.mUIEmptyDataController.requireBy(position).onBindViewHolder(holder, position, payloads);
        }

        @Nullable
        @Override
        public CharSequence getEmptyItemLetter(int position) {
            return this.mUIEmptyDataController.requireBy(position).getItemLetter(position);
        }

        @Override
        public long getEmptyItemId(int position) {
            return this.mUIEmptyDataController.requireBy(position).getItemId(position);
        }

        @Override
        public int getEmptyItemViewType(int position) {
            return this.mUIEmptyDataController.requireBy(position).getItemViewType(position);
        }

        @Override
        public int getEmptyItemCount() {
            return this.mUIEmptyDataController.size();
        }

        // Group.

        @Nullable
        @Override
        public VH onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            final LayoutInflater inflater = this.requireLayoutInflater();
            return this.getCallback().onCreateViewHolder(inflater, parent, itemViewType);
        }

        @Override
        public void onBindGroupViewHolder(@NonNull VH holder, int groupPosition, @NonNull List<Object> payloads) {
            super.onBindGroupViewHolder(holder, groupPosition, payloads);
            this.getCallback().onBindViewHolder(holder, groupPosition, payloads);
        }

        @Override
        public void onBindGroupViewHolder(@NonNull VH holder, int groupPosition) {
            // nothing
        }

        @Nullable
        @Override
        public CharSequence getGroupItemLetter(int groupPosition) {
            return this.getCallback().getItemLetter(groupPosition);
        }

        @Override
        public long getGroupItemId(int groupPosition) {
            return this.getCallback().getItemId(groupPosition);
        }

        @Override
        public int getGroupItemViewType(int groupPosition) {
            return this.getCallback().getItemViewType(groupPosition);
        }

        @Override
        public int getGroupItemCount() {
            return this.getUIDataController().size();
        }

        // Child.

        @Nullable
        @Override
        public VH onCreateChildViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            final LayoutInflater inflater = this.requireLayoutInflater();
            return this.getCallback().onCreateChildViewHolder(inflater, parent, itemViewType);
        }

        @Override
        public void onBindChildViewHolder(@NonNull VH holder, int groupPosition, int childPosition, @NonNull List<Object> payloads) {
            super.onBindChildViewHolder(holder, groupPosition, childPosition, payloads);
            this.getCallback().onBindChildViewHolder(holder, groupPosition, childPosition, payloads);
        }

        @Nullable
        @Override
        public CharSequence getChildItemLetter(int groupPosition, int childPosition) {
            return this.getCallback().getChildItemLetter(groupPosition, childPosition);
        }

        @Override
        public long getChildItemId(int groupPosition, int childPosition) {
            return this.getCallback().getChildItemId(groupPosition, childPosition);
        }

        @Override
        public int getChildItemViewType(int groupPosition, int childPosition) {
            return this.getCallback().getChildItemViewType(groupPosition, childPosition);
        }

        @Override
        public int getChildItemCount(int groupPosition) {
            return this.getCallback().getChildItemCount(groupPosition);
        }

        @NonNull
        public final Callback<VH> getCallback() {
            return this.mUIListController.getUIComponent();
        }

        @NonNull
        public final <T> UIDataController<T> getUIDataController() {
            return this.<T>getUIPageController().getUIDataController();
        }

        @NonNull
        public final <T> UIListController<T> getUIPageController() {
            return (UIListController<T>) this.mUIListController;
        }

        @NonNull
        public UIDataController<ItemComponent<VH>> getUIHeadComponentController() {
            return this.mUIHeadDataController;
        }

        @NonNull
        public UIDataController<ItemComponent<VH>> getUITailComponentController() {
            return this.mUITailDataController;
        }

        @NonNull
        public UIDataController<ItemComponent<VH>> getUIEmptyComponentController() {
            return this.mUIEmptyDataController;
        }
    }

    public static abstract class LazyAdapter<T> extends Adapter<ViewHolder<T>> {

        public LazyAdapter(@NonNull UIPageControllerOwner owner) {
            this(owner.<UIListController<T>>getUIPageController());
        }

        public LazyAdapter(@NonNull UIListController<T> uiListController) {
            super(uiListController);
        }

        @Nullable
        public final <P extends T> P findDataBy(int position) {
            return this.getUIDataController().findBy(position);
        }

        @NonNull
        public final <P extends T> P requireDataBy(int position) {
            return this.getUIDataController().requireBy(position);
        }
    }

    public interface ItemComponent<VH extends ViewHolder<?>> {

        @NonNull
        VH onCreateViewHolder(@NonNull LayoutInflater inflater,
                              @NonNull ViewGroup parent, int itemViewType);

        default void onBindViewHolder(@NonNull VH holder, int position,
                                      @NonNull List<Object> payloads) {
            this.onBindViewHolder(holder, position);
        }

        void onBindViewHolder(@NonNull VH holder, int position);

        @Nullable
        default CharSequence getItemLetter(int position) {
            return null;
        }

        default long getItemId(int position) {
            // Don't change it.
            return position;
        }

        default int getItemViewType(int position) {
            // Don't change it.
            return position;
        }
    }

    public static abstract class SimpleItemComponent<VH extends ViewHolder<?>>
            implements ItemComponent<VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull LayoutInflater inflater,
                                     @NonNull ViewGroup parent, int itemViewType) {
            throw new NullPointerException("ERROR");
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
        }
    }

    public static class LayoutItemComponent<T> extends SimpleItemComponent<ViewHolder<T>> {
        @LayoutRes
        protected final int mLayoutId;

        public LayoutItemComponent(@LayoutRes int layoutId) {
            this.mLayoutId = layoutId;
        }

        @NonNull
        @Override
        public ViewHolder<T> onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                @NonNull ViewGroup parent, int itemViewType) {
            return new ViewHolder<>(inflater.inflate(this.mLayoutId, parent, false));
        }
    }
}
