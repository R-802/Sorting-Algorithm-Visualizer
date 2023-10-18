package utilities;

import main.Controller;

/**
 * A utility class providing various array operations,
 * including swap, initial swap, and shuffle functionalities.
 * These operations are designed to be used in the context of
 * the provided Controller class, especially in visual sorting
 * or algorithm demonstrations.
 */
public class Operations {

    /**
     * Swaps the values at the specified indices in the controller's array.
     * This method also updates the highlighted indices in the controller
     * and increments the array accesses count.
     *
     * @param c The controller instance containing the array and highlighted indices.
     * @param i The index of the first element to be swapped.
     * @param j The index of the second element to be swapped.
     */
    public static void swap(final Controller c, int i, int j) {
        c.highlighted.set(1, i);
        c.highlighted.set(2, j);
        c.arrayAccesses += 2;
        int temp = c.array[i];
        c.array[i] = c.array[j];
        c.array[j] = temp;
    }

    /**
     * Performs a simple swap of elements in the array during initialization
     * without additional operations such as highlighting or counting accesses.
     *
     * @param c Controller object containing the array to be operated on.
     * @param i Index of the first element to be swapped.
     * @param j Index of the second element to be swapped.
     */
    public static void initialSwap(final Controller c, int i, int j) {
        int temp = c.array[i];
        c.array[i] = c.array[j];
        c.array[j] = temp;
    }

    /**
     * Shuffles the elements of an integer array using the Fisher-Yates shuffle algorithm.
     * This method randomizes the order of the elements in the array by iteratively swapping
     * each element with a randomly selected element in the array.
     *
     * @param c The controller object associated with the array operations.
     * @param a The integer array to be shuffled.
     */
    public static void shuffle(final Controller c, int[] a) {
        for (int i = 0; i < a.length; i++) {
            // Swap the current element with a randomly selected element in the array.
            initialSwap(c, i, (int) (Math.random() * a.length));
        }
    }
}
