package net.andreinc.neatmaps;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.andreinc.neatmaps.drafts.LProbLongBinsMap_draft.*;

public class BitwiseTests {
    @Test
    public void testIsEmptyBin() {
        long[] arr = new long[1<<30];
        initToEmpty(arr);
        for(int i = 0; i < arr.length; ++i) {
            Assertions.assertTrue(isEmptyBin(arr, i));
        }
    }
    @Test
    public void testSetEmptyBin() {
        long[] arr = new long[1<<30];
        for(int i = 0; i < arr.length; i+=2) {
            setEmptyBin(arr, i);
        }
        for(int i = 0; i < arr.length; i++) {
            if (i%2==0) {
                Assertions.assertTrue(isEmptyBin(arr, i));
            }
            else {
                Assertions.assertFalse(isEmptyBin(arr, i));
            }
        }
    }

    @Test
    public void testSetGetHash() {
        long[] arr = new long[1<<30];
        initToEmpty(arr);
        for (int i = 0; i < arr.length; i++) {
            setHash(arr, i, i);
        }
        for(int i = 0; i < arr.length; i++) {
            Assertions.assertEquals(getHash(arr, i), i);
        }
    }

    @Test
    public void testSetGetIndex() {
        long[] arr = new long[1<<30];
        initToEmpty(arr);
        for(int i = 0; i < arr.length; i++) {
            setIndex(arr, i, i);
        }
        for (int i = 0; i < arr.length; i++) {
            Assertions.assertEquals(getIndex(arr, i), i);
        }
    }

    @Test
    public void testSetGetHashAndSetGetIndex() {
        long[] arr = new long[1<<30];
        initToEmpty(arr);
        for(int i = 0; i < arr.length; i++) {
            setHash(arr, i, i);
            setIndex(arr, i, Integer.MAX_VALUE - i);
        }
        for(int i = 0; i < arr.length; i++) {
            Assertions.assertTrue(!isEmptyBin(arr,i));
            Assertions.assertEquals(getHash(arr,i), i);
            Assertions.assertEquals(getIndex(arr,i), Integer.MAX_VALUE-i);
        }
    }
}
