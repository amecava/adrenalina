package it.polimi.ingsw.client.view.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ButtonPowerUp extends Button {
    String name;
    String color;
    private static EventHandler<MouseEvent> eventHandler;

    public ButtonPowerUp(String name, String color, ImageView card) {
        this.name = name;
        this.color = color;
        super.setGraphic(card);
        this.setOnMouseClicked(eventHandler);
    }

    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent){

        eventHandler = mouseEvent;
    }

    public void update() {

        this.setOnMouseClicked(eventHandler);
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
