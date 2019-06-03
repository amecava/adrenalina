package it.polimi.ingsw.client.view.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ButtonWeapon extends Button {

    int cardId;
    String name;
    boolean loaded = true;
    String colorOfSpawn;

    public ButtonWeapon(int id, String name, ImageView card) {
        this.cardId = id;
        this.name = name;
        super.setGraphic(card);
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ButtonWeapon.this.setInfoCard();
            }
        });
    }

    public void setInfoCard() {
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "askCardInfo");
                JsonQueue.add("cardId", Integer.toString(ButtonWeapon.this.cardId));
                JsonQueue.send();
            }
        });
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int id) {
        this.cardId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setColorOfSpawn(String color) {
        this.colorOfSpawn = color;
    }

    public String getColorOfSpawn() {
        return this.colorOfSpawn;
    }
}
