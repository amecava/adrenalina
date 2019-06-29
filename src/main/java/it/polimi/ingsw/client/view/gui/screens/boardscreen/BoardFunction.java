package it.polimi.ingsw.client.view.gui.screens.boardscreen;

import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.ButtonSquare;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class BoardFunction {

    private BoardFunction() {

        //
    }

    public static List<ButtonSquare> getSquareList(AnchorPane board) {

        return board.getChildren().stream()
                .filter(x -> x.getId().equals("squares"))
                .map(x -> (VBox) x)
                .flatMap(x -> x.getChildren().stream().map(y -> (HBox) y))
                .flatMap(x -> x.getChildren().stream().map(y -> (StackPane) y))
                .flatMap(n -> n.getChildren().stream())
                .filter(n -> n.getId().equals("button"))
                .map(n -> (ButtonSquare) n)
                .filter(ButtonSquare::isPresent)
                .collect(Collectors.toList());
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
            int width, int height, SequentialTransition sequentialTransition) {

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

            final ButtonWeapon rotatedButton = new ButtonWeapon(id, backImageRotated, cardImageRotated, axis);

            rotatedButton.setColor(color);

            rotatedButton.setOnMouseClicked(mouseEvent -> {

                JsonQueue.add("method", "askCardInfo");
                JsonQueue.add("cardId", Integer.toString(
                        ((ButtonWeapon) mouseEvent.getSource())
                                .getCardId()));

                JsonQueue.send();
            });

            TranslateTransition move = new TranslateTransition(Duration.millis(500), rotatedButton);

            move.setFromX(calculateDeckDistanceX(rotate, children.size()));
            move.setFromY(calculateDeckDistanceY(rotate, children.size()));
            move.setToX(0);
            move.setToY(0);

            RotateTransition rotation = new RotateTransition(Duration.millis(200), rotatedButton);

            rotation.setFromAngle(-rotate);
            rotation.setToAngle(0);

            rotation.setOnFinished(actionEvent -> {

                rotatedButton.flipTransition(Duration.millis(300), Event::consume).play();
            });

            children.add(rotatedButton);

            sequentialTransition.getChildren().addAll(move, rotation);
        }
    }

    private static int calculateDeckDistanceX(int rotate, int size) {

        if (rotate == 0) {

            return 256 - size * 82;

        } else if (rotate == 90) {

            return 633;
        }

        return -11;
    }

    private static int calculateDeckDistanceY(int rotate, int size) {

        if (rotate == 0) {

            return 158;

        } else if (rotate == 90) {

            return -30 - size * 82;
        }

        return -143 - size * 82;
    }
}
