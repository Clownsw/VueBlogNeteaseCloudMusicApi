package cn.smilex.vueblog.util;

import java.util.Map;

/**
 * @author smilex
 */
public interface MapBuilder<MapImpl extends Map<K, V>, K, V> {
    MapBuilder<MapImpl, K, V> put(K key, V value);

    Map<K, V> getMap();
}
