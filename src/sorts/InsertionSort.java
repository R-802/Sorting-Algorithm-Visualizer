package sorts;

import interfaces.Sort;
import main.Controller;
import utilities.Operations;

import static utilities.Delays.sleep;

public class InsertionSort implements Sort {
    @Override
    public void runSort(Controller c) {
        int pos;
        for (int i = 1; i < c.numberOfElements; i++) {
            pos = i;
            c.highlighted.set(1, i);
            c.highlighted.set(2, -5);
            while (pos > 0 && c.array[pos] <= c.array[pos - 1]) {
                c.comparisons += 2;
                sleep(c, 1);
                if (c.stopSort) return;
                Operations.swap(c, pos, pos - 1);
                pos--;
            }
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n^2)";
    }
}
