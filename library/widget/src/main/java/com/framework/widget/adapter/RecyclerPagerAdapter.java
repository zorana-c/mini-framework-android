package com.framework.widget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.framework.widget.cache.RecycledPool;

import java.util.Objects;

/**
 * @Author create by Zhengzelong on 2021/7/14
 * @Email : 171905184@qq.com
 * @Description :
 * <p>
 * {@link androidx.viewpager.widget.ViewPager}
 */
public abstract class RecyclerPagerAdapter<VH extends RecyclerPagerAdapter.ViewHolder> extends PagerAdapter {
    public static final int NO_POSITION = -1;
    public static final int INVALID_TYPE = -1;

    private RecycledPool<VH> mRecycledPool;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener<VH> mOnItemClickListener;
    private OnItemLongClickListener<VH> mOnItemLongClickListener;

    /**
     * @deprecated
     */
    @Override
    public final int getCount() {
        return this.getItemCount();
    }

    @CallSuper
    @Override
    public void startUpdate(@NonNull ViewGroup parent) {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
    }

    @NonNull
    @Override
    public final VH instantiateItem(@NonNull ViewGroup parent, int position) {
        final int itemViewType = this.getItemViewType(position);
        VH holder = this.getRecycledPool().getRecycled(itemViewType);
        if (holder == null) {
            holder = this.createViewHolder(parent, itemViewType);
        }
        this.bindViewHolder(holder, position);
        parent.addView(holder.itemView);
        holder.onAttachedToWindow();
        this.onViewAttachedToWindow(holder);
        return holder;
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup parent, int position, @NonNull Object object) {
        final VH holder = (VH) object;
        this.onViewDetachedFromWindow(holder);
        holder.onDetachedFromWindow();
        parent.removeView(holder.itemView);
        this.getRecycledPool().putRecycled(holder.mItemViewType, holder);
        holder.resetInternal();
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public final boolean isViewFromObject(@NonNull View itemView, @NonNull Object object) {
        return ((ViewHolder) object).itemView == itemView;
    }

    public void onViewAttachedToWindow(@NonNull VH holder) {
    }

    public void onViewDetachedFromWindow(@NonNull VH holder) {
    }

    @NonNull
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int itemViewType);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    public abstract int getItemCount();

    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    public RecycledPool<VH> getRecycledPool() {
        if (this.mRecycledPool == null) {
            this.mRecycledPool = new RecycledPool<>();
        }
        return this.mRecycledPool;
    }

    public void setRecycledPool(@NonNull RecycledPool<VH> recycledPool) {
        this.mRecycledPool = recycledPool;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<VH> listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener<VH> listener) {
        this.mOnItemLongClickListener = listener;
    }

    @NonNull
    public final View inflate(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
        return this.inflate(layoutId, parent, false);
    }

    @NonNull
    public View inflate(@LayoutRes int layoutId, @Nullable ViewGroup parent, boolean attachToRoot) {
        return this.requireLayoutInflater().inflate(layoutId, parent, attachToRoot);
    }

    @Nullable
    public final LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    @NonNull
    public final LayoutInflater requireLayoutInflater() {
        return Objects.requireNonNull(this.getLayoutInflater());
    }

    @NonNull
    public final VH createViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        final VH holder = this.onCreateViewHolder(parent, itemViewType);
        if (holder.itemView.getParent() != null) {
            throw new IllegalStateException("ViewHolder views must not be attached when"
                    + " created. Ensure that you are not passing 'true' to the attachToRoot"
                    + " parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
        }
        holder.mItemViewType = itemViewType;
        return holder;
    }

    public final void bindViewHolder(@NonNull VH holder, int position) {
        holder.mAdapter = this;
        holder.mPosition = position;
        holder.setOnClickListener(holder.itemView);
        holder.setOnLongClickListener(holder.itemView);
        this.onBindViewHolder(holder, position);
    }

    private void dispatchOnItemClick(@NonNull ViewHolder holder, @NonNull View target) {
        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick((VH) holder, target, holder.getPosition());
        }
    }

    private boolean dispatchOnItemLongClick(@NonNull ViewHolder holder, @NonNull View target) {
        if (this.mOnItemLongClickListener != null) {
            return this.mOnItemLongClickListener.onItemLongClick((VH) holder, target, holder.getPosition());
        }
        return false;
    }

    public static abstract class ViewHolder {
        @NonNull
        public final View itemView;
        @Nullable
        private ComponentListener mComponentListener;

        int mPosition = NO_POSITION;
        int mItemViewType = INVALID_TYPE;
        @Nullable
        RecyclerPagerAdapter<?> mAdapter;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        @CallSuper
        public void onAttachedToWindow() {
            // nothing
        }

        @CallSuper
        public void onDetachedFromWindow() {
            // nothing
        }

        public final int getPosition() {
            return this.mPosition;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        @Nullable
        public final <V extends View> V findViewById(@IdRes int id) {
            return this.itemView.findViewById(id);
        }

        @NonNull
        public final <V extends View> V requireViewById(@IdRes int id) {
            return Objects.requireNonNull(this.findViewById(id));
        }

        @Nullable
        public final <T extends RecyclerPagerAdapter<? extends ViewHolder>> T getAdapter() {
            return (T) this.mAdapter;
        }

        @NonNull
        public final <T extends RecyclerPagerAdapter<? extends ViewHolder>> T requireAdapter() {
            return Objects.requireNonNull(this.getAdapter());
        }

        public final void setOnClickListener(@IdRes int id) {
            this.setOnClickListener(this.requireViewById(id));
        }

        public final void setOnClickListener(@NonNull View target) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            target.setOnClickListener(this.mComponentListener);
        }

        public final void setOnLongClickListener(@IdRes int id) {
            this.setOnLongClickListener(this.requireViewById(id));
        }

        public final void setOnLongClickListener(@NonNull View target) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            target.setOnLongClickListener(this.mComponentListener);
        }

        void resetInternal() {
            this.mAdapter = null;
            this.mPosition = NO_POSITION;
            this.mComponentListener = null;
            this.itemView.setOnClickListener(null);
            this.itemView.setOnLongClickListener(null);
        }

        private void dispatchOnClick(@NonNull View target) {
            final RecyclerPagerAdapter<?> adapter = this.getAdapter();
            if (adapter == null) {
                return;
            }
            adapter.dispatchOnItemClick(this, target);
        }

        private boolean dispatchOnLongClick(@NonNull View target) {
            final RecyclerPagerAdapter<?> adapter = this.getAdapter();
            if (adapter == null) {
                return false;
            }
            return adapter.dispatchOnItemLongClick(this, target);
        }

        private final class ComponentListener
                implements View.OnClickListener, View.OnLongClickListener {
            @Override
            public void onClick(@NonNull View target) {
                ViewHolder.this.dispatchOnClick(target);
            }

            @Override
            public boolean onLongClick(@NonNull View target) {
                return ViewHolder.this.dispatchOnLongClick(target);
            }
        }
    }

    public interface OnItemClickListener<VH extends ViewHolder> {

        void onItemClick(@NonNull VH holder, @NonNull View target, int position);
    }

    public interface OnItemLongClickListener<VH extends ViewHolder> {

        boolean onItemLongClick(@NonNull VH holder, @NonNull View target, int position);
    }
}
