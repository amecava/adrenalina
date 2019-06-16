package it.polimi.ingsw.client.view.gui.screens.boardscreen;

import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

class BoardFunction {

    private BoardFunction() {

        //
    }

    static synchronized void removeCardFromSpawn(ObservableList<Node> children, List<JsonObject> jsonList) {

        new ArrayList<>(children).stream()
                .map(x -> (ButtonWeapon) x)
                .forEach(x -> {

                    if (jsonList.stream().noneMatch(
                            z -> z.getString("color").equals(x.getColor()) && z
                                    .getJsonArray("tools").stream()
                                    .map(JsonValue::asJsonObject)
                                    .anyMatch(w -> w.getInt("id") == x.getCardId()))) {

                        x.flipTransition(Duration.millis(300), actionEvent -> {

                            children.remove(x);

                        }).play();
                    }
                });
    }

    static synchronized void addCardToSpawn(ObservableList<Node> children, String color, int id, Point3D axis, int rotate,
            int width, int height, StackPane weaponDeck, SequentialTransition sequentialTransition) {

        if (children.stream().map(z -> (ButtonWeapon) z)
                .noneMatch(z -> z.getColor().equals(color) && z.getCardId() == id)) {

            ImageView backImage = new ImageView(Images.weaponsMap.get(0));
            ImageView cardImage = new ImageView(Images.weaponsMap.get(id));

            backImage.setRotate(rotate);
            cardImage.setRotate(rotate);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            Image rotatedImage1 = backImage.snapshot(params, null);
            Image rotatedImage2 = cardImage.snapshot(params, null);
            ImageView backImageRotated = new ImageView(rotatedImage1);
            ImageView cardImageRotated = new ImageView(rotatedImage2);

            backImageRotated.setFitWidth(width);
            backImageRotated.setFitHeight(height);
            cardImageRotated.setFitWidth(width);
            cardImageRotated.setFitHeight(height);

            final ButtonWeapon rotatedButton = new ButtonWeapon(id, backImageRotated, cardImageRotated, axis, false);

            rotatedButton.setColor(color);

            rotatedButton.setOnMouseClicked(mouseEvent -> {

                JsonQueue.add("method", "askCardInfo");
                JsonQueue.add("cardId", Integer.toString(
                        ((ButtonWeapon) mouseEvent.getSource())
                                .getCardId()));

                JsonQueue.send();
            });

            TranslateTransition move = new TranslateTransition(Duration.millis(500), rotatedButton);

            move.setFromX(0);
            move.setFromY(-600);
            move.setToX(0);
            move.setToY(0);

            RotateTransition rotation = new RotateTransition(Duration.millis(300), rotatedButton);

            rotation.setFromAngle(-rotate);
            rotation.setToAngle(0);

            rotation.setOnFinished(actionEvent -> {

                rotatedButton.flipTransition(Duration.millis(300), Event::consume).play();
            });

            children.add(rotatedButton);

            sequentialTransition.getChildren().addAll(move, rotation);
        }
    }
}
