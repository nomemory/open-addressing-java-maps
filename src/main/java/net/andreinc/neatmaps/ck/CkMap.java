package net.andreinc.neatmaps.ck;

import java.util.*;

public class  CkMap<K,V> implements Map<K, V> {

    protected static final int[] BIG_PRIMES = {
            999613, 999623, 999631,
            999653, 999667, 999671,
            999683, 999721, 999727,
            999749, 999763, 999769,
            999773, 999809, 999853,
            999863, 999883, 999907,
            999917, 999931, 999953,
            999959, 999961, 999979,
    };

    protected static final int CK_MAP_CAPACITY_I = 13;
    protected static final int CK_DEFAULT_SLOTS_PER_BUCKET = 4;
    protected static final int CK_DEFAULT_NUM_ARRAYS = 4;
    protected static final int CK_STASH_CAPACITY = 1<<12;

    protected int powerOfTwo;
    protected int size = 0;

    protected final int numArrays;
    protected final int slotsPerBucket;

    protected CkArray<K,V>[] arrays;
    protected TreeMap<K, V> stash = new TreeMap<>();

    public CkMap() {
        this(CK_MAP_CAPACITY_I, CK_DEFAULT_NUM_ARRAYS, CK_DEFAULT_SLOTS_PER_BUCKET);
    }

    public CkMap(int powerOfTwo, int numArrays, int slotsPerBucket) {
        this.powerOfTwo = powerOfTwo;
        this.numArrays = numArrays;
        this.slotsPerBucket = slotsPerBucket;
        this.arrays = new CkArray[numArrays];
        for(int i = 0, j=0; i < this.arrays.length; i++, j+=3) {
            arrays[i] = new CkArray<>(slotsPerBucket, powerOfTwo,
                    new int[]{BIG_PRIMES[j], BIG_PRIMES[j+1], BIG_PRIMES[j+2]});
        }
    }

    public CkArray<K,V>[] getArrays() {
        return arrays;
    }

    public static void main(String[] args) {
        CkMap<String, String> map = new CkMap<>();
        for (int i = 0; i < 100_000; i++) {
            map.put(i+"", i+"");
        }
        for (int i = 0; i < 100_000; i++) {
//            System.out.println(map.get(i+""));
          if (!(i+"").equals(map.get(i+""))) {
              System.out.println(">>>" + i);
              throw new IllegalStateException("Something wrong");
          }
        }
        for(int i = 0; i < map.numArrays; i++) {
            System.out.println("array size: " + i + " " + map.arrays[i].buckets.length);
            System.out.println("null densitiy " + i + " = " + map.arrays[i].nullDensity());
//            System.out.println(Arrays.toString(map.arrays[i].buckets));
        }
        System.out.println("stash size=" + map.stash.size());
    }

    // --------------------- Map Related ---------------------

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return (this.size==0);
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

//    @Override
//    public V get(Object key) {
//        V value;
//        for(int i = 0; i < arrays.length; i++) {
//            value = arrays[i].get(key);
//            if (value!=null) {
//                return value;
//            }
//        }
//        value = stash.get(key);
//        return value;
//    }

    @Override
    public V get(Object key) {
        int hash = arrays[0].hash(key);
        int idx = (hash & arrays[0].ndSize) * slotsPerBucket;

        if (arrays[0].buckets[idx]!=null && hash == arrays[0].buckets[idx].hash) {
            return (arrays[0].buckets[idx].key.equals(key)) ? arrays[0].buckets[idx].value : null;
        }
        if (arrays[0].buckets[idx+1]!=null && hash == arrays[0].buckets[idx+1].hash) {
            return arrays[0].buckets[idx+1].value;
        }
        if (arrays[0].buckets[idx+2]!=null && hash == arrays[0].buckets[idx+2].hash) {
            return arrays[0].buckets[idx+2].value;
        }
        if (arrays[0].buckets[idx+3]!=null && hash == arrays[0].buckets[idx+3].hash) {
            return arrays[0].buckets[idx+3].value;
        }

        hash = arrays[1].hash(key);
        idx = (hash & arrays[2].ndSize) * slotsPerBucket;

        if (arrays[1].buckets[idx]!=null && hash == arrays[1].buckets[idx].hash) {
            return arrays[1].buckets[idx].value;
        }
        if (arrays[1].buckets[idx+1]!=null && hash == arrays[1].buckets[idx].hash) {
            return arrays[1].buckets[idx+1].value;
        }
        if (arrays[1].buckets[idx+2]!=null && hash == arrays[1].buckets[idx].hash) {
            return arrays[1].buckets[idx+2].value;
        }
        if (arrays[1].buckets[idx+3]!=null && hash == arrays[1].buckets[idx].hash) {
            return arrays[1].buckets[idx+3].value;
        }

        hash = arrays[2].hash(key);
        idx = (hash & arrays[2].ndSize) * slotsPerBucket;

        if (arrays[2].buckets[idx]!=null && hash == arrays[2].buckets[idx].hash) {
            return arrays[2].buckets[idx].value;
        }
        if (arrays[2].buckets[idx+1]!=null && hash == arrays[2].buckets[idx+1].hash) {
            return arrays[2].buckets[idx+1].value;
        }
        if (arrays[2].buckets[idx+2]!=null && hash == arrays[2].buckets[idx+2].hash) {
            return arrays[2].buckets[idx+2].value;
        }
        if (arrays[2].buckets[idx+3]!=null && hash == arrays[2].buckets[idx+3].hash) {
            return arrays[2].buckets[idx+3].value;
        }

        hash = arrays[3].hash(key);
        idx = (hash & arrays[3].ndSize) * slotsPerBucket;

        if (arrays[3].buckets[idx]!=null && hash == arrays[3].buckets[idx].hash) {
            return arrays[3].buckets[idx].value;
        }
        if (arrays[3].buckets[idx+1]!=null && hash == arrays[3].buckets[idx+1].hash) {
            return arrays[3].buckets[idx+1].value;
        }
        if (arrays[3].buckets[idx+2]!=null && hash == arrays[3].buckets[idx+2].hash) {
            return arrays[3].buckets[idx+2].value;
        }
        if (arrays[3].buckets[idx+3]!=null && hash == arrays[3].buckets[idx+3].hash) {
            return arrays[3].buckets[idx+3].value;
        }

        return stash.get(key);
    }

    protected void grow() {
        final CkMap<K,V> newMap = new CkMap<>(this.powerOfTwo+1, this.numArrays, this.slotsPerBucket);

        for(int i = 0; i < arrays.length; i++) {
            for(int j = 0; j < arrays[0].buckets.length; j++) {
                if (arrays[i].buckets[j]!=null) {
                    newMap.put(arrays[i].buckets[j].key, arrays[i].buckets[j].value);
                }
            }
        }

        stash.forEach((k,v)->{
            newMap.put(k, v);
        });

        this.powerOfTwo = newMap.powerOfTwo;
        this.arrays = newMap.arrays;;
        this.stash  = newMap.stash;
    }

    @Override
    public V put(K key, V value) {
//        if (stash.size()>CK_STASH_CAPACITY) {
//            grow();
//        }
        CkArray.CkArrayResponse<V> put = CkArray.CkArrayResponse.fail();
        V oldVal = null;
        for(int i = 0; i < arrays.length; i++) {
            put = arrays[i].put(key, value);
            if (put.success) {
                oldVal = put.oldVal;
                break;
            }
        }
        if (!put.success) {
            stash.put(key, value);
        }
        return oldVal;
    }

    @Override
    public V remove(Object key) {
        return null;
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
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for(int j = 0; j < arrays[0].buckets.length; j++) {
            for(int i = 0; i < arrays.length; i++) {
                buff.append(arrays[i].buckets[j]);
                buff.append("----");
            }
            buff.append("\n");
        }
        return buff.toString();
    }

    public TreeMap<K, V> getStash() {
        return stash;
    }
}
