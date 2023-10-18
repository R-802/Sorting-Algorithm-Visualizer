package utilities;

import main.Controller;

/**
 * Provides utility functions to add sleep delays during sorting operations for animation purposes.
 */
final public class Delays {
    // Animation Delays in milliseconds
    private static final int minAnimationDelay = 1;
    private static final int maxAnimationDelay = 250;
    private static int animationSpeed;

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
     * Computes a sleep time (delay) that is inversely proportional to the square root
     * of the number of elements being sorted. This delay is bound by specified minimum and maximum delay values.
     *
     * @return The computed delay in milliseconds.
     */
    private static int sleepTime(final Controller controller) {
        int adaptiveDelay = (int) (maxAnimationDelay / Math.sqrt(controller.numberOfElements));
        int adjustedDelay = interpolateDelay(adaptiveDelay);
        return Math.max(minAnimationDelay, Math.min(adjustedDelay, maxAnimationDelay));
    }

    /**
     * Computes a sleep time (delay) for algorithms that might run too fast. This is based on the number of elements
     * being sorted and the current animation speed. The computed delay is then multiplied by the given delayMultiplier.
     *
     * @param delayMultiplier The multiplier for adjusting the delay duration.
     * @return The computed delay in milliseconds after applying the delayMultiplier.
     */
    public static int sleepTime(final Controller controller, double delayMultiplier) {
        int adaptiveDelay = (int) (maxAnimationDelay / Math.sqrt(controller.numberOfElements));
        int adjustedDelay = interpolateDelay(adaptiveDelay);

        // Apply the delay multiplier
        adjustedDelay *= (int) delayMultiplier;
        return Math.max(minAnimationDelay, Math.min(adjustedDelay, maxAnimationDelay));
    }

    /**
     * Calculates an interpolated delay based on the provided adaptive delay.
     * The interpolation is done considering the current animation speed.
     *
     * @param adaptiveDelay The base delay that should be interpolated.
     * @return The interpolated delay in milliseconds.
     */
    private static int interpolateDelay(int adaptiveDelay) {
        return ((100 - animationSpeed) * adaptiveDelay + (animationSpeed * minAnimationDelay) / 100);
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

    /**
     * Gets the current animation speed.
     *
     * @return The current animation speed.
     */
    public static int getAnimationSpeed(int speed) {
        return animationSpeed;
    }
}
