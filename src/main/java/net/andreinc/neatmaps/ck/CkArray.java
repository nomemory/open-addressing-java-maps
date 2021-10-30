package net.andreinc.neatmaps.ck;

import java.util.Arrays;

class CkArray<K,V> {

    protected int size = 0;
    protected final int itemsPerBucket;
    protected final int powerOfTwo;
    protected final int ndSize;
    protected CkEntry<K,V> buckets[];
    protected final int[] bigPrimes;

    public CkArray(int itemsPerBucket, int powerOfTwo, int[] bigPrimes) {
        this.itemsPerBucket = itemsPerBucket;
        this.powerOfTwo = powerOfTwo;
        this.ndSize = (1<<powerOfTwo)-1;
        this.bigPrimes = bigPrimes;
        this.buckets = new CkEntry[(1<<this.powerOfTwo) * itemsPerBucket];
    }

    protected int hash(Object value) {
        int hash = value.hashCode();
        hash ^= (hash >> 16);
        hash *= bigPrimes[0];
        hash ^= (hash >> 16);
        hash *= bigPrimes[1];
        hash ^= (hash >> 16);
        hash *= bigPrimes[2];
        return hash & 0xfffffff;
    }

    public V get(Object key) {
        int hash = hash(key);
        int idx = (hash & ndSize) * itemsPerBucket;
        for(int i = 0; i < itemsPerBucket; i++, idx++) {
            if (null != buckets[idx] && hash == buckets[idx].hash && key.equals(buckets[idx].key)) {
                return buckets[idx].value;
            }
        }
        return null;
    }

    public CkArrayResponse<V> put(K key, Object val) {
        int hash = hash(key);
        int idx = (hash & ndSize) * itemsPerBucket;
        for(int i = 0; i < itemsPerBucket; i++, idx++) {
            // There's an empty bucket
            if (null==buckets[idx]) {
                buckets[idx] = new CkEntry<>(key, (V)val, hash);
                this.size++;
                return CkArrayResponse.success(null);
            } else {
                // The value is already here, we just update it
                if (buckets[idx].hash == hash && buckets[idx].key.equals(key)) {
                    V oldValue = buckets[idx].value;
                    buckets[idx].value = (V) val;
                    return CkArrayResponse.success(oldValue);
                }
            }
        }
        return CkArrayResponse.fail();
    }

    public double nullDensity() {
        int nulls = 0;
        for(int i = 0; i < buckets.length; i++) {
            if (buckets[i]==null) {
                nulls++;
            }
        }
        return (double) nulls / (double) buckets.length;
    }

    @Override
    public String toString() {
        return "CkBucket{" +
                ", buckets=" + Arrays.toString(buckets) +
                '}';
    }

    protected static class CkArrayResponse<V> {

        protected static final CkArrayResponse FAIL_ANSWER = new CkArrayResponse(null, false);

        protected static final <V> CkArrayResponse<V> success(V v) {
            return new CkArrayResponse(v, true);
        }

        protected static final CkArrayResponse fail() {
            return FAIL_ANSWER;
        }

        public CkArrayResponse(V oldVal, boolean success) {
            this.oldVal = oldVal;
            this.success = success;
        }

        protected final V oldVal;
        protected final boolean success;
    }
}