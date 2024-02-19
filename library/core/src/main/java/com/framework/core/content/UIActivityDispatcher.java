package com.framework.core.content;

import android.content.Intent;
import android.view.KeyEvent;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * @Author create by Zhengzelong on 2022/6/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIActivityDispatcher {

    interface Cancellable {
        /**
         * Cancel the subscription. This call should be idempotent,
         * making it safe to call multiple times.
         */
        void cancel();
    }

    private final ArrayDeque<UIActivityCallback> callbackDeque = new ArrayDeque<>();

    @MainThread
    public boolean hasEnabledCallbacks() {
        final Iterator<UIActivityCallback> iterator;
        iterator = this.callbackDeque.descendingIterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEnabled()) {
                return true;
            }
        }
        return false;
    }

    @MainThread
    public void onNewIntent(@NonNull Intent intent) {
        final Iterator<UIActivityCallback> iterator;
        iterator = this.callbackDeque.iterator();
        while (iterator.hasNext()) {
            final UIActivityCallback uiActivityCallback = iterator.next();
            if (uiActivityCallback.isEnabled()) {
                uiActivityCallback.onNewIntent(intent);
            }
        }
    }

    @MainThread
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        final Iterator<UIActivityCallback> iterator;
        iterator = this.callbackDeque.descendingIterator();
        while (iterator.hasNext()) {
            final UIActivityCallback uiActivityCallback = iterator.next();
            if (uiActivityCallback.isEnabled() &&
                    uiActivityCallback.onKeyDown(keyCode, event)) {
                return true;
            }
        }
        return false;
    }

    @MainThread
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        final Iterator<UIActivityCallback> iterator;
        iterator = this.callbackDeque.descendingIterator();
        while (iterator.hasNext()) {
            final UIActivityCallback uiActivityCallback = iterator.next();
            if (uiActivityCallback.isEnabled() &&
                    uiActivityCallback.onKeyUp(keyCode, event)) {
                return true;
            }
        }
        return false;
    }

    @MainThread
    public boolean onBackPressed() {
        final Iterator<UIActivityCallback> iterator;
        iterator = this.callbackDeque.descendingIterator();
        while (iterator.hasNext()) {
            final UIActivityCallback uiActivityCallback = iterator.next();
            if (uiActivityCallback.isEnabled() &&
                    uiActivityCallback.onBackPressed()) {
                return true;
            }
        }
        return false;
    }

    @MainThread
    public void addCallback(@NonNull UIActivityCallback uiActivityCallback) {
        this.addCancellableCallback(uiActivityCallback);
    }

    @MainThread
    public void addCallback(@NonNull LifecycleOwner owner, @NonNull UIActivityCallback uiActivityCallback) {
        this.addCallback(owner.getLifecycle(), uiActivityCallback);
    }

    @MainThread
    public void addCallback(@NonNull Lifecycle lifecycle, @NonNull UIActivityCallback uiActivityCallback) {
        if (Lifecycle.State.DESTROYED == lifecycle.getCurrentState()) {
            return;
        }
        new LifecycleKeyEventCancellable(this, uiActivityCallback, lifecycle);
    }

    private void addKeyEventCallback(@NonNull UIActivityCallback uiActivityCallback) {
        this.callbackDeque.add(uiActivityCallback);
    }

    private void removeKeyEventCallback(@NonNull UIActivityCallback uiActivityCallback) {
        this.callbackDeque.remove(uiActivityCallback);
    }

    @NonNull
    @MainThread
    private Cancellable addCancellableCallback(@NonNull UIActivityCallback uiActivityCallback) {
        return new KeyEventCancellable(this, uiActivityCallback);
    }

    private static final class KeyEventCancellable implements Cancellable {
        private final UIActivityDispatcher uiActivityDispatcher;
        private final UIActivityCallback uiActivityCallback;

        public KeyEventCancellable(@NonNull UIActivityDispatcher uiActivityDispatcher,
                                   @NonNull UIActivityCallback uiActivityCallback) {
            this.uiActivityCallback = uiActivityCallback;
            this.uiActivityCallback.addCancellable(this);
            this.uiActivityDispatcher = uiActivityDispatcher;
            this.uiActivityDispatcher.addKeyEventCallback(this.uiActivityCallback);
        }

        @Override
        public void cancel() {
            this.uiActivityDispatcher.removeKeyEventCallback(this.uiActivityCallback);
            this.uiActivityCallback.removeCancellable(this);
        }
    }

    private static final class LifecycleKeyEventCancellable implements LifecycleEventObserver, Cancellable {
        private final UIActivityDispatcher uiActivityDispatcher;
        private final UIActivityCallback uiActivityCallback;
        private final Lifecycle lifecycle;
        @Nullable
        private Cancellable cancellable;

        public LifecycleKeyEventCancellable(@NonNull UIActivityDispatcher uiActivityDispatcher,
                                            @NonNull UIActivityCallback uiActivityCallback,
                                            @NonNull Lifecycle lifecycle) {
            this.uiActivityCallback = uiActivityCallback;
            this.uiActivityCallback.addCancellable(this);
            this.uiActivityDispatcher = uiActivityDispatcher;
            this.lifecycle = lifecycle;
            this.lifecycle.addObserver(this);
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (Lifecycle.Event.ON_START == event) {
                this.cancellable = this.addCancellableCallback(this.uiActivityCallback);
            }
            if (Lifecycle.Event.ON_STOP == event) {
                if (this.cancellable != null) {
                    this.cancellable.cancel();
                }
            }
            if (Lifecycle.Event.ON_DESTROY == event) {
                this.cancel();
            }
        }

        @NonNull
        @MainThread
        private Cancellable addCancellableCallback(@NonNull UIActivityCallback uiActivityCallback) {
            return this.uiActivityDispatcher.addCancellableCallback(uiActivityCallback);
        }

        @Override
        public void cancel() {
            this.lifecycle.removeObserver(this);
            this.uiActivityCallback.removeCancellable(this);
            if (this.cancellable != null) {
                this.cancellable.cancel();
                this.cancellable = null;
            }
        }
    }
}
