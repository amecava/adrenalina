package it.polimi.ingsw.client.view.gui.buttons;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

/**
 * button for showing and implementing power up features
 */
public class ButtonPowerUp extends ButtonFlip {

    /**
     * name of the power up
     */
    private String name;
    /**
     * color of the power up
     */
    private String color;
    /**
     * target type of the power up
     */
    private String targetType;
    /**
     * double needed to correctly execute the power up effect
     */
    private double args;
    /**
     * expresses if the power up has a linked cost for using its  effect
     */
    private boolean hasCost;

    /**
     * event handler linked with the mouse clicked
     */
    private static EventHandler<MouseEvent> eventHandler;

    /**
     * creating the power up
     * @param name name of the power up
     * @param color color of the power up
     * @param targetType target type of the power up
     * @param args double needed to correctly execute the power up effect
     * @param hasCost event handler linked with the mouse clicked
     * @param back back image of the button
     * @param card front image of the button
     */
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

    /**
     * gets the target type of the power up
     * @return the target type of the power up
     */
    public String getTargetType() {

        return this.targetType;
    }

    /**
     * gets the arguments  of the power up
     * @return the arguments of the power up
     */
    public double getArgs() {

        return this.args;
    }

    /**
     * is true if the power up has a linked cost
     * @return true if the power up has a linked cost
     */
    public boolean hasCost() {

        return this.hasCost;
    }

    /**
     * gets the name of the power up
     * @return the name of the power up
     */
    public String getName() {

        return this.name;
    }

    /**
     * sets the name of the power up
     * @param name the name of the power up
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * gets the color of the power up
     * @return the color of the power up
     */
    public String getColor() {

        return this.color;
    }

    /**
     * sets the color of the power up
     * @param color the color of the power up
     */
    public void setColor(String color) {

        this.color = color;
    }

    /**
     * sets the event handler linked with a mouse click
     * @param mouseEvent the event handler linked with the mouse click
     */
    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent) {

        eventHandler = mouseEvent;
    }

    /**
     * when a new power up button is created the static event handler gets linked with a
     * mouse event  on the power up button
     */
    public void update() {

        this.setOnMouseClicked(eventHandler);
    }
}
