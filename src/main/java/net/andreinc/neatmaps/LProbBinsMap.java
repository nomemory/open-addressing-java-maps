package net.andreinc.neatmaps;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LProbBinsMap<K,V> implements Map<K, V> {

    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.6;
    private static final double DEFAULT_MIN_LOAD_FACTOR = DEFAULT_MAX_LOAD_FACTOR / 4;
    private static final int DEFAULT_BINS_CAPACITY_POW_2 = 10;
    private static final int EMPTY_SLOT = -1;
    private static final int TOMBSTONE = -2;

    private int size = 0;
    private int bc2 = DEFAULT_BINS_CAPACITY_POW_2;

    private int[] bins = new int[1<<DEFAULT_BINS_CAPACITY_POW_2];
    private LProbEntry<K,V>[] entries = new LProbEntry[bins.length>>1];

    protected static void initToEmpty(int[] bins) {
        for (int i = 0; i < bins.length; i++) bins[i]=-1;
    }

    public LProbBinsMap() {
        initToEmpty(this.bins);
    }

    public static int hash(final Object obj) {
        int h = obj.hashCode();
        h ^= h >> 16;
        h *= 0x3243f6a9;
        h ^= h >> 16;
        return h & 0xfffffff;
    }

    protected final void rehash(int capModifier) {

        this.bc2+=capModifier;
        LProbEntry<K,V>[] oldEntries = this.entries;

        this.bins = new int[1<<bc2];
        this.entries = new LProbEntry[oldEntries.length];
        this.size = 0;

        initToEmpty(this.bins);

        for(int i = 0; i < oldEntries.length; i++) {
            if (oldEntries[i]!=null) {
                put(oldEntries[i].key, oldEntries[i].value, oldEntries[i].hash);
            }
        }
    }

    protected final void increaseBinsCapacity() {
        final double lf = (double)(size) / bins.length;
        if (lf > DEFAULT_MAX_LOAD_FACTOR) {
            rehash(1);
        }
    }

    protected final void increaseEntriesCapacity() {
        if (size==entries.length) {
            int newCap = (int) (entries.length * 1.5);
            this.entries = Arrays.copyOf(this.entries, newCap);
        }
    }

    protected V put(K key, V value, int hash) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        increaseBinsCapacity();
        increaseEntriesCapacity();
        int idx = hash & (bins.length-1), eIdx;
        while(true) {
            if (bins[idx]<0) {
                bins[idx] = size;
                entries[size] = new LProbEntry<>(key, value, hash);
                size++;
                return null;
            }
            else if (entries[bins[idx]].hash == hash && key.equals(entries[bins[idx]].key)) {
                V old = entries[bins[idx]].value;
                entries[bins[idx]].value = value;
                return old;
            }
            idx++;
            if (idx==bins.length) idx=0;
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
    public V get(Object key) {
        if (null==key) {
            throw new IllegalArgumentException("Map doesn't support null keys");
        }
        int hash = hash(key);
        int idx = hash & (bins.length-1);
        if (bins[idx]==EMPTY_SLOT) {
            return null;
        }
        do {
            if (bins[idx]!=TOMBSTONE && entries[bins[idx]].hash==hash && key.equals(entries[bins[idx]].key)) {
                return entries[bins[idx]].value;
            }
            idx++;
            if (idx == bins.length) idx = 0;
        } while(bins[idx]!=EMPTY_SLOT);
        return null;
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key);
        int idx = hash & (bins.length-1);
        if (bins[idx]==EMPTY_SLOT) {
            return null;
        }
        do {
            if (bins[idx] !=TOMBSTONE && entries[bins[idx]].hash==hash && key.equals(entries[bins[idx]].key)) {
                V old = entries[bins[idx]].value;
                entries[bins[idx]] = null;
                bins[idx] = TOMBSTONE;
                size--;
                return old;
            }
            idx++;
            if (idx==bins.length) idx = 0;
        } while(bins[idx]!=EMPTY_SLOT);
        return null;
    }


    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < entries.length; i++) {
            if (entries[i]!=null) {
                buff.append("entries[").append(i).append("]=")
                        .append(entries[i]).append("\n");
            }
        }
        for(int i = 0; i < bins.length; i++) {
            buff.append("bins[").append(i).append("]=");
            if (bins[i]==EMPTY_SLOT) {
                buff.append("EMPTY\n");
            }
            else {
                buff.append("{ hash = ").append(entries[bins[i]].hash).append(" ,")
                        .append(" key = ").append(entries[bins[i]].key).append(" ,")
                        .append("index = ").append(entries[bins[i]].value).append(" }\n");
            }
        }
        return buff.toString();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        return null!=get(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i]!=null) {
                if (value.equals(entries[i].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this.bins = new int[1<<DEFAULT_BINS_CAPACITY_POW_2];
        this.entries = new LProbEntry[bins.length>>1];
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        HashSet<K> r = new HashSet<>();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i]!=null) {
                r.add(entries[i].key);
            }
        }
        return r;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        ArrayList<V> r = new ArrayList<>();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i]!=null) {
                r.add(entries[i].value);
            }
        }
        return r;
    }

    @NotNull
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> r = new HashSet<>();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i]!=null) {
                r.add(entries[i]);
            }
        }
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LProbBinsMap)) return false;
        LProbBinsMap<?, ?> that = (LProbBinsMap<?, ?>) o;
        return size == that.size && bc2 == that.bc2 && Arrays.equals(bins, that.bins) && Arrays.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, bc2);
        result = 31 * result + Arrays.hashCode(bins);
        result = 31 * result + Arrays.hashCode(entries);
        return result;
    }

    protected static class LProbEntry<K, V> implements Map.Entry<K, V> {

        protected K key;
        protected V value;
        protected int hash;

        public LProbEntry(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
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
            V oldVal = this.value;
            this.value = value;
            return oldVal;
        }
    }
}
