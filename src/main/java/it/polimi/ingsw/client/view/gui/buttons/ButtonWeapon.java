package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ButtonWeapon extends ButtonFlip {

    private String color;
    private int cardId;

    private boolean loaded = true;

    private static EventHandler<MouseEvent> eventHandler;

    public ButtonWeapon(int id, ImageView back, ImageView card, Point3D axis) {

        super(back, card, axis);

        this.cardId = id;

        this.setOnMouseClicked(eventHandler);
    }

    public String getColor() {

        return this.color;
    }

    public void setColor(String color) {

        this.color = color;
    }

    public int getCardId() {

        return this.cardId;
    }

    public void setCardId(int cardId) {

        this.cardId = cardId;
    }

    public boolean isLoaded() {

        return this.loaded;
    }

    public void setLoaded(boolean loaded) {

        this.loaded = loaded;
    }

    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent) {

        eventHandler = mouseEvent;
    }

    public void update() {

        this.setOnMouseClicked(eventHandler);
    }
}
