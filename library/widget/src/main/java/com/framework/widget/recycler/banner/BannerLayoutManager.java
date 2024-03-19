package com.framework.widget.recycler.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.view.AbsSavedState;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.recycler.pager.PagerLayoutManager;
import com.framework.widget.recycler.pager.PagerSmoothScroller;

/**
 * @Author create by Zhengzelong on 2023-03-28
 * @Email : 171905184@qq.com
 * @Description :
 */
public class BannerLayoutManager extends PagerLayoutManager {
    public static final long PLAY_DELAY_MS = 2500L;
    /**
     * 播放延迟时长(ms)
     */
    private long mPlayDelayMillis = PLAY_DELAY_MS;
    /**
     * 是否等待播放中
     */
    private boolean mPlayInPending;
    /**
     * 是否正在播放中
     */
    private boolean mPlayInProgress;
    /**
     * 是否启用自动播放
     */
    private boolean mAutoPlayEnabled;
    /**
     * 播放生命周期
     */
    private BannerPlayLiO mBannerPlayLiO;
    /**
     * 播放操作执行类
     */
    private BannerPlayExec mBannerPlayExec;

    public BannerLayoutManager(@NonNull Context context) {
        this(context, HORIZONTAL);
    }

    public BannerLayoutManager(@NonNull Context context, int orientation) {
        this(context, orientation, false);
    }

    public BannerLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.setAutoPlayEnabled(true);
        this.setLoopScrollEnabled(true);
    }

    public BannerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BannerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setAutoPlayEnabled(true);
        this.setLoopScrollEnabled(true);
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        this.restorePendingPlayState();
    }

    @Override
    public void onAdapterChanged(@Nullable RecyclerView.Adapter oldAdapter,
                                 @Nullable RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        this.restorePendingPlayState();
    }

    @Override
    public void onLayoutCompleted(@NonNull RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.restorePendingPlayState();
    }

    @Override
    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.State state, int position) {
        final PagerSmoothScroller smoothScroller;
        smoothScroller = new PagerSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        this.startSmoothScroll(smoothScroller);
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mPlayInProgress |= this.mPlayInPending;
        savedState.mPlayInProgress |= this.mPlayInProgress;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            final Parcelable superState = savedState.getSuperState();
            this.mPlayInPending = savedState.mPlayInProgress;
            if (superState != null) {
                super.onRestoreInstanceState(superState);
            } else {
                this.requestLayout();
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onPageScrollStateChanged(int scrollState) {
        super.onPageScrollStateChanged(scrollState);
        if (RecyclerView.SCROLL_STATE_IDLE == scrollState) {
            this.restorePendingPlayState();
        } else {
            this.mPlayInPending |= this.stopPlay();
        }
    }

    public void performEventStateChanged(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (MotionEvent.ACTION_DOWN == actionMasked) {
            this.mPlayInPending |= this.stopPlay();
        }
        if (MotionEvent.ACTION_UP == actionMasked ||
                MotionEvent.ACTION_CANCEL == actionMasked) {
            this.restorePendingPlayState();
        }
    }

    public boolean isPlayInPending() {
        return this.mPlayInPending;
    }

    public boolean isPlayInProgress() {
        return this.mPlayInProgress;
    }

    public boolean isAutoPlayEnabled() {
        return this.mAutoPlayEnabled;
    }

    public void setAutoPlayEnabled(boolean enabled) {
        if (this.mAutoPlayEnabled != enabled) {
            this.mAutoPlayEnabled = enabled;
            // Timely stop it.
            if (!enabled) {
                this.stopPlay();
            }
        }
    }

    public long getPlayDelayMillis() {
        return this.mPlayDelayMillis;
    }

    public void setPlayDelayMillis(long delayMillis) {
        this.mPlayDelayMillis = delayMillis;
    }

    public void setLifecycle(@NonNull LifecycleOwner owner) {
        this.setLifecycle(owner.getLifecycle());
    }

    public void setLifecycle(@NonNull Lifecycle lifecycle) {
        if (this.mBannerPlayLiO != null) {
            this.mBannerPlayLiO.destroy();
        }
        this.mBannerPlayLiO = new BannerPlayLiO(lifecycle);
    }

    public synchronized boolean startPlay() {
        if (this.allowPendingPlayAction()) {
            this.stopPlay();
            this.mPlayInPending = true;
            return false;
        }
        if (this.mPlayInProgress) {
            return false;
        }
        this.mPlayInProgress = true;
        if (this.mBannerPlayExec == null) {
            this.mBannerPlayExec = new BannerPlayExec();
        }
        this.mBannerPlayExec.next(this.mPlayDelayMillis);
        return true;
    }

    public synchronized boolean stopPlay() {
        if (this.mBannerPlayExec != null) {
            this.mBannerPlayExec.stop();
        }
        final boolean handled = this.mPlayInProgress;
        this.mPlayInProgress = false;
        return handled;
    }

    public synchronized void destroyPlay() {
        this.stopPlay();
        if (this.mBannerPlayExec != null) {
            this.mBannerPlayExec.destroy();
            this.mBannerPlayExec = null;
        }
    }

    @CallSuper
    protected void restorePendingPlayState() {
        if (this.mPlayInPending) {
            this.mPlayInPending = false;
            this.startPlay();
        }
    }

    @CallSuper
    protected boolean allowPendingPlayAction() {
        if (this.mPlayInPending) {
            return true;
        }
        if (this.getItemCount() == 0) {
            return true;
        }
        final RecyclerView rv = this.getRecyclerView();
        if (rv == null) {
            return true;
        }
        final int scrollState = rv.getScrollState();
        return RecyclerView.SCROLL_STATE_IDLE != scrollState;
    }

    private void handleAction(@NonNull BannerPlayExec exec) {
        if (this.allowPendingPlayAction()) {
            this.mPlayInPending = this.stopPlay();
            return;
        }
        int position;
        position = this.getCurrentPosition() + 1;
        position = this.convertLayoutPosition(position);
        this.setCurrentPosition(position, true);

        final boolean needsPlay;
        if (position < 0
                || position >= this.getItemCount()) {
            needsPlay = false;
        } else {
            needsPlay = this.mAutoPlayEnabled;
        }
        if (needsPlay) {
            exec.next(this.mPlayDelayMillis);
        } else {
            this.stopPlay();
        } // Done play.
    }

    private final class BannerPlayExec implements Runnable {
        @Nullable
        private Handler handler;

        @Override
        public void run() {
            synchronized (BannerLayoutManager.class) {
                BannerLayoutManager.this.handleAction(this);
            }
        }

        public void next(long delayMillis) {
            synchronized (BannerLayoutManager.class) {
                if (this.handler == null) {
                    this.handler = new Handler(Looper.getMainLooper());
                }
                this.handler.removeCallbacks(this);
                this.handler.postDelayed(this, delayMillis);
            }
        }

        public void stop() {
            synchronized (BannerLayoutManager.class) {
                if (this.handler != null) {
                    this.handler.removeCallbacks(this);
                }
            }
        }

        public void destroy() {
            synchronized (BannerLayoutManager.class) {
                this.stop();
                this.handler = null;
            }
        }
    }

    private final class BannerPlayLiO implements LifecycleObserver {
        @Nullable
        private Lifecycle lifecycle;

        private BannerPlayLiO(@NonNull Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            this.lifecycle.addObserver(this);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void resume() {
            BannerLayoutManager.this.startPlay();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void pause() {
            BannerLayoutManager.this.stopPlay();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void destroy() {
            if (this.lifecycle != null) {
                this.lifecycle.removeObserver(this);
                this.lifecycle = null;
            }
            BannerLayoutManager.this.destroyPlay();
        }
    }

    public static class SavedState extends AbsSavedState {
        private boolean mPlayInProgress;

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mPlayInProgress = false;
        }

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source parcel to read from
         */
        public SavedState(@NonNull Parcel source) {
            this(source, null);
        }

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source parcel to read from
         * @param loader ClassLoader to use for reading
         */
        public SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            this.mPlayInProgress = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mPlayInProgress ? 1 : 0);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            /**
             * Create a new instance of the Parcelable class, instantiating it
             * from the given Parcel whose data had previously been written by
             * {@link Parcelable#writeToParcel Parcelable.writeToParcel()} and
             * using the given ClassLoader.
             *
             * @param source The Parcel to read the object's data from.
             * @param loader The ClassLoader that this object is being created in.
             * @return Returns a new instance of the Parcelable class.
             */
            @Override
            public SavedState createFromParcel(@NonNull Parcel source, @Nullable ClassLoader loader) {
                return new SavedState(source, loader);
            }

            /**
             * Create a new instance of the Parcelable class, instantiating it
             * from the given Parcel whose data had previously been written by
             * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
             *
             * @param source The Parcel to read the object's data from.
             * @return Returns a new instance of the Parcelable class.
             */
            @Override
            public SavedState createFromParcel(@NonNull Parcel source) {
                return new SavedState(source);
            }

            /**
             * Create a new array of the Parcelable class.
             *
             * @param size Size of the array.
             * @return Returns an array of the Parcelable class, with every entry
             * initialized to null.
             */
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[0];
            }
        };
    }
}
