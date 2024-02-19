package com.framework.demo.helper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.demo.R;
import com.framework.widget.sliver.SliverRefreshLayout;

/**
 * @Author create by Zhengzelong on 2023-06-21
 * @Email : 171905184@qq.com
 * @Description :
 * <p>
 * 列表置顶滑动辅助类
 */
public class UIListToppingHelper {

    @NonNull
    public static <T> UIListToppingHelper with(@NonNull UIPageControllerOwner owner) {
        return new UIListToppingHelper(owner.getUIPageController());
    }

    public static <T> void notifyDataSetChanged(@NonNull UIPageControllerOwner owner) {
        notifyDataSetChanged(owner.<UIListController<T>>getUIPageController());
    }

    @Nullable
    private View.OnClickListener toppingClickListener;

    /* package */ UIListToppingHelper(@NonNull UIListController<?> uiPageController) {
        this.attachedToController(uiPageController);
    }

    protected void attachedToController(@NonNull UIListController<?> uiPageController) {
        final UIListController<?> pc = uiPageController;
        final SliverRefreshLayout rl = pc.getSliverRefreshLayout();

        View topping = pc.findViewById(R.id.topping);
        if (topping == null) {
            final SliverRefreshLayout.LayoutParams lp;
            lp = rl.generateDefaultLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.END | Gravity.BOTTOM;
            lp.scrolling = false;

            final LayoutInflater li = LayoutInflater.from(rl.getContext());
            topping = li.inflate(R.layout.layout_topping_view, rl, false);
            topping.setLayoutParams(lp);

            rl.addView(topping);
            rl.bringChildToFront(topping);
        }
        topping.setOnClickListener(it -> {
            if (this.toppingClickListener != null) {
                this.toppingClickListener.onClick(it);
            } else {
                pc.smoothScrollToPosition(0);
            }
        });
        // 添加滑动动画效果
        pc.addOnScrollListener(new ComponentListener(topping));
    }

    @NonNull
    public UIListToppingHelper setOnClickListener(@Nullable View.OnClickListener listener) {
        this.toppingClickListener = listener;
        return this;
    }

    private static final class ComponentListener extends RecyclerView.OnScrollListener {
        @NonNull
        private final View topping;

        public ComponentListener(@NonNull View topping) {
            this.topping = topping;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            final View t = this.topping;
            final int o = recyclerView.computeVerticalScrollOffset();
            final int e = recyclerView.computeVerticalScrollExtent();
            final Boolean tag = (Boolean) t.getTag();

            if (Math.abs(o) >= e && Math.abs(e) > 0) {
                if (tag != null && !tag) {
                    t.setTag(true);
                    t.animate()
                            .translationX(0.f)
                            .setDuration(420L)
                            .start();
                }
            } else {
                if (tag == null || tag) {
                    t.setTag(false);
                    t.animate()
                            .translationX(getTotalSize(t))
                            .setDuration(420L)
                            .start();
                }
            }
        }

        private static int getTotalSize(@NonNull View view) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                return view.getMeasuredWidth() + mlp.rightMargin;
            }
            return view.getMeasuredWidth();
        }
    }

    private static <T> void notifyDataSetChanged(@NonNull UIListController<T> uiPageController) {
        final RecyclerView rv = uiPageController.getExpandableRecyclerView();
        final RecyclerView.Adapter<?> ad = rv.getAdapter();
        if (ad == null) {
            return;
        }
        ad.notifyItemRangeChanged(0, ad.getItemCount());
    }
}
