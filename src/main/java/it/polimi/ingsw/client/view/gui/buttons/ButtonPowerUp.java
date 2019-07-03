package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

public class ButtonPowerUp extends ButtonFlip {

    private String name;
    private String color;
    private String targetType;
    private double args;
    private boolean hasCost;

    private static EventHandler<MouseEvent> eventHandler;

    public ButtonPowerUp(String name, String color, String targetType, double args, boolean hasCost,
            ImageView back, ImageView card) {

        super(back, card, Rotate.Y_AXIS);

        this.name = name;
        this.color = color;
        this.targetType = targetType;
        this.args = args;
        this.hasCost = hasCost;

        this.setOnMouseClicked(eventHandler);
    }

    public String getTargetType() {

        return this.targetType;
    }

    public double getArgs() {

        return this.args;
    }

    public boolean hasCost() {

        return this.hasCost;
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
