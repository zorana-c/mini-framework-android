package com.framework.core.content;

import android.content.Intent;
import android.view.KeyEvent;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author create by Zhengzelong on 2022/6/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIActivityCallback {
    private final CopyOnWriteArrayList<UIActivityDispatcher.Cancellable>
            cancellableList = new CopyOnWriteArrayList<>();
    private boolean mEnabled;

    public UIActivityCallback() {
        this(false);
    }

    public UIActivityCallback(boolean enabled) {
        this.mEnabled = enabled;
    }

    @MainThread
    public void onNewIntent(@NonNull Intent intent) {
    }

    @MainThread
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return false;
    }

    @MainThread
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return false;
    }

    @MainThread
    public boolean onBackPressed() {
        return false;
    }

    @MainThread
    public final boolean isEnabled() {
        return this.mEnabled;
    }

    @MainThread
    public final void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    @MainThread
    public final void remove() {
        final Iterator<UIActivityDispatcher.Cancellable> iterator;
        iterator = this.cancellableList.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancel();
        }
    }

    void addCancellable(@NonNull UIActivityDispatcher.Cancellable cancellable) {
        this.cancellableList.add(cancellable);
    }

    void removeCancellable(@NonNull UIActivityDispatcher.Cancellable cancellable) {
        this.cancellableList.remove(cancellable);
    }
}
