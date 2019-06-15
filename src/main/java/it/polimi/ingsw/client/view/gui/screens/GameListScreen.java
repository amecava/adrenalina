package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.handlers.Notifications;
import it.polimi.ingsw.client.view.gui.animations.Explosion;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class GameListScreen {

    private GameListScreen() {

        //
    }

    private static VBox games = new VBox();
    private static ScrollPane scrollPane = new ScrollPane();

    private static boolean empty = true;

    public static void generateScreen() {

        BorderPane borderPane = GUIView.createBorderPane(true, false);

        scrollPane.setMaxHeight(200);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setMaxWidth(800);
        scrollPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        scrollPane.setContent(games);

        borderPane.setCenter(scrollPane);

        HBox newGame = new HBox();
        newGame.setSpacing(20);

        HBox show = new HBox();
        show.setSpacing(20);

        Button createGame = new GameButton("crea Partita");

        Label gameName = new Label("Nome:");
        gameName.setTextFill(Color.WHITE);
        gameName.setWrapText(true);

        TextField insertGameName = new TextField();
        insertGameName.setOnKeyTyped(keyEvent -> {

            if (keyEvent.getCharacter().equals(" ")) {
                ((TextField) keyEvent.getSource()).deletePreviousChar();
            }
        });
        insertGameName.setPrefSize(80, 30);

        Label numberOfDeaths = new Label("Morti:");
        numberOfDeaths.setTextFill(Color.WHITE);
        numberOfDeaths.setWrapText(true);

        TextField insertNumberOdDeaths = new TextField();
        insertNumberOdDeaths.setOnKeyTyped(keyEvent -> {

            if (insertNumberOdDeaths.getCharacters().toString().length() > 1) {

                insertNumberOdDeaths.deletePreviousChar();

            } else if (!insertNumberOdDeaths.getCharacters().toString().matches("([5-8])")) {

                insertNumberOdDeaths.deletePreviousChar();
                Notifications.createNotification("error", "Metti un numero da 5 a 8");
            }
        });

        insertNumberOdDeaths.setPrefSize(60, 20);
        Label frenzy = new Label("Frensia finale:");
        frenzy.setTextFill(Color.WHITE);
        frenzy.setWrapText(true);

        CheckBox checkBoxFrenzy = new CheckBox();
        Button confirmGame = new GameButton("crea");

        /////////////////////////////////////////////
        confirmGame.setOnMouseClicked(mouseEvent -> {

            JsonQueue.add("method", "askCreateGame");
            JsonQueue.add("gameId", insertGameName.getText());
            JsonQueue.add("numberOfDeaths", insertNumberOdDeaths.getText());
            JsonQueue.add("frenzy", checkBoxFrenzy.isSelected() ? "frenesia" : "");

            JsonQueue.send();
        });
        show.getChildren()
                .addAll(gameName, insertGameName, numberOfDeaths, insertNumberOdDeaths, frenzy,
                        checkBoxFrenzy, confirmGame);
        show.setVisible(false);
        show.setMinWidth(500);
        newGame.getChildren().addAll(createGame, show);
        createGame.setOnMouseClicked(mouseEvent -> show.setVisible(!show.isVisible()));
        borderPane.setBottom(newGame);
        BorderPane.setAlignment(newGame, Pos.BOTTOM_LEFT);
        createGame.prefHeightProperty().bind(show.prefHeightProperty());
        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }

    public static void updateScreen(JsonObject object) {

        Platform.runLater(() -> {

            JsonArray jsonArray = object.getJsonArray("gameList");

            games.setPrefWidth(scrollPane.getMaxWidth());
            games.setBackground(new Background(
                    new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

            if (jsonArray.isEmpty()) {

                empty = true;

                Button game = new Button();
                game.setBackground(new Background(
                        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                game.setMinWidth(games.getMaxWidth());
                game.setPrefWidth(games.getMaxWidth());
                game.setPrefHeight(200);
                game.setText("Non sono ancora state create partite.\n"
                        + "Per crearne una clicca il pulsante qui sotto.");

                game.setStyle(" -fx-text-inner-color: white; -fx-font: 20px Silom");
                game.setTextFill(Color.WHITE);
                game.setOpacity(0.6);
                games.getChildren().add(game);
            }

            jsonArray.stream().map(JsonValue::asJsonObject).forEach(x -> {

                StringBuilder infos = new StringBuilder();

                infos.append("Nome partita: ").append(x.getString("gameId")).append("\n");
                infos.append("Morti: ").append(x.getInt("numberOfDeaths")).append("\n");
                infos.append("Frenesia finale: ").append(x.getBoolean("frenzy") ? "Sì\n" : "No\n");
                infos.append("Giocatori connessi: " + x.getJsonArray("playerList")
                        .stream()
                        .map(JsonValue::asJsonObject)
                        .map(y -> y.getString("playerId") + ": " + y
                                .getString("character") + (
                                y.getBoolean("connected")
                                        ? "" : " (disconnesso)"))
                        .collect(Collectors.toList()));

                Button game = new Button();
                game.setBackground(new Background(
                        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                game.setText(infos.toString());
                game.setWrapText(true);
                game.setId(x.getString("gameId"));
                game.setTextFill(Color.WHITE);
                game.setMinWidth(games.getMaxWidth());
                game.setPrefWidth(games.getMaxWidth());
                game.setPrefHeight(200);
                game.setOpacity(0.6);
                game.setStyle(" -fx-text-inner-color: white; -fx-font: 20px Silom");

                game.setOnMouseEntered(mouseEvent -> {

                    ((Button) (mouseEvent.getSource())).setOpacity(100);
                    GUIView.getCurrentStage().getScene().setCursor(Cursor.HAND);
                });

                game.setOnMouseExited(mouseEvent -> {

                    ((Button) (mouseEvent.getSource())).setOpacity(0.6);
                    GUIView.getCurrentStage().getScene().setCursor(Cursor.DEFAULT);
                });

                game.setOnMouseClicked(mouseEvent -> {

                    if (!x.getBoolean("gameStarted")) {

                        selectCharacterScreen(game.getId());

                    } else {

                        Notifications.createNotification("error", "La partita è già iniziata!");
                    }
                });

                if (!empty) {

                    games.getChildren().stream()
                            .filter(y -> y.getId().equals(x.getString("gameId")))
                            .findFirst()
                            .ifPresent(y -> games.getChildren().remove(y));
                } else {

                    games.getChildren().clear();

                    empty = false;
                }

                games.getChildren().add(game);
            });
        });
    }

    private static void selectCharacterScreen(String game) {

        BorderPane borderPane = GUIView.createBorderPane(true, false);

        VBox vBox = new VBox();
        vBox.setSpacing(50);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label(game);
        label.setFont(Font.font("Silom", FontWeight.BOLD, 70));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);

        HBox characters = new HBox();
        characters.setAlignment(Pos.CENTER);
        characters.setSpacing(20);

        Button back = new GameButton("indietro");

        back.setOnMouseClicked(mouseEvent ->

            Platform.runLater(GameListScreen::generateScreen)
        );

        vBox.getChildren().addAll(label, characters, back);

        Images.playersMap.forEach((key, value) -> {

            ImageView imageView = new ImageView(value);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);

            Button button = new GameButton(imageView);

            button.setOnMouseClicked(mouseEvent -> {

                Entry<ImageView, Animation> entry = Explosion.getExplosion(4, mouseEvent);

                entry.getValue().setOnFinished(y -> {

                    borderPane.getChildren().remove(entry.getKey());

                    JsonQueue.add("method", "selectGame");

                    JsonQueue.add("gameId", game);
                    JsonQueue.add("character", key);

                    JsonQueue.send();

                });

                entry.getValue().play();

                borderPane.getChildren().add(entry.getKey());
            });

            characters.getChildren().add(button);

        });

        characters.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }
}
