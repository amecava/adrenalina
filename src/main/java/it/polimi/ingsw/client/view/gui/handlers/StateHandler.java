package it.polimi.ingsw.client.view.gui.handlers;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.ButtonPowerUp;
import it.polimi.ingsw.client.view.gui.buttons.ButtonSquare;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import it.polimi.ingsw.client.view.gui.buttons.InfoButton;
import it.polimi.ingsw.client.view.gui.screens.boardscreen.BoardFunction;
import it.polimi.ingsw.client.view.gui.screens.boardscreen.BoardScreen;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class StateHandler {

    private StateHandler() {

        //
    }

    public static synchronized void updateState(JsonObject object) {

        Platform.runLater(() -> {

            eliminateSetOnMouseClicked();

            if (BoardScreen.collectiveButtons != null) {

                BoardScreen.collectiveButtons.getChildren().clear();
            }

            ////////////////////////////////////////////////////////////////////state che impongono

            switch (object.getString("state")) {

                case "spawnState":

                    BoardScreen.isSpawnState = true;

                    ButtonPowerUp.setOnMouse(mouseEvent -> {

                        JsonQueue.add("method", "spawn");
                        JsonQueue.add("name",
                                ((ButtonPowerUp) mouseEvent.getSource()).getName());
                        JsonQueue.add("color",
                                ((ButtonPowerUp) mouseEvent.getSource())
                                        .getColor());

                        JsonQueue.send();
                    });

                    BoardScreen.getPlayerPowerUpList().forEach(ButtonPowerUp::update);

                    Button spawnButton = new InfoButton("clicca un powerup per spawnare");

                    spawnButton.setMouseTransparent(true);

                    BoardScreen.collectiveButtons.getChildren().add(spawnButton);
                    resizeButtons();
                    break;

                case "shootState":

                    BoardScreen.isSpawnState = false;

                    if (object.getJsonArray("methods").size() == 1) {

                        JsonQueue.add("method", "endAction");
                        JsonQueue.send();

                    } else {

                        object.getJsonArray("methods").forEach(x -> {

                            String method = x.toString()
                                    .substring(1, x.toString().length() - 1);

                            if (method.equals("askUsePrimary")) {

                                Button effettoPrimario = new GameButton("effetto primario");

                                effettoPrimario.setOnMouseClicked(
                                        mouseEvent -> createShootStage("askUsePrimary",
                                                object.getJsonNumber("primaryArgs").doubleValue(),
                                                object.getBoolean("primaryCost"),
                                                object.getString("primaryTargetType"),
                                                null, null));
                                BoardScreen.collectiveButtons.getChildren()
                                        .add(effettoPrimario);
                                resizeButtons();

                            } else if (method.equals("askUseAlternative")) {

                                Button effettoAlternativo = new GameButton(
                                        "effetto alternativo");

                                effettoAlternativo.setOnMouseClicked(
                                        mouseEvent -> createShootStage("askUseAlternative",
                                                object.getJsonNumber("alternativeArgs")
                                                        .doubleValue(),
                                                object.getBoolean("alternativeCost"),
                                                object.getString("alternativeTargetType"),
                                                null,
                                                null));
                                BoardScreen.collectiveButtons.getChildren()
                                        .add(effettoAlternativo);
                                resizeButtons();

                            } else if (method.equals("askUseOptional1")) {

                                Button opzionale1 = new GameButton("effetto opzionale 1");

                                opzionale1.setOnMouseClicked(
                                        mouseEvent -> createShootStage("askUseOptional1",
                                                object.getJsonNumber("optional1Args").doubleValue(),
                                                object.getBoolean("optional1Cost"),
                                                object.getString("optional1TargetType"),
                                                null, null));
                                BoardScreen.collectiveButtons.getChildren().add(opzionale1);
                                resizeButtons();

                            } else if (method.equals("askUseOptional2")) {

                                Button opzionale2 = new GameButton("effetto opzionale 2");

                                opzionale2.setOnMouseClicked(
                                        mouseEvent -> createShootStage("askUseOptional2",
                                                object.getJsonNumber("optional2Args").doubleValue(),
                                                object.getBoolean("optional2Cost"),
                                                object.getString("optional2TargetType"),
                                                null, null));
                                BoardScreen.collectiveButtons.getChildren().add(opzionale2);
                                resizeButtons();

                            } else if (method.equals("endAction")) {
                                Button endAction = createEndActionButton();
                                BoardScreen.collectiveButtons.getChildren().addAll(endAction);
                                resizeButtons();
                            }
                        });
                    }
                    break;

                case "actionState":

                    BoardScreen.isSpawnState = false;

                    if (object.getJsonArray("methods").size() == 1) {

                        JsonQueue.add("method", "endAction");
                        JsonQueue.send();

                    } else {

                        object.getJsonArray("methods").forEach(x -> {

                            String method = x.toString()
                                    .substring(1, x.toString().length() - 1);

                            if (method.equals("moveAction")) {

                                Button moveActionButton = new InfoButton(
                                        "clicca uno square per muoverti");

                                moveActionButton.setMouseTransparent(true);

                                ButtonSquare.setOnMouse1(object.getJsonArray("available"),
                                        mouseEvent -> {

                                            ButtonSquare destination = ((ButtonSquare) mouseEvent
                                                    .getSource());

                                            JsonQueue.add("method", "moveAction");
                                            JsonQueue.add("squareColor", destination.getColor());
                                            JsonQueue.add("squareId",
                                                    String.valueOf(destination.getSquareId()));

                                            JsonQueue.send();
                                        });

                                BoardScreen.getSquareList().forEach(ButtonSquare::update);

                                BoardScreen.collectiveButtons.getChildren()
                                        .add(moveActionButton);
                                resizeButtons();


                            } else if (method.equals("askCollect")) {

                                Button collectButton = new InfoButton(
                                        "clicca il tuo quadrato e raccogli");

                                collectButton.setMouseTransparent(true);

                                ButtonSquare.setOnMouse2(mouseEvent -> {
                                    ButtonSquare destination = ((ButtonSquare) mouseEvent
                                            .getSource());

                                    if (destination.isSpawn()) {

                                        Stage collectStage = new Stage();
                                        collectStage.initModality(Modality.APPLICATION_MODAL);
                                        collectStage.initOwner(GUIView.getCurrentStage());

                                        VBox root = new VBox();
                                        root.setAlignment(Pos.CENTER);
                                        root.setBackground(new Background(
                                                new BackgroundImage(
                                                        Images.imagesMap.get("background"),
                                                        BackgroundRepeat.REPEAT,
                                                        BackgroundRepeat.REPEAT,
                                                        BackgroundPosition.DEFAULT,
                                                        BackgroundSize.DEFAULT)));
                                        root.setSpacing(20);

                                        ToggleGroup cardsInSquareGroup = new ToggleGroup();

                                        HBox cardsInSquare = new HBox();

                                        HBox toggleBox = new HBox();
                                        toggleBox.setSpacing(100);

                                        BoardScreen.getSpawnWeaponList().stream()
                                                .filter(c -> c.getColor()
                                                        .equals(destination.getColor()))
                                                .forEach(c -> {

                                                    ImageView card = new ImageView(
                                                            Images.weaponsMap
                                                                    .get(c.getCardId()));
                                                    card.setFitWidth(100);
                                                    card.setFitHeight(150);

                                                    Button cardButton = new GameButton(card);

                                                    RadioButton radioButton = new RadioButton();
                                                    radioButton.setUserData(
                                                            String.valueOf(c.getCardId()));

                                                    cardsInSquare.getChildren().add(cardButton);
                                                    radioButton
                                                            .setToggleGroup(cardsInSquareGroup);

                                                    toggleBox.getChildren().add(radioButton);

                                                    cardButton.setOnMouseClicked(
                                                            mouseEvent1 ->
                                                                    cardsInSquareGroup
                                                                            .selectToggle(
                                                                                    radioButton));
                                                });
                                        root.getChildren().addAll(cardsInSquare, toggleBox);

                                        HBox myPlayerCards = new HBox();
                                        HBox playerToggles = new HBox();
                                        playerToggles.setSpacing(100);
                                        ToggleGroup playerCardsGroup = new ToggleGroup();
                                        BoardScreen.getPlayerWeaponList().forEach(w -> {

                                            ImageView weaponImage = new ImageView(
                                                    Images.weaponsMap.get(w.getCardId()));
                                            weaponImage.setFitHeight(150);
                                            weaponImage.setFitWidth(100);
                                            Button weaponCardButton = new GameButton(
                                                    weaponImage);

                                            RadioButton weaponRadioButton = new RadioButton();
                                            weaponRadioButton.setUserData(
                                                    String.valueOf(w.getCardId()));
                                            myPlayerCards.getChildren().add(weaponCardButton);
                                            weaponRadioButton.setToggleGroup(playerCardsGroup);
                                            playerToggles.getChildren().add(weaponRadioButton);
                                            weaponCardButton.setOnMouseClicked(
                                                    mouseEvent1 ->
                                                            playerCardsGroup.selectToggle(
                                                                    weaponRadioButton));


                                        });
                                        root.getChildren().addAll(myPlayerCards, playerToggles);

                                        HBox myPowerUps = new HBox();
                                        HBox powerUpCheckBox = new HBox();
                                        powerUpCheckBox.setSpacing(100);
                                        BoardScreen.getPlayerPowerUpList().stream()
                                                .forEach(p -> {

                                                    ImageView powerUpImage = new ImageView(
                                                            Images.powerUpsMap
                                                                    .get(new StringBuilder()
                                                                            .append(p.getName())
                                                                            .append(" ")
                                                                            .append(p
                                                                                    .getColor())
                                                                            .toString()));
                                                    powerUpImage.setFitWidth(100);
                                                    powerUpImage.setFitHeight(150);
                                                    Button powerUpButton = new GameButton(
                                                            powerUpImage);

                                                    CheckBox checkBox = new CheckBox();
                                                    checkBox.setId(new StringBuilder()
                                                            .append(p.getName())
                                                            .append("-").append(p.getColor())
                                                            .toString());

                                                    myPowerUps.getChildren()
                                                            .addAll(powerUpButton);
                                                    powerUpCheckBox.getChildren()
                                                            .addAll(checkBox);

                                                    powerUpButton.setOnMouseClicked(
                                                            mouseEvent1 ->

                                                                    checkBox.setSelected(
                                                                            !checkBox
                                                                                    .isSelected()));
                                                });

                                        root.getChildren().addAll(myPowerUps, powerUpCheckBox);

                                        Button enter = new GameButton("Conferma");

                                        enter.setOnMouseClicked(mouseEvent1 -> {
                                            if (toggleBox.getChildren().stream().anyMatch(
                                                    m -> ((RadioButton) m).isSelected())) {

                                                JsonQueue.add("method", "askCollect");
                                                JsonQueue.add("cardIdCollect",
                                                        toggleBox.getChildren().stream()
                                                                .filter(m -> ((RadioButton) m)
                                                                        .isSelected())
                                                                .findFirst().get().getUserData()
                                                                .toString());

                                                if (playerToggles.getChildren().stream()
                                                        .anyMatch(m -> ((RadioButton) m)
                                                                .isSelected())) {

                                                    JsonQueue.add("cardIdDiscard",
                                                            playerToggles.getChildren().stream()
                                                                    .filter(m -> ((RadioButton) m)
                                                                            .isSelected())
                                                                    .findFirst().get()
                                                                    .getUserData()
                                                                    .toString());
                                                } else {

                                                    JsonQueue.add("cardIdDiscard", "");
                                                }

                                                if (powerUpCheckBox.getChildren().stream()
                                                        .anyMatch(m -> ((CheckBox) m)
                                                                .isSelected())) {

                                                    StringBuilder line = new StringBuilder();

                                                    line.append("powerup(");

                                                    powerUpCheckBox.getChildren().stream()
                                                            .filter(m -> ((CheckBox) m)
                                                                    .isSelected())
                                                            .forEach(s -> {

                                                                line.append(s.getId());
                                                                line.append(" ");
                                                            });
                                                    line.append(")");

                                                    JsonQueue.add("powerups", line.toString());

                                                } else {

                                                    JsonQueue.add("powerups", "");
                                                }

                                                JsonQueue.send();
                                                collectStage.close();
                                            } else {

                                                Notifications.createNotification("error",
                                                        "Devi selezionare quale carta vuoi raccogliere.");
                                            }
                                        });
                                        root.getChildren().add(enter);
                                        Scene collectScene = new Scene(root);
                                        collectStage.setScene(collectScene);
                                        collectStage.show();

                                    } else {

                                        JsonQueue.add("method", "askCollect");
                                        JsonQueue.add("cardIdCollect", "");
                                        JsonQueue.add("cardIdDiscard", "");
                                        JsonQueue.add("powerups", "");
                                        JsonQueue.send();
                                    }
                                });

                                BoardScreen.getSquareList().forEach(ButtonSquare::update);
                                BoardScreen.collectiveButtons.getChildren().add(collectButton);
                                resizeButtons();

                            } else if (method.equals("askActivateWeapon")) {

                                Button activeWeaponButton = new InfoButton(
                                        "clicca una carta per attivarla");

                                BoardScreen.collectiveButtons.getChildren().add(activeWeaponButton);

                                ButtonWeapon.setOnMouse(mouseEvent -> {

                                    BoardScreen.activatedWeapon = ((ButtonWeapon) mouseEvent
                                            .getSource()).getCardId();

                                    JsonQueue.add("method", "askActivateWeapon");
                                    JsonQueue.add("cardId", String.valueOf(
                                            ((ButtonWeapon) mouseEvent.getSource())
                                                    .getCardId()));

                                    JsonQueue.send();
                                });
                                BoardScreen.getPlayerWeaponList().forEach(ButtonWeapon::update);

                                resizeButtons();


                            } else if (method.equals("askReload")) {

                                Button reloadButton = new GameButton("ricarica");

                                BoardScreen.collectiveButtons.getChildren().add(reloadButton);

                                reloadButton.setOnMouseClicked(
                                        mouseEvent -> createReloadStage(method));

                            } else if (method
                                    .equals("endAction")) {
                                Button endAction = createEndActionButton();
                                BoardScreen.collectiveButtons.getChildren().add(endAction);
                                resizeButtons();
                            }

                        });
                    }
                    break;

                default:

                    BoardScreen.isSpawnState = false;

                    object.getJsonArray("methods").forEach(x -> {

                        String method = x.toString()
                                .substring(1, x.toString().length() - 1);

                        if (method.equals("selectAction")) {

                            HBox actions = new HBox();
                            actions.setSpacing(0);

                            object.getJsonObject("bridge").getJsonObject("actionBridge")
                                    .getJsonArray("possibleActionsArray").stream()
                                    .map(JsonValue::asJsonObject)
                                    .map(y -> y.getInt("id"))
                                    .forEach(k -> {

                                        ImageView action = new ImageView(
                                                Images.possibleActionsMap
                                                        .get(GUIView.getCharacter() + k));

                                        int valueOfAction;

                                        switch (k) {
                                            case 0:
                                                valueOfAction = 4;
                                                break;

                                            case 4:
                                            case 7:
                                            case 10:
                                                valueOfAction = 2;
                                                break;

                                            case 5:
                                            case 8:
                                                valueOfAction = 3;
                                                break;

                                            case 6:
                                            case 9:
                                                valueOfAction = 1;
                                                break;

                                            default:
                                                valueOfAction = k;
                                        }

                                        String actionValueString = Integer
                                                .toString(valueOfAction);

                                        Button actionButton = new GameButton(action);
                                        actionButton.setId(String.valueOf(valueOfAction));

                                        actionButton.setOnMouseClicked(
                                                mouseEvent1 -> {

                                                    JsonQueue.add("method", "selectAction");
                                                    JsonQueue.add("actionNumber",
                                                            actionValueString);
                                                    JsonQueue.send();
                                                });

                                        action.setFitHeight(80);
                                        action.setFitWidth(50);
                                        actions.getChildren().add(actionButton);
                                    });

                            BoardScreen.collectiveButtons.getChildren().add(actions);

                            if (object.getJsonObject("bridge").getJsonObject("actionBridge")
                                    .getInt("remainingActions") == 0) {

                                actions.getChildren().stream()
                                        .filter(y -> !y.getId().equals("4"))
                                        .map(y -> (Button) y)
                                        .forEach(y -> {

                                            y.setMouseTransparent(true);
                                        });
                            }

                        } else if (method.equals("askUsePowerUp")) {

                            Button usePowerUpButton = new InfoButton(
                                    "clicca un powerUp per usarlo");

                            ButtonPowerUp.setOnMouse(mouseEvent2 ->

                                    createShootStage("askUsePowerUp",
                                            ((ButtonPowerUp) mouseEvent2
                                                    .getSource()).getArgs(),
                                            ((ButtonPowerUp) mouseEvent2
                                                    .getSource()).hasCost(),
                                            ((ButtonPowerUp) mouseEvent2
                                                    .getSource()).getTargetType(),
                                            new StringBuilder()
                                                    .append("powerup( ")
                                                    .append(((ButtonPowerUp) mouseEvent2
                                                            .getSource())
                                                            .getName())
                                                    .append("-")
                                                    .append(((ButtonPowerUp) mouseEvent2
                                                            .getSource())
                                                            .getColor())
                                                    .append(" )")
                                                    .toString(),
                                            new StringBuilder()
                                                    .append(((ButtonPowerUp) mouseEvent2
                                                            .getSource())
                                                            .getName())
                                                    .append(" ")
                                                    .append(((ButtonPowerUp) mouseEvent2
                                                            .getSource())
                                                            .getColor())
                                                    .toString())
                            );

                            BoardScreen.getPlayerPowerUpList()
                                    .forEach(ButtonPowerUp::update);

                            BoardScreen.collectiveButtons.getChildren().add(usePowerUpButton);

                            resizeButtons();

                        } else if (method.equals("endOfTurn")) {

                            Button fineTurno = new GameButton("fine turno");

                            fineTurno.setOnMouseClicked(mouseEvent -> {

                                JsonQueue.add("method", "endOfTurn");
                                JsonQueue.add("endOfTurn", "");

                                JsonQueue.send();
                            });

                            BoardScreen.collectiveButtons.getChildren().add(fineTurno);
                            resizeButtons();
                        }
                    });
            }
        });
    }

    private static void createReloadStage(String methodName) {

        Stage reloadStage = new Stage();
        reloadStage.initModality(Modality.APPLICATION_MODAL);
        reloadStage.initOwner(GUIView.getCurrentStage());

        VBox root = new VBox();
        root.setSpacing(5);
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        Label cardsLabel = new Label();
        cardsLabel.setText("Le carte da ricaricare:");
        cardsLabel.setWrapText(true);
        cardsLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));
        cardsLabel.setTextFill(Color.WHITE);

        root.getChildren().add(cardsLabel);

        HBox myPlayerCards = new HBox();
        HBox playerToggles = new HBox();
        playerToggles.setSpacing(100);
        ToggleGroup playerCardsGroup = new ToggleGroup();

        BoardScreen.getPlayerWeaponList().stream()
                .filter(card -> !card.isLoaded())
                .forEach(w -> {

                    ImageView weaponImage = new ImageView(
                            Images.weaponsMap.get(w.getCardId()));
                    weaponImage.setFitHeight(150);
                    weaponImage.setFitWidth(100);

                    Button weaponCardButton = new GameButton(weaponImage);

                    RadioButton weaponRadioButton = new RadioButton();
                    weaponRadioButton.setUserData(
                            String.valueOf(w.getCardId()));

                    myPlayerCards.getChildren().add(weaponCardButton);
                    weaponRadioButton.setToggleGroup(playerCardsGroup);
                    playerToggles.getChildren().add(weaponRadioButton);

                    weaponCardButton
                            .setOnMouseClicked(mouseEvent -> playerCardsGroup.selectToggle(
                                    weaponRadioButton));
                });

        root.getChildren().addAll(myPlayerCards, playerToggles);

        Label powerUpsLabel = new Label();

        if (!BoardScreen.getPlayerPowerUpList().isEmpty()) {
            powerUpsLabel.setText("I tuoi powerUp:");
        }
        powerUpsLabel.setWrapText(true);
        powerUpsLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));
        powerUpsLabel.setTextFill(Color.WHITE);

        root.getChildren().add(powerUpsLabel);

        HBox myPowerUps = new HBox();
        HBox powerUpCheckBox = new HBox();
        powerUpCheckBox.setSpacing(100);
        BoardScreen.getPlayerPowerUpList().stream()
                .forEach(p -> {

                    ImageView powerUpImage = new ImageView(
                            Images.powerUpsMap
                                    .get(new StringBuilder().append(p.getName())
                                            .append(" ").append(p.getColor())
                                            .toString()));
                    powerUpImage.setFitWidth(100);
                    powerUpImage.setFitHeight(150);
                    Button powerUpButton = new GameButton(powerUpImage);

                    CheckBox checkBox = new CheckBox();
                    checkBox.setId(new StringBuilder().append(p.getName())
                            .append("-").append(p.getColor())
                            .toString());

                    myPowerUps.getChildren().addAll(powerUpButton);
                    powerUpCheckBox.getChildren().addAll(checkBox);

                    powerUpButton.setOnMouseClicked(mouseEvent -> checkBox.setSelected(
                            !checkBox.isSelected()));
                });

        root.getChildren().addAll(myPowerUps, powerUpCheckBox);

        Button confirm = new GameButton("Conferma");

        confirm.setOnMouseClicked(mouseEvent -> {

            if (playerToggles.getChildren().stream()
                    .anyMatch(m -> ((RadioButton) m).isSelected())) {

                JsonQueue.add("method", methodName);
                JsonQueue.add("id",
                        playerToggles.getChildren().stream()
                                .filter(m -> ((RadioButton) m)
                                        .isSelected())
                                .findFirst().get().getUserData()
                                .toString());

                if (powerUpCheckBox.getChildren().stream()
                        .anyMatch(m -> ((CheckBox) m)
                                .isSelected())) {

                    StringBuilder line = new StringBuilder();

                    line.append("powerup(");

                    powerUpCheckBox.getChildren().stream()
                            .filter(m -> ((CheckBox) m)
                                    .isSelected())
                            .forEach(s -> {

                                line.append(s.getId());
                                line.append(" ");
                            });
                    line.append(")");

                    JsonQueue.add("powerup", line.toString());

                } else {

                    JsonQueue.add("powerup", "");
                }

                JsonQueue.send();
                reloadStage.close();
            } else {

                Notifications.createNotification("error",
                        "Devi selezionare quale carta vuoi ricaricare.");
            }
        });

        root.getChildren().add(confirm);

        Scene reloadScene = new Scene(root);
        reloadStage.setScene(reloadScene);
        reloadStage.show();
    }

    private static void eliminateSetOnMouseClicked() {

        try {

            ButtonPowerUp.setOnMouse(mouseEvent -> {

                //
            });
            BoardScreen.getPlayerPowerUpList().forEach(ButtonPowerUp::update);

            ButtonWeapon.setOnMouse(mouseEvent -> {

                JsonQueue.add("method", "askCardInfo");
                JsonQueue.add("cardId",
                        Integer.toString(((ButtonWeapon) mouseEvent.getSource()).getCardId()));

                JsonQueue.send();
            });
            BoardScreen.getPlayerWeaponList().forEach(ButtonWeapon::update);
            BoardScreen.getSpawnWeaponList().forEach(ButtonWeapon::update);

            ButtonSquare.setOnMouse1(null, null);
            ButtonSquare.setOnMouse2(null);

            BoardScreen.getSquareList().forEach(ButtonSquare::update);

        } catch (NullPointerException e) {

            //
        }
    }

    private static void resizeButtons() {
        int numberOfButtons = BoardScreen.collectiveButtons.getChildren().size();

        if (numberOfButtons > 4) {
            BoardScreen.collectiveButtons.getChildren().stream()
                    .map(x -> (ImageView) ((Button) x).getGraphic())
                    .forEach(x -> x.setFitHeight((195.0 - numberOfButtons * 7) / numberOfButtons));
        }
    }

    private static void createShootStage(String effectType, double args, boolean hasCost,
            String targetType,
            String powerString, String powerUpName) {

        StringBuilder target = new StringBuilder();
        StringBuilder destination = new StringBuilder();

        if (args == 0 && (!hasCost || BoardScreen.getPlayerPowerUpList().isEmpty())) {

            JsonQueue.add("method", effectType);
            JsonQueue.add("line", powerString == null ? "" : powerString);
            JsonQueue.send();

        } else {

            Stage shootStage = new Stage();
            shootStage.initModality(Modality.APPLICATION_MODAL);
            shootStage.initOwner(GUIView.getCurrentStage());

            VBox root = new VBox();
            root.setSpacing(5);
            root.setAlignment(Pos.CENTER);
            root.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
            root.setAlignment(Pos.CENTER);
            root.getChildren().add(new Label());

            shootStage.setScene(new Scene(root));

            if (args == 0) {

                thirdUseEffectScreen(shootStage, root, effectType, target, destination,
                        powerString);

            } else {

                AnchorPane board = BoardScreen
                        .createBoard(false, 2);// scala della meta' in lunghezza

                if (args == 0.5) {

                    createDestination(shootStage, root, board, effectType, args, hasCost, target,
                            destination, powerString);

                } else if (args == 1 || args == 2) {

                    createTarget(shootStage, root, board, effectType, args, hasCost, targetType,
                            target, destination, powerString, powerUpName);
                }
            }
        }
    }

    private static void createTarget(Stage shootStage, VBox root, AnchorPane board,
            String effectType, double args, boolean hasCost,
            String targetType,
            StringBuilder target, StringBuilder destination, String powerString,
            String powerUpName) {

        Label label = new Label();
        label.setFont(Font.font("Silom", 25));
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);

        root.getChildren().add(label);

        Button confirm = new GameButton("conferma");
        confirm.setVisible(false);
        confirm.setMouseTransparent(true);

        if (targetType.equals("PLAYER") || targetType.equals("RECOIL") || targetType
                .equals("MOVE")) {

            label.setText("Seleziona uno o più target");

            HBox playersConnected = new HBox();

            HBox checkBoxes = new HBox();
            checkBoxes.setSpacing(180);

            int numberOfPlayersConnected = BoardScreen.playersInGame.size() - 1;
            int scaleFactor = numberOfPlayersConnected >= 3 ? numberOfPlayersConnected : 3;

            BoardScreen.playersInGame.stream()
                    .filter(x -> !x.equals(GUIView.getCharacter()))
                    .forEach(x -> {

                        ImageView playerConnected = new ImageView(Images.playersMap.get(x));
                        playerConnected.setFitWidth(495.0 / scaleFactor);
                        playerConnected.setFitHeight(220);
                        CheckBox playerCheckBox = new CheckBox();
                        playerCheckBox.setPrefSize(30, 30);
                        playerCheckBox.setId(x);
                        checkBoxes.getChildren().add(playerCheckBox);
                        Button playerConnectedButton = new Button(null, playerConnected);
                        checkBoxes.setSpacing(180);
                        playerConnectedButton.setBackground(new Background(
                                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY,
                                        Insets.EMPTY)));
                        playerConnectedButton.setOnMouseClicked(
                                mouseEvent -> {
                                    playerCheckBox
                                            .setSelected(!playerCheckBox.isSelected());

                                    if (checkBoxes.getChildren().stream()
                                            .noneMatch(c -> ((CheckBox) c).isSelected())) {

                                        confirm.setVisible(false);
                                        confirm.setMouseTransparent(true);

                                    } else {

                                        confirm.setVisible(true);
                                        confirm.setMouseTransparent(false);
                                    }
                                });

                        playersConnected.getChildren().add(playerConnectedButton);

                    });

            root.getChildren().addAll(playersConnected, checkBoxes, confirm);

            confirm.setOnMouseClicked(mouseEvent -> {

                if (checkBoxes.getChildren().stream()
                        .noneMatch(c -> ((CheckBox) c).isSelected())) {
                    //

                } else {

                    target.append("target(");
                    checkBoxes.getChildren().stream()
                            .filter(c -> ((CheckBox) c).isSelected())
                            .forEach(c -> target.append(c.getId()).append(" "));
                    target.append(")");

                }

                if (args == 1 && (!hasCost || BoardScreen.getPlayerPowerUpList().isEmpty())) {

                    JsonQueue.add("method", effectType);
                    JsonQueue.add("line",
                            new StringBuilder().append(target.toString())
                                    .toString());
                    JsonQueue.send();

                    shootStage.close();

                } else if (args == 1 && hasCost) {

                    thirdUseEffectScreen(shootStage, root, effectType, target, destination,
                            powerString);

                } else if (args == 2) {

                    createDestination(shootStage, root, board, effectType, args, hasCost, target,
                            destination, powerString);
                }
            });

        } else if (targetType.equals("SQUARE") || targetType.equals("ROOM")) {

            if (targetType.equals("SQUARE")) {

                label.setText("Seleziona uno o più square");

            } else {

                label.setText("Seleziona una stanza");
            }

            target.append("target(");

            root.getChildren().addAll(board, confirm);

            confirm.setOnAction(mouseEvent -> {

                target.append(" )");

                if (args == 1 && (!hasCost || BoardScreen.getPlayerPowerUpList().isEmpty())) {

                    JsonQueue.add("method", effectType);
                    JsonQueue.add("line",
                            new StringBuilder().append(target.toString())
                                    .toString());
                    JsonQueue.send();

                    shootStage.close();

                } else if (args == 1 && hasCost) {

                    thirdUseEffectScreen(shootStage, root, effectType, target, destination,
                            powerString);

                } else if (args == 2) {

                    createDestination(shootStage, root, board, effectType, args, hasCost, target,
                            destination, powerString);
                }
            });

            confirm.setOnMouseClicked(event -> confirm.fire());

            BoardFunction.getSquareList(board).forEach(s -> s.setOnMouseClicked(
                    mouseEvent -> {

                        s.setOnMouseEntered(null);
                        s.setOnMouseExited(null);
                        s.setOpacity(0.7);

                        target.append(((ButtonSquare) mouseEvent.getSource())
                                .getColor().toLowerCase());

                        if (targetType.equals("SQUARE")) {

                            target.append("-")
                                    .append(((ButtonSquare) mouseEvent.getSource())
                                            .getSquareId());
                            target.append(" ");

                        } else {

                            confirm.fire();
                        }

                        confirm.setVisible(true);
                        confirm.setMouseTransparent(false);
                    }));
        }

        if (!shootStage.isShowing()) {

            shootStage.show();
        }
    }

    private static void createDestination(Stage shootStage, VBox root, AnchorPane board,
            String effectType, double args, boolean hasCost,
            StringBuilder target, StringBuilder destination, String powerUpString) {

        root.getChildren().clear();
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        Label selectDestinationLabel = new Label();
        selectDestinationLabel.setText("Seleziona la destinazione");
        selectDestinationLabel.setFont(Font.font("Silom", FontWeight.BOLD, 25));
        selectDestinationLabel.setTextFill(Color.WHITE);

        BoardFunction.getSquareList(board).forEach(s -> {
            s.setOpacity(0.0);
            s.setOnMouseEntered(mouseEvent -> {
                s.setOpacity(0.3);
                s.getScene().setCursor(Cursor.HAND);
            });
            s.setOnMouseExited(mouseEvent -> {
                s.setOpacity(0.0);
                s.getScene().setCursor(Cursor.DEFAULT);
            });
        });
        board.setVisible(true);
        root.getChildren().addAll(selectDestinationLabel, board);

        BoardFunction.getSquareList(board).forEach(s ->

                s.setOnMouseClicked(
                        mouseEvent -> {

                            destination.append("destinazione(")
                                    .append(((ButtonSquare) mouseEvent.getSource())
                                            .getColor().toLowerCase())
                                    .append("-")
                                    .append(((ButtonSquare) mouseEvent.getSource())
                                            .getSquareId())
                                    .append(")");

                            if (!hasCost || BoardScreen.getPlayerPowerUpList().isEmpty()) {

                                JsonQueue.add("method", effectType);
                                JsonQueue.add("line",
                                        new StringBuilder().append(target.toString())
                                                .append(destination.toString())
                                                .append(powerUpString == null ? "" : powerUpString)
                                                .toString());
                                JsonQueue.send();

                                shootStage.close();

                            } else {

                                thirdUseEffectScreen(shootStage, root, effectType, target,
                                        destination, powerUpString);
                            }
                        }));

        if (!shootStage.isShowing()) {

            shootStage.show();
        }
    }

    private static void thirdUseEffectScreen(Stage shootStage, VBox root, String effectType,
            StringBuilder target,
            StringBuilder destination, String powerUpString) {

        StringBuilder paymentLine = new StringBuilder();

        root.getChildren().clear();
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        if (!effectType.equals("askUsePowerUp")) {

            Label selectPaymentLabel = new Label();
            selectPaymentLabel.setText("Puoi pagare con un power up");
            selectPaymentLabel.setFont(Font.font("Silom", FontWeight.BOLD, 25));
            selectPaymentLabel.setTextFill(Color.WHITE);

            root.getChildren().add(selectPaymentLabel);

            Button confirmButton = new GameButton("vai!");

            HBox buttonsHBox = new HBox();

            buttonsHBox.setSpacing(200);

            buttonsHBox.getChildren().addAll(confirmButton);

            HBox powerUpButtonsHBox = new HBox();
            HBox checkBoxHBox = new HBox();

            checkBoxHBox.setLayoutX(20);

            checkBoxHBox.setSpacing(170);

            BoardScreen.getPlayerPowerUpList().forEach(p -> {

                String nameId = new StringBuilder().append(p.getName())
                        .append("-")
                        .append(p.getColor().toLowerCase())
                        .toString();

                CheckBox powerUpCheckBox = new CheckBox();
                powerUpCheckBox.setId(nameId);

                ImageView powerUp = new ImageView(Images.powerUpsMap
                        .get(new StringBuilder().append(p.getName())
                                .append(" ")
                                .append(p.getColor())
                                .toString()));

                powerUp.setFitWidth(160);
                powerUp.setFitHeight(220);

                Button pButton = new GameButton(powerUp);

                pButton.setOnMouseClicked(
                        mouseEvent -> powerUpCheckBox
                                .setSelected(!powerUpCheckBox.isSelected()));

                checkBoxHBox.getChildren().add(powerUpCheckBox);
                powerUpButtonsHBox.getChildren().add(pButton);
            });
            root.getChildren().addAll(powerUpButtonsHBox, checkBoxHBox, confirmButton);
            confirmButton.setOnMouseClicked(mouseEvent -> {

                if (checkBoxHBox.getChildren().stream()
                        .noneMatch(c -> ((CheckBox) c).isSelected())) {

                    JsonQueue.add("method", effectType);
                    JsonQueue.add("line",
                            new StringBuilder().append(target.toString())
                                    .append(destination.toString())
                                    .toString());
                    JsonQueue.send();

                    shootStage.close();


                } else {
                    paymentLine.append("powerup(");

                    checkBoxHBox.getChildren().stream()
                            .filter(c -> ((CheckBox) c).isSelected())
                            .forEach(c -> paymentLine.append(c.getId()).append(" "));

                    paymentLine.append(")");

                    JsonQueue.add("method", effectType);
                    JsonQueue.add("line",
                            new StringBuilder().append(target.toString())
                                    .append(destination.toString())
                                    .append(paymentLine.toString())
                                    .toString());
                    JsonQueue.send();

                    shootStage.close();
                }
            });
        } else {

            Label payWithCube = new Label();
            payWithCube.setText("Seleziona un cubo");
            payWithCube.setFont(Font.font("Silom", FontWeight.BOLD, 25));
            payWithCube.setTextFill(Color.WHITE);

            root.getChildren().add(payWithCube);

            Button yellow = new GameButton(new ImageView(Images.cubesMap.get("GIALLO")));
            Button red = new GameButton(new ImageView(Images.cubesMap.get("ROSSO")));
            Button blue = new GameButton(new ImageView(Images.cubesMap.get("BLU")));
            yellow.setId("GIALLO");
            red.setId("ROSSO");
            blue.setId("BLU");

            HBox cubes = new HBox();
            cubes.setAlignment(Pos.CENTER);
            cubes.setSpacing(10);

            cubes.getChildren().addAll(yellow, red, blue);

            root.getChildren().add(cubes);

            cubes.getChildren().stream()
                    .map(x -> (GameButton) x)
                    .forEach(x -> {

                        x.setOnMouseClicked(mouseEvent -> {

                            paymentLine.append("paga( ")
                                    .append(x.getId())
                                    .append(" )");

                            JsonQueue.add("method", effectType);
                            JsonQueue.add("line",
                                    new StringBuilder().append(powerUpString)
                                            .append(target.toString())
                                            .append(destination.toString())
                                            .append(paymentLine.toString())
                                            .toString());
                            JsonQueue.send();

                            shootStage.close();
                        });
                    });
        }

        if (!shootStage.isShowing()) {

            shootStage.show();
        }
    }

    private static Button createEndActionButton() {
        Button endAction = new GameButton("fine azione");

        endAction.setOnMouseClicked(mouseEvent -> {

            JsonQueue.add("method", "endAction");
            JsonQueue.add("cardId", "");

            JsonQueue.send();
        });

        return endAction;
    }
}
