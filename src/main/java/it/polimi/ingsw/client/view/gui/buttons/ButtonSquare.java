package it.polimi.ingsw.client.view.gui.buttons;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javax.json.JsonArray;
import javax.json.JsonValue;

/**
 * Square button for expressing particular square features
 */
public class ButtonSquare extends AbstractButton {

    /**
     * color of the square
     */
    private String color;
    /**
     * square's id
     */
    private int squareId;
    /**
     * expresses if the square is a spawn square  or not
     */
    private boolean spawn = false;
    /**
     * expresses if the referenced square is the current position of the linked player
     */
    private boolean playerPosition = false;

    /**
     * expresses if the square is a reachable square.
     */
    private static JsonArray available;
    /**
     * one type of event handler associated with a mouse event on the square
     */
    private static EventHandler<MouseEvent> eventHandler1;
    /**
     * another  type of event handler associated with a mouse event on the square
     */
    private static EventHandler<MouseEvent> eventHandler2;

    /**
     * transition for changing the color of the square when it's highlighted
     */
    private FadeTransition fadeTransition;

    /**
     * creating the button square
     */
    public ButtonSquare() {

        this.setMouseTransparent(true);
    }

    /**
     * creating the button sqaure
     * @param color color of the square
     * @param squareId id of the square
     */
    public ButtonSquare(String color, int squareId) {

        super();

        this.color = color;
        this.squareId = squareId;

        this.setOpacity(0.0);

        this.setOnMouseClicked(eventHandler1);

        this.fadeTransition = new FadeTransition(Duration.millis(500), this);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * gets the color of the square
     * @return the color of the square
     */
    public String getColor() {

        return this.color;
    }

    /**
     * gets the id of the square
     * @return the id of the square
     */
    public int getSquareId() {

        return this.squareId;
    }

    /**
     * checks if the square is a spawn square
     * @return true if the square is a spawn square
     */
    public boolean isSpawn() {

        return this.spawn;
    }

    /**
     * sets the square as a spawn square  or as a regular square
     * @param spawn expresses the type of the square
     */
    public void setSpawn(boolean spawn) {

        this.spawn = spawn;
    }

    /**
     * sets if the linked player is currently in this square
     * @param position true if this square is  the current
     * position of the linked player.
     */
    public void setPlayerPosition(boolean position) {

        this.playerPosition = position;

        this.update();
    }

    /**
     * boolean that expresses if the square is present in the board or not due to
     * the fact that the board is 4*3 , but not all the squares are present.
     * @return true if the square is present
     */
    public Boolean isPresent() {

        return this.color != null;
    }

    /**
     * sets the event handler for a mouse event on the square
     * @param jsonArray the list of reachable squares from the current position
     * @param mouseEvent event handler that will be linked
     */
    public static void setOnMouse1(JsonArray jsonArray, EventHandler<MouseEvent> mouseEvent) {

        available = jsonArray;
        eventHandler1 = mouseEvent;
    }

    /**
     * sets the event handler for a mouse event on the square
     * @param mouseEvent event handler associated with the mouse event
     */
    public static void setOnMouse2(EventHandler<MouseEvent> mouseEvent) {

        eventHandler2 = mouseEvent;
    }

    /**
     * updates the state of a square when highlighted or when clicked
     */
    public void update() {


        this.setMouseTransparent(true);

        this.fadeTransition.stop();
        this.setOpacity(0.0);

        if (!this.playerPosition && eventHandler1 != null) {

            available.stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        if (x.getString("color").equals(this.color)
                                && x.getInt("squareId") == this.squareId) {

                            this.setMouseTransparent(false);

                            this.setOnMouseClicked(eventHandler1);

                            fadeTransition.play();
                        }
                    });

        } else if (this.playerPosition && eventHandler2 != null) {

            this.setMouseTransparent(false);

            this.setOnMouseClicked(eventHandler2);

            fadeTransition.play();
        }
    }
}
