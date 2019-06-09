package it.polimi.ingsw.client.view.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameButton extends Button {

    public GameButton(String s, ImageView imageView) {

        super(s, imageView);

        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        this.setContentDisplay(ContentDisplay.CENTER);
        this.setFont(Font.font("Silom", 18));
        this.setTextFill(Color.rgb(203, 203, 203));
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setOnMouseExited(GUIView.smaller);
        this.setOnMouseEntered(GUIView.bigger);
    }
}
