package net.andreinc.neatmaps;

public class Main {

    public static void main(String[] args) {
        final int hash = 32132932;
        final int shifter = 5;
        final int bucketsLength = 1 << 4;

        int idx = hash & (bucketsLength-1);
        System.out.println(idx);

        int j = 5;
        int perturb = hash;
        while(j-->0) {
            idx = 5 * idx + perturb;
            perturb>>=shifter;
            idx = idx & (bucketsLength-1);
            System.out.println(idx);
        }
    }
}
