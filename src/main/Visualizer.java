package main;

import ecs100.UI;
import interfaces.Sort;
import sorts.BogoSort;
import sorts.BubbleSort;
import sorts.InsertionSort;
import utilities.Delays;
import utilities.Operations;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static utilities.Delays.sleep;

/**
 * The `Visualizer` class provides a graphical user interface (GUI) for visualizing
 * various sorting algorithms in real-time. Users can interact with the visualizer
 * to observe the sorting process, customize parameters, and control the sorting animations.
 * The class initializes the GUI, configures window properties, and handles user interactions.
 * <p>
 * Architecture:
 * - Uses a separate thread to ensure smooth graphical rendering and user interaction.
 * - Dedicated UI thread continuously updates the visualization.
 * - Sorting algorithms run on separate threads.
 * <p>
 * Features:
 * - Users can pause, resume, and customize the visualization.
 * - Provides real-time feedback on the sorting process.
 * - Exception handling for robust execution.
 *
 * @author Shemaiah Rangitaawa
 * @version 1.1
 * @since 10/18/2023
 */
public final class Visualizer {
    private static final int OVERLAY_X_OFFSET = 429; // TODO: Fix these offsets. Getting rid of them would be great
    private static final int OVERLAY_Y_OFFSET = 56;
    private static int INITIAL_LENGTH = 50;
    private static final double MINIMUM_PITCH = 25;
    private static final double MAXIMUM_PITCH = 100;
    private static Controller controller = new Controller(INITIAL_LENGTH);
    private static String heading = "Sorting Algorithm Visualizer v1.0";
    private static Thread audioThread;
    private static Thread UIThread;
    private static Synthesizer synth;
    private static MidiChannel channel;

    /*----------------Graphics Setup-----------------*/

    /**
     * Sets up the graphical user interface (GUI) for the Sorting Algorithm Visualizer.
     * This method initializes the UI framework, configures window properties, adds UI components
     * like buttons and sliders, and handles user interactions for sorting and visualization.
     */
    public static void setupGUI() {
        UI.initialise(); // Initialize UI and configure window properties
        // Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        // UI.setWindowSize((int) size.getWidth() / 2, (int) size.getHeight() / 2);
        UI.setWindowSize(1280, 720); // TODO: Scale to users display
        UI.getFrame().setLocation(0, 0);
        UI.getFrame().setTitle("Sorting Algorithm Visualizer v1.0");
        UI.getFrame().setVisible(true);
        UI.setDivider(0.2); // TODO: Fix overlay glitch on divider adjustment

        // Modify the color of the highlighted elements
        UI.addButton("Highlight Color", () -> {
            Color chosenColor = JColorChooser.showDialog(null, "Choose Highlight Color", controller.highlightedColor);
            if (chosenColor != null) {  // If user didn't cancel
                controller.highlightedColor = chosenColor; // Change color
            }
        });

        // Slider modifies numberOfElements and reinitialized array
        UI.addSlider("Number of Elements", 2, 2048, INITIAL_LENGTH, (double newLength) -> {
            if (!controller.sorting) {
                controller = new Controller((int) newLength);
                initialize(controller.array);
                startUIThread(); // Redisplay
            } else {
                // Do not allow array adjustment during sorting
                INITIAL_LENGTH = controller.numberOfElements;
                UI.println("Cannot adjust during sort!");
            }
        });

        // Adjusts the speed of animation
        UI.addSlider("Animation Speed", 0, 100, (double newSpeed) -> Delays.setAnimationSpeed((int) newSpeed));

        // Sorting
        UI.addButton("Bubble Sort", () -> { // TODO: Optimize these sort calls, seems jank
            heading = "Bubble Sort O(n^2)";
            try {
                runAlgorithm(new BubbleSort());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        UI.addButton("Insertion Sort", () -> {
            heading = "Insertion Sort O(n^2)";
            try {
                runAlgorithm(new InsertionSort());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        UI.addButton("Bogo Sort", () -> {
            heading = "Bogo Sort O(n!)";
            try {
                runAlgorithm(new BogoSort());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Utilities
        UI.addButton("Pause", () -> { // TODO: Pause on click
            controller.pauseSort = true;
            channel.allSoundOff();
            audioThread.interrupt();
            UI.printMessage("Paused");
        });
        UI.addButton("Resume", () -> { // TODO: Fix sound and UI glitches
            controller.sorting = true;
            startUIThread();
            UI.printMessage("");
        });
        UI.addButton("Reset", () -> {
            startUIThread();
            controller.stopSort = true;
            reset();
        });
        UI.addButton("Quit", UI::quit);

        // Draw the canvas and initiate the UI thread to handle UI updates
        controller.setNumberOfElements(INITIAL_LENGTH);
        initialize(controller.array);
        startUIThread();
    }

    /**
     * Starts a dedicated thread for continuous rendering of the graphical visualization
     * of the sorting algorithm and updates the user interface accordingly. This thread
     * constantly redraws the visualization canvas with the current state of the sorting
     * elements, their colors, and displays algorithm-related data.
     *
     * @implNote This method uses a dedicated thread to ensure smooth and continuous rendering
     * of the sorting visualization while allowing interaction with the UI during
     * the sorting process.
     */
    private static void startUIThread() {
        // Thread to manage the continuous rendering of the graphical visualization
        UIThread = new Thread(() -> {
            int canvasWidth = UI.getCanvasWidth(), canvasHeight = UI.getCanvasHeight();
            Image imageBuffer = UI.getFrame().createVolatileImage(canvasWidth, canvasHeight);
            Graphics graphics = imageBuffer.getGraphics();
            double x, y;
            do {
                // Adaptive buffer adjustment
                if (UI.getCanvasWidth() != canvasWidth || UI.getCanvasHeight() != canvasHeight) {
                    canvasWidth = UI.getCanvasWidth();
                    canvasHeight = UI.getCanvasHeight();
                    imageBuffer = UI.getFrame().createVolatileImage(canvasWidth, canvasHeight);
                    graphics = imageBuffer.getGraphics();
                }

                // Foreground
                graphics.setColor(Color.black);
                graphics.fillRect(0, 0, canvasWidth, canvasHeight);

                // Determine scaling for rectangles
                x = (double) canvasWidth / controller.numberOfElements;
                y = (double) (canvasHeight - 30) / controller.numberOfElements;

                // Render array as white rectangles
                Color fillColor, outlineColor;
                for (int i = 0; i < controller.numberOfElements; i++) {
                    // Determine the highlight color
                    if (controller.highlighted.contains(i)) {
                        fillColor = controller.highlightedColor;
                    } else {
                        fillColor = Color.white;
                    }

                    // Everything becomes outlineColor when array length > 300
                    if (controller.numberOfElements > 250) {
                        outlineColor = fillColor;
                    } else {
                        outlineColor = Color.black;
                    }

                    int rectangleX = (int) (i * x);
                    int rectangleY = (int) (UI.getFrame().getHeight() - controller.array[i] * y);
                    int rectangleWidth = Math.max((int) x, 1);
                    int rectangleHeight = Math.max((int) (controller.array[i] * y), 1);

                    // Render the rectangle
                    graphics.setColor(fillColor);
                    graphics.fillRect(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
                    graphics.setColor(outlineColor);
                    graphics.drawRect(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
                }

                // Display algorithm data in top left corner of the canvas
                graphics.setColor(Color.white);
                graphics.drawString(heading, 10, 20);
                graphics.drawString("Comparisons: " + controller.comparisons, 10, 40);
                graphics.drawString("Array Accesses: " + controller.arrayAccesses, 11, 55);
                graphics.drawString("Number of Elements: " + controller.numberOfElements, 10, 70);

                Graphics overlay = UI.getFrame().getGraphics();
                overlay.drawImage(imageBuffer, OVERLAY_X_OFFSET, OVERLAY_Y_OFFSET, null);
            } while (!controller.pauseSort); // Do while not paused
        });
        UIThread.start();
    }

    private static void stopUIThread() {
        UIThread.interrupt();
    }

    /*----------------Audio-----------------*/

    /**
     * Initializes and starts the audio system for playback.
     * <p>
     * This method does the following:
     * <ol>
     *   <li>Closes any previously opened synthesizer.</li>
     *   <li>Loads a specific sound font (Perfect Sine.sf2).</li>
     *   <li>Initializes a synthesizer and opens it.</li>
     *   <li>Loads the first instrument from the sound font into the synthesizer.</li>
     *   <li>Sets the loaded instrument for the main MIDI channel.</li>
     *   <li>Turns off the reverb and sustain effects on the main channel.</li>
     *   <li>Starts an audio thread for managing the note playback.</li>
     * </ol>
     */
    public static void startAudio() {
        if (synth != null && synth.isOpen()) {
            synth.close(); // Close any previously opened synthesizer
        }

        // Load sound font
        try (InputStream stream = Visualizer.class.getResourceAsStream("/sound/Perfect Sine.sf2")) {
            if (stream == null) {
                throw new IOException("Couldn't find the sound resource.");
            }
            synth = MidiSystem.getSynthesizer();
            synth.open();
            Soundbank soundbank = MidiSystem.getSoundbank(stream);
            Instrument instrument = soundbank.getInstruments()[0];
            if (!synth.loadInstrument(instrument)) {
                throw new InvalidMidiDataException("Unable to load the instrument.");
            }
            channel = synth.getChannels()[0];
            channel.programChange(instrument.getPatch().getProgram());
            channel.controlChange(91, 0); // Turn off reverb
            channel.controlChange(64, 0);  // Turn off sustain
            startAudioThread();
        } catch (MidiUnavailableException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + ": The MIDI device is unavailable. Sound is disabled.");
        } catch (IOException | InvalidMidiDataException e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + ". Sound is disabled.");
        }
    }

    /**
     * Initializes and starts the audio thread for playback.
     * <p>
     * The audio thread continuously monitors the state of the controller. For each highlighted element, the method calculates:
     * <ul>
     *   <li>The normalized value of the element.</li>
     *   <li>The desired pitch based on the normalized value.</li>
     *   <li>Base pitch and pitch bend values derived from the desired pitch.</li>
     *   <li>MIDI velocity based on the difference between the maximum pitch and base pitch.</li>
     * </ul>
     * Once the desired note characteristics are calculated, the method starts playback of the note.
     * To ensure that only a single note is played at any given time, the loop breaks after starting a note.
     * The thread continues to play notes as long as the controller is sorting.
     * </p>
     */
    private static void startAudioThread() {
        audioThread = new Thread(() -> {
            while (controller.sorting) {
                channel.allNotesOff();
                for (int i : controller.highlighted) {
                    if (i != -1) {
                        double normalizedValue = controller.array[Math.min(Math.max(i, 0), controller.numberOfElements - 1)] / (double) controller.numberOfElements;
                        double calculatedPitch = normalizedValue * (MAXIMUM_PITCH - MINIMUM_PITCH) + MINIMUM_PITCH;
                        int basePitch = (int) calculatedPitch;
                        int pitchBendValue = (int) ((calculatedPitch - basePitch) * 8192d) + 8192; // In MIDI, 8192 is the center pitch bend value (no pitch bend)

                        // Compute MIDI velocity based on pitch difference and number of controller elements.
                        int velocity = (int) (Math.pow(MAXIMUM_PITCH - basePitch, 2d) * Math.pow(controller.numberOfElements, -0.25) * 32d);
                        channel.setPitchBend(pitchBendValue);
                        channel.noteOn(basePitch, velocity);
                        break; // Ensure only one note is played. Break after starting a note
                    }
                }
                sleep(controller, 1);
            }
        });
        audioThread.start();
    }

    private static void stopAudioThread() {
        audioThread.interrupt();
    }

    /*----------------Array-----------------*/

    /**
     * Resets the state of the sorting algorithm and related variables, allowing for a fresh start.
     * This method clears highlights, initializes the array, resets flags, and metrics,
     * and prepares the system for a new sorting operation.
     *
     * @throws RuntimeException if an InterruptedException occurs during the completion animation.
     */
    private static void reset() { // TODO: Reset during pause? Don't reset highlight color
        controller.clearHighlights();
        channel.allSoundOff();

        // If we've completed sorting
        if (controller.stopSort || !controller.sorting) {
            try { // Show animation
                completedSort();
                Thread.sleep(1000); // Pause
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        // Reset array, flags, and metrics
        stopUIThread();
        stopAudioThread();
        controller.arrayAccesses = 0;
        controller.comparisons = 0;
        controller.stopSort = false;
        controller.sorting = false;
        controller.clearHighlights();
        initialize(controller.array);
    }

    /**
     * Initiates an animation to visually indicate the completion of a sorting operation.
     * This method highlights each element in the sorted array in sequence to provide a visual
     * representation of the sorted result.
     */
    private static void completedSort() {
        for (int i = 0; i < controller.numberOfElements; i++) {
            // Highlight the current element in the sorted array
            controller.highlighted.set(i, i);
            // controller.highlighted.set(0, i);
            sleep(controller);
        }
    }

    /*----------------Util-----------------*/

    /**
     * Executes the specified sorting algorithm on the controller's array.
     * This method also manages the sorting state, starts audio playback during sorting,
     * and resets the controller after the sorting has completed.
     *
     * @param sort The sorting algorithm to be executed. This should be an instance
     *             of a class implementing a sort interface or any relevant abstraction
     *             that encapsulates a sorting strategy.
     * @see Sort
     */
    private static void runAlgorithm(Sort sort) {
        controller.sorting = true;
        startAudio();
        sort.runSort(controller);
        controller.sorting = false;
        reset();
    }

    /**
     * Initializes and shuffles the integer array
     * <p>
     * First, each array index is set to its corresponding value (0 to length-1)
     * Then, the array is shuffled to randomize the order of these values
     *
     * @param a the integer array to be initialized and shuffled
     */
    private static void initialize(int[] a) {
        // Initialize marked list with -1 values, same length as array
        for (int i = 0; i < controller.numberOfElements; i++) controller.highlighted.add(-1);
        for (int i = 0; i < a.length; i++) a[i] = i; // Set each array element to its index value
        Operations.shuffle(controller, a); // Randomize array elements' order
    }

    /*----------------Main-----------------*/

    /**
     * The entry point of the Sorting Algorithm Visualizer application.
     * Initializes and sets up the graphical user interface (GUI) for the visualizer.
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        setupGUI();
    }
}
