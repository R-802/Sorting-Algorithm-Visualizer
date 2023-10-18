package utilities;

import main.Controller;

import static utilities.Delays.sleep;

public class Operations {
    public static void swap(final Controller sc, int i, int j, double pause) {
        sc.highlighted.set(1, i);
        sc.highlighted.set(2, j);
        sc.arrayAccesses += 2;
        sleep(sc, pause);
        int temp = sc.array[i];
        sc.array[i] = sc.array[j];
        sc.array[j] = temp;
    }

    /**
     * Used during array initialization
     *
     * @param sc Controller object
     * @param i  element at i
     * @param j  index to be swapped to
     */
    public static void swap(final Controller sc, int i, int j) {
        int temp = sc.array[i];
        sc.array[i] = sc.array[j];
        sc.array[j] = temp;
    }
}
