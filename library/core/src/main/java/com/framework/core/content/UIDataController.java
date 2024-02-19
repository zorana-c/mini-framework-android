package com.framework.core.content;

import android.database.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @Author create by Zhengzelong on 2022/2/16
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDataController<T> {
    @NonNull
    private final ArrayList<T> mDataSources = new ArrayList<>();
    @NonNull
    private final DataObservable mDataObservable = new DataObservable();
    @Nullable
    private ArrayList<OnDataChangedListener<T>> mOnDataChangedListeners;

    @NonNull
    public final UIDataController<T> unregisterAll() {
        this.mDataObservable.unregisterAll();
        return this;
    }

    @NonNull
    public final UIDataController<T> registerDataObserver(@NonNull DataObserver observer) {
        this.mDataObservable.registerObserver(observer);
        return this;
    }

    @NonNull
    public final UIDataController<T> unregisterDataObserver(@NonNull DataObserver observer) {
        this.mDataObservable.unregisterObserver(observer);
        return this;
    }

    @NonNull
    public final UIDataController<T> clearOnDataChangedListeners() {
        if (this.mOnDataChangedListeners != null) {
            this.mOnDataChangedListeners.clear();
        }
        return this;
    }

    @NonNull
    public final UIDataController<T> addOnDataChangedListener(@NonNull OnDataChangedListener<T> listener) {
        if (this.mOnDataChangedListeners == null) {
            this.mOnDataChangedListeners = new ArrayList<>();
        }
        this.mOnDataChangedListeners.add(listener);
        return this;
    }

    @NonNull
    public final UIDataController<T> removeOnDataChangedListener(@NonNull OnDataChangedListener<T> listener) {
        if (this.mOnDataChangedListeners != null) {
            this.mOnDataChangedListeners.remove(listener);
        }
        return this;
    }

    // Method Set

    @NonNull
    public final UIDataController<T> set(@NonNull T dataSource) {
        return this.setAll(Collections.singletonList(dataSource));
    }

    @NonNull
    public final UIDataController<T> setAll(@NonNull Collection<T> dataSources) {
        this.clear();
        return this.addAll(dataSources);
    }

    // Method Add

    @NonNull
    public final UIDataController<T> add(@NonNull T dataSource) {
        return this.add(-1, dataSource);
    }

    @NonNull
    public final UIDataController<T> add(int index, @NonNull T dataSource) {
        return this.addAll(index, Collections.singletonList(dataSource));
    }

    @NonNull
    public final UIDataController<T> addAll(@NonNull Collection<T> dataSources) {
        return this.addAll(-1, dataSources);
    }

    @NonNull
    public UIDataController<T> addAll(int index, @NonNull Collection<T> dataSources) {
        final int N = this.mDataSources.size();
        if (index < 0) {
            index = N;
        } else {
            index = Math.max(0, Math.min(index, N));
        }
        this.mDataSources.addAll(index, dataSources);
        this.notifyItemRangeInserted(index, dataSources.size());
        return this;
    }

    // Method Remove

    @Nullable
    public final T removeFromHead() {
        return this.removeAt(0);
    }

    @Nullable
    public final T removeFromTail() {
        return this.removeAt(this.mDataSources.size() - 1);
    }

    @Nullable
    public final T removeAt(int index) {
        final List<T> dataSources = this.removeAtRange(index, 1);
        if (dataSources.isEmpty()) {
            return null;
        }
        return dataSources.get(0);
    }

    @NonNull
    public List<T> removeAtRange(int indexStart, int itemCount) {
        final int N = this.mDataSources.size();
        if (indexStart < 0 || indexStart >= N) {
            throw new IndexOutOfBoundsException("Index: " + indexStart + ", Size: " + N);
        }
        final ArrayList<T> dataSources = new ArrayList<>();
        for (int i = indexStart; i < (indexStart + itemCount); itemCount--) {
            if (i >= this.mDataSources.size()) {
                break;
            }
            dataSources.add(this.mDataSources.remove(i));
        }
        this.notifyItemRangeRemoved(indexStart, dataSources.size());
        return dataSources;
    }

    public final boolean remove(@NonNull T dataSource) {
        final int index = this.mDataSources.indexOf(dataSource);
        if (index == -1) {
            return false;
        }
        return this.removeAt(index) != null;
    }

    // Method Clear

    public boolean clear() {
        final int N = this.mDataSources.size();
        if (N == 0) {
            return false;
        }
        this.mDataSources.clear();
        this.notifyItemRangeRemoved(0, N);
        return true;
    }

    // Method Move

    @NonNull
    public final UIDataController<T> moveToHead(int index) {
        return this.move(index, 0);
    }

    @NonNull
    public final UIDataController<T> moveToTail(int index) {
        return this.move(index, this.mDataSources.size() - 1);
    }

    @NonNull
    public final UIDataController<T> move(int fromIndex, int toIndex) {
        return this.move(fromIndex, toIndex, 1);
    }

    @NonNull
    public UIDataController<T> move(int fromIndex, int toIndex, int itemCount) {
        if (fromIndex == toIndex) {
            return this;
        }
        if (itemCount != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        final int N = this.mDataSources.size();
        if (fromIndex < 0 || fromIndex >= N) {
            throw new IndexOutOfBoundsException("Index: " + fromIndex + ", Size: " + N);
        }
        if (toIndex < 0 || toIndex >= N) {
            throw new IndexOutOfBoundsException("Index: " + toIndex + ", Size: " + N);
        }
        final T dataSource = this.mDataSources.remove(fromIndex);
        this.mDataSources.add(toIndex, dataSource);
        this.notifyItemRangeMoved(fromIndex, toIndex, itemCount);
        return this;
    }

    // Method Get

    @Nullable
    public <R extends T> R findDataSourceBy(int index) {
        final int N = this.mDataSources.size();
        if (index < 0 || index >= N) {
            return null;
        }
        return (R) this.mDataSources.get(index);
    }

    @NonNull
    public final <R extends T> R requireDataSourceBy(int index) {
        final R dataSource = this.findDataSourceBy(index);
        if (dataSource == null) {
            final int N = this.mDataSources.size();
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + N);
        }
        return dataSource;
    }

    @NonNull
    public List<T> getDataSourceList() {
        return this.mDataSources;
    }

    @NonNull
    public Iterator<T> iterator() {
        return this.mDataSources.iterator();
    }

    @NonNull
    public ListIterator<T> listIterator() {
        return this.mDataSources.listIterator();
    }

    // Method Other

    public boolean isEmpty() {
        return this.mDataSources.isEmpty();
    }

    public boolean contains(@NonNull T dataSource) {
        return this.mDataSources.contains(dataSource);
    }

    public int size() {
        return this.mDataSources.size();
    }

    public int indexOf(@NonNull T dataSource) {
        return this.mDataSources.indexOf(dataSource);
    }

    public int lastIndexOf(@NonNull T dataSource) {
        return this.mDataSources.lastIndexOf(dataSource);
    }

    private void notifyItemRangeInserted(int positionStart, int itemCount) {
        this.mDataObservable.notifyItemRangeInserted(positionStart, itemCount);

        if (this.mOnDataChangedListeners == null) {
            return;
        }
        for (OnDataChangedListener<T> listener : this.mOnDataChangedListeners) {
            listener.onItemRangeInserted(this, positionStart, itemCount);
        }
    }

    private void notifyItemRangeRemoved(int positionStart, int itemCount) {
        this.mDataObservable.notifyItemRangeRemoved(positionStart, itemCount);

        if (this.mOnDataChangedListeners == null) {
            return;
        }
        for (OnDataChangedListener<T> listener : this.mOnDataChangedListeners) {
            listener.onItemRangeRemoved(this, positionStart, itemCount);
        }
    }

    private void notifyItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        this.mDataObservable.notifyItemRangeMoved(fromPosition, toPosition, itemCount);

        if (this.mOnDataChangedListeners == null) {
            return;
        }
        for (OnDataChangedListener<T> listener : this.mOnDataChangedListeners) {
            listener.onItemRangeMoved(this, fromPosition, toPosition, itemCount);
        }
    }

    private static final class DataObservable extends Observable<DataObserver> {

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onItemRangeInserted(positionStart, itemCount)) {
                    return;
                }
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onItemRangeRemoved(positionStart, itemCount)) {
                    return;
                }
            }
        }

        public void notifyItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onItemRangeMoved(fromPosition, toPosition, itemCount)) {
                    return;
                }
            }
        }
    }

    public interface DataObserver {

        default boolean onItemRangeInserted(int positionStart, int itemCount) {
            return false;
        }

        default boolean onItemRangeRemoved(int positionStart, int itemCount) {
            return false;
        }

        default boolean onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            return false;
        }
    }

    public interface OnDataChangedListener<T> {

        default void onItemRangeInserted(@NonNull UIDataController<T> uiDataController,
                                         int positionStart,
                                         int itemCount) {
            // nothing
        }

        default void onItemRangeRemoved(@NonNull UIDataController<T> uiDataController,
                                        int positionStart,
                                        int itemCount) {
            // nothing
        }

        default void onItemRangeMoved(@NonNull UIDataController<T> uiDataController,
                                      int fromPosition,
                                      int toPosition,
                                      int itemCount) {
            // nothing
        }
    }
}
