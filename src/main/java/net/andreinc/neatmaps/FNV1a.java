package net.andreinc.neatmaps;

/**
 * FNV1a 32 and 64 bit variant
 * 32 bit Java port of http://www.isthe.com/chongo/src/fnv/hash_32a.c
 * 64 bit Java port of http://www.isthe.com/chongo/src/fnv/hash_64a.c
 */
public class FNV1a {
    private static final int FNV1_32_INIT = 0x811c9dc5;
    private static final int FNV1_PRIME_32 = 16777619;
    private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV1_PRIME_64 = 1099511628211L;

    /**
     * FNV1a 32 bit variant.
     *
     * @param data - input byte array
     * @return - hashcode
     */
    public static int hash32(byte[] data) {
        return hash32(data, data.length);
    }

    /**
     * FNV1a 32 bit variant.
     *
     * @param data   - input byte array
     * @param length - length of array
     * @return - hashcode
     */
    public static int hash32(byte[] data, int length) {
        int hash = FNV1_32_INIT;
        for (int i = 0; i < length; i++) {
            hash ^= (data[i] & 0xff);
            hash *= FNV1_PRIME_32;
        }

        return hash;
    }

    /**
     * FNV1a 64 bit variant.
     *
     * @param data - input byte array
     * @return - hashcode
     */
    public static long hash64(byte[] data) {
        return hash64(data, data.length);
    }

    /**
     * FNV1a 64 bit variant.
     *
     * @param data   - input byte array
     * @param length - length of array
     * @return - hashcode
     */
    public static long hash64(byte[] data, int length) {
        long hash = FNV1_64_INIT;
        for (int i = 0; i < length; i++) {
            hash ^= (data[i] & 0xff);
            hash *= FNV1_PRIME_64;
        }

        return hash;
    }
}

