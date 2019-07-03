package it.polimi.ingsw.client.view.gui.buttons;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javax.json.JsonArray;
import javax.json.JsonValue;

public class ButtonSquare extends AbstractButton {

    private String color;
    private int squareId;

    private boolean spawn = false;
    private boolean playerPosition = false;

    private static JsonArray available;
    private static EventHandler<MouseEvent> eventHandler1;
    private static EventHandler<MouseEvent> eventHandler2;

    private FadeTransition fadeTransition;

    public ButtonSquare() {

        this.setMouseTransparent(true);
    }

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

    public void setPlayerPosition(boolean position) {

        this.playerPosition = position;

        this.update();
    }

    public Boolean isPresent() {

        return this.color != null;
    }

    public static void setOnMouse1(JsonArray jsonArray, EventHandler<MouseEvent> mouseEvent) {

        available = jsonArray;
        eventHandler1 = mouseEvent;
    }

    public static void setOnMouse2(EventHandler<MouseEvent> mouseEvent) {

        eventHandler2 = mouseEvent;
    }

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
