package it.polimi.ingsw.client.view.gui.buttons;

import it.polimi.ingsw.client.view.gui.animations.Images;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * button that has particular graphics.
 * This buttons are used as selection buttons in order to select
 * the wanted action
 */
public class GameButton extends AbstractButton {

    /**
     * creating the game button
     * @param imageView image displayed on the button
     */
    public GameButton(ImageView imageView) {

        super();

        this.setGraphic(imageView);
    }

    /**
     * creating the button with no image
     * @param s parameter for the game button needed in order to be created
     */
    public GameButton(String s) {

        super();

        ImageView imageView = new ImageView(Images.imagesMap.get("button"));

        imageView.setFitHeight(36);
        imageView.setFitWidth(240);

        this.setText(s);
        this.setGraphic(imageView);

        this.setFont(Font.font("Silom", 15));
        this.setTextFill(Color.rgb(203, 203, 203));
    }
}

