package it.polimi.ingsw.client.view.gui;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class ButtonPowerUp extends Button {
    String name;
    String color;


    public ButtonPowerUp(String name, String color, ImageView card) {
        this.name = name;
        this.color = color;
        super.setGraphic(card);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
