package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
* Final screen in which the chart is shown.
 */
public class EndGameScreen {

    /**
     * box containing all the players characters
     */
    private static HBox characters = new HBox();

    /**
     * a box containing the centre of the stage
     */
    private static VBox center = new VBox();

    /**
     * creates the end game screen
     */
    private EndGameScreen() {

        //
    }

    /**
     * generates the end game screen with the winners
     * @param object containing the information of the winning players
     */
    public static void generateScreen(JsonObject object) {

        BorderPane borderPane = GUIView.createBorderPane(true, false);

        Label gameEndedLabel = new Label();
        gameEndedLabel.setFont(Font.font("Silom", FontWeight.BOLD, FontPosture.ITALIC, 30));
        gameEndedLabel.setTextFill(Color.WHITE);
        gameEndedLabel.setAlignment(Pos.CENTER);
        gameEndedLabel.setText(
                "La partita è terminata: complimenti " + object.getJsonArray("array")
                        .getJsonObject(0).getString("playerId") + "!");

        characters.setSpacing(60);
        characters.setAlignment(Pos.CENTER);

        for (JsonValue playersObject : object.getJsonArray("array")) {

            ImageView player = new ImageView(
                    Images.playersMap.get(playersObject.asJsonObject().getString("character")));
            player.setPreserveRatio(true);
            player.setFitHeight(180);

            Label label = new Label();
            label.setFont(Font.font("Silom", FontWeight.BOLD, FontPosture.ITALIC, 30));
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);
            label.setText(new StringBuilder()
                    .append(playersObject.asJsonObject().getInt("points"))
                    .append(" punti")
                    .toString());

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(20);

            vBox.getChildren().addAll(player, label);

            characters.getChildren().add(vBox);
        }

        center.setAlignment(Pos.CENTER);
        center.setSpacing(40);
        center.getChildren().addAll(gameEndedLabel, characters);

        borderPane.setCenter(center);
        BorderPane.setAlignment(center, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }

}
