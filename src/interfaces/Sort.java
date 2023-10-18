package interfaces;

import main.Controller;

/**
 * The Sort interface defines a contract for sorting algorithms.
 * Classes implementing this interface are responsible for implementing
 * the core logic of a specific sorting algorithm. It also provides optional
 * methods for retrieving the algorithm's name, time complexity, and space complexity.
 */
public interface Sort {
    /**
     * Gets the name of the sorting algorithm.
     *
     * @return The name of the sorting algorithm.
     */
    default String getName() {
        return "";
    }

    /**
     * Gets the Big O notation representing the algorithm's time complexity for display purposes.
     *
     * @return The Big O notation representing the algorithm's time complexity.
     */
    default String getTimeComplexity() {
        return "";
    }

    /**
     * Gets the Big O notation representing the algorithm's space complexity for display purposes.
     *
     * @return The Big O notation representing the algorithm's space complexity.
     */
    default String getSpaceComplexity() {
        return "";
    }

    /**
     * Implement the sorting algorithm's core logic within this method.
     * Be sure to check for termination conditions using a `return` statement
     * if the sorting process should stop, which can be determined by the
     * `Controller.stopSort` flag.
     *
     * @param controller Provides access to the integer array that needs to be sorted via `controller.array[]`.
     */
    void runSort(Controller controller);
}
