package com.framework.widget.cache;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2021/7/14
 * @Email : 171905184@qq.com
 * @Description :
 */
public class RecycledPool<T> {
    private static final int DEFAULT_MAX_SCRAP = 5;

    private static class ScrapData<T> {
        private final ArrayList<T> mScrapHeap = new ArrayList<>();

        private int mMaxScrap = DEFAULT_MAX_SCRAP;

        private boolean isPeaking() {
            return this.mScrapHeap.size() >= mMaxScrap;
        }
    }

    private final SparseArray<ScrapData<T>> mScrap = new SparseArray<>();

    public void setMaxRecycledViews(int type, int max) {
        final ScrapData<T> scrapData = this.getScrapDataForType(type);
        scrapData.mMaxScrap = max;
        final ArrayList<T> scrapHeap = scrapData.mScrapHeap;
        while (scrapHeap.size() > max) {
            scrapHeap.remove(scrapHeap.size() - 1);
        }
    }

    @Nullable
    public T getRecycled(int type) {
        final ScrapData<T> scrapData = this.getScrapDataForType(type);
        if (scrapData.mScrapHeap.isEmpty()) {
            return null;
        }
        final ArrayList<T> scrapHeap = scrapData.mScrapHeap;
        return scrapHeap.remove(scrapHeap.size() - 1);
    }

    public boolean putRecycled(int type, @NonNull T scrap) {
        final ScrapData<T> scrapData = this.getScrapDataForType(type);
        if (scrapData.isPeaking()) {
            return false;
        }
        return scrapData.mScrapHeap.add(scrap);
    }

    public int getRecycledCount(int type) {
        return this.getScrapDataForType(type).mScrapHeap.size();
    }

    public void clear() {
        ScrapData<T> scrapData;
        for (int i = 0; i < this.mScrap.size(); i++) {
            scrapData = this.mScrap.valueAt(i);
            scrapData.mScrapHeap.clear();
        }
    }

    @NonNull
    protected ScrapData<T> getScrapDataForType(int type) {
        ScrapData<T> scrapData = this.mScrap.get(type);
        if (scrapData == null) {
            scrapData = new ScrapData<>();
            this.mScrap.put(type, scrapData);
        }
        return scrapData;
    }
}
