package net.andreinc.neatmaps;

import java.util.*;

import static java.lang.String.format;

// contribution by dzaima (https://gist.github.com/dzaima)
// https://gist.github.com/dzaima/be4ef7efeda45a2930b1fb093f871c43

@SuppressWarnings("unchecked")
public class LProbSOAMap<K, V> implements Map<K,V> {

    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
    private static final double DEFAULT_MIN_LOAD_FACTOR = DEFAULT_MAX_LOAD_FACTOR / 4;
    private static final int DEFAULT_MAP_CAPACITY_POW_2 = 6;

    private int size = 0;
    private int tombstones = 0;
    private int capPow2 = DEFAULT_MAP_CAPACITY_POW_2;

    public K[] bucketsK = (K[]) new Object[1<<DEFAULT_MAP_CAPACITY_POW_2];
    public V[] bucketsV = (V[]) new Object[1<<DEFAULT_MAP_CAPACITY_POW_2];
    public int[] bucketsH = new int[1<<DEFAULT_MAP_CAPACITY_POW_2]; // k=null: hash=0 - empty; hash=1 - tombstone

    /**
     * A method that mixes even further the bits of the resulting obj.hashCode().
     * Inspired by the Murmur Hash mixer (finalizer).
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

    protected final void reHashElements(int capModifier) {
        this.capPow2+=capModifier;
        K[] oldBucketsK = this.bucketsK;
        V[] oldBucketsV = this.bucketsV;
        int[] oldBucketsH = this.bucketsH;
        this.bucketsK = (K[]) new Object[1 << capPow2];
        this.bucketsV = (V[]) new Object[1 << capPow2];
        this.bucketsH = new int[1 << capPow2];
        this.size = 0;
        this.tombstones = 0;
        for (int i = 0; i < oldBucketsK.length; ++i) {
            if (oldBucketsK[i] != null) {
                this.put(oldBucketsK[i], oldBucketsV[i], oldBucketsH[i]);
            }
        }
    }

    protected final void increaseCapacity() {
        final double lf = (double)(size+tombstones) / bucketsK.length;
        if (lf > DEFAULT_MAX_LOAD_FACTOR) {
            reHashElements(1);
        }
    }

    protected final void decreaseCapacity() {
        final double lf = (double)(size) / bucketsK.length;
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
        return get(key)!=null;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < bucketsK.length; i++) {
            if (value.equals(bucketsV[i])) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key) {
        int hash = hash(key);
        int idx = hash & (bucketsK.length-1);
        int bucketH = bucketsH[idx];
        K bucketK = bucketsK[idx];
        if (bucketK==null && bucketH==0) {
            return null;
        }
        do {
            if (bucketH == hash && key.equals(bucketK)) {
                return bucketsV[idx];
            }
            idx++;
            if (idx==bucketsK.length) idx = 0;
            bucketK = bucketsK[idx];
            bucketH = bucketsH[idx];
        } while(!(bucketK==null && bucketH==0));
        return null;
    }

    protected V put(K key, V value, int hash) {
        // We increase capacity if it's needed
        increaseCapacity();
        // We calculate the base bucket for the entry
        int idx = hash & (bucketsK.length-1);
        while(true) {
            // If the slot is either empty or a tombstone, we insert the new item
            if (bucketsK[idx] == null) {
                bucketsK[idx] = key;
                bucketsV[idx] = value;
                bucketsH[idx] = hash;
                size++;
                if (hash==1)
                    tombstones--;
                // No value was updated so we return null
                return null;
            }
            else if (bucketsH[idx] == hash && key.equals(bucketsK[idx])) {
                // The element already existed in the map
                // We keep the old value to return it later
                // We update the element to new value
                V ret;
                ret = bucketsV[idx];
                bucketsV[idx] = value;
                // We return the value that was replaced
                return ret;
            }
            idx++;
            if (bucketsK.length==idx) idx = 0;
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
        int hash = hash(key);
        int idx = hash & (bucketsK.length-1);
        if (null==bucketsK[idx] && 0==bucketsH[idx]) {
            return null;
        }
        do {
            if (bucketsH[idx] == hash && key.equals(bucketsK[idx])) {
                V oldVal = bucketsV[idx];
                bucketsK[idx] = null;
                bucketsV[idx] = null;
                bucketsH[idx] = 1;
                tombstones++;
                size--;
                return oldVal;
            }
            idx++;
            if (idx == bucketsK.length) idx = 0;
        } while (!(null==bucketsK[idx] && 0==bucketsH[idx]));
        decreaseCapacity();
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(var e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        bucketsK = (K[]) new Object[1<<DEFAULT_MAP_CAPACITY_POW_2];
        bucketsV = (V[]) new Object[1<<DEFAULT_MAP_CAPACITY_POW_2];
        bucketsH = new int[1<<DEFAULT_MAP_CAPACITY_POW_2];
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> result = new HashSet<>();
        for(int i = 0; i < bucketsK.length; i++) {
            if (bucketsK[i]!=null) {
                result.add(bucketsK[i]);
            }
        }
        return result;
    }

    @Override
    public Collection<V> values() {
        HashSet<V> result = new HashSet<>();
        for(int i = 0; i < bucketsK.length; i++) {
            if (bucketsK[i]!=null) {
                result.add(bucketsV[i]);
            }
        }
        return result;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Map.Entry<K, V>> result = new HashSet<>();
        for(int i = 0; i < bucketsK.length; i++) {
            if (bucketsK[i]!=null) {
                result.add(new LProbArrMapEntry(bucketsK[i], bucketsV[i]));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < bucketsK.length; ++i) {
            buff.append(format("bucket[%d] = ", i));
            if (null == bucketsK[i]) {
                buff.append(bucketsH[i]==0? "NULL\n" : "TOMBSTONE\n");
            } else {
                buff.append(format(" {hash=%d, key=%s, value=%s }\n",
                        bucketsH[i], bucketsK[i], bucketsV[i]));
            }
        }
        return buff.toString();
    }

    protected static class LProbArrMapEntry<K, V> implements Map.Entry<K, V> { // exists only for entrySet

        public K key;
        public V value;

        public LProbArrMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "OaEntry{" +
                    "key=" + key +
                    ", val=" + value +
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
            throw new RuntimeException("LProbArrMapEntry.setValue not supported!");
        }
    }
}