package net.andreinc.neatmaps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class OaMapLP_noMixer<K, V> implements Map<K,V> {

    private static final double OA_MAP_LOAD_FACTOR = 0.6;
    private static final int OA_MAP_CAPACITY_I = 12;
    private static final int OA_MAP_MAX_PROBING = 1<<5;
    private static final int OA_PERTURB_SHIFT = 6;

    private int size = 0;
    private int tombstones = 0;
    private int cI = OA_MAP_CAPACITY_I;
    private OaEntry<K, V> buckets[] = new OaEntry[1<<OA_MAP_CAPACITY_I];

    // INTERNAL METHODS

    private static final int hash32(final Object obj) {
        return obj.hashCode();
    }

    private final boolean shouldGrow() {
        return ((double) (size+tombstones)) / buckets.length > OA_MAP_LOAD_FACTOR;
    }

    private final void grow() {
        OaEntry<K,V> oldBuckets[] = this.buckets;
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

    @Override
    public V get(Object key) {
        int hash = hash32(key);
        int idx = hash & (buckets.length-1);
        if (null != buckets[idx]) {
            int perturb = hash;
            do {
                if (buckets[idx].hash == hash && buckets[idx].key.equals(key))
                        break;
                idx++;
                if (idx == buckets.length) idx = 0;
            } while (null != buckets[idx]);
        }
        return buckets[idx] == null ? null : buckets[idx].val;
    }

    protected V put(K key, V value, int hash) {
        if (shouldGrow()) {
            grow();
        }
        int probing = 0;
        int idx = hash & (buckets.length-1);
        if (null != buckets[idx]) {
            do {
                if (buckets[idx].key == null)
                    break;
                if (buckets[idx].hash == hash && buckets[idx].key.equals(key))
                        break;
                probing++;
                idx++;
                if (idx == buckets.length) idx = 0;
            } while (null != buckets[idx]);
        }
        V ret = null;
        size++;
        if (null==buckets[idx]) {
            buckets[idx] = OaEntry.createEntry(key, value, hash);
        } else {
            ret = buckets[idx].val;
            buckets[idx].key = key;
            buckets[idx].hash = hash;
            buckets[idx].val = value;
        }
        if (probing>OA_MAP_MAX_PROBING) {
//            System.out.println("probing: " + probing);
            grow();
        }
        return ret;
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, hash32(key));
    }

    @Override
    public V remove(Object key) {
        //TODO shrink
        int hash = hash32(key);
        int idx = hash & (buckets.length-1);
        if (null!=buckets[idx]) {
            int perturb = hash;
            do {
                if (buckets[idx].key == null)
                    break;
                if (buckets[idx].hash == hash && buckets[idx].key.equals(key))
                        break;
                idx++;
                if (idx == buckets.length) idx = 0;
            } while (null != buckets[idx]);
        }
        OaEntry<K,V> oldEntry = buckets[idx];
        if (null!=buckets[idx]) {
            buckets[idx].key = null;
            buckets[idx].val = null;
            buckets[idx].hash = 0;
            tombstones++;
            size--;
        }
        return oldEntry == null ? null : oldEntry.val;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(var e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        buckets = new OaEntry[1<<OA_MAP_CAPACITY_I];
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
                buff.append(format(" {hash=%d, key=%s, value=%s }\n",
                        buckets[i].hash, buckets[i].key, buckets[i].val));
            }
        }
        return buff.toString();
    }

    public static class OaEntry<K, V> implements Entry<K, V> {

        private K key;
        private V val;
        private int hash;

        public static <K, V> OaEntry<K, V> createEntry(K key, V val, int hash) {
            OaEntry<K,V> result = new OaEntry<>();
            result.key = key;
            result.val = val;
            result.hash = hash;
            return result;
        }

        @Override
        public String toString() {
            return "OaEntry{" +
                    "key=" + key +
                    ", val=" + val +
                    ", hash=" + hash +
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
}