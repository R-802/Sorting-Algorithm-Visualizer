package sorts;

import interfaces.Sort;
import main.Controller;

import static utilities.Delays.sleep;
import static utilities.Operations.swap;

final public class BubbleSort implements Sort {
    @Override
    public void runSort(Controller c) {
        for (int i = c.numberOfElements - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                sleep(c, 0.005);
                if (c.stopSort) return;
                if (c.array[j] > c.array[j + 1]) {
                    c.comparisons++;
                    swap(c, j, j + 1, 0.02);
                } else {
                    c.highlighted.set(1, j + 1);
                    c.highlighted.set(2, -5);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n^2)";
    }
}
