package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.animations.Explosion;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import java.util.Map.Entry;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class GameNotStartedScreen {

    private GameNotStartedScreen() {

        //
    }

    private static HBox characters = new HBox();
    private static Label countDown = new Label();

    public static void generateScreen() {

        BorderPane borderPane = GUIView.createBorderPane(true, false);

        VBox center = new VBox();
        center.setSpacing(30);
        center.setAlignment(Pos.CENTER);

        HBox boards = new HBox();
        boards.setMouseTransparent(false);
        boards.setAlignment(Pos.CENTER);

        Images.boardsMap.forEach((key, entry) -> {

            ImageView imageView = new ImageView(entry);

            imageView.setPreserveRatio(true);
            imageView.setFitHeight(150);

            Button board = new GameButton(imageView);

            board.setOnMouseClicked(mouseEvent -> {

                Entry<ImageView, Animation> x = Explosion.getExplosion(4, mouseEvent);

                x.getValue().setOnFinished(y -> {

                    borderPane.getChildren().remove(x.getKey());

                    JsonQueue.add("method", "voteBoard");
                    JsonQueue.add("vote", key.replace("board", ""));

                    JsonQueue.send();

                });

                x.getValue().play();

                borderPane.getChildren().add(x.getKey());
            });

            boards.getChildren().add(board);
        });

        characters.setSpacing(60);
        characters.setAlignment(Pos.CENTER);

        Images.playersMap.forEach((key, value) -> {

            ImageView desaturated = new ImageView(value);
            desaturated.setPreserveRatio(true);
            desaturated.setFitHeight(180);
            desaturated.setOpacity(0.2);
            ColorAdjust desaturate = new ColorAdjust();
            desaturate.setSaturation(-1);
            desaturated.setEffect(desaturate);

            Label label = new Label();
            label.setFont(Font.font("Silom", 30));
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(20);

            vBox.getChildren().addAll(desaturated, label);
            vBox.setId(key);

            characters.getChildren().add(vBox);
        });

        countDown.setWrapText(true);
        countDown.setTextFill(Color.WHITE);
        countDown.setFont(Font.font("Silom", 20));
        countDown.setAlignment(Pos.CENTER);

        center.getChildren().addAll(boards, characters, countDown);

        borderPane.setCenter(center);
        BorderPane.setAlignment(center, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }

    public static void updateScreen(JsonObject object) {

        Platform.runLater(() -> {

            object.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        VBox characterVBox = characters.getChildren().stream()
                                .map(y -> (VBox) y)
                                .filter(y -> y.getId().equals(x.getString("character")))
                                .findFirst()
                                .orElse(null);

                        if (characterVBox != null
                                && characterVBox.getChildren().get(0).getOpacity() == 0.2) {

                            ImageView imageView = (ImageView) characterVBox.getChildren().get(0);
                            imageView.setFitHeight(200);
                            imageView.setOpacity(1);
                            ((ColorAdjust) imageView.getEffect()).setSaturation(0);

                            RotateTransition rotator = new RotateTransition(Duration.millis(300),
                                    imageView);
                            rotator.setAxis(Rotate.Y_AXIS);
                            rotator.setFromAngle(0);
                            rotator.setToAngle(360);
                            rotator.setInterpolator(Interpolator.LINEAR);
                            rotator.setCycleCount(1);

                            rotator.play();

                            Label label = (Label) characterVBox.getChildren().get(1);
                            label.setText(x.getString("playerId"));
                        }
                    });

            int count = object.getInt("countdown");

            if (count < 10) {

                countDown.setText("La partita inizierà tra " + count + " secondi.");

            } else {

                countDown.setText("In attesa di tre giocatori connessi.");
            }
        });
    }
}
