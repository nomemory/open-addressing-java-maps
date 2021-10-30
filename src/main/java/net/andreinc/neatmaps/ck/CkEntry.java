package net.andreinc.neatmaps.ck;


import java.util.Map;

class CkEntry<K, V> implements Map.Entry<K, V> {

    protected K key;
    protected V value;
    protected int hash;

    public CkEntry(K key, V value, int hash) {
        this.key = key;
        this.value = value;
        this.hash = hash;
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

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("[key=")
                .append(key)
                .append(" val=")
                .append(value)
                .append("]");
        return buff.toString();
    }
}

