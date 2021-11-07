package net.andreinc.neatmaps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OAHopscotchMap_draft<K,V> implements Map<K,V> {

    protected static final int DEFAULT_SEGMENT_CAPACITY_POW_2 = 5;
    protected static final int DEFAULT_SEGMENT_CAPACITY = 1 << DEFAULT_SEGMENT_CAPACITY_POW_2;
    protected static final int DEFAULT_CAPACITY_POW_2 = 6;
    protected static final int DEFAULT_PERCEIVED_CAPACITY = 1 << DEFAULT_CAPACITY_POW_2;
    protected static final int DEFAULT_CAPACITY = DEFAULT_PERCEIVED_CAPACITY << DEFAULT_SEGMENT_CAPACITY_POW_2;

    protected int size = 0;
    protected int realCapacity = DEFAULT_CAPACITY;
    protected int perceivedCapacity = DEFAULT_PERCEIVED_CAPACITY;
    protected int segmentCapacity = DEFAULT_SEGMENT_CAPACITY;

    private static final int hash(final Object obj) {
        int h = obj.hashCode();
        h ^= h >> 16;
        h *= 0x3243f6a9;
        h ^= h >> 16;
        return h & 0xfffffff;
    }

    protected Entry<K,V>[] buckets = new Entry[realCapacity];

    public static void main(String[] args) {
        int size = 100;
        OAHopscotchMap_draft<String,String> m = new OAHopscotchMap_draft<>();
        for (int i = 0; i < size; i++) {
            m.put(i+"", i+"");
        }
        System.out.println("size=" + m.buckets.length);
        for (int i = 0; i < size; i++) {
            if (m.get(i+"") == null) {
                System.err.println(i);
                System.err.println(m.get(i+""));
            }
            else {
                System.out.println(m.get(i+"") );
            }
        }
    }

    @Override
    public V get(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = (hash & (perceivedCapacity-1)) << DEFAULT_SEGMENT_CAPACITY_POW_2;
        for (int i = 0; i < segmentCapacity; i++, idx++) {
            if (buckets[idx]!=null && buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                return buckets[idx].val;
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        return put(key, value, hash(key));
    }

    protected V put(K key, V value, int hash) {
        int idx = (hash & (perceivedCapacity-1)) << DEFAULT_SEGMENT_CAPACITY_POW_2;
        for(int i = 0; i < segmentCapacity; i++, idx++) {
            if (buckets[idx]==null) {
                buckets[idx] = new Entry<>(key, value, hash);
                size++;
                return null;
            }
            else if (buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                V old = buckets[idx].val;
                buckets[idx].val = value;
                return old;
            }
        }
        resize();
        return put(key, value);
    }

    public void resize() {
        this.size = 0;
        this.perceivedCapacity <<= 1;
        this.realCapacity = perceivedCapacity << DEFAULT_SEGMENT_CAPACITY_POW_2;
        Entry<K,V>[] old = this.buckets;
        this.buckets = new Entry[realCapacity];
        for (int i = 0; i < old.length; i++) {
            if (old[i]!=null) {
                this.put(old[i].key, old[i].val, old[i].hash);
            }
        }
    }

    @Override
    public V remove(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = (hash & (perceivedCapacity-1)) << segmentCapacity;
        for(int i = idx; i < segmentCapacity; i++) {
            if (buckets[i] == null) {
                continue;
            }
            else if (buckets[i].hash == hash && key.equals(buckets[i].key)) {
                V old = buckets[idx].val;
                buckets[i] = null;
                return old;
            }
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
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < buckets.length; i++) {
            buff.append(buckets[i]).append("\n");
        }

        return buff.toString();
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V val;
        private int hash;

        public Entry(K key, V val, int hash) {
            this.key = key;
            this.val = val;
            this.hash = hash;
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
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", val=" + val +
                    ", hash=" + hash +
                    '}';
        }
    }
}
