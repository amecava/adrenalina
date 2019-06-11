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

        imageView.setFitHeight(36);
        imageView.setFitWidth(240);

        this.setContentDisplay(ContentDisplay.CENTER);
        this.setFont(Font.font("Silom", 15));
        this.setTextFill(Color.rgb(203, 203, 203));
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setOnMouseExited(GUIView.smaller);
        this.setOnMouseEntered(GUIView.bigger);
    }
}
