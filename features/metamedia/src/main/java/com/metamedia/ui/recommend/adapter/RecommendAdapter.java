package com.metamedia.ui.recommend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSink;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.datasource.TransferListener;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIDataController;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.widget.recycler.pager.PagerLayoutManager;
import com.metamedia.MetaMediaInit;
import com.metamedia.bean.Video;

/**
 * @Author create by Zhengzelong on 2024-01-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public class RecommendAdapter<T extends Video> extends UIListController.LazyAdapter<T> {
    @NonNull
    private final DataSource.Factory mDataSourceFactory;
    @NonNull
    private final ComponentListener<T> mComponentListener;

    public RecommendAdapter(@NonNull UIPageControllerOwner owner) {
        this(owner.<UIListController<T>>getUIPageController());
    }

    public RecommendAdapter(@NonNull UIListController<T> uiListController) {
        super(uiListController);
        // 很关键: 使ViewHolder具有唯一性, 保证每次刷新不被重置, 同时保存现有播放状态
        this.setHasStableIds(true);
        this.mDataSourceFactory = this.generateFactory(uiListController);
        this.mComponentListener = new ComponentListener<>(uiListController);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final PagerLayoutManager layoutManager;
        layoutManager = new PagerLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setOffscreenPageLimit(3);
        layoutManager.addOnPageChangeListener(this.mComponentListener);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        final PagerLayoutManager layoutManager;
        layoutManager = (PagerLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.removeOnPageChangeListener(this.mComponentListener);
        }
        // this.destroyAllPlayer(recyclerView);
    }

    @Override
    public long getGroupItemId(int groupPosition) {
        // 很关键: 使ViewHolder具有唯一性, 保证每次刷新不被重置, 同时保存现有播放状态
        final T item = this.requireDataSourceBy(groupPosition);
        return item.nanoId();
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @NonNull
    public final DataSource.Factory getDataSourceFactory() {
        return this.mDataSourceFactory;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @NonNull
    public final MediaSource getMediaSource(@NonNull MediaItem mediaItem) {
        return new ProgressiveMediaSource
                .Factory(this.mDataSourceFactory)
                .createMediaSource(mediaItem);
    }

    @SuppressLint("UnsafeOptInUsageError")
    @NonNull
    private DataSource.Factory generateFactory(@NonNull UIPageController uiPageController) {
        final Context context = uiPageController.requireContext();
        // 创建缓存策略
        final Cache cache = MetaMediaInit.cache();
        // 创建网络工厂
        final DataSource.Factory httpDataSourceFactory;
        httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(context, context.getPackageName()))
                .setAllowCrossProtocolRedirects(true)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
        // 创建宽带监听
        final TransferListener transferListener;
        transferListener = new DefaultBandwidthMeter.Builder(context)
                .build();
        // 创建来源工厂
        final DataSource.Factory upstreamDataSourceFactory;
        upstreamDataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory)
                .setTransferListener(transferListener);
        // 创建写入工厂
        final DataSink.Factory writeDataSourceFactory = new CacheDataSink.Factory()
                .setCache(cache)
                .setFragmentSize(Long.MAX_VALUE)
                .setBufferSize(CacheDataSink.DEFAULT_BUFFER_SIZE);
        // 创建读取工厂
        final DataSource.Factory readDataSourceFactory = new FileDataSource.Factory();
        // 创建数据工厂
        return new CacheDataSource.Factory()
                .setCache(cache)
                .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE
                        | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setCacheWriteDataSinkFactory(writeDataSourceFactory)
                .setCacheReadDataSourceFactory(readDataSourceFactory)
                .setUpstreamDataSourceFactory(upstreamDataSourceFactory);
    }

    private void destroyAllPlayer(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        final int N = layoutManager.getChildCount();
        for (int index = 0; index < N; index++) {
            final View itemView = layoutManager.getChildAt(index);
            if (itemView == null) {
                continue;
            }
            final RecommendViewHolder<T> holder;
            holder = (RecommendViewHolder<T>) recyclerView.getChildViewHolder(itemView);
            holder.destroy();
        }
    }

    private static final class ComponentListener<T extends Video>
            extends PagerLayoutManager.SimpleOnPageChangeListener
            implements DefaultLifecycleObserver, UIDataController.OnDataChangedListener<T> {
        @NonNull
        private final Runnable mPlayAction = this::playWhen;
        @NonNull
        private final UIListController<T> mUIListController;
        @Nullable
        private RecommendViewHolder<T> mHolder;
        private boolean mIsPlayingDestroy;
        private boolean mIsPlayAfterPause;

        public ComponentListener(@NonNull UIListController<T> uiListController) {
            this.mUIListController = uiListController;
            // listen data
            uiListController.addOnDataChangedListener(this);
            // listen life
            final LifecycleOwner owner = uiListController.getUIComponent();
            final Lifecycle l = owner.getLifecycle();
            l.addObserver(this);
        }

        @Override
        public void onPageScrollStateChanged(@NonNull RecyclerView recyclerView,
                                             int scrollState) {
            this.playWhen();
        }

        @Override
        public void onItemRangeInserted(@NonNull UIDataController<T> uiDataController,
                                        int positionStart,
                                        int itemCount) {
            this.postPlayWhen();
        }

        @Override
        public void onItemRangeRemoved(@NonNull UIDataController<T> uiDataController,
                                       int positionStart,
                                       int itemCount) {
            this.postPlayWhen();
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            final RecommendViewHolder<T> holder = this.mHolder;
            if (holder == null) {
                this.playWhen();
            } else if (this.mIsPlayAfterPause) {
                holder.play();
            }
            this.mIsPlayAfterPause = false;
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            final RecommendViewHolder<T> holder = this.mHolder;
            if (holder != null) {
                this.mIsPlayAfterPause = holder.isPlaying();
                holder.pause();
            }
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            this.mIsPlayingDestroy = true;
            final UIListController<T> uiListController;
            uiListController = this.mUIListController;
            // un listen data
            uiListController.removeOnDataChangedListener(this);
            // destroy player
            uiListController.clear();
            uiListController.setAdapter(null);
            // un listen life
            final Lifecycle l = owner.getLifecycle();
            l.removeObserver(this);
            this.mHolder = null;
        }

        public void playWhen() {
            if (this.mIsPlayingDestroy) {
                return;
            }
            final RecyclerView rv;
            rv = this.mUIListController.getExpandableRecyclerView();
            if (rv == null) {
                return;
            }
            rv.removeCallbacks(this.mPlayAction);
            final int scrollState = rv.getScrollState();
            if (scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }
            final PagerLayoutManager layoutManager;
            layoutManager = (PagerLayoutManager) rv.getLayoutManager();
            if (layoutManager == null) {
                return;
            }
            final View snapView = layoutManager.findSnapView();
            if (snapView == null) {
                return;
            }
            final RecommendViewHolder<T> holder;
            holder = (RecommendViewHolder<T>) rv.getChildViewHolder(snapView);
            final RecommendViewHolder<T> oldHolder = this.mHolder;
            final RecommendViewHolder<T> newHolder = holder;
            if (oldHolder == newHolder) {
                if (oldHolder != null) {
                    oldHolder.play();
                }
                return;
            }
            if (oldHolder != null) {
                oldHolder.pause();
            }
            this.mHolder = newHolder;
            if (newHolder != null) {
                newHolder.play();
            }
        }

        public void postPlayWhen() {
            if (this.mIsPlayingDestroy) {
                return;
            }
            final RecyclerView rv;
            rv = this.mUIListController.getExpandableRecyclerView();
            if (rv == null) {
                return;
            }
            rv.removeCallbacks(this.mPlayAction);
            rv.postDelayed(this.mPlayAction, 0);
        }
    }
}
