package com.framework.demo.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIDataController;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.demo.R;
import com.framework.widget.sliver.SliverContainer;
import com.framework.widget.sliver.SliverRefreshLayout;

/**
 * @Author create by Zhengzelong on 2023-08-16
 * @Email : 171905184@qq.com
 * @Description :
 * <p>
 * 列表预加载更多辅助类
 */
public class UIListPreLoadingHelper {

    @NonNull
    public static <T> UIListPreLoadingHelper with(@NonNull UIPageControllerOwner owner) {
        return new UIListPreLoadingHelper(owner.getUIPageController());
    }

    /* package */ UIListPreLoadingHelper(@NonNull UIListController<?> uiPageController) {
        this.attachedToController(uiPageController);
    }

    @Nullable
    private ComponentContainer<?> componentContainer;

    protected <T> void attachedToController(@NonNull UIListController<T> uiPageController) {
        final UIListController<T> pc = uiPageController;
        final SliverRefreshLayout srl = pc.getSliverRefreshLayout();

        int rl = srl.getRefreshLocate();
        if (rl == SliverContainer.SCROLL_LOCATE_ALL) {
            rl = SliverContainer.SCROLL_LOCATE_HEAD;
        }
        if (rl == SliverContainer.SCROLL_LOCATE_TAIL) {
            rl = SliverContainer.SCROLL_LOCATE_NONE;
        }
        final ComponentContainer<T> cc;
        cc = new ComponentContainer<>(pc);

        // 添加滑动监听事件
        pc.setRefreshLocate(rl);
        pc.registerObserver(cc);
        pc.addTailComponent(cc);
        pc.addOnScrollListener(cc);
        this.componentContainer = cc;
    }

    @NonNull
    public UIListPreLoadingHelper setTargetPosition(int targetPosition) {
        if (this.componentContainer != null) {
            this.componentContainer.setTargetPosition(targetPosition);
        }
        return this;
    }

    private static final class ComponentContainer<T> extends RecyclerView.OnScrollListener implements
            UIListController.ItemComponent<UIListController.ViewHolder<T>>,
            UIDataController.Observer {
        @NonNull
        private final UIListController<T> uiListController;
        @Nullable
        private OrientationHelper horOrientationHelper;
        @Nullable
        private OrientationHelper verOrientationHelper;
        private int targetPosition;
        private boolean scrolled;
        private boolean moreData;

        public ComponentContainer(@NonNull UIListController<T> uiListController) {
            this.uiListController = uiListController;
        }

        // OnScrollListener

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (dx != 0 || dy != 0) {
                this.scrolled = true;
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
            if (this.scrolled && RecyclerView.SCROLL_STATE_IDLE == scrollState) {
                this.scrolled = false;
                this.snapActionToExistingPosition(recyclerView);
            }
        }

        public void setTargetPosition(int targetPosition) {
            this.targetPosition = targetPosition;
        }

        private void snapActionToExistingPosition(@NonNull RecyclerView recyclerView) {
            final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm == null) {
                return;
            }
            final int itemCount = lm.getItemCount();
            final int targetPos = itemCount - 1 - this.targetPosition;
            if (targetPos < 0 || targetPos >= itemCount) {
                return;
            }
            final int N = lm.getChildCount();
            if (N == 0) {
                return;
            }
            final OrientationHelper oh = this.getHelper(lm);
            if (oh == null) {
                return;
            }
            int preLoadingPos = RecyclerView.NO_POSITION;
            for (int index = N - 1; index >= 0; index--) {
                final View child = lm.getChildAt(index);
                if (child == null) {
                    continue;
                }
                final int position = lm.getPosition(child);
                if (position < targetPos) {
                    break;
                }
                final int ds = oh.getDecoratedStart(child);
                if (ds >= oh.getStartAfterPadding()
                        && ds < oh.getEndAfterPadding()) {
                    preLoadingPos = position;
                    break;
                }
            }
            if (preLoadingPos != RecyclerView.NO_POSITION) {
                if (this.moreData) {
                    this.uiListController.notifyDataSetLoadMore();
                }
            }
        }

        @Nullable
        private OrientationHelper getHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
            if (layoutManager.canScrollHorizontally()) {
                return this.getHorHelper(layoutManager);
            }
            if (layoutManager.canScrollVertically()) {
                return this.getVerHelper(layoutManager);
            }
            return null;
        }

        @NonNull
        private OrientationHelper getHorHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
            if (this.horOrientationHelper == null
                    || this.horOrientationHelper.getLayoutManager() != layoutManager) {
                this.horOrientationHelper = OrientationHelper.createHorizontalHelper(layoutManager);
            }
            return this.horOrientationHelper;
        }

        @NonNull
        private OrientationHelper getVerHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
            if (this.verOrientationHelper == null
                    || this.verOrientationHelper.getLayoutManager() != layoutManager) {
                this.verOrientationHelper = OrientationHelper.createVerticalHelper(layoutManager);
            }
            return this.verOrientationHelper;
        }

        // ItemComponent

        @NonNull
        @Override
        public UIListController.ViewHolder<T> onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                                 @NonNull ViewGroup parent, int itemViewType) {
            final View itemView;
            itemView = inflater.inflate(R.layout.layout_preload_view, parent, false);
            return new UIListController.ViewHolder<>(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UIListController.ViewHolder<T> holder, int position) {
            final TextView textView;
            textView = holder.requireViewById(R.id.text1);
            textView.setText(this.moreData ? "正在加载" : "没有更多了");
        }

        // DataObserver

        @Override
        public boolean onRangeInserted(int positionStart, int itemCount) {
            final boolean moreData = itemCount >= UIListController.LIST_MIN_LIMIT;
            if (this.moreData != moreData) {
                this.moreData = moreData;
                final UIListController.Adapter<?> adapter = this.uiListController.getAdapter();
                if (adapter != null) {
                    adapter.notifyTailItemRangeChanged(0, adapter.getTailItemCount());
                }
            }
            return false;
        }
    }
}
