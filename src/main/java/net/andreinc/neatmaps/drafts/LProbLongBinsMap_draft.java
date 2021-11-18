//package net.andreinc.neatmaps.drafts;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
//
//public class LProbLongBinsMap_draft<K,V> implements Map<K, V> {
//
//    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
//    private static final double DEFAULT_MIN_LOAD_FACTOR = DEFAULT_MAX_LOAD_FACTOR / 4;
//    private static final int DEFAULT_BINS_CAPACITY_POW_2 = 10;
//    private static final long EMPTY_SLOT = 4294967296L;
//
//    private int size = 0;
//    private int bc2 = DEFAULT_BINS_CAPACITY_POW_2;
//
//    private long[] bins = new long[1<<DEFAULT_BINS_CAPACITY_POW_2];
//    private LProbEntry<K,V>[] entries = new LProbEntry[bins.length>>1];
//
//    protected static String binary(long value) {
//        return String.format("%64s", Long.toBinaryString(value)).replace(" ", "0");
//    }
//
//    protected static String binary(int value) {
//        return String.format("%32s", Integer.toBinaryString(value)).replace(" ", "0");
//    }
//
//    protected static void initToEmpty(long[] bins) {
//        for(int i = 0; i < bins.length; i++) {
//            setEmptyBin(bins, i);
//        }
//    }
//
//    protected static boolean isEmptyBin(long[] bins, int idx) { return ((bins[idx]>>32)&1)==1; }
//    protected static boolean isEmptyBin(long bin) { return ((bin>>32)&1)==1;}
//    protected static void setEmptyBin(long[] bins, int idx) { bins[idx]|=(1L<<32); }
//    protected static int getIndex(long[] bins, int idx) { return (int)bins[idx]; }
//    protected static int getIndex(long bin) { return (int)bin; }
//    protected static void setIndex(long[] bins, int idx, int value) { bins[idx] &= ~(1L<<32); bins[idx] |= (value & 0xffffffffL); }
//    protected static int getHash(long[] bins, int idx) { return (int)(bins[idx]>>33); }
//    protected static int getHash(long bin) {return (int)(bin>>33); }
//    protected static void setHash(long[] bins, int idx, int hash) { bins[idx] |= ((long)hash<<33); }
//
//    public LProbLongBinsMap_draft() {
//        initToEmpty(this.bins);
//    }
//
//    public static int hash(final Object obj) {
//        int h = obj.hashCode();
//        h ^= h >> 16;
//        h *= 0x3243f6a9;
//        h ^= h >> 16;
//        return h & 0xfffffff;
//    }
//
//    protected final void rehash(int capModifier) {
//        this.bc2+=capModifier;
//        long[] oldBins = this.bins;
//        this.bins = new long[1<<bc2];
//        initToEmpty(this.bins);
//        for(int i = 0; i < oldBins.length; i++) {
//            if (!isEmptyBin(oldBins, i)) {
//                int hash = getHash(oldBins, i);
//                int entryIndex = getIndex(oldBins, i);
//                int idx = hash & (bins.length - 1);
//                while (true) {
//                    if (isEmptyBin(bins, idx)) {
//                        setIndex(bins, idx, entryIndex);
//                        setHash(bins, idx, hash);
//                        break;
//                    }
//                    idx++;
//                    if (idx == this.bins.length) idx = 0;
//                }
//            }
//        }
//    }
//
//    protected final void increaseBinsCapacity() {
//        final double lf = (double)(size) / bins.length;
//        if (lf > DEFAULT_MAX_LOAD_FACTOR) {
//            rehash(1);
//        }
//    }
//
//    protected final void increaseEntriesCapacity() {
//        if (size==entries.length) {
//            int newCap = (int) (entries.length * 1.5);
//            this.entries = Arrays.copyOf(this.entries, newCap);
//        }
//    }
//
//    @Override
//    public V get(Object key) {
//        if (null==key) {
//            throw new IllegalArgumentException("Map doesn't support null keys");
//        }
//        int hash = hash(key);
//        int idx = hash & (bins.length-1);
//        long bin = bins[idx];
//        if (bin==EMPTY_SLOT) {
//            return null;
//        }
//        int eIdx;
//        do {
//            eIdx = getIndex(bin);
//            if (getHash(bin)==hash && key.equals(entries[eIdx].key)) {
//                return entries[eIdx].value;
//            }
//            idx++;
//            if(idx==bins.length) idx = 0;
//            bin = bins[idx];
//        } while(bin==EMPTY_SLOT);
//        return null;
//    }
//
//    @Override
//    public V put(K key, V value) {
//        if (null==key) {
//            throw new IllegalArgumentException("Map doesn't support null keys");
//        }
//        increaseBinsCapacity();
//        increaseEntriesCapacity();
//        int hash = hash(key);
//        int idx = hash & (bins.length-1), eIdx;
//        while(true) {
//            if (isEmptyBin(bins, idx)) {
//                entries[size] = LProbEntry.createEntry(key, value);
//                setHash(bins, idx, hash);
//                setIndex(bins, idx, size);
//                size++;
//                return null;
//            }
//            else if (getHash(bins, idx) == hash) {
//                eIdx = getIndex(bins, idx);
//                if (key.equals(entries[eIdx].key)) {
//                    V old = entries[eIdx].value;
//                    entries[eIdx].value = value;
//                    return old;
//                }
//            }
//            idx++;
//            if (idx==bins.length) idx=0;
//        }
//    }
//
//    @Override
//    public V remove(Object key) {
//        return null;
//    }
//
//
//    @Override
//    public String toString() {
//        StringBuilder buff = new StringBuilder();
//        for(int i = 0; i < entries.length; i++) {
//            if (entries[i]!=null) {
//                buff.append("entries[").append(i).append("]=")
//                        .append(entries[i]).append("\n");
//            }
//        }
//        for(int i = 0; i < bins.length; i++) {
//            buff.append("bins[").append(i).append("]=");
//            if (isEmptyBin(bins, i)) {
//                buff.append("EMPTY\n");
//            }
//            else {
//                buff.append("{ hash = ").append(getHash(bins, i)).append(" ,")
//                        .append("index = ").append(getIndex(bins, i)).append(" }\n");
//            }
//        }
//        return buff.toString();
//    }
//
//    @Override
//    public int size() {
//        return this.size;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return size==0;
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        return get(key)!=null;
//    }
//
//    @Override
//    public boolean containsValue(Object value) {
//        return false;
//    }
//
//    @Override
//    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
//
//    }
//
//    @Override
//    public void clear() {
//
//    }
//
//    @NotNull
//    @Override
//    public Set<K> keySet() {
//        return null;
//    }
//
//    @NotNull
//    @Override
//    public Collection<V> values() {
//        return null;
//    }
//
//    @NotNull
//    @Override
//    public Set<Map.Entry<K, V>> entrySet() {
//        return null;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return false;
//    }
//
//    protected static class LProbEntry<K, V> implements Map.Entry<K, V> {
//
//        public K key;
//        public V value;
//
//        public static <K, V> LProbEntry<K, V> createEntry(K key, V val) {
//            LProbEntry<K,V> result = new LProbEntry<>();
//            result.key = key;
//            result.value = val;
//            return result;
//        }
//
//        @Override
//        public String toString() {
//            return "OaEntry{" +
//                    "key=" + key +
//                    ", val=" + value +
//                    '}';
//        }
//
//        @Override
//        public K getKey() {
//            return key;
//        }
//
//        @Override
//        public V getValue() {
//            return value;
//        }
//
//        @Override
//        public V setValue(V value) {
//            V oldVal = this.value;
//            this.value = value;
//            return oldVal;
//        }
//    }
//}
