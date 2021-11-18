package net.andreinc.neatmaps;

import java.util.*;

import static java.lang.String.format;

public class RobinHoodMap<K, V> implements Map<K,V> {

    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
    private static final double DEFAULT_MIN_LOAD_FACTOR = DEFAULT_MAX_LOAD_FACTOR / 4;
    private static final int DEFAULT_MAP_CAPACITY_POW_2 = 6;

    private int size = 0;
    private int tombstones = 0;
    private int capPow2 = DEFAULT_MAP_CAPACITY_POW_2;
    private Entry<K, V> buckets[] = new Entry[1 << DEFAULT_MAP_CAPACITY_POW_2];

    // INTERNAL METHODS

    private static final int hash(final Object obj) {
        int h = obj.hashCode();
        h ^= h >> 16;
        h *= 0x3243f6a9;
        h ^= h >> 16;
        return h & 0xfffffff;
    }

    protected final void reHashElements(int capModifier) {
        this.capPow2 +=capModifier;
        Entry<K, V>[] oldBuckets = this.buckets;
        this.buckets = new Entry[1 << capPow2];
        this.size = 0;
        this.tombstones = 0;
        for (int i = 0; i < oldBuckets.length; ++i) {
            if (null != oldBuckets[i] && oldBuckets[i].key != null) {
                this.put(oldBuckets[i].key, oldBuckets[i].value, oldBuckets[i].hash);
            }
        }
    }

    protected final void increaseCapacity() {
        final double lf = (double)(size+tombstones) / buckets.length;
        if (lf > DEFAULT_MAX_LOAD_FACTOR) {
            reHashElements(1);
        }
    }

    protected final void decreaseCapacity() {
        final double lf = (double)(size) / buckets.length;
        if (lf < DEFAULT_MIN_LOAD_FACTOR && this.capPow2 > DEFAULT_MAP_CAPACITY_POW_2) {
            reHashElements(-1);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < buckets.length; i++) {
            if (null != buckets[i]) {
                if (value.equals(buckets[i].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = hash & (buckets.length-1);
        if (buckets[idx]==null) {
            return null;
        }
        do {
            if (buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                return buckets[idx].value;
            }
            idx++;
            if (idx==buckets.length) idx = 0;
        } while(null!=buckets[idx]);
        return null;
    }

    protected V put(K key, V value, int hash) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        increaseCapacity();
        K cKey = key;
        V cVal = value;
        int cHash = hash;
        V old = null;
        int probing = 0;
        int idx = hash & (buckets.length - 1);
        while (true){
            if (null == buckets[idx]) {
                buckets[idx] = new Entry<>(cKey, cVal, cHash, probing);
                this.size++;
                break;
            } else if (hash == buckets[idx].hash && key.equals(buckets[idx].key)) {
                old = buckets[idx].value;
                buckets[idx].value = value;
                buckets[idx].dist = probing;
                break;
            } else if (probing > buckets[idx].dist) {
                K tmpKey;
                V tmpVal;
                int tmpHash;
                int tmpDist;
                tmpHash = buckets[idx].hash;
                tmpVal = buckets[idx].value;
                tmpDist = buckets[idx].dist;
                tmpKey = buckets[idx].key;
                buckets[idx].hash = cHash;
                buckets[idx].value = cVal;
                buckets[idx].key = cKey;
                buckets[idx].dist = probing;
                cHash = tmpHash;
                cVal = tmpVal;
                cKey = tmpKey;
                probing = tmpDist;
            }
            probing++;
            idx++;
            if (idx == buckets.length) idx = 0;
        }
        return old;
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, hash(key));
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key);
        int idx = hash & (buckets.length - 1);
        if (buckets[idx]==null) {
            return null;
        }
        do {
            if (buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                V oldVal = buckets[idx].value;
                buckets[idx].key = null;
                buckets[idx].value = null;
                buckets[idx].hash = 0;
                tombstones++;
                size--;
                return oldVal;
            }
            idx++;
            if (idx == buckets.length) idx = 0;
        } while (null != buckets[idx]);
        decreaseCapacity();
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        buckets = new Entry[1 << DEFAULT_MAP_CAPACITY_POW_2];
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> result = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                result.add(buckets[i].key);
            }
        }
        return result;
    }

    @Override
    public Collection<V> values() {
        HashSet<V> result = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                result.add(buckets[i].value);
            }
        }
        return result;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> result = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                result.add(buckets[i]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < buckets.length; ++i) {
            buff.append(format("bucket[%d] = ", i));
            if (null == buckets[i]) {
                buff.append("NULL\n");
            } else if (buckets[i].key == null) {
                buff.append("TOMBSTONE\n");
            } else {
                buff.append(format(" {hash=%d, key=%s, value=%s dist=%d}\n",
                        buckets[i].hash, buckets[i].key, buckets[i].value, buckets[i].dist));
            }
        }
        return buff.toString();
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;
        private int hash;
        private int dist;

        public Entry(K key, V val, int hash, int dist) {
            this.key = key;
            this.value = val;
            this.hash = hash;
            this.dist = dist;
        }

        @Override
        public String toString() {
            return "OaEntry{" +
                    "key=" + key +
                    ", val=" + value +
                    ", hash=" + hash +
                    ", dist=" + dist +
                    '}';
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldVal = this.value;
            this.value = value;
            return oldVal;
        }
    }
}
