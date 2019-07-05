package it.polimi.ingsw.client.view.gui.buttons;

import it.polimi.ingsw.client.view.gui.GUIView;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * abstract class for extending functionality of basic javafx buttons
 */
abstract class AbstractButton extends javafx.scene.control.Button {

    /**
     * defines the variables of the abstract button
     */
    AbstractButton() {

        this.setContentDisplay(ContentDisplay.CENTER);
        this.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        ScaleTransition stBig = new ScaleTransition();
        ScaleTransition stSmall = new ScaleTransition();

        stBig.setFromX(1.0);
        stBig.setFromY(1.0);
        stBig.setToX(1.2);
        stBig.setToY(1.2);

        stBig.setDuration(new Duration(20));

        stSmall.setFromX(1.0);
        stSmall.setToX(1.0);
        stSmall.setFromY(1.0);
        stSmall.setToX(1.0);

        stSmall.setDuration(new Duration(20));

        this.setOnMouseEntered(mouseEvent -> {

            GUIView.getCurrentStage().getScene().setCursor(Cursor.HAND);

            stBig.setNode(this);
            stBig.play();
        });

        this.setOnMouseExited(mouseEvent -> {

            GUIView.getCurrentStage().getScene().setCursor(Cursor.DEFAULT);

            stSmall.setNode(this);
            stSmall.play();
        });
    }
}
