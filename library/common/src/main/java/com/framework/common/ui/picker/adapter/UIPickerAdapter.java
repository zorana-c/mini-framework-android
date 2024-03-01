package com.framework.common.ui.picker.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.recycler.pager.PagerLayoutManager;
import com.framework.widget.recycler.picker.AppCompatPickerView;

/**
 * @Author create by Zhengzelong on 2023-04-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIPickerAdapter<VH extends UIPickerViewHolder>
        extends AppCompatPickerView.Adapter<VH> {
    @Nullable
    private final AppCompatPickerView upstream;
    @Nullable
    private UpstreamComponent upstreamComponent;

    public UIPickerAdapter() {
        this(null);
    }

    public UIPickerAdapter(@Nullable AppCompatPickerView upstream) {
        this.upstream = upstream;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.upstreamComponent = UpstreamComponent.bind(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (this.upstreamComponent != null) {
            this.upstreamComponent.unbind();
        }
    }

    public abstract void onUpstreamPositionChanged(int upstreamPosition);

    public final boolean hasUpstream() {
        return this.upstream != null;
    }

    @Nullable
    public final <R extends AppCompatPickerView> R getUpstream() {
        return (R) this.upstream;
    }

    @NonNull
    public final <R extends AppCompatPickerView> R requireUpstream() {
        final R upstream = this.getUpstream();
        if (upstream == null) {
            throw new NullPointerException("ERROR");
        }
        return upstream;
    }

    @Nullable
    public final <R extends UIPickerAdapter<?>> R getUpstreamAd() {
        final AppCompatPickerView upstream = this.getUpstream();
        if (upstream == null) {
            return null;
        }
        return (R) upstream.getAdapter();
    }

    @NonNull
    public final <R extends UIPickerAdapter<?>> R requireUpstreamAd() {
        final R upstreamAd = this.getUpstreamAd();
        if (upstreamAd == null) {
            throw new NullPointerException("ERROR");
        }
        return upstreamAd;
    }

    public final int getCurrentPosition() {
        final AppCompatPickerView thisV = this.getRecyclerView();
        if (thisV == null) {
            return RecyclerView.NO_POSITION;
        }
        return thisV.getCurrentPosition();
    }

    public final int getUpstreamPosition() {
        final UIPickerAdapter<?> upstreamAd = this.getUpstreamAd();
        if (upstreamAd == null) {
            return RecyclerView.NO_POSITION;
        }
        return upstreamAd.getCurrentPosition();
    }

    private void dispatchUpstreamPositionChanged() {
        final int upstreamPosition;
        upstreamPosition = this.getUpstreamPosition();
        this.onUpstreamPositionChanged(upstreamPosition);
    }

    private static class UpstreamComponent implements
            PagerLayoutManager.OnPageChangeListener,
            AppCompatPickerView.OnAdapterChangedListener {
        private final UIPickerAdapter<?> ad;
        private UpstreamComponent.AdapterDataObserver ado;

        @NonNull
        static UpstreamComponent bind(@NonNull UIPickerAdapter<?> adapter) {
            return new UpstreamComponent(adapter);
        }

        private UpstreamComponent(@NonNull UIPickerAdapter<?> adapter) {
            this.ad = adapter;
            final AppCompatPickerView upstream = adapter.getUpstream();
            if (upstream != null) {
                upstream.addOnPageChangeListener(this);
                upstream.addOnAdapterChangedListener(this);
            } else {
                this.notifyUpstreamPositionChanged();
            }
        }

        @Override
        public void onAdapterChanged(@NonNull RecyclerView recyclerView,
                                     @Nullable RecyclerView.Adapter<?> oldAdapter,
                                     @Nullable RecyclerView.Adapter<?> newAdapter) {
            if (oldAdapter != null) {
                oldAdapter.unregisterAdapterDataObserver(this.ado);
            }
            if (newAdapter != null) {
                if (this.ado == null) {
                    this.ado = new AdapterDataObserver();
                }
                newAdapter.registerAdapterDataObserver(this.ado);
            }
            this.notifyUpstreamPositionChanged();
        }

        @Override
        public void onPageSelected(@NonNull RecyclerView recyclerView, int position) {
            this.notifyUpstreamPositionChanged();
        }

        public void notifyUpstreamPositionChanged() {
            this.ad.dispatchUpstreamPositionChanged();
        }

        public void unbind() {
            final AppCompatPickerView upstream = this.ad.getUpstream();
            if (upstream != null) {
                upstream.removeOnPageChangeListener(this);
                upstream.removeOnAdapterChangedListener(this);
            }
        }

        private final class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
            @Override
            public void onChanged() {
                UpstreamComponent.this.notifyUpstreamPositionChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                UpstreamComponent.this.notifyUpstreamPositionChanged();
            }
        }
    }
}
