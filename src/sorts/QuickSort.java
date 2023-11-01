package sorts;

import interfaces.Sort;
import main.Controller;

import static utilities.Delays.sleep;
import static utilities.Operations.swap;

public class QuickSort implements Sort {

    private static void quickSort(Controller c, int p, int r) {
        if (p < r) {
            int q = partition(c, p, r);
            sleep(c, 1);
            quickSort(c, p, q);
            quickSort(c, q + 1, r);
        }
    }

    private static int partition(Controller c, int p, int r) {
        int x = c.array[p];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            do {
                i++;
                if (i < r) {
                    c.highlighted.set(1, i);
                    sleep(c);
                    c.comparisons += 2;
                }
            } while (i < r && c.array[i] < x);

            do {
                j--;
                if (j > p) {
                    c.highlighted.set(2, j);
                    sleep(c);
                    c.comparisons += 2;
                }
            } while (j > p && c.array[j] > x);

            if (i < j) swap(c, i, j);
            else return j;
        }
    }

    @Override
    public void runSort(Controller controller) {
        quickSort(controller, 0, controller.numberOfElements - 1);
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(nlog(n))";
    }
}
