package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class ButtonSquare extends AbstractButton {

    private String color;
    private int squareId;

    private boolean spawn = false;
    private boolean playerPosition = false;

    private static EventHandler<MouseEvent> eventHandler1;
    private static EventHandler<MouseEvent> eventHandler2;

    public ButtonSquare() {

        this.setMouseTransparent(true);
    }

    public ButtonSquare(String color, int squareId) {

        super();

        this.color = color;
        this.squareId = squareId;

        this.setOpacity(0.0);

        this.setOnMouseClicked(eventHandler1);
    }

    public String getColor() {

        return this.color;
    }


    public int getSquareId() {

        return this.squareId;
    }

    public boolean isSpawn() {

        return this.spawn;
    }

    public void setSpawn(boolean spawn) {

        this.spawn = spawn;
    }

    public void setPlayerPosition() {

        this.playerPosition = true;

        this.setOnMouseClicked(eventHandler2);
    }

    public Boolean isPresent() {

        return this.color != null;
    }

    public static void setOnMouse1(EventHandler<MouseEvent> mouseEvent) {

        eventHandler1 = mouseEvent;
    }

    public static void setOnMouse2(EventHandler<MouseEvent> mouseEvent) {

        eventHandler2 = mouseEvent;
    }

    public void update() {

        if (!this.playerPosition) {

            this.setOnMouseClicked(eventHandler1);

        } else {

            this.setOnMouseClicked(eventHandler2);
        }
    }
}
