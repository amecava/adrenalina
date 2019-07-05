package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * weapon button for expressing particular weapon features in a button node
 */
public class ButtonWeapon extends ButtonFlip {

    /**
     * color of the weapon
     */
    private String color;
    /**
     * id of the weapon
     */
    private int cardId;
    /**
     * expresses if the weapon is loaded or not
     */
    private boolean loaded = true;
    /**
     * event handler linked with the mouse event on the weapon
     */
    private static EventHandler<MouseEvent> eventHandler;

    /**
     * creating a weapon button
     * @param id of the weapon
     * @param back  image of the button
     * @param card front image of the weapon button
     * @param axis of rotation
     */
    public ButtonWeapon(int id, ImageView back, ImageView card, Point3D axis) {

        super(back, card, axis);

        this.cardId = id;

        this.setOnMouseClicked(eventHandler);
    }

    /**
     * gets the color of the weapon
     * @return the color of the weapon
     */
    public String getColor() {

        return this.color;
    }

    /**
     * sets the color of the weapon
     * @param color of the weapon
     */
    public void setColor(String color) {

        this.color = color;
    }

    /**
     * gets the id of the card
     * @return the id of the card
     */
    public int getCardId() {

        return this.cardId;
    }

    /**
     * sets the card id
     * @param cardId the card id
     */
    public void setCardId(int cardId) {

        this.cardId = cardId;
    }

    /**
     * checks if the weapon card is loaded or not
     * @return true if the weapon is loaded
     */
    public boolean isLoaded() {

        return this.loaded;
    }

    /**
     * sets the weapon loaded or unloaded
     * @param loaded changes the state of the weapon based on its value
     */
    public void setLoaded(boolean loaded) {

        this.loaded = loaded;
    }

    /**
     * links the mouse event with the right event handler
     * @param mouseEvent event handler for responding to a click or a mouse event
     */
    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent) {

        eventHandler = mouseEvent;
    }

    /**
     * whan a new weapon button is created the update method is called so that
     * the right static eventt handler gets associated with the mouse event
     */
    public void update() {

        this.setOnMouseClicked(eventHandler);
    }
}
