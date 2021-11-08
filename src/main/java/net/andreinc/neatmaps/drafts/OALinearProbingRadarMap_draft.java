package net.andreinc.neatmaps.drafts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class OALinearProbingRadarMap_draft<K, V> implements Map<K,V> {

    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
    private static final int DEFAULT_MAP_CAPACITY_POW_2 = 6;
    private static final short DEFAULT_MAX_PROBING = 1 << 5;

    private int size = 0;
    private int tombstones = 0;
    private int cI = DEFAULT_MAP_CAPACITY_POW_2;
    public OaEntry<K, V>[] buckets = new OaEntry[1<< DEFAULT_MAP_CAPACITY_POW_2];

    public static String toBinary(int n, int len)
    {
        String binary = "";
        for (long i = (1L << len - 1); i > 0; i = i / 2) {
            binary += (n & i) != 0 ? "1" : "0";
        }
        return binary;
    }

    /**
     * A method that mixes even further the bits of the resulting obj.hashCode().
     * Inspired by the Murmur Hash mixer.
     *
     * @param obj The object for which we are computing the hashcode
     * @return The hashcode of the object.
     */
    public static int hash(final Object obj) {
        int h = obj.hashCode();
        h ^= h >> 16;
        h *= 0x3243f6a9;
        h ^= h >> 16;
        return h & 0xfffffff;
    }

    /**
     * A method that computes the load factor. It takes tombstones into consideration.
     *
     * @return
     */
    private double loadFactor() {  return ((double) (size+tombstones)) / buckets.length; }

    private boolean shouldGrow() {
        return loadFactor() > DEFAULT_MAX_LOAD_FACTOR;
    }

    private final void grow() {
        OaEntry<K,V>[] oldBuckets = this.buckets;
        this.cI++;
        this.buckets = new OaEntry[1<<cI];
        this.size=0;
        this.tombstones=0;
        for(int i = 0; i < oldBuckets.length; ++i) {
            if (null!=oldBuckets[i] && oldBuckets[i].key!=null) {
                this.put(oldBuckets[i].key, oldBuckets[i].val, oldBuckets[i].hash);
            }
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
        return get(key)!=null;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < buckets.length; i++) {
            if (null!=buckets[i]) {
                if (value.equals(buckets[i].getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public V get(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = hash & (buckets.length-1);
        if (buckets[idx]==null) {
            return null;
        }
        int radar = buckets[idx].radar;
        for(int bit = 0; bit < 32; bit++) {
            if (((radar>>bit)&1)==1 && buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                return buckets[idx].val;
            }
            idx++;
            if (idx == buckets.length) idx = 0;
        }
        return null;
    }

    protected V put(K key, V value, int hash) {
        if (shouldGrow()) {
            grow();
        }
        int idx = hash & (buckets.length-1);
        int base = idx;
        int probing = 0;
        while(true) {
            if (probing==32) {
                grow();
                return put(key, value, hash);
            }
            if (buckets[idx] == null) {
                // It's a free spot
                buckets[idx] = new OaEntry(key, value, hash);
                buckets[base].radar |= (1 << probing);
                size++;
                return null;
            }
            else if (buckets[idx].key == null) {
                // It's a tombstone
                buckets[idx].key = key;
                buckets[idx].val = value;
                buckets[idx].hash = hash;
                buckets[base].radar |= (1 << probing);
            }
            else if (buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                // We perform an update on the element
                V ret;
                ret = buckets[idx].val;
                buckets[idx].key = key;
                buckets[idx].hash = hash;
                buckets[idx].val = value;
                return ret;
            }
            probing++;
            idx++;
            if (buckets.length==idx) idx = 0;
        }
    }

    @Override
    public V put(K key, V value) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        return put(key, value, hash(key));
    }

    @Override
    public V remove(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        //TODO shrink
        int hash = hash(key);
        int idx = hash & (buckets.length-1);
        if (null!=buckets[idx]) {
            do {
                if (buckets[idx].hash == hash && key.equals(buckets[idx].key))
                    break;
                idx++;
                if (idx == buckets.length) idx = 0;
            } while (null != buckets[idx]);
        }
        V oldVal = null;
        if (null!=buckets[idx]) {
            oldVal = buckets[idx].val;
            buckets[idx].key = null;
            buckets[idx].val = null;
            buckets[idx].hash = 0;
            tombstones++;
            size--;
        }
        return oldVal;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(var e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        buckets = new OaEntry[1<< DEFAULT_MAP_CAPACITY_POW_2];
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> result = new HashSet<>();
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                result.add(buckets[i].key);
            }
        }
        return result;
    }

    @Override
    public Collection<V> values() {
        HashSet<V> result = new HashSet<>();
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                result.add(buckets[i].val);
            }
        }
        return result;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        HashSet<Entry<K, V>> result = new HashSet<>();
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]!=null) {
                result.add(buckets[i]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < buckets.length; ++i) {
            buff.append(format("bucket[%d] = ", i));
            if (null == buckets[i]) {
                buff.append("NULL\n");
            }
            else if (buckets[i].key==null) {
                buff.append("TOMBSTONE\n");
            } else {
                buff.append(format(" {hash=%d, key=%s, value=%s, radar=%s }\n",
                        buckets[i].hash, buckets[i].key, buckets[i].val, toBinary(buckets[i].radar, 32)));
            }
        }
        return buff.toString();
    }

    protected static class OaEntry<K, V> implements Entry<K, V> {

        public K key;
        public V val;
        public int hash;
        public int radar;

        public OaEntry(K key, V val, int hash) {
            this.key = key;
            this.val = val;
            this.hash = hash;
            this.radar = 0;
        }

        @Override
        public String toString() {
            return "OaEntry{" +
                    "key=" + key +
                    ", val=" + val +
                    ", hash=" + hash +
                    ", radar=" + Integer.toBinaryString(radar) +
                    '}';
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return val;
        }

        @Override
        public V setValue(V value) {
            V oldVal = this.val;
            this.val = value;
            return oldVal;
        }
    }

    public static void main(String[] args) {
        int size = 100;
        OALinearProbingRadarMap_draft rm = new OALinearProbingRadarMap_draft();
        for (int i = 0; i < size; i++) {
            rm.put(i, i);
        }
        for (int i = 0; i < size; i++) {
            System.out.println(rm.get(i));
        }
        System.out.println(rm);
    }
}
