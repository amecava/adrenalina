package it.polimi.ingsw.client.view.gui.animations;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * animation composing the explosion in the explosion animation
 */
public class SpriteAnimation extends Transition {

    /**
     * image of the animation
     */
    private final ImageView imageView;
    /**
     * duration of the animation
     */
    private final int count;
    /**
     * columns of the animation
     */
    private final int columns;
    /**
     * width of the animation
     */
    private final int width;
    /**
     * height of the animation
     */
    private final int height;

    /**
     * the last index of the animation
     */
    private int lastIndex;

    /**
     * creating the animation
     * @param imageView image of the animation
     * @param duration duration of the animation
     * @param count frames of the animation
     * @param columns columns of the animation
     * @param width width of the animation
     * @param height height of the animation
     */
    SpriteAnimation(ImageView imageView, Duration duration,
            int count, int columns, int width, int height) {

        this.imageView = imageView;
        this.count = count;
        this.columns = columns;
        this.width = width;
        this.height = height;

        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    /**
     * interpolates the values in order to get the correct result
     * @param k multiplying factor for the interpolation
     */
    @Override
    protected void interpolate(double k) {

        final int index = Math.min((int) Math.floor(k * count), count - 1);

        if (index != lastIndex) {

            final int x = (index % columns) * width;
            final int y = (index / columns) * height;

            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
        }
    }
}