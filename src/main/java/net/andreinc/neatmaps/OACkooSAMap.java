package net.andreinc.neatmaps;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OACkooSAMap<K,V> implements Map<K, V> {

    protected static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
    protected static final int DEFAULT_SIZE_POWER_OF_TWO = 6;
    protected static final int DEFAULT_CAPACITY_SEGMENT = 1 << DEFAULT_SIZE_POWER_OF_TWO;
    protected static final int DEFAULT_CAPACITY_BUCKETS = DEFAULT_CAPACITY_SEGMENT << 1;
    protected static final int DEFAULT_MAX_SWAPPING = DEFAULT_CAPACITY_SEGMENT >> 3;

    protected int size = 0;
    protected int offset = DEFAULT_CAPACITY_SEGMENT;
    protected int capacityBuckets = DEFAULT_CAPACITY_BUCKETS;
    protected int maxSwapping = DEFAULT_MAX_SWAPPING;

    public static int hash1(Object obj) {
        int hash = obj.hashCode();
        hash ^= (hash >> 16);
        hash *= 2654435769L;
        hash ^= hash >> 16;
        return hash & 0xfffffff;
    }

    public static int hash2(Object obj) {
        int hash = obj.hashCode();
        hash ^= (hash >> 16);
        hash *= 137438953471L;
        hash ^= hash >> 16;
        return hash & 0xfffffff;
    }

    protected Entry<K,V>[] buckets;

    public OACkooSAMap() {
        buckets = new Entry[DEFAULT_CAPACITY_BUCKETS];
    }

    @Override
    public V get(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash, idx;
        hash = hash1(key);
        idx = hash & (offset -1);
        if (null!=buckets[idx] && buckets[idx].hash1 == hash && key.equals(buckets[idx].key)) {
            return buckets[idx].value;
        }
        hash = hash2(key);
        idx = offset + (hash & (offset -1));
        if (null!=buckets[idx] && buckets[idx].hash2 == hash && key.equals(buckets[idx].key)) {
            return buckets[idx].value;
        }
        return null;
    }

    protected void resize() {
        offset <<=1;
        capacityBuckets <<=1;
        this.size=0;
        Entry<K,V>[] old = this.buckets;
        this.buckets = new Entry[capacityBuckets];
        for(int i = 0; i < old.length;i++) {
            if (old[i]!=null) {
                this.put(old[i].key, old[i].value);
            }
        }
    }

    @Override
    public V put(K key, V value) {

        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }

        if ((double)size/buckets.length>DEFAULT_MAX_LOAD_FACTOR) {
            resize();
        }

        int idx;
        int cHash1 = hash1(key), tHash1;
        int cHash2 = hash2(key), tHash2;
        K cKey = key, tKey;
        V cVal = value, tValue ;

        int swaps = maxSwapping;
        while(swaps-->0) {
            idx = cHash1 & (offset -1);
            if (buckets[idx]==null) {
                buckets[idx] = new Entry<>(cKey, cVal, cHash1, cHash2);
                size++;
                return null;
            }
            else if (buckets[idx].hash1 == cHash1 && key.equals(buckets[idx].key)) {
                V old = buckets[idx].value;
                buckets[idx].value = cVal;
                return old;
            }
            else {
                idx = (cHash2 & (offset-1)) + offset;
                if (buckets[idx]==null) {
                    buckets[idx] = new Entry<>(cKey, cVal, cHash1, cHash2);
                    size++;
                    return null;
                }
                else if (buckets[idx].hash2 == cHash2 && key.equals(buckets[idx].key)) {
                    V old = buckets[idx].value;
                    buckets[idx].value = cVal;
                    return old;
                }
                tKey = buckets[idx].key;
                tValue = buckets[idx].value;
                tHash1 = buckets[idx].hash1;
                tHash2 = buckets[idx].hash2;
                buckets[idx].key = cKey;
                buckets[idx].value = cVal;
                buckets[idx].hash1 = cHash1;
                buckets[idx].hash2 = cHash2;
                cKey = tKey;
                cVal = tValue;
                cHash1 = tHash1;
                cHash2 = tHash2;
            }
        }
        // The insert loop has been broken, we resize the hash table was resized accordingly
        // We put the key again, after the hash table was resized
        resize();
        return put(key,value);
    }

    @Override
    public V remove(Object key) {

        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }

        int hash, idx;
        hash = hash1(key);
        idx = hash & (offset-1);
        if (null!=buckets[idx] && buckets[idx].hash1 == hash && key.equals(buckets[idx].key)) {
            V old = buckets[idx].value;
            buckets[idx] = null;
            size--;
            return old;
        }
        hash = hash2(key);
        idx = offset + (hash & (offset-1));
        if (null!=buckets[idx] && buckets[idx].hash2 == hash && key.equals(buckets[idx].key)) {
            V old = buckets[idx].value;
            buckets[idx] = null;
            size--;
            return old;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key)!=null;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null && value.equals(buckets[i].value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.offset = DEFAULT_CAPACITY_SEGMENT;
        this.capacityBuckets = DEFAULT_CAPACITY_BUCKETS;
        this.maxSwapping = DEFAULT_MAX_SWAPPING;
        this.buckets = new Entry[capacityBuckets];
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                set.add(buckets[i].key);
            }
        }
        return set;
    }

    @Override
    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                list.add(buckets[i].value);
            }
        }
        return list;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                set.add(buckets[i]);
            }
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OACkooSAMap)) return false;
        OACkooSAMap<?, ?> that = (OACkooSAMap<?, ?>) o;
        return size == that.size &&
                offset == that.offset &&
                capacityBuckets == that.capacityBuckets &&
                maxSwapping == that.maxSwapping &&
                Arrays.equals(buckets, that.buckets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, offset, capacityBuckets, maxSwapping);
        result = 31 * result + Arrays.hashCode(buckets);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < buckets.length; i++) {
            buff.append(buckets[i]).append("\n");
        }

        return buff.toString();
    }

    protected static class Entry<K,V> implements Map.Entry<K, V> {

        protected K key;
        protected V value;
        protected int hash1;
        protected int hash2;

        public Entry(K key, V value, int hash1, int hash2) {
            this.key = key;
            this.value = value;
            this.hash1 = hash1;
            this.hash2 = hash2;
        }

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }

        @Override
        public V setValue(V value) {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    ", hash1=" + hash1 +
                    ", hash2=" + hash2 +
                    '}';
        }
    }
}
