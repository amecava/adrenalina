package it.polimi.ingsw.client.view.gui;


import java.awt.Color;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class ButtonSquare extends Button {

    String color;
    String id;
    Boolean present;
    boolean isSpawn;
    boolean currentPosition=false;

    private static EventHandler<MouseEvent> eventHandler;
    private static EventHandler<MouseEvent> collectEventHandler;

    public ButtonSquare(String color, String id) {
        this.color = color;
        this.id = id;
        this.present = true;
        this.setOpacity(0.0);

        this.setOnMouseClicked(eventHandler);
    }

    public ButtonSquare(boolean present) {
        this.present = present;
        this.setOpacity(0.0);
    }

    public static void setOnMouse(EventHandler<MouseEvent> mouseEvent){

        eventHandler = mouseEvent;
    }
    public  static  void setOnMouseCollect(EventHandler<MouseEvent> mouseEvent){
        collectEventHandler=mouseEvent;
    }

    public void update() {
        if (this.currentPosition){
            this.setOnMouseClicked(collectEventHandler);
        }
        else {
            this.setOnMouseClicked(eventHandler);
        }
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getButtonSquareId() {
        return this.id;
    }

    public void setButtonSqaureId(String id) {
        this.id = id;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }


    public boolean isSpawn() {
        return isSpawn;
    }

    public void setSpawn(boolean spawn) {
        isSpawn = spawn;
        this.setOnMouseClicked(collectEventHandler);
    }


    public void setCurrentPosition (boolean iscurrentPosition){
        this.currentPosition=iscurrentPosition;
    }

    public boolean isCurrentPosition() {
        return currentPosition;
    }
}
