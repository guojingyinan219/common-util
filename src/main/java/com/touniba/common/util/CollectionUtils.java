package com.touniba.common.util;


import com.jdddata.common.function.BiFunction;
import com.jdddata.common.function.Function;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Magoo on 2016/12/7.
 */
public class CollectionUtils {
    private CollectionUtils() {
    } // Private

    /**
     * Represent for java.util.Map.computeIfAbsent, allow throw exception.
     *
     * @param map
     * @param key
     * @param function
     * @param <K>
     * @param <V>
     * @return
     * @throws Exception
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> function) throws Exception {
        Objects.requireNonNull(function);
        V v;
        if ((v = map.get(key)) == null) {
            V newValue;
            if ((newValue = function.apply(key)) != null) {
                map.put(key, newValue);
                return newValue;
            }
        }
        return v;
    }

    /**
     * Represent for java.util.Map.computeIfPresent, allow throw exception.
     *
     * @param map
     * @param key
     * @param function
     * @param <K>
     * @param <V>
     * @return
     * @throws Exception
     */
    public static <K, V> V computeIfPresent(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> function) throws Exception {
        Objects.requireNonNull(function);
        V oldValue;
        if ((oldValue = map.get(key)) != null) {
            V newValue = function.apply(key, oldValue);
            if (newValue != null) {
                map.put(key, newValue);
                return newValue;
            } else {
                map.remove(key);
                return null;
            }
        } else {
            return null;
        }
    }
}
