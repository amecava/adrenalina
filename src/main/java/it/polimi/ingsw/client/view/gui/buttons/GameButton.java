package it.polimi.ingsw.client.view.gui.buttons;

import it.polimi.ingsw.client.view.gui.animations.Images;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameButton extends AbstractButton {

    public GameButton(ImageView imageView) {

        super();

        this.setGraphic(imageView);
    }

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

