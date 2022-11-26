package cn.smilex.vueblog.util.impl;

import cn.smilex.vueblog.util.MapBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author smilex
 */
public class HashMapBuilder<K, V> implements MapBuilder<HashMap<K, V>, K, V> {
    private final HashMap<K, V> map;

    public HashMapBuilder() {
        this.map = new HashMap<>();
    }

    public HashMapBuilder(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    @Override
    public MapBuilder<HashMap<K, V>, K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    @Override
    public Map<K, V> getMap() {
        return this.map;
    }
}
