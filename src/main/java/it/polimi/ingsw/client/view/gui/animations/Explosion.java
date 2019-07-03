package it.polimi.ingsw.client.view.gui.animations;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Explosion {

    private static final int COLUMNS = 8;
    private static final int COUNT = 64;
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private Explosion() {

        //
    }

    public static Entry<ImageView, Animation> getExplosion(int id, MouseEvent event) {

        ImageView imageView = new ImageView(Images.imagesMap.get("explosion" + id));
        imageView.setViewport(new Rectangle2D(0, 0, WIDTH, HEIGHT));

        imageView.setX(event.getSceneX() - ((double) WIDTH / 2));
        imageView.setY(event.getSceneY() - ((double) HEIGHT / 2));

        Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(1000),
                COUNT, COLUMNS,
                WIDTH, HEIGHT
        );

        animation.setCycleCount(1);

        return new SimpleEntry<>(imageView, animation);
    }
}