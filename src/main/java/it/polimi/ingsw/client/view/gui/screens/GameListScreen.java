package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.handlers.Notifications;
import it.polimi.ingsw.client.view.gui.animations.Explosion;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import java.util.Map.Entry;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Screen for choosing the game to join or creating a new game.
 */
public class GameListScreen {

    /**
     * creates the game list screen
     */
    private GameListScreen() {

        //
    }

    private static BorderPane borderPane;


    /**
     * a list af all the available games on the server
     */
    private static VBox games = new VBox();
    /**
     * scroll pane containing the list of games
     */
    private static ScrollPane scrollPane = new ScrollPane();
    /**
     *expresses if the game list screen is empty or not
     */
    private static boolean empty = true;

    public static void generateScreen() {

        borderPane = GUIView.createBorderPane(true, false);

        scrollPane.setMaxHeight(400);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxWidth(800);
        scrollPane.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        borderPane.getStylesheets().add("scrollpane.css");

        games.setPrefWidth(scrollPane.getMaxWidth());
        games.setSpacing(20);
        games.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        scrollPane.setContent(games);

        borderPane.setCenter(scrollPane);

        VBox newGame = new VBox();
        newGame.setAlignment(Pos.CENTER);
        newGame.setSpacing(20);

        Button createGame = new GameButton("crea partita");
        createGame.setAlignment(Pos.CENTER);

        HBox newGameFieldsVBox = new HBox();
        newGameFieldsVBox.setAlignment(Pos.CENTER);
        newGameFieldsVBox.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY,
                        Insets.EMPTY)));

        HBox nameAndTextField = new HBox();
        nameAndTextField.setAlignment(Pos.CENTER);
        nameAndTextField.setSpacing(10);

        Label gameName = new Label("Nome:");
        gameName.setAlignment(Pos.CENTER);
        gameName.setTextFill(Color.WHITE);
        gameName.setWrapText(true);
        gameName.setFont(Font.font("Silom", 20));
        gameName.setTextAlignment(TextAlignment.CENTER);


        TextField insertGameName = new TextField();
        insertGameName.setAlignment(Pos.CENTER);
        insertGameName.setFont(Font.font("Silom", FontWeight.NORMAL, 20));
        insertGameName.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white");
        insertGameName.setPrefSize(80, 20);
        insertGameName.setOnKeyTyped(keyEvent -> {

            if (keyEvent.getCharacter().equals(" ")) {
                ((TextField) keyEvent.getSource()).deletePreviousChar();
            }
        });

        nameAndTextField.getChildren().addAll(gameName, insertGameName);

        HBox deathLabelAndTextField = new HBox();
        deathLabelAndTextField.setAlignment(Pos.CENTER);
        deathLabelAndTextField.setSpacing(10);

        Label numberOfDeaths = new Label("Morti:");
        numberOfDeaths.setAlignment(Pos.CENTER);
        numberOfDeaths.setTextFill(Color.WHITE);
        numberOfDeaths.setWrapText(true);
        numberOfDeaths.setFont(Font.font("Silom", 20));
        numberOfDeaths.setTextAlignment(TextAlignment.CENTER);

        TextField insertNumberOdDeaths = new TextField();
        insertNumberOdDeaths.setFont(Font.font("Silom", FontWeight.NORMAL, 20));
        insertNumberOdDeaths.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white");
        insertNumberOdDeaths.setAlignment(Pos.CENTER);

        insertNumberOdDeaths.setPrefSize(60, 12);
        insertNumberOdDeaths.setOnKeyTyped(keyEvent -> {

            if (insertNumberOdDeaths.getCharacters().toString().length() > 1) {

                insertNumberOdDeaths.deletePreviousChar();

            } else if (!insertNumberOdDeaths.getCharacters().toString().matches("([5-8])")) {

                insertNumberOdDeaths.deletePreviousChar();
                Notifications.createNotification("error", "Deve essere un numero da 5 a 8");
            }
        });

        deathLabelAndTextField.getChildren().addAll(numberOfDeaths, insertNumberOdDeaths);

        HBox frenzyLabelAndCheckBox = new HBox();
        frenzyLabelAndCheckBox.setAlignment(Pos.CENTER);
        frenzyLabelAndCheckBox.setSpacing(10);

        Label frenzy = new Label("Frensia finale:");
        frenzy.setAlignment(Pos.CENTER);
        frenzy.setTextFill(Color.WHITE);
        frenzy.setWrapText(true);
        frenzy.setFont(Font.font("Silom", 20));
        frenzy.setTextAlignment(TextAlignment.CENTER);

        CheckBox checkBoxFrenzy = new CheckBox();

        frenzyLabelAndCheckBox.getChildren().addAll(frenzy, checkBoxFrenzy);

        Button confirmGame = new GameButton("crea");

        confirmGame.setOnMouseClicked(mouseEvent -> {

            newGameFieldsVBox.setVisible(!newGameFieldsVBox.isVisible());

            if (!insertGameName.getText().equals("") && !insertNumberOdDeaths.getText().equals("")) {

                JsonQueue.add("method", "askCreateGame");
                JsonQueue.add("gameId", insertGameName.getText());
                JsonQueue.add("numberOfDeaths", insertNumberOdDeaths.getText());
                JsonQueue.add("frenzy", checkBoxFrenzy.isSelected() ? "frenesia" : "");
                JsonQueue.send();
            } else {

                Notifications.createNotification("error", "Riempi i campi nome e numero morti.");
            }
        });

        newGameFieldsVBox.getChildren().addAll(nameAndTextField, deathLabelAndTextField, frenzyLabelAndCheckBox, confirmGame);

        newGame.getChildren().addAll(createGame, newGameFieldsVBox);

        newGameFieldsVBox.setVisible(false);

        createGame.setOnMouseClicked(mouseEvent -> newGameFieldsVBox.setVisible(!newGameFieldsVBox.isVisible()));

        borderPane.setBottom(newGame);

        BorderPane.setAlignment(newGame, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }

    /**
     * this method updates the game list screen after a new method has successfully been
     * created so that avery client can see all the available games
     * @param object containing all the available games with their parameters
     */
    public static void updateScreen(JsonObject object) {

        Platform.runLater(() -> {

            JsonArray jsonArray = object.getJsonArray("gameList");

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

                StackPane pane = new StackPane();
                pane.setId(x.getString("gameId"));
                pane.setAlignment(Pos.CENTER);

                ImageView imageView = new ImageView(Images.imagesMap.get("button"));
                imageView.setFitWidth(games.getPrefWidth() - 200);
                imageView.setFitHeight(175);
                imageView.setId("image");

                VBox vBox = new VBox();
                vBox.setId(x.getString("gameId"));
                vBox.setBackground(new Background(
                        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(-10);

                pane.getChildren().addAll(imageView, vBox);

                HBox elements = new HBox();
                elements.setAlignment(Pos.CENTER);
                elements.setSpacing(20);

                Label name = new Label(x.getString("gameId"));
                name.setAlignment(Pos.CENTER);
                name.setTextFill(Color.WHITE);
                name.setTextAlignment(TextAlignment.CENTER);
                name.setFont(Font.font("Silom", 40));
                HBox skulls = new HBox();
                skulls.setAlignment(Pos.CENTER);

                for (int i = 0; i < x.getInt("numberOfDeaths"); i++) {

                    ImageView image = new ImageView(Images.dropsMap.get("morte"));
                    image.setPreserveRatio(true);
                    image.setFitWidth(30);

                    skulls.getChildren().add(image);
                }

                HBox players = new HBox();
                players.setAlignment(Pos.CENTER);
                players.setSpacing(30);

                x.getJsonArray("playerList")
                        .stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(y -> {

                            VBox player = new VBox();
                            player.setAlignment(Pos.CENTER);

                            ImageView image = new ImageView(Images.playersMap.get(y.getString("character")));
                            image.setPreserveRatio(true);
                            image.setFitWidth(40);

                            if (!y.getBoolean("connected")) {

                                ColorAdjust desaturate = new ColorAdjust();
                                desaturate.setSaturation(-1);
                                image.setEffect(desaturate);
                            }

                            Label pn = new Label(y.getString("playerId"));
                            pn.setAlignment(Pos.CENTER);
                            pn.setTextFill(Color.WHITE);
                            pn.setTextAlignment(TextAlignment.CENTER);
                            pn.setFont(Font.font("Silom", 15));

                            player.getChildren().addAll(image, pn);

                            players.getChildren().add(player);

                        });

                elements.getChildren().addAll(name, skulls);

                if (x.getBoolean("frenzy")) {

                    Label infos = new Label(" F");
                    infos.setAlignment(Pos.CENTER);
                    infos.setTextFill(Color.WHITE);
                    infos.setFont(Font.font("Silom", 40));

                    elements.getChildren().add(infos);
                }

                vBox.getChildren().addAll(elements, players);

                vBox.setOnMouseEntered(mouseEvent -> {

                    vBox.setOpacity(1);
                    GUIView.getCurrentStage().getScene().setCursor(Cursor.HAND);
                });

                vBox.setOnMouseExited(mouseEvent -> {

                    vBox.setOpacity(0.6);
                    GUIView.getCurrentStage().getScene().setCursor(Cursor.DEFAULT);
                });

                vBox.setOnMouseClicked(mouseEvent -> {

                    Entry<ImageView, Animation> entry = Explosion.getExplosion(4, mouseEvent);

                    entry.getValue().setOnFinished(y -> {

                        borderPane.getChildren().remove(entry.getKey());

                        if (!x.getBoolean("gameStarted")) {

                            selectCharacterScreen(vBox.getId());

                        } else {

                            Notifications.createNotification("error", "La partita è già iniziata!");
                        }
                    });

                    entry.getValue().play();

                    borderPane.getChildren().add(entry.getKey());
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

                games.getChildren().add(pane);
            });
        });
    }

    /**
     * creates a new screen in which the player must select the character to use in the game
     * @param game name of the selected game
     */
    private static void selectCharacterScreen(String game) {

        BorderPane borderPane2 = GUIView.createBorderPane(true, false);

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

                    borderPane2.getChildren().remove(entry.getKey());

                    JsonQueue.add("method", "selectGame");

                    JsonQueue.add("gameId", game);
                    JsonQueue.add("character", key);

                    JsonQueue.send();

                });

                entry.getValue().play();

                borderPane2.getChildren().add(entry.getKey());
            });

            characters.getChildren().add(button);

        });

        characters.setAlignment(Pos.CENTER);

        borderPane2.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane2));
    }
}
