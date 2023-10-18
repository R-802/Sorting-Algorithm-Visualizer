package main;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Controller class manages the state and properties of the sorting process
 * and provides methods to control and monitor it.
 */
public class Controller {
    // Properties
    public int[] array;
    public ArrayList<Integer> highlighted;
    public Color highlightedColor; // Default highlight color

    // Metrics
    public int numberOfElements;
    public long arrayAccesses;
    public long comparisons;

    // Flags for controlling sorting and UI interaction
    public volatile boolean pauseSort = false; // Pause flag for sorting
    public volatile boolean stopSort = false; // Stop flag for sorting
    public volatile boolean sorting = false; // Sorting in progress flag

    /**
     * Class constructor initializes the Controller with the specified number of elements.
     *
     * @param numberOfElements The number of elements in the array to be sorted.
     */
    public Controller(int numberOfElements) {
        this.numberOfElements = numberOfElements;
        this.array = new int[numberOfElements];
        highlighted = new ArrayList<>();
        highlightedColor = Color.green;
        arrayAccesses = 0;
        comparisons = 0;
    }

    /**
     * Clears all highlighted elements in the array.
     */
    public void clearHighlights() {
        for (int i = 0; i < numberOfElements; i++) {
            highlighted.set(i, -1);
        }
    }

    /**
     * Sets the number of elements in the array.
     *
     * @param n The new number of elements to set.
     */
    public void setNumberOfElements(int n) {
        numberOfElements = n;
    }
}
