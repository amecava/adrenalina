package it.polimi.ingsw.client.view.gui;


import java.awt.Color;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public class ButtonSquare extends Button {
    Color color;
    int id;
    Boolean empty;
    boolean isCardHolder;
    public ButtonSquare(int id, ImageView imageView){
        this.id=id;
        this.setGraphic(imageView);
    }

    public  ButtonSquare(Color color, int id){
        this.color=color;
        this.id=id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public int getButtonSquareId() {
        return id;
    }

    public void setButtonSqaureId(int id) {
        this.id = id;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public boolean isCardHolder() {
        return isCardHolder;
    }

    public void setCardHolder(boolean cardHolder) {
        isCardHolder = cardHolder;
    }
}
