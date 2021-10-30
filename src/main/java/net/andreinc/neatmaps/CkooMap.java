package net.andreinc.neatmaps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CkooMap<K,V> implements Map<K,V> {

    public static void main(String[] args) {
        CkooMap<String, String> map = new CkooMap<>();
        for(int i = 0; i < 1000000; i++) {
            map.put(i+"", "");
        }
        for(int i = 0; i < 1000000; i++) {
            if (null==map.get(i+"")) {
                throw new IllegalStateException("cannot find " + i);
            }
        }
        System.out.println("nd1 = " + map.nullDensity1());
        System.out.println("nd2 = " + map.nullDensity2());
        System.out.println("stash.size" + map.stash.size());
        System.out.println("map.size" + map.size);
    }

    protected static final int DEFAULT_MAX_SWAPS = 16;
    protected static final int DEFAULT_CAPACITY_POW2 = 21;
    protected static final int DEFAULT_CAPACITY = 1 << DEFAULT_CAPACITY_POW2;
    protected static final int DEFAULT_MAX_STASH_CAPACITY = 1 << 9;

    protected int size = 0;
    protected int maxStashCapacity = DEFAULT_MAX_STASH_CAPACITY;
    protected CkooEntry<K,V>[] buckets1 = new CkooEntry[DEFAULT_CAPACITY];
    protected CkooEntry<K,V>[] buckets2 = new CkooEntry[DEFAULT_CAPACITY];

    protected final Map<K,V> stash = new HashMap<>();

    protected CkooEntry<K,V> move() {
        return null;
    }

    private static final double nullDensity(CkooEntry[] buckets) {
        double nulls=0;
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i]==null) nulls++;
        }
        return nulls/buckets.length*100.0;
    }

    public double nullDensity1() {
        return nullDensity(buckets1);
    }

    public double nullDensity2() {
        return nullDensity(buckets2);
    }

    protected final static int hash1(Object obj) {
        int hash = obj.hashCode();
        hash ^= (hash >> 16);
        hash *= 13;
        return hash & 0xfffffff;
    }

    protected final static int hash2(Object obj) {
        int hash = obj.hashCode();
        hash ^= (hash >> 16);
        hash *= 999613;
        return hash & 0xfffffff;
    }

    protected static class CkooEntry<K,V> {
        int hash1;
        int hash2;
        K key;
        V value;
        public CkooEntry(int hash1, int hash2, K key, V value) {
            this.hash1 = hash1;
            this.hash2 = hash2;
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        int hash, idx;
        // BUCKET 1
        hash = hash1(key);
        idx = hash & (buckets1.length-1);
        if (buckets1[idx]!=null)
            if (buckets1[idx].hash1==hash)
                if(buckets1[idx].key.equals(key))
                    return buckets1[idx].value;

        // BUCKET 2
        hash = hash2(key);
        idx = hash & (buckets2.length-1);
        if (buckets2[idx]!=null)
            if (buckets2[idx].hash2==hash)
                if (buckets2[idx].key.equals(key))
                    return buckets2[idx].value;

        return stash.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        int hash, idx;
        size++;
        // BUCKET 1
        hash = hash1(key);
        idx = hash & (buckets1.length-1);
        if (buckets1[idx]==null) {
            buckets1[idx] = new CkooEntry<>(hash, 0, key, value);
            return null;
        }

        hash = hash2(key);
        idx = hash & (buckets2.length-1);
        if (buckets2[idx]==null) {
            buckets2[idx] = new CkooEntry<>(0, hash, key, value);
            return null;
        }

        stash.put(key, value);

        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return null;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public Map<K, V> getStash() {
        return stash;
    }
}
