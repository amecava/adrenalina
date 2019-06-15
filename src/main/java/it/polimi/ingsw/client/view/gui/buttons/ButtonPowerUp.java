package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ButtonPowerUp extends AbstractButton {

    private String name;
    private String color;

    private static EventHandler<MouseEvent> eventHandler;

    public ButtonPowerUp(String name, String color, ImageView card) {

        super();

        this.name = name;
        this.color = color;

        super.setGraphic(card);

        this.setOnMouseClicked(eventHandler);
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getColor() {

        return this.color;
    }

    public void setColor(String color) {

        this.color = color;
    }

    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent) {

        eventHandler = mouseEvent;
    }

    public void update() {

        this.setOnMouseClicked(eventHandler);
    }
}
