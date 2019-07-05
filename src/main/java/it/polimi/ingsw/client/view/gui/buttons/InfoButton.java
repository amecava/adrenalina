package it.polimi.ingsw.client.view.gui.buttons;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * button for displaying information on actions that the player can do during his action
 */
public class InfoButton extends javafx.scene.control.Button {

    /**
     * creating the info button
     * @param infoText info text displaying in the info button
     */
    public InfoButton(String infoText) {

        super();

        this.setGraphic(new ImageView());
        this.setContentDisplay(ContentDisplay.CENTER);
        this.setText(infoText);
        this.setFont(Font.font("Silom", 15));
        this.setTextFill(Color.WHITE);
        this.setMouseTransparent(true);
        this.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
