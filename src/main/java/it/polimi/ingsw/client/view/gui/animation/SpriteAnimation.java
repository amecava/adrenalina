package it.polimi.ingsw.client.view.gui.animation;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SpriteAnimation extends Transition {

    private final ImageView imageView;
    private final int count;
    private final int columns;
    private final int width;
    private final int height;

    private int lastIndex;

    SpriteAnimation(
            ImageView imageView,
            Duration duration,
            int count,   int columns,
            int width,   int height) {

        this.imageView = imageView;
        this.count     = count;
        this.columns   = columns;
        this.width     = width;
        this.height    = height;

        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

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