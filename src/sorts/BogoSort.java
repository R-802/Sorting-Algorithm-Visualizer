package sorts;

import interfaces.Sort;
import main.Controller;

import static utilities.Operations.swap;

public class BogoSort implements Sort {
    @Override
    public void runSort(Controller c) {
        while (!isSorted(c)) {
            if (c.stopSort) return;
            for (int i = 0; i < c.numberOfElements; i++) {
                swap(c, i, (int) (Math.random() * c.numberOfElements));
            }
        }
    }

    private boolean isSorted(Controller c) {
        int length = c.array.length;
        c.comparisons += length - 1;
        c.arrayAccesses += 2L * (length - 1);
        for (int i = 1; i < length; i++) {
            c.highlighted.set(1, i);
            c.highlighted.set(2, i - 1);

            // If the current element is less than the previous one, the array is not sorted.
            if (c.array[i] < c.array[i - 1]) {
                return false;
            }
        }
        return true;  // If the loop completes without returning, the array is sorted.
    }

    @Override
    public String getName() {
        return "Bogo Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n^2)";
    }
}
