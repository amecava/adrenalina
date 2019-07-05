package it.polimi.ingsw.client.view.gui.buttons;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * button used for rotating the images in the buttons
 */
abstract class ButtonFlip extends AbstractButton {

    /**
     * axes of rotation
     */
    private Point3D axis;
    /**
     * boolean property for showing the front or the rear of the image in the button
     */
    private BooleanProperty showFront = new SimpleBooleanProperty(false);

    /**
     * creating the button that needs to be rotated
     * @param back image of the back
     * @param card image of the front
     * @param axis axis point of rotation
     */
    ButtonFlip(ImageView back, ImageView card, Point3D axis) {

        super();

        super.setGraphic(card);

        card.imageProperty().bind(Bindings.when(this.showFront).then(card.getImage())
                .otherwise(back.getImage()));

        this.axis = axis;

        if (axis.equals(Rotate.Y_AXIS)) {

            card.scaleXProperty().bind(Bindings.when(this.showFront).then(-1d).otherwise(1d));

        } else {

            card.scaleYProperty().bind(Bindings.when(this.showFront).then(-1d).otherwise(1d));
        }
    }

    /**
     * Creates the transition for the button to flip
     * @param duration of the transition
     * @param event event linked with finish of the rotation
     * @return a sequential transition after the rotation
     */
    public SequentialTransition flipTransition(Duration duration, EventHandler<ActionEvent> event) {

        RotateTransition rotator1 = new RotateTransition(duration, this);
        rotator1.setAxis(this.axis);
        rotator1.setFromAngle(0);
        rotator1.setToAngle(90);
        rotator1.setInterpolator(Interpolator.LINEAR);
        rotator1.setCycleCount(1);

        rotator1.setOnFinished(actionEvent -> this.flip());

        RotateTransition rotator2 = new RotateTransition(duration, this);
        rotator2.setAxis(this.axis);
        rotator2.setFromAngle(90);
        rotator2.setToAngle(180);
        rotator2.setInterpolator(Interpolator.LINEAR);
        rotator2.setCycleCount(1);

        rotator2.setOnFinished(event);

        return new SequentialTransition(rotator1, rotator2);
    }

    /**
     * flips the button and the images
     */
    private void flip() {

        showFront.setValue(!showFront.get());
    }
}
