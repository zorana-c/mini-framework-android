package com.framework.core.content;

import android.database.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @Author create by Zhengzelong on 2022/2/16
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDataController<T> {
    @NonNull
    private final ArrayList<T> mDataSourceList = new ArrayList<>();
    @NonNull
    private final DataObservable mDataObservable = new DataObservable();

    public UIDataController() {
        this(null);
    }

    public UIDataController(@Nullable Adapter adapter) {
        if (adapter != null) {
            this.registerObserver(new AdapterObserver(adapter));
        }
    }

    @NonNull
    public final Observer registerObserverWith(@NonNull Adapter adapter) {
        final Observer observer = new AdapterObserver(adapter);
        this.registerObserver(observer);
        return observer;
    }

    @NonNull
    public final UIDataController<T> unregisterAll() {
        this.mDataObservable.unregisterAll();
        return this;
    }

    @NonNull
    public final UIDataController<T> registerObserver(@NonNull Observer observer) {
        this.mDataObservable.registerObserver(observer);
        return this;
    }

    @NonNull
    public final UIDataController<T> unregisterObserver(@NonNull Observer observer) {
        this.mDataObservable.unregisterObserver(observer);
        return this;
    }

    @NonNull
    public final UIDataController<T> notifyDataSetChanged() {
        this.mDataObservable.notifyChanged();
        return this;
    }

    @NonNull
    public final UIDataController<T> notifyInserted(int position) {
        return this.notifyRangeInserted(position, 1);
    }

    @NonNull
    public final UIDataController<T> notifyRangeInserted(int positionStart, int itemCount) {
        this.mDataObservable.notifyRangeInserted(positionStart, itemCount);
        return this;
    }

    @NonNull
    public final UIDataController<T> notifyRemoved(int position) {
        return this.notifyRangeRemoved(position, 1);
    }

    @NonNull
    public final UIDataController<T> notifyRangeRemoved(int positionStart, int itemCount) {
        this.mDataObservable.notifyRangeRemoved(positionStart, itemCount);
        return this;
    }

    @NonNull
    public final UIDataController<T> notifyChanged(int position) {
        return this.notifyRangeChanged(position, 1);
    }

    @NonNull
    public final UIDataController<T> notifyRangeChanged(int positionStart, int itemCount) {
        this.mDataObservable.notifyRangeChanged(positionStart, itemCount);
        return this;
    }

    @NonNull
    public final UIDataController<T> notifyMoved(int fromPosition, int toPosition) {
        return this.notifyRangeMoved(fromPosition, toPosition, 1);
    }

    @NonNull
    public final UIDataController<T> notifyRangeMoved(int fromPosition, int toPosition, int itemCount) {
        this.mDataObservable.notifyRangeMoved(fromPosition, toPosition, itemCount);
        return this;
    }

    // Method Insert

    @NonNull
    public final UIDataController<T> set(@Nullable T t) {
        this.removeAll();
        return this.add(t);
    }

    @NonNull
    public final UIDataController<T> setAll(@NonNull Collection<? extends T> c) {
        this.removeAll();
        return this.addAll(c);
    }

    @NonNull
    public final UIDataController<T> add(@Nullable T t) {
        return this.add(-1, t);
    }

    @NonNull
    public final UIDataController<T> add(int index, @Nullable T t) {
        return this.addAll(index, Collections.singletonList(t));
    }

    @NonNull
    public final UIDataController<T> addAll(@NonNull Collection<? extends T> c) {
        return this.addAll(-1, c);
    }

    @NonNull
    public final UIDataController<T> addAll(int index, @NonNull Collection<? extends T> c) {
        final int N = this.size();
        if (index == -1) {
            index = N;
        }
        index = Math.max(0, Math.min(index, N));
        this.mDataSourceList.addAll(index, c);
        this.notifyRangeInserted(index, c.size());
        return this;
    }

    // Method Delete

    @NonNull
    public final Collection<T> removeAll() {
        final Collection<T> collection = this.findAll();
        final ArrayList<T> resultList = new ArrayList<>(collection);
        collection.clear();
        this.notifyRangeRemoved(0, resultList.size());
        return resultList;
    }

    @NonNull
    public final Collection<T> removeAt(int index) {
        return this.removeAt(index, 1);
    }

    @NonNull
    public final Collection<T> removeAt(int indexStart, int itemCount) {
        final ArrayList<T> resultList = new ArrayList<>(itemCount);
        for (; itemCount > 0; itemCount--) {
            if (this.containsAt(indexStart)) {
                resultList.add(this.mDataSourceList.remove(indexStart));
            } else break;
        }
        this.notifyRangeRemoved(indexStart, resultList.size());
        return resultList;
    }

    @NonNull
    public final Collection<T> remove(@Nullable T t) {
        return this.removeIf(Predicate.isEqual(t));
    }

    @NonNull
    public final Collection<T> removeIf(@NonNull Predicate<? super T> p) {
        final ArrayList<T> resultList = new ArrayList<>(this.size());
        int index = 0;
        while (this.containsAt(index)) {
            final T t = this.findBy(index);
            if (p.test(t)) {
                resultList.add(this.mDataSourceList.remove(index));
                this.notifyRemoved(index);
            } else index++;
        }
        return resultList;
    }

    @NonNull
    public final Collection<T> removeAll(@NonNull Collection<? extends T> c) {
        final ArrayList<T> resultList = new ArrayList<>(c.size());
        final Iterator<T> each = (Iterator<T>) c.iterator();
        while (each.hasNext()) {
            resultList.addAll(this.remove(each.next()));
        }
        return resultList;
    }

    // Method Update

    public final boolean updateAll() {
        this.notifyDataSetChanged();
        return true;
    }

    public final boolean updateAt(int index) {
        return this.updateAt(index, 1);
    }

    public final boolean updateAt(int indexStart, int itemCount) {
        this.notifyRangeChanged(indexStart, itemCount);
        return true;
    }

    public final boolean update(@Nullable T t) {
        return this.updateIf(Predicate.isEqual(t));
    }

    public final boolean updateIf(@NonNull Predicate<? super T> p) {
        boolean updated = false;
        final int N = this.size();
        for (int index = 0; index < N; index++) {
            final T t = this.findBy(index);
            if (p.test(t)) {
                this.notifyChanged(index);
                if (!updated) {
                    updated = true;
                }
            }
        }
        return updated;
    }

    public final boolean updateAll(@NonNull Collection<? extends T> c) {
        boolean updated = false;
        final Iterator<T> each = (Iterator<T>) c.iterator();
        while (each.hasNext()) {
            updated |= this.update(each.next());
        }
        return updated;
    }

    // Method Move

    public final boolean moveToHead(int index) {
        return this.moveAt(index, 0);
    }

    public final boolean moveToTail(int index) {
        return this.moveAt(index, this.size() - 1);
    }

    public final boolean moveAt(int fromIndex, int toIndex) {
        return this.moveAt(fromIndex, toIndex, 1);
    }

    public final boolean moveAt(int fromIndex, int toIndex, int itemCount) {
        final int N = this.size();
        toIndex = Math.max(0, Math.min(toIndex, N - 1));
        fromIndex = Math.max(0, Math.min(fromIndex, N - 1));
        if (fromIndex == toIndex) {
            return false;
        }
        if (itemCount != 1) {
            throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
        }
        final ArrayList<T> list = this.mDataSourceList;
        list.add(toIndex, list.remove(fromIndex));
        this.notifyRangeMoved(fromIndex, toIndex, itemCount);
        return true;
    }

    // Method Select

    @NonNull
    public final Iterator<T> iterator() {
        return this.findAll().iterator();
    }

    @NonNull
    public final Collection<T> findAll() {
        return this.mDataSourceList;
    }

    @Nullable
    public final <R extends T> R findHead() {
        return this.findBy(0);
    }

    @Nullable
    public final <R extends T> R findTail() {
        return this.findBy(this.size() - 1);
    }

    @Nullable
    public final <R extends T> R findBy(int index) {
        if (this.containsAt(index)) {
            return (R) this.mDataSourceList.get(index);
        }
        return null;
    }

    @NonNull
    public final <R extends T> R requireBy(int index) {
        final R t = this.findBy(index);
        if (t == null) {
            throw new NullPointerException("Index: " + index + ", Size: " + this.size());
        }
        return t;
    }

    @NonNull
    public final Collection<T> findIf(@NonNull Predicate<? super T> p) {
        final ArrayList<T> resultList = new ArrayList<>(this.size());
        final Iterator<T> each = this.iterator();
        while (each.hasNext()) {
            final T t = each.next();
            if (p.test(t)) {
                resultList.add(t);
            }
        }
        return resultList;
    }

    // Method Other

    public final boolean empty() {
        return this.size() == 0;
    }

    public final boolean containsAt(int index) {
        return index >= 0 && index < this.size();
    }

    public final boolean contains(@Nullable T t) {
        return this.containsIf(Predicate.isEqual(t));
    }

    public final boolean containsIf(@NonNull Predicate<? super T> p) {
        return this.indexIf(p) >= 0;
    }

    public final boolean containsAll(@NonNull Collection<? extends T> c) {
        final Iterator<T> each = (Iterator<T>) c.iterator();
        while (each.hasNext()) {
            final T t = each.next();
            if (!this.contains(t)) {
                return false;
            }
        }
        return true;
    }

    public final int size() {
        return this.findAll().size();
    }

    public final int indexOf(@Nullable T t) {
        return this.indexIf(Predicate.isEqual(t));
    }

    public final int indexIf(@NonNull Predicate<? super T> p) {
        final int N = this.size();
        for (int index = 0; index < N; index++) {
            final T t = this.findBy(index);
            if (p.test(t)) {
                return index;
            }
        }
        return ~0;
    }

    public final int lastIndexOf(@Nullable T t) {
        return this.lastIndexIf(Predicate.isEqual(t));
    }

    public final int lastIndexIf(@NonNull Predicate<? super T> p) {
        final int N = this.size();
        for (int index = N - 1; index >= 0; index--) {
            final T t = this.findBy(index);
            if (p.test(t)) {
                return index;
            }
        }
        return ~0;
    }

    public final void forEach(@NonNull Consumer<? super T> c) {
        final Iterator<T> each = this.iterator();
        while (each.hasNext()) {
            c.accept(each.next());
        }
    }

    private static final class DataObservable extends Observable<Observer> {
        public void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onChanged()) {
                    return;
                }
            }
        }

        public void notifyRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onRangeInserted(positionStart, itemCount)) {
                    return;
                }
            }
        }

        public void notifyRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onRangeRemoved(positionStart, itemCount)) {
                    return;
                }
            }
        }

        public void notifyRangeChanged(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onRangeChanged(positionStart, itemCount)) {
                    return;
                }
            }
        }

        public void notifyRangeMoved(int fromPosition, int toPosition, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                if (this.mObservers.get(i).onRangeMoved(fromPosition, toPosition, itemCount)) {
                    return;
                }
            }
        }
    }

    private static final class AdapterObserver implements Observer {
        @NonNull
        private final Adapter mAdapter;

        public AdapterObserver(@NonNull Adapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public boolean onChanged() {
            this.mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRangeInserted(int positionStart, int itemCount) {
            this.mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRangeRemoved(int positionStart, int itemCount) {
            this.mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRangeChanged(int positionStart, int itemCount) {
            this.mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRangeMoved(int fromPosition, int toPosition, int itemCount) {
            this.mAdapter.notifyDataSetChanged();
            return true;
        }
    }

    public interface Adapter {
        void notifyDataSetChanged();
    }

    public interface Observer {
        default boolean onChanged() {
            // nothing
            return false;
        }

        default boolean onRangeInserted(int positionStart, int itemCount) {
            // nothing
            return false;
        }

        default boolean onRangeRemoved(int positionStart, int itemCount) {
            // nothing
            return false;
        }

        default boolean onRangeChanged(int positionStart, int itemCount) {
            // nothing
            return false;
        }

        default boolean onRangeMoved(int fromPosition, int toPosition, int itemCount) {
            // nothing
            return false;
        }
    }

    public interface Consumer<T> {
        /**
         * Performs this operation on the given argument.
         *
         * @param t – the input argument.
         */
        void accept(@Nullable T t);

        @NonNull
        default Consumer<T> andThen(@NonNull Consumer<? super T> c) {
            return t -> {
                this.accept(t);
                // On After.
                c.accept(t);
            };
        }
    }

    public interface Predicate<T> {
        /**
         * Returns a predicate that tests if two arguments
         * are equal according to Objects.equals(Object, Object).
         *
         * @param t – the object reference with which to compare for
         *          equality, which may be null.
         */
        @NonNull
        static <T> Predicate<T> isEqual(@Nullable T t) {
            return o -> {
                if (t == null) {
                    if (o == null) return true;
                } else {
                    if (t.equals(o)) return true;
                }
                return false;
            };
        }

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param t – the input argument.
         * @return true if the input argument matches the predicate,
         * otherwise false.
         */
        boolean test(@Nullable T t);

        @NonNull
        default Predicate<T> and(@NonNull Predicate<? super T> p) {
            return t -> this.test(t) && p.test(t);
        }

        @NonNull
        default Predicate<T> or(@NonNull Predicate<? super T> p) {
            return t -> this.test(t) || p.test(t);
        }

        @NonNull
        default Predicate<T> negate() {
            return t -> !this.test(t);
        }

        @NonNull
        default Predicate<T> nonNull() {
            return t -> t != null && this.test(t);
        }

        @NonNull
        default Predicate<T> not(@NonNull Predicate<? super T> p) {
            return (Predicate<T>) p.negate();
        }
    }
}
