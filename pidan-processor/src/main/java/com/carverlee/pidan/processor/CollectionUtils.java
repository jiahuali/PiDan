package com.carverlee.pidan.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author carverLee
 * 2019/7/22 14:27
 */
public class CollectionUtils {
    private CollectionUtils() {

    }

    public static <T> T findItem(Collection<T> data, Query<T> query) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        for (T item : data) {
            if (query.match(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> List<T> findItemList(Collection<T> data, Query<T> query) {
        List<T> result = new ArrayList<>();
        if (data == null || data.isEmpty()) {
            return result;
        }
        for (T item : data) {
            if (query.match(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> void doForEach(Collection<T> data, Action<T> action) {
        if (data == null || data.isEmpty()) {
            return;
        }
        for (T item : data) {
            if (item != null) {
                action.action(item);
            }
        }
    }

    public static <T> void copy(Collection<T> src, Collection<T> dest, Query<T> filter) {
        if (src == null || src.isEmpty() || dest == null) {
            return;
        }
        for (T item : src) {
            if (filter.match(item)) {
                dest.add(item);
            }
        }
    }

    public interface Query<T> {
        boolean match(T data);
    }

    public interface Action<T> {
        void action(T data);
    }

}
