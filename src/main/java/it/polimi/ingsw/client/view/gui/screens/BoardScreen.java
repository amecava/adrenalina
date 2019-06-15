package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.buttons.ButtonPowerUp;
import it.polimi.ingsw.client.view.gui.buttons.ButtonSquare;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
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
import javax.json.JsonObject;
import javax.json.JsonValue;

public class BoardScreen {

    private static JsonObject jsonObject;

    public static List<ButtonPowerUp> powerUpList = new ArrayList<>();
    public static List<ButtonWeapon> thisPlayerWeaponsList = new ArrayList<>();
    public static List<ButtonWeapon> weaponsInSpawnSquare = new ArrayList<>();
    public static List<Integer> actionsList = new ArrayList<>();
    public static List<String> playersInGame = new ArrayList<>();
    public static List<ButtonSquare> squareList = new ArrayList<>();
    public static List<ButtonSquare> squareListForShootState = new ArrayList<>();

    public static String character;
    public static final VBox collectiveButtons = new VBox();

    private static int boardId;
    public static int activatedWeapon;

    private BoardScreen() {

        //
    }

    public static void generateScreen(JsonObject object) {

        //
    }

    public static void updateScreen(JsonObject object) {

        jsonObject = object;
        boardId = jsonObject.getJsonObject("board").getInt("boardId");

        Platform.runLater(() -> {

            VBox pannelloCentrale = new VBox();
            BorderPane borderPane = new BorderPane();
            borderPane.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
            ((BorderPane) GUIView.getCurrentStage().getScene().getRoot()).getChildren().clear();

            AnchorPane anchorPane = (AnchorPane) createBoard(true, 1);

            /////////////////////////////////////////////////metto armi nello slot armi
            weaponsInSpawnSquare.clear();
            HBox weaponsTop = new HBox();
            weaponsTop.setMouseTransparent(false);
            VBox weaponsDx = new VBox();
            weaponsDx.setMouseTransparent(false);
            VBox weaponsSx = new VBox();
            weaponsSx.setMouseTransparent(false);

            weaponsSx.setSpacing(10);
            weaponsDx.setSpacing(10);
            weaponsTop.setSpacing(3);

            AnchorPane.setTopAnchor(weaponsTop, 3.0);
            AnchorPane.setLeftAnchor(weaponsTop, 393.0);
            AnchorPane.setRightAnchor(weaponsTop, 118.0);

            AnchorPane.setLeftAnchor(weaponsSx, 0.0);
            AnchorPane.setBottomAnchor(weaponsSx, 105.0);
            AnchorPane.setTopAnchor(weaponsSx, 207.0);

            AnchorPane.setTopAnchor(weaponsDx, 320.0);
            AnchorPane.setLeftAnchor(weaponsDx, 644.0);
            AnchorPane.setBottomAnchor(weaponsDx, 3.0);
            //////////////////////////////////////////////////////////

            jsonObject.getJsonObject("board")
                    .getJsonArray("arrays").stream()
                    .flatMap(x -> x.asJsonArray().stream())
                    .map(JsonValue::asJsonObject).filter(x -> !x.containsKey("empty"))
                    .filter(x -> x.getBoolean("isSpawn")).forEach(x -> {

                String color = x.getString("color");

                x.getJsonArray("tools").stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(y -> {

                            ImageView cardImage = new ImageView(
                                    Images.weaponsMap.get(y.getInt("id")));
                            ButtonWeapon rotatedButton = null;
                            ImageView definiteRotateImage = null;

                            switch (color) {

                                case "ROSSO":

                                    cardImage.setRotate(90);
                                    SnapshotParameters params = new SnapshotParameters();
                                    params.setFill(Color.TRANSPARENT);
                                    Image rotatedImage = cardImage.snapshot(params, null);
                                    definiteRotateImage = new ImageView(rotatedImage);
                                    definiteRotateImage.setFitWidth(92);
                                    definiteRotateImage.setFitHeight(61);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            definiteRotateImage);
                                    rotatedButton.setColor("ROSSO");

                                    weaponsSx.getChildren().add(rotatedButton);

                                    break;

                                case "BLU":

                                    cardImage.setRotate(0);
                                    SnapshotParameters paramsBlue = new SnapshotParameters();
                                    paramsBlue.setFill(Color.TRANSPARENT);
                                    Image rotatedImage180 = cardImage.snapshot(paramsBlue, null);
                                    definiteRotateImage = new ImageView(rotatedImage180);
                                    definiteRotateImage.setFitWidth(61);
                                    definiteRotateImage.setFitHeight(92);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            definiteRotateImage);
                                    rotatedButton.setColor("BLU");

                                    weaponsTop.getChildren().add(rotatedButton);

                                    break;

                                case "GIALLO":

                                    cardImage.setRotate(270);
                                    SnapshotParameters paramsGiallo = new SnapshotParameters();
                                    paramsGiallo.setFill(Color.TRANSPARENT);
                                    Image rotatedImage270 = cardImage.snapshot(paramsGiallo, null);
                                    definiteRotateImage = new ImageView(rotatedImage270);
                                    definiteRotateImage.setFitWidth(92);
                                    definiteRotateImage.setFitHeight(61);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            definiteRotateImage);
                                    rotatedButton.setColor("GIALLO");
                                    weaponsDx.getChildren().add(rotatedButton);

                                    break;

                                default:

                            }

                            weaponsInSpawnSquare.add(rotatedButton);

                            rotatedButton.setOnMouseClicked(mouseEvent -> {

                                JsonQueue.add("method", "askCardInfo");
                                JsonQueue.add("cardId", Integer.toString(
                                        ((ButtonWeapon) mouseEvent.getSource()).getCardId()));

                                JsonQueue.send();
                            });
                        });

            });

            anchorPane.getChildren().addAll(weaponsDx, weaponsSx, weaponsTop);

            anchorPane.setMouseTransparent(false);

            ////////////////////////////////////////////////////  metto giocatori e ammotiles nei quadrati!!
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
                                                        .get(s.getJsonArray("colors").stream()
                                                                .map(JsonValue::toString)
                                                                .map(p -> p.substring(1,
                                                                        p.length() - 1))
                                                                .collect(Collectors.joining())));

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
                                                Images.playersMap.get(t.getString("character")));

                                        playersInSquare.getChildren().add(player);

                                        double scale =
                                                playersInSquare.getChildren().size() == 1 ? 1.5
                                                        : playersInSquare.getChildren().size();

                                        playersInSquare.getChildren().stream()
                                                .forEach(m -> {
                                                    ((ImageView) m)
                                                            .setFitWidth(105 / scale);
                                                    ((ImageView) m)
                                                            .setFitHeight(125 / scale);
                                                });
                                        if (t.getString("playerId").equals(GUIView.getPlayerId())) {
                                            squareList.stream()
                                                    .filter(ButtonSquare::isPresent)
                                                    .filter(s -> s.getColor().equals(color) && s
                                                            .getSquareId() == id)
                                                    .findAny().get().setPlayerPosition();
                                        }
                                    });
                        }

                        ButtonSquare tmp = squareList.stream().filter(ButtonSquare::isPresent)
                                .filter(s -> s.getColor().equals(color)
                                        && s.getSquareId() == id)
                                .findAny()
                                .orElseThrow(IllegalArgumentException::new);

                        StackPane pane = (StackPane) tmp.getParent();
                        pane.getChildren().clear();
                        tilesInSquare.setAlignment(Pos.BOTTOM_CENTER);
                        pane.getChildren().addAll(tilesInSquare);
                        pane.getChildren().addAll(playersInSquare, tmp);
                    });
            //////////////////////////////////centro metto morti nella plncia morti board
            HBox killsOfAllPlayers = new HBox();
            jsonObject.getJsonObject("deaths").getJsonArray("deathBridgeArray").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView killshot = new ImageView(
                                Images.dropsMap
                                        .get(x.toString().substring(1, x.toString().length() - 1)));
                        killshot.setFitWidth(40);
                        killshot.setFitHeight(40);
                        killsOfAllPlayers.getChildren().add(killshot);

                    });
            AnchorPane.setLeftAnchor(killsOfAllPlayers, 70.0);
            AnchorPane.setTopAnchor(killsOfAllPlayers, 30.0);
            anchorPane.getChildren().add(killsOfAllPlayers);

            ////////////////////////////////////////////////////////////  prendo le azioni possibili che puo fare un giocatore
            actionsList.clear();
            JsonObject characterJson = jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(GUIView.getPlayerId())).findFirst()
                    .get();
            character = characterJson.getString("character");
            characterJson.getJsonObject("bridge").getJsonObject("actionBridge")
                    .getJsonArray("possibleActionsArray").stream().map(JsonValue::asJsonObject)
                    .forEach(x -> actionsList.add(x.getInt("id")));

            /////////////////////////////////////////////////////////////////////////// centro powerUp e armi e tuoi cubes
            thisPlayerWeaponsList.clear();
            powerUpList.clear();

            HBox myPlayerCubes = new HBox();
            myPlayerCubes.setPrefHeight(30);

            HBox cards = new HBox();
            cards.setPrefWidth(990);
            cards.setPrefHeight((565.0 / 3) + 10);

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
                        myPlayerCubes.getChildren().add(cubeView);
                    });

            thisPlayerObject.getJsonArray("weapons").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView card;

                        card = new ImageView(
                                x.getBoolean("isLoaded")
                                        ? Images.weaponsMap.get(x.getInt("id"))
                                        : Images.weaponsMap.get(0));

                        card.setFitWidth(130);
                        card.setFitHeight((565.0 / 3) + 15);

                        ButtonWeapon imageButton = new ButtonWeapon(
                                x.getInt("id"),
                                card);

                        imageButton.setLoaded(x.getBoolean("isLoaded"));

                        imageButton.setOnMouseClicked(mouseEvent -> {

                            JsonQueue.add("method", "askCardInfo");
                            JsonQueue.add("cardId", Integer.toString(
                                    ((ButtonWeapon) mouseEvent.getSource()).getCardId()));

                            JsonQueue.send();
                        });
                        thisPlayerWeaponsList.add(imageButton);
                        cards.getChildren().add(imageButton);
                    });

            thisPlayerObject.getJsonArray("powerUps").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView powerUp = new ImageView(
                                Images.powerUpsMap.get(new StringBuilder()
                                        .append(x.getString("name"))
                                        .append(" ")
                                        .append(x.getString("color"))
                                        .toString()));

                        powerUp.setFitWidth(130);
                        powerUp.setFitHeight((565.0 / 3) + 15);
                        ButtonPowerUp powerUpButton = new ButtonPowerUp(x.getString("name"),
                                x.getString("color"), powerUp);
                        powerUpList.add(powerUpButton);
                        cards.getChildren().add(powerUpButton);
                    });

            pannelloCentrale.getChildren().addAll(anchorPane, cards);

            Label myPlayersPoints = new Label();
            myPlayersPoints.setText("Punti: " + thisPlayerObject.getInt("points"));
            myPlayersPoints.setTextFill(Color.WHITE);
            myPlayersPoints.setFont(Font.font("Silom", FontWeight.BOLD, 20));

            anchorPane.getChildren().addAll(myPlayersPoints, myPlayerCubes);

            AnchorPane.setLeftAnchor(myPlayersPoints, 10.0);
            AnchorPane.setBottomAnchor(myPlayersPoints, 35.0);
            AnchorPane.setLeftAnchor(myPlayerCubes, 10.0);
            AnchorPane.setBottomAnchor(myPlayerCubes, 0.0);

            pannelloCentrale.setSpacing(0);
            borderPane.setCenter(pannelloCentrale);
            /////////////////////////////////////////////////destra metto plance giocatori
            AnchorPane rightAnchorPane = new AnchorPane();
            VBox bridges = new VBox();
            AnchorPane.setTopAnchor(bridges, 0.0);
            bridges.setSpacing(5);
            jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

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
                        bridges.getChildren().add(bridge);


                    });
            rightAnchorPane.getChildren().add(bridges);
            /////////////////////////////metto segnalini danno su giocatori e segnalini morti
            jsonObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        String allPlayersCharacter = x.getString("character");
                        Node characterBridge = bridges.getChildren().stream()
                                .filter(k -> k.getId().equals(allPlayersCharacter)).findFirst()
                                .get();
                        HBox shots = new HBox();
                        shots.setSpacing(2);
                        HBox kills = new HBox();
                        int numberOfBridge = bridges.getChildren().indexOf(characterBridge);
                        x.getJsonObject("bridge").getJsonArray("deathBridgeArray").stream()
                                .map(JsonValue::asJsonObject).filter(z -> z.getBoolean("used"))
                                .forEach(z -> {

                                    ImageView kill = new ImageView(Images.dropsMap.get("morte"));
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
                        AnchorPane.setTopAnchor(shots, (double) numberOfBridge * (565.0 / 5) + 35);
                        AnchorPane.setLeftAnchor(shots, 40.0);
                        AnchorPane.setRightAnchor(shots, 120.0);
                        rightAnchorPane.getChildren().addAll(shots, kills);
                    });
            ///////////////////////////// bottoni

            AnchorPane.setTopAnchor(collectiveButtons, 565.0);
            AnchorPane.setLeftAnchor(collectiveButtons, 130.0);
            collectiveButtons.setSpacing(7);
            rightAnchorPane.getChildren().add(collectiveButtons);
            borderPane.setRight(rightAnchorPane);

            /////////////////////////////////////////////////
            GUIView.changeScene(borderPane);
        });
    }

    public static synchronized Node createBoard(boolean isUpdateBoard, double scaleFactor) {

        ImageView boardImage;

        if (isUpdateBoard) {

            boardImage = new ImageView(Images.gameBoardMap.get("board" + (boardId + 1)));

        } else {

            boardImage = new ImageView(Images.boardsMap.get("board" + (boardId + 1)));
        }

        if (isUpdateBoard) {
            squareList.clear();
        } else {
            squareListForShootState.clear();
        }
        boardImage.setFitWidth(750 / scaleFactor);// /3.4
        boardImage
                .setFitHeight(565 / scaleFactor);//1.321 rapporto tra lunghezza e altezza originale

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
                            StackPane stackPane = new StackPane();
                            stackPane.setPrefHeight(145 / scaleFactor);
                            stackPane.setPrefWidth(130 / scaleFactor);
                            stackPane.getChildren().add(buttonSquare);
                            if (isUpdateBoard) {
                                squareList.add(buttonSquare);
                            } else {
                                squareListForShootState.add(buttonSquare);
                            }
                            line.getChildren().add(stackPane);
                            if (line.getChildren().size() == 4) {
                                HBox row = new HBox();
                                row.getChildren().addAll(line.getChildren());
                                squares.getChildren().add(row);
                                line.getChildren().clear();
                            }
                        }
                );
        anchorPane.getChildren().addAll(boardImage, squares);
        return anchorPane;
    }
}


