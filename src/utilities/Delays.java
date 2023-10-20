package utilities;

import main.Controller;

/**
 * Provides utility functions to add sleep delays during sorting operations for animation purposes.
 */
final public class Delays {
    // Animation Delays in milliseconds
    private static final int minAnimationDelay = 1;
    private static final int maxAnimationDelay = 150;
    private static int animationSpeed = 50;

    /**
     * Introduces a delay that depends on the number of elements being sorted and the current animation speed.
     * The delay is inversely proportional to the square root of the number of elements.
     *
     * @throws RuntimeException if the sleep operation is interrupted.
     */
    public static void sleep(final Controller controller) {
        try {
            Thread.sleep(sleepTime(controller, 1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Introduces a delay with a given multiplier. The delay calculation is based on the number of elements being sorted
     * and the current animation speed, then multiplied by the provided delayMultiplier.
     *
     * @param delayMultiplier The multiplier for adjusting the delay duration.
     * @throws RuntimeException if the sleep operation is interrupted.
     */
    public static void sleep(Controller controller, double delayMultiplier) {
        try {
            Thread.sleep(sleepTime(controller, delayMultiplier));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates the interpolated delay based on the provided adaptive delay,
     * taking into account the current animation speed and delayMultiplier.
     *
     * @param controller      The Controller instance.
     * @param delayMultiplier The multiplier for adjusting the delay duration.
     * @return The interpolated delay in milliseconds.
     */
    private static int sleepTime(final Controller controller, double delayMultiplier) {
        int adjustedDelay = interpolateDelay(controller.numberOfElements);

        // Apply the delay multiplier
        adjustedDelay = (int) (adjustedDelay * delayMultiplier);
        System.out.println();
        return Math.max(minAnimationDelay, Math.min(adjustedDelay, maxAnimationDelay));
    }

    /**
     * Calculates an interpolated delay based on the number of elements.
     * The interpolation is done while considering the current animation speed.
     * The delay will interpolate between minAnimationDelay and maxAnimationDelay based on the animationSpeed.
     *
     * @param numberOfElements The number of elements in the array.
     * @return The interpolated delay in milliseconds.
     */
    private static int interpolateDelay(int numberOfElements) {
        double baseDelay = maxAnimationDelay / Math.sqrt(numberOfElements);
        // Decrease the delay as animationSpeed increases
        return (int) (baseDelay - ((baseDelay - minAnimationDelay) * (animationSpeed / 100.0)));
    }

    /*---------------Setters----------------*/

    /**
     * Sets the animation speed used for delay computations.
     * <p>
     * The animation speed is used to interpolate the adaptive delay to control the pacing of animations.
     * Higher values represent faster animations, and lower values correspond to slower animations.
     * </p>
     *
     * @param speed The animation speed to set, typically in a range (e.g., 0 to 100).
     */
    public static void setAnimationSpeed(int speed) {
        animationSpeed = speed;
    }

}
