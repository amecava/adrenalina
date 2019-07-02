package it.polimi.ingsw.client.view.gui.screens.boardscreen;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import it.polimi.ingsw.client.view.gui.handlers.CardHandler;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.buttons.ButtonPowerUp;
import it.polimi.ingsw.client.view.gui.buttons.ButtonSquare;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import it.polimi.ingsw.server.model.board.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class BoardScreen {

    private static int boardId;
    private static JsonObject jsonObject;

    public static boolean isSpawnState = true;

    private static AnchorPane board;

    private static final HBox killsOfAllPlayers = new HBox();
    private static final HBox weaponsTop = new HBox();
    private static final VBox weaponsDx = new VBox();
    private static final VBox weaponsSx = new VBox();
    private static final StackPane weaponDeck = new StackPane();
    private static final StackPane powerUpDeck = new StackPane();


    private static final Label playerPoints = new Label();
    private static final HBox playerCubes = new HBox();
    private static final HBox playerCards = new HBox();

    private static final AnchorPane right = new AnchorPane();

    private static final VBox bridges = new VBox();
    public static final VBox collectiveButtons = new VBox();

    public static int activatedWeapon;
    public static List<String> playersInGame = new ArrayList<>();

    private static final BooleanProperty ready = new SimpleBooleanProperty(false);

    private BoardScreen() {

        //
    }

    public static List<ButtonSquare> getSquareList() {

        return BoardFunction.getSquareList(board);
    }

    public static List<ButtonWeapon> getPlayerWeaponList() {

        return playerCards.getChildren().stream()
                .filter(n -> n.getId().equals("weapon"))
                .map(n -> (ButtonWeapon) n)
                .collect(Collectors.toList());
    }

    public static List<ButtonPowerUp> getPlayerPowerUpList() {

        return playerCards.getChildren().stream()
                .filter(n -> n.getId().equals("powerUp"))
                .map(n -> (ButtonPowerUp) n)
                .collect(Collectors.toList());
    }

    public static List<ButtonWeapon> getSpawnWeaponList() {

        return Stream.of(
                weaponsTop.getChildren().stream(),
                weaponsDx.getChildren().stream(),
                weaponsSx.getChildren().stream())
                .flatMap(Function.identity())
                .map(x -> (ButtonWeapon) x)
                .collect(Collectors.toList());
    }

    private static void generateScreen(JsonObject object) {

        jsonObject = object;
        boardId = jsonObject.getJsonObject("board").getInt("boardId");

        BorderPane borderPane = GUIView.createBorderPane(false, false);

        VBox centre = new VBox();
        board = createBoard(true, 1);
        board.setMouseTransparent(false);

        weaponsTop.setId("top");
        weaponsTop.setMouseTransparent(false);

        weaponsDx.setId("dx");
        weaponsDx.setMouseTransparent(false);

        weaponsSx.setId("sx");
        weaponsSx.setMouseTransparent(false);

        weaponsSx.setSpacing(10);
        weaponsDx.setSpacing(10);
        weaponsTop.setSpacing(3);

        weaponDeck.setId("weaponDeck");
        weaponDeck.setMouseTransparent(false);
        weaponDeck.setPrefWidth(61);
        weaponDeck.setPrefHeight(92);

        AnchorPane.setTopAnchor(weaponsTop, 3.0);
        AnchorPane.setLeftAnchor(weaponsTop, 393.0);

        AnchorPane.setTopAnchor(weaponsSx, 207.0);
        AnchorPane.setLeftAnchor(weaponsSx, 0.0);

        AnchorPane.setTopAnchor(weaponsDx, 320.0);
        AnchorPane.setLeftAnchor(weaponsDx, 644.0);

        AnchorPane.setTopAnchor(weaponDeck, 165.0);
        AnchorPane.setLeftAnchor(weaponDeck, 658.0);

        board.getChildren().addAll(weaponsDx, weaponsSx, weaponsTop, weaponDeck);

        killsOfAllPlayers.setId("kills");
        AnchorPane.setLeftAnchor(killsOfAllPlayers, 70.0);
        AnchorPane.setTopAnchor(killsOfAllPlayers, 30.0);

        playerCubes.setId("cubes");
        playerCubes.setPrefHeight(30);

        playerCards.setId("cards");
        playerCards.setPrefWidth(990);
        playerCards.setPrefHeight((565.0 / 3) + 10);

        centre.getChildren().addAll(board, playerCards);

        playerPoints.setId("points");
        playerPoints.setTextFill(Color.WHITE);
        playerPoints.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        AnchorPane.setLeftAnchor(playerPoints, 10.0);
        AnchorPane.setBottomAnchor(playerPoints, 35.0);
        AnchorPane.setLeftAnchor(playerCubes, 10.0);
        AnchorPane.setBottomAnchor(playerCubes, 0.0);

        board.getChildren().addAll(killsOfAllPlayers, playerPoints, playerCubes);

        centre.setSpacing(0);
        borderPane.setCenter(centre);

        bridges.setId("bridges");
        bridges.setSpacing(0);

        AnchorPane.setTopAnchor(bridges, 0.0);
        right.getChildren().add(bridges);

        AnchorPane.setTopAnchor(collectiveButtons, 565.0);
        AnchorPane.setLeftAnchor(collectiveButtons, 130.0);
        collectiveButtons.setSpacing(7);
        collectiveButtons.setId("buttons");
        right.getChildren().add(collectiveButtons);

        borderPane.setRight(right);

        ImageView backCard = new ImageView(Images.weaponsMap.get(0));
        backCard.setFitHeight(92);
        backCard.setFitWidth(61);

        Button infoCard = new GameButton(backCard);
        infoCard.setId("infoCard");

        infoCard.setOnMouseClicked(mouseEvent -> {

            Stage infoCardStage = new Stage();
            infoCardStage.setMinWidth(430);
            infoCardStage.setMaxWidth(430);
            infoCardStage.initModality(Modality.APPLICATION_MODAL);
            infoCardStage.initOwner(GUIView.getCurrentStage());

            ScrollPane images = new ScrollPane();
            images.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));

            HBox twoCards = new HBox();
            twoCards.setMaxWidth(500);
            twoCards.setSpacing(80);
            twoCards.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));

            VBox allCards = new VBox();
            allCards.setMaxWidth(500);
            allCards.setSpacing(0);
            allCards.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));

            for (int i = 1; i < 22; i++) {
                ImageView back = new ImageView(Images.weaponsMap.get(0));
                ImageView card = new ImageView(Images.weaponsMap.get(i));
                card.setFitWidth(150);
                card.setFitHeight(250);
                ButtonWeapon buttonWeapon = new ButtonWeapon(i, back, card,
                        Rotate.Y_AXIS);

                buttonWeapon.setOnMouseClicked(weaponMouseEvent -> {

                    JsonQueue.add("method", "askCardInfo");
                    JsonQueue.add("cardId", Integer.toString(
                            ((ButtonWeapon) weaponMouseEvent.getSource())
                                    .getCardId()));
                    JsonQueue.send();
                });
                twoCards.getChildren().add(buttonWeapon);

                if (twoCards.getChildren().size() == 2) {

                    allCards.getChildren().add(twoCards);
                    twoCards = new HBox();
                    twoCards.setSpacing(80);
                    twoCards.setBackground(new Background(
                            new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                                    BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                                    BackgroundSize.DEFAULT)));
                }

                buttonWeapon.setVisible(false);
                buttonWeapon.flipTransition(Duration.millis(1),
                        actionEvent -> buttonWeapon.setVisible(true)).play();
            }
            images.setMaxWidth(500);
            images.setContent(allCards);
            images.setVbarPolicy(ScrollBarPolicy.ALWAYS);
            images.setHbarPolicy(ScrollBarPolicy.NEVER);
            Scene imagesScene = new Scene(new StackPane(images), 450, 500);
            infoCardStage.setResizable(false);
            infoCardStage.setScene(imagesScene);
            infoCardStage.show();
        });
        infoCard.setAlignment(Pos.CENTER);

        AnchorPane.setLeftAnchor(infoCard, 649.0);
        AnchorPane.setTopAnchor(infoCard, 160.0);

        board.getChildren().add(infoCard);
        ready.setValue(true);

        GUIView.changeScene(borderPane);
    }

    public static synchronized void updateScreen(JsonObject object) {

        if (object == null) {

            return;
        }

        jsonObject = object;

        Platform.runLater(() -> {

            if (Boolean.FALSE.equals(ready.get())) {

                generateScreen(jsonObject);
            }

            ///////////////////////////////////////////////////////////////////////////////

            List<JsonObject> jsonList = jsonObject.getJsonObject("board")
                    .getJsonArray("arrays").stream()
                    .flatMap(y -> y.asJsonArray().stream())
                    .map(JsonValue::asJsonObject).filter(y -> !y.containsKey("empty"))
                    .filter(y -> y.getBoolean("isSpawn"))
                    .collect(Collectors.toList());

            BoardFunction.removeCardFromSpawn(weaponsTop.getChildren(), jsonList);

            BoardFunction.removeCardFromSpawn(weaponsDx.getChildren(), jsonList);

            BoardFunction.removeCardFromSpawn(weaponsSx.getChildren(), jsonList);

            SequentialTransition sequentialTransition = new SequentialTransition();

            jsonList.forEach(x -> {

                x.getJsonArray("tools").stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(y -> {

                            if (x.getString("color").equals("BLU")) {

                                BoardFunction.addCardToSpawn(
                                        weaponsTop.getChildren(),
                                        x.getString("color"),
                                        y.getInt("id"),
                                        Rotate.Y_AXIS,
                                        0,
                                        61,
                                        92,
                                        sequentialTransition);

                            } else if (x.getString("color").equals("GIALLO")) {

                                BoardFunction.addCardToSpawn(
                                        weaponsDx.getChildren(),
                                        x.getString("color"),
                                        y.getInt("id"),
                                        Rotate.X_AXIS,
                                        -90,
                                        92,
                                        61,
                                        sequentialTransition);

                            } else if (x.getString("color").equals("ROSSO")) {

                                BoardFunction.addCardToSpawn(
                                        weaponsSx.getChildren(),
                                        x.getString("color"),
                                        y.getInt("id"),
                                        Rotate.X_AXIS,
                                        90,
                                        92,
                                        61,
                                        sequentialTransition);
                            }
                        });

            });

            sequentialTransition.getChildren().add(0, new PauseTransition(Duration.seconds(1)));
            sequentialTransition.play();

            ///////////////////////////////////////////////////////////////////////////////

            //TODO
            playersInGame.clear();
            jsonObject.getJsonObject("board").getJsonArray("arrays").stream()
                    .flatMap(x -> x.asJsonArray().stream())
                    .map(JsonValue::asJsonObject)
                    .filter(x -> !x.containsKey("empty"))
                    .forEach(z -> {

                        String color = z.getString("color");
                        int id = z.getInt("squareId");

                        HBox playersInSquare = new HBox();

                        HBox tilesInSquare = new HBox();

                        if (!z.getBoolean("isSpawn")) {

                            z.getJsonArray("tools").stream()
                                    .map(JsonValue::asJsonObject)
                                    .forEach(s -> {

                                        ImageView tile = new ImageView(
                                                Images.ammoTilesMap
                                                        .get(s.getJsonArray("colors")
                                                                .stream()
                                                                .map(JsonValue::toString)
                                                                .map(p -> p.substring(1,
                                                                        p.length() - 1))
                                                                .collect(
                                                                        Collectors
                                                                                .joining())));

                                        tile.setFitHeight(45);
                                        tile.setFitWidth(45);
                                        tilesInSquare.getChildren().add(tile);
                                    });
                        }

                        if (z.get("playersIn") != JsonValue.NULL) {

                            z.getJsonArray("playersIn").stream()
                                    .map(JsonValue::asJsonObject)
                                    .forEach(t -> {
                                        playersInGame.add(t.getString("character"));
                                        ImageView player = new ImageView(
                                                Images.playersMap
                                                        .get(t.getString("character")));

                                        playersInSquare.getChildren().add(player);

                                        double scale =
                                                playersInSquare.getChildren().size() == 1
                                                        ? 1.5
                                                        : playersInSquare.getChildren()
                                                                .size();

                                        playersInSquare.getChildren().stream()
                                                .forEach(m -> {
                                                    ((ImageView) m)
                                                            .setFitWidth(105 / scale);
                                                    ((ImageView) m)
                                                            .setFitHeight(125 / scale);
                                                });
                                        if (t.getString("playerId")
                                                .equals(GUIView.getPlayerId())) {

                                            getSquareList().stream()
                                                    .forEach(x -> x.setPlayerPosition(
                                                            false));

                                            getSquareList().stream()
                                                    .filter(s -> s.getColor().equals(color)
                                                            && s
                                                            .getSquareId() == id)
                                                    .findAny().get()
                                                    .setPlayerPosition(true);
                                        }
                                    });
                        }

                        ButtonSquare tmp = getSquareList().stream()
                                .filter(s -> s.getColor().equals(color) && s
                                        .getSquareId() == id)
                                .findAny().get();

                        StackPane pane = (StackPane) tmp.getParent();
                        //TODO
                        pane.getChildren().clear();
                        tilesInSquare.setAlignment(Pos.BOTTOM_CENTER);
                        tilesInSquare.setId("tiles");
                        playersInSquare.setId("players");
                        pane.getChildren().addAll(tilesInSquare);
                        pane.getChildren().addAll(playersInSquare, tmp);
                    });

            //////////////////////////////////centro metto morti nella plncia morti board

            //TODO
            killsOfAllPlayers.getChildren().clear();

            jsonObject.getJsonObject("deaths").getJsonArray("deathBridgeArray").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView killshot = new ImageView(
                                Images.dropsMap
                                        .get(x.toString()
                                                .substring(1, x.toString().length() - 1)));
                        killshot.setFitWidth(40);
                        killshot.setFitHeight(40);
                        killsOfAllPlayers.getChildren().add(killshot);

                    });

            ////////////////////////////////////////////////////////////  prendo le azioni possibili che puo fare un giocatore

            JsonObject characterJson = jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(GUIView.getPlayerId()))
                    .findFirst()
                    .get();

            GUIView.setCharacter(characterJson.getString("character"));

            /////////////////////////////////////////////////////////////////////////// centro powerUp e armi e tuoi cubes

            playerCubes.getChildren().clear();

            //TODO
            playerCards.getChildren().clear();

            JsonObject thisPlayerObject = jsonObject
                    .getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(GUIView.getPlayerId()))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

            thisPlayerObject.getJsonArray("ammoCubes").stream()
                    .map(x -> x.toString().substring(1, x.toString().length() - 1))
                    .forEach(cube -> {

                        ImageView cubeView = new ImageView(Images.cubesMap.get(cube));
                        cubeView.setFitHeight(30);
                        cubeView.setFitWidth(30);
                        playerCubes.getChildren().add(cubeView);
                    });

            thisPlayerObject.getJsonArray("weapons").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView back = new ImageView(
                                Images.weaponsMap.get(0));
                        ImageView card = new ImageView(
                                x.getBoolean("isLoaded")
                                        ? Images.weaponsMap.get(x.getInt("id"))
                                        : Images.weaponsMap.get(0));

                        card.setFitWidth(130);
                        card.setFitHeight((565.0 / 3) + 15);

                        ButtonWeapon imageButton = new ButtonWeapon(
                                x.getInt("id"),
                                back,
                                card,
                                Rotate.Y_AXIS);
                        imageButton.setId("weapon");

                        imageButton.setLoaded(x.getBoolean("isLoaded"));

                        imageButton.setOnMouseClicked(mouseEvent -> {

                            JsonQueue.add("method", "askCardInfo");
                            JsonQueue.add("cardId", Integer.toString(
                                    ((ButtonWeapon) mouseEvent.getSource()).getCardId()));

                            JsonQueue.send();
                        });

                        imageButton.setVisible(false);
                        imageButton.flipTransition(Duration.millis(1),
                                actionEvent -> imageButton.setVisible(true)).play();
                        playerCards.getChildren().add(imageButton);
                    });

            thisPlayerObject.getJsonArray("powerUps").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView back = new ImageView(
                                Images.powerUpsMap.get("back"));

                        ImageView powerUp = new ImageView(
                                Images.powerUpsMap.get(new StringBuilder()
                                        .append(x.getString("name"))
                                        .append(" ")
                                        .append(x.getString("color"))
                                        .toString()));

                        powerUp.setFitWidth(130);
                        powerUp.setFitHeight((565.0 / 3) + 15);
                        ButtonPowerUp powerUpButton = new ButtonPowerUp(x.getString("name"),
                                x.getString("color"),
                                x.getString("targetType"),
                                x.getJsonNumber("args").doubleValue(),
                                x.getBoolean("hasCost"),
                                back, powerUp);
                        powerUpButton.setId("powerUp");
                        powerUpButton.setVisible(false);

                        if (!isSpawnState) {

                            powerUpButton.setOnMouseClicked(
                                    mouseEvent -> CardHandler.powerUpCardInfo(x));
                        }
                        powerUpButton.flipTransition(Duration.millis(1),
                                actionEvent -> powerUpButton.setVisible(true)).play();
                        playerCards.getChildren().add(powerUpButton);
                    });

            playerPoints.setText("Punti: " + thisPlayerObject.getInt("points"));

            /////////////////////////////////////////////////destra metto plance giocatori

            //TODO
            bridges.getChildren().clear();

            new ArrayList<>(right.getChildren()).stream()
                    .filter(x -> x.getId().equals("shots") || x.getId().equals("kills"))
                    .forEach(x -> right.getChildren().remove(x));

            jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        StackPane imageAndButton = new StackPane();
                        imageAndButton.setId(x.getString("character"));

                        ImageView bridge = new ImageView(
                                Images.bridgesMap.get(new StringBuilder()
                                        .append(x.getString("character"))
                                        .append(x.getJsonObject("bridge")
                                                .getJsonObject("damageBridge")
                                                .getBoolean("isFinalFrenzy"))
                                        .toString()));
                        bridge.setId(x.getString("character"));
                        bridge.setFitWidth(990.0 / 3 + 150);
                        bridge.setFitHeight(565.0 / 5);

                        Button toShowCards = new Button();
                        toShowCards.setId(x.getString("character"));
                        toShowCards.setBackground(new Background(
                                new BackgroundFill(Color.WHITE, CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                        toShowCards.setPrefWidth(bridge.getFitWidth());
                        toShowCards.setPrefHeight(bridge.getFitHeight());
                        toShowCards.setOpacity(0);
                        toShowCards.setOnMouseEntered(
                                mouseEvent -> {
                                    GUIView.getCurrentStage().getScene()
                                            .setCursor(Cursor.HAND);
                                    toShowCards.setOpacity(0.2);
                                }
                        );
                        toShowCards.setOnMouseExited(
                                mouseEvent -> {
                                    GUIView.getCurrentStage().getScene()
                                            .setCursor(Cursor.DEFAULT);
                                    toShowCards.setOpacity(0);
                                }
                        );
                        toShowCards.setOnMouseClicked(
                                mouseEvent -> CardHandler.specificWeaponCardInfo(x));

                        imageAndButton.getChildren().addAll(bridge, toShowCards);

                        bridges.getChildren().add(imageAndButton);

                    });

            /////////////////////////////metto segnalini danno su giocatori e segnalini morti

            jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        String allPlayersCharacter = x.getString("character");
                        Node characterBridge = bridges.getChildren().stream()
                                .filter(k -> k.getId().equals(allPlayersCharacter))
                                .findFirst()
                                .get();
                        HBox shots = new HBox();
                        shots.setId("shots");
                        shots.setSpacing(2);
                        HBox kills = new HBox();
                        kills.setId("kills");
                        int numberOfBridge = bridges.getChildren().indexOf(characterBridge);
                        x.getJsonObject("bridge").getJsonArray("deathBridgeArray").stream()
                                .map(JsonValue::asJsonObject)
                                .filter(z -> z.getBoolean("used"))
                                .forEach(z -> {

                                    ImageView kill = new ImageView(
                                            Images.dropsMap.get("morte"));
                                    kill.setFitHeight(25);
                                    kill.setFitWidth(25);
                                    kills.getChildren().add(kill);
                                });
                        x.getJsonObject("bridge").getJsonObject("damageBridge")
                                .getJsonArray("shots").forEach(z -> {
                            ImageView shot = new ImageView(Images.dropsMap.get(
                                    z.toString().substring(1, z.toString().length() - 1)));

                            shot.setFitHeight(35);
                            shot.setFitWidth(25);
                            shots.getChildren().add(shot);

                        });
                        AnchorPane.setTopAnchor(kills,
                                (double) numberOfBridge * (565.0 / 5) + 115 - 30);
                        AnchorPane.setLeftAnchor(kills, 103.0);
                        AnchorPane.setTopAnchor(shots,
                                (double) numberOfBridge * (565.0 / 5) + 35);
                        AnchorPane.setLeftAnchor(shots, 40.0);
                        AnchorPane.setRightAnchor(shots, 120.0);
                        right.getChildren().addAll(shots, kills);
                    });
        });
    }

    public static AnchorPane createBoard(boolean cards, double scaleFactor) {

        ImageView boardImage;

        if (cards) {

            boardImage = new ImageView(Images.gameBoardMap.get("board" + (boardId + 1)));

        } else {

            boardImage = new ImageView(Images.boardsMap.get("board" + (boardId + 1)));
        }

        boardImage.setFitWidth(750 / scaleFactor);
        boardImage.setFitHeight(565 / scaleFactor);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(750 / scaleFactor, 565 / scaleFactor);
        VBox squares = new VBox();
        AnchorPane.setTopAnchor(squares, 117.0 / scaleFactor);
        AnchorPane.setBottomAnchor(squares, 65 / scaleFactor);
        AnchorPane.setLeftAnchor(squares, 116 / scaleFactor);
        AnchorPane.setRightAnchor(squares, 120 / scaleFactor);
        HBox line;
        line = new HBox();
        line.setSpacing(0);

        jsonObject.getJsonObject("board").getJsonArray("arrays").stream()
                .flatMap(x -> x.asJsonArray().stream().map(JsonValue::asJsonObject))
                .forEach(x -> {
                            ButtonSquare buttonSquare;

                            if (!x.containsKey("empty")) {
                                buttonSquare = new ButtonSquare(
                                        x.getString("color"), x.getInt("squareId"));
                                buttonSquare.setBackground(new Background(
                                        new BackgroundFill(Color.rgb(217, 217, 217),
                                                CornerRadii.EMPTY,
                                                Insets.EMPTY)));
                                buttonSquare.setSpawn(x.getBoolean("isSpawn"));
                                buttonSquare.setOnMouseEntered(mouseEvent -> {

                                    ((ButtonSquare) (mouseEvent.getSource())).setOpacity(0.3);
                                    GUIView.getCurrentStage().getScene().setCursor(Cursor.HAND);
                                });
                                buttonSquare.setOnMouseExited(mouseEvent -> {

                                    ((ButtonSquare) (mouseEvent.getSource())).setOpacity(0.0);
                                    GUIView.getCurrentStage().getScene().setCursor(Cursor.DEFAULT);
                                });

                            } else {
                                buttonSquare = new ButtonSquare();

                            }
                            buttonSquare.setPrefHeight(145 / scaleFactor);
                            buttonSquare.setPrefWidth(130 / scaleFactor);
                            buttonSquare.setId("button");
                            StackPane stackPane = new StackPane();
                            stackPane.setPrefHeight(145 / scaleFactor);
                            stackPane.setPrefWidth(130 / scaleFactor);
                            stackPane.getChildren().add(buttonSquare);

                            line.getChildren().add(stackPane);

                            if (line.getChildren().size() == 4) {
                                HBox row = new HBox();
                                row.getChildren().addAll(line.getChildren());
                                squares.getChildren().add(row);
                                line.getChildren().clear();
                            }
                        }
                );

        boardImage.setId("boardImage");
        squares.setId("squares");
        anchorPane.getChildren().addAll(boardImage, squares);
        return anchorPane;
    }
}


