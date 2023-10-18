package sorts;

import interfaces.Sort;
import main.Controller;
import utilities.Operations;

import static utilities.Delays.sleep;

final public class BubbleSort implements Sort {
    @Override
    public void runSort(Controller c) {
        for (int i = c.numberOfElements - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (c.stopSort) return;
                sleep(c, 1);
                if (c.array[j] > c.array[j + 1]) {
                    c.comparisons++;
                    Operations.swap(c, j, j + 1);
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
