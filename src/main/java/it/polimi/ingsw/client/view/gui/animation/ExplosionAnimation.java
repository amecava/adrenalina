package it.polimi.ingsw.client.view.gui.animation;

import it.polimi.ingsw.client.view.gui.Images;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class ExplosionAnimation {

    private ExplosionAnimation() {

        //
    }

    private static final int COLUMNS = 8;
    private static final int COUNT = 64;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    public static Entry<ImageView, Animation> getExplosion(int id, MouseEvent event) {

        ImageView imageView = new ImageView(Images.explosionsMap.get("explosion" + id));
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

        imageView.setX(event.getSceneX() - ((double) WIDTH / 2));
        imageView.setY(event.getSceneY() - ((double) HEIGHT / 2));

        Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(1000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );

        animation.setCycleCount(1);

        return new SimpleEntry<>(imageView, animation);
    }
}
