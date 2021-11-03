package net.andreinc.neatmaps;

import net.andreinc.mockneat.abstraction.MockUnitString;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.String.format;
import static net.andreinc.mockneat.unit.address.Addresses.addresses;
import static net.andreinc.mockneat.unit.misc.Cars.cars;
import static net.andreinc.mockneat.unit.objects.Probabilities.probabilities;
import static net.andreinc.mockneat.unit.text.Words.words;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

public class OALinearProbingMap<K, V> implements Map<K,V> {

    private static final double OA_MAX_LOAD_FACTOR = 0.7;
    private static final double OA_MIN_LOAD_FACTOR_WITH_MAX_PROBING = 0.5;
    private static final int OA_MAP_CAPACITY_POW_2 = 5;
    private static final int OA_MAP_MAX_PROBING = 1<<5;

    private int size = 0;
    private int tombstones = 0;
    private int cI = OA_MAP_CAPACITY_POW_2;
    public OaEntry<K, V> buckets[] = new OaEntry[1<< OA_MAP_CAPACITY_POW_2];

    /**
     * A method that mixes even further the bits of the resulting obj.hashCode().
     * Inspired by the Murmur Hash mixer.
     *
     * @param obj The object for which we are computing the hashcode
     * @return The hashcode of the object.
     */
    private static final int hash(final Object obj) {
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
    private final double loadFactor() {  return ((double) (size+tombstones)) / buckets.length; }

    private final boolean shouldGrow() {
        return loadFactor() > OA_MAX_LOAD_FACTOR;
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
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = hash & (buckets.length-1);
        if (null != buckets[idx]) {
            do {
                if (buckets[idx].hash == hash && key.equals(buckets[idx].key)) {
                    return buckets[idx].val;
                }
                idx++;
                if (idx == buckets.length) idx = 0;
            } while (null != buckets[idx]);
        }
        return null;
    }

    protected V put(K key, V value, int hash) {
        if (shouldGrow()) {
            grow();
        }
        int probing = 0;
        int idx = hash & (buckets.length-1);
        if (null != buckets[idx]) {
            int perturb = hash;
            do {
                if (buckets[idx].key == null)
                    break;
                if (buckets[idx].hash == hash && key.equals(buckets[idx].key))
                    break;
                probing++;
                idx++;
                if(idx==buckets.length) idx=0;
            } while (null != buckets[idx]);
        }
        V ret = null;
        if (null==buckets[idx]) {
            buckets[idx] = OaEntry.createEntry(key, value, hash);
            size++;
        } else {
            ret = buckets[idx].val;
            buckets[idx].key = key;
            buckets[idx].hash = hash;
            buckets[idx].val = value;
        }
//        if (probing>OA_MAP_MAX_PROBING && loadFactor()>OA_MIN_LOAD_FACTOR_WITH_MAX_PROBING) {
//            grow();
//        }
        return ret;
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
        buckets = new OaEntry[1<< OA_MAP_CAPACITY_POW_2];
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

        public K key;
        public V val;
        public int hash;

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

    public static void main(String[] args) {
        final int size=900000;

        MockUnitString keyGenerator =
                probabilities(String.class)
                        .add(0.2, names().full())
                        .add(0.2, addresses())
                        .add(0.2, words())
                        .add(0.2, cars())
                        .add(0.2, ints().mapToString())
                        .mapToString();

        OALinearProbingMap<String, Integer> oaRobinHoodMap = new OALinearProbingMap<>();
        List<String> keyList = keyGenerator.list(size).get();


        for(int i = 0; i < size; i++) {
            oaRobinHoodMap.put(keyList.get(i), i);
        }

        for(int i = 0; i < size; i++) {
            if (oaRobinHoodMap.get(keyList.get(i))==null) {
                System.err.println(">>" + keyList.get(i));
                break;
            }
        }

        System.out.println(oaRobinHoodMap.size() + "=size");
        System.out.println(new HashSet<>(keyList).size() + "=size");
    }
}
