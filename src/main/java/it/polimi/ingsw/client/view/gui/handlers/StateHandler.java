package it.polimi.ingsw.client.view.gui.handlers;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.ButtonPowerUp;
import it.polimi.ingsw.client.view.gui.buttons.ButtonSquare;
import it.polimi.ingsw.client.view.gui.buttons.ButtonWeapon;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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

public class StateHandler {

    private StateHandler() {

        //
    }

    public static synchronized void updateState(JsonObject object) {

        Platform.runLater(() -> {

            eliminateSetOnMouseClicked();

            // TODO
            if (BoardScreen.collectiveButtons != null) {

                BoardScreen.collectiveButtons.getChildren().clear();
            }

            ////////////////////////////////////////////////////bottoni sempre disponibli infocarte , info powerup

            object.getJsonArray("info").forEach(x -> {

                String method = x.toString().substring(1, x.toString().length() - 1);

                if (method.equals("askCardInfo")) {

                    Button infoCarte = new GameButton("info carte");

                    infoCarte.setOnMouseClicked(mouseEvent -> {

                        Stage infoCarteStage = new Stage();
                        infoCarteStage.initModality(Modality.APPLICATION_MODAL);
                        infoCarteStage.initOwner(GUIView.getCurrentStage());
                        ScrollPane images = new ScrollPane();
                        HBox twoCrads = new HBox();
                        twoCrads.setSpacing(80);
                        VBox allcards = new VBox();
                        allcards.setSpacing(30);
                        for (int i = 1; i < 22; i++) {
                            ImageView back = new ImageView(Images.weaponsMap.get(0));
                            ImageView card = new ImageView(Images.weaponsMap.get(i));
                            card.setFitWidth(150);
                            card.setFitHeight(250);
                            ButtonWeapon buttonWeapon = new ButtonWeapon(i, back, card, Rotate.Y_AXIS);

                            buttonWeapon.setOnMouseClicked(weaponMouseEvent -> {

                                JsonQueue.add("method", "askCardInfo");
                                JsonQueue.add("cardId", Integer.toString(
                                        ((ButtonWeapon) weaponMouseEvent.getSource())
                                                .getCardId()));
                                JsonQueue.send();
                            });
                            twoCrads.getChildren().add(buttonWeapon);

                            if (twoCrads.getChildren().size() == 2) {

                                allcards.getChildren().add(twoCrads);
                                twoCrads = new HBox();
                                twoCrads.setSpacing(80);


                            }

                            buttonWeapon.setVisible(false);
                            buttonWeapon.flipTransition(Duration.millis(1), actionEvent -> buttonWeapon.setVisible(true)).play();
                        }
                        images.setContent(allcards);
                        images.setVbarPolicy(ScrollBarPolicy.ALWAYS);
                        Scene imagesScene = new Scene(new StackPane(images), 450, 500);
                        infoCarteStage.setScene(imagesScene);
                        infoCarteStage.show();
                    });
                    infoCarte.setAlignment(Pos.CENTER);
                    BoardScreen.collectiveButtons.getChildren().add(infoCarte);
                    resizeButtons();

                } else if (method.equals("askInfoPowerUp")) {

                    //TODO infopowerup
                    //

                }
            });

            ////////////////////////////////////////////////////////////////////state che impongono

            switch (object.getString("state")) {

                case "spawnState":

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

                    Button spawnButton = new GameButton(
                            "clicca un powerup per spawnare");

                    spawnButton.setMouseTransparent(true);

                    BoardScreen.collectiveButtons.getChildren().add(spawnButton);
                    resizeButtons();
                    break;

                case "shootState":

                    object.getJsonArray("methods").forEach(x -> {

                        String method = x.toString()
                                .substring(1, x.toString().length() - 1);

                        if (method.equals("askUsePrimary")) {

                            Button effettoPrimario = new GameButton("effetto primario");

                            effettoPrimario.setOnMouseClicked(
                                    mouseEvent -> createShootStage("askUsePrimary"));
                            BoardScreen.collectiveButtons.getChildren()
                                    .add(effettoPrimario);
                            resizeButtons();

                        } else if (method.equals("askUseAlternative")) {

                            Button effettoAlternativo = new GameButton(
                                    "effetto alternativo");

                            effettoAlternativo.setOnMouseClicked(
                                    mouseEvent -> createShootStage("askUseAlternative"));
                            BoardScreen.collectiveButtons.getChildren()
                                    .add(effettoAlternativo);
                            resizeButtons();

                        } else if (method.equals("askUseOptional1")) {

                            Button opzionale1 = new GameButton("effetto opzionale 1");

                            opzionale1.setOnMouseClicked(
                                    mouseEvent -> createShootStage("askUseOptional1"));
                            BoardScreen.collectiveButtons.getChildren().add(opzionale1);
                            resizeButtons();

                        } else if (method.equals("askUseOptional2")) {

                            Button opzionale2 = new GameButton("effetto opzionale 2");

                            opzionale2.setOnMouseClicked(
                                    mouseEvent -> createShootStage("askUseOptional2"));
                            BoardScreen.collectiveButtons.getChildren().add(opzionale2);
                            resizeButtons();

                        } else if (method.equals("endAction")) {
                            Button endAction = createEndActionButton();
                            BoardScreen.collectiveButtons.getChildren().addAll(endAction);
                            resizeButtons();
                        }
                    });
                    break;

                case "actionState":

                    object.getJsonArray("methods").forEach(x -> {

                        String method = x.toString()
                                .substring(1, x.toString().length() - 1);

                        if (method.equals("moveAction")) {

                            Button moveActionButton = new GameButton(
                                    "clicca uno square per muoverti");

                            moveActionButton.setMouseTransparent(true);

                            ButtonSquare.setOnMouse1(mouseEvent -> {

                                ButtonSquare destination = ((ButtonSquare) mouseEvent
                                        .getSource());

                                JsonQueue.add("method", "moveAction");
                                JsonQueue.add("squareColor", destination.getColor());
                                JsonQueue.add("squareId",
                                        String.valueOf(destination.getSquareId()));

                                JsonQueue.send();
                            });

                            BoardScreen.getSquareList()
                                    .forEach(ButtonSquare::update);

                            BoardScreen.collectiveButtons.getChildren()
                                    .add(moveActionButton);
                            resizeButtons();


                        } else if (method.equals("askCollect")) {

                            Button collectButton = new GameButton(
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

                            BoardScreen.getSquareList()
                                    .forEach(ButtonSquare::update);
                            BoardScreen.collectiveButtons.getChildren().add(collectButton);
                            resizeButtons();

                        } else if (method.equals("askActivateWeapon")) {

                            Button activateCardButton = new GameButton(
                                    "clicca un'arma per attivarla");

                            activateCardButton.setMouseTransparent(true);

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

                            BoardScreen.collectiveButtons.getChildren()
                                    .add(activateCardButton);
                            resizeButtons();


                        } else if (method.equals("askReload")) {

                            Button reloadButton = new GameButton("Ricarica");

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
                    break;

                default:

                    object.getJsonArray("methods").forEach(x -> {

                        String method = x.toString()
                                .substring(1, x.toString().length() - 1);

                        if (method.equals("selectAction")) {

                            Button selezionaAzione = new GameButton("seleziona azione");

                            BoardScreen.collectiveButtons.getChildren()
                                    .add(selezionaAzione);

                            selezionaAzione.setOnMouseClicked(mouseEvent -> {

                                Stage chooseAction = new Stage();
                                chooseAction.initModality(Modality.APPLICATION_MODAL);
                                chooseAction.initOwner(GUIView.getCurrentStage());
                                VBox root = new VBox();
                                root.setSpacing(10);
                                root.setBackground(new Background(
                                        new BackgroundImage(
                                                Images.imagesMap.get("background"),
                                                BackgroundRepeat.REPEAT,
                                                BackgroundRepeat.REPEAT,
                                                BackgroundPosition.DEFAULT,
                                                BackgroundSize.DEFAULT)));

                                HBox actions = new HBox();
                                actions.setSpacing(0);

                                BoardScreen.actionsList.forEach(k -> {

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

                                    actionButton.setOnMouseClicked(
                                            mouseEvent1 -> {

                                                JsonQueue.add("method", "selectAction");
                                                JsonQueue.add("actionNumber",
                                                        actionValueString);
                                                JsonQueue.send();
                                                chooseAction.close();

                                            });

                                    action.setFitHeight(80);
                                    action.setFitWidth(50);
                                    actions.getChildren().add(actionButton);
                                });

                                Button quit = new GameButton("quit");
                                quit.setOnMouseClicked(mouseEvent1 -> chooseAction.close());

                                root.getChildren().addAll(actions, quit);
                                chooseAction.setScene(new Scene(root));
                                chooseAction.show();
                            });

                        } else if (method.equals("askUsePowerUp")) {

                            ButtonPowerUp.setOnMouse(mouseEvent -> {

                                //
                            });

                            BoardScreen.getPlayerPowerUpList()
                                    .forEach(ButtonPowerUp::update);
                            //se schiacci powerUp usi powerup (usa effetto nuova finestra)
                            //setOnMouse

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
        powerUpsLabel.setText("I tuoi powerUp:");
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

                Notifications.createNotification("Attenzione!",
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

            ButtonSquare.setOnMouse1(mouseEvent -> {

                //
            });
            ButtonSquare.setOnMouse2(mouseEvent -> {

                //
            });

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

    private static void createShootStage(String effectType) {

        VBox root = new VBox();
        root.setSpacing(5);
        StringBuilder target = new StringBuilder();
        StringBuilder destination = new StringBuilder();
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        AnchorPane board = BoardScreen
                .createBoard(false, 2);// scala della meta' in lunghezza
        HBox playersConnected = new HBox();
        HBox checkBoxes = new HBox();
        VBox playersAndCheckBox = new VBox();
        HBox endingButtons = new HBox();
        endingButtons.setSpacing(450);
        int numberOfPlayersConnected = BoardScreen.playersInGame.size() - 1;
        int scaleFactor = numberOfPlayersConnected >= 3 ? numberOfPlayersConnected : 3;
        checkBoxes.setSpacing((495.0 / scaleFactor) + 10);

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
                    checkBoxes.setSpacing(playerConnectedButton.getWidth());
                    playerConnectedButton.setBackground(new Background(
                            new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY,
                                    Insets.EMPTY)));
                    playerConnectedButton.setOnMouseClicked(
                            mouseEvent -> playerCheckBox.setSelected(!playerCheckBox.isSelected()));

                    playersConnected.getChildren().add(playerConnectedButton);

                });

        StackPane playersAndMap = new StackPane();

        Button confirm = new GameButton("conferma");
        confirm.setVisible(false);
        confirm.setMouseTransparent(true);

        endingButtons.getChildren().add(confirm);
        Button skipTarget = new GameButton("salta target");
        skipTarget.prefHeightProperty().bind(confirm.prefHeightProperty());
        skipTarget.prefWidthProperty().bind(confirm.prefWidthProperty());
        skipTarget.setOnMouseClicked(
                mouseEvent -> createDestination(root, board, effectType, target, destination));

        playersAndCheckBox.getChildren()
                .addAll(playersConnected, checkBoxes);

        playersAndMap.setVisible(false);
        playersAndMap.getChildren().addAll(board, playersAndCheckBox);
        Stage shootStage = new Stage();
        shootStage.initModality(Modality.APPLICATION_MODAL);
        shootStage.initOwner(GUIView.getCurrentStage());
        HBox cardAndRadioButtons = new HBox();
        ToggleGroup radioButtonToggle = new ToggleGroup();
        ImageView cardImage = new ImageView(Images.weaponsMap.get(BoardScreen.activatedWeapon));
        cardImage.setFitWidth(100);
        cardImage.setFitHeight(150);
        cardAndRadioButtons.getChildren().add(cardImage);

        VBox radioButtonAndLabel = new VBox(30);
        HBox lineOfRadioAndPlayer = new HBox();
        RadioButton playerTarget = new RadioButton();
        playerTarget.setToggleGroup(radioButtonToggle);
        playerTarget.setOnMouseClicked(mouseEvent -> {

            target.setLength(0);
            confirm.setVisible(true);
            confirm.setMouseTransparent(false);
            playersAndMap.setVisible(true);
            board.setVisible(false);
            playersAndCheckBox.setVisible(true);
            playersAndMap.getChildren().clear();
            playersAndMap.getChildren().addAll(board, playersAndCheckBox);
        });
        Label playerLabel = new Label("Target: personaggio");
        lineOfRadioAndPlayer.getChildren().addAll(playerLabel, playerTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndPlayer);
        playerLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        HBox lineOfRadioAndButton = new HBox();
        RadioButton squareTarget = new RadioButton();

        board.getChildren().stream()
                .filter(n -> n.getId().equals("squares"))
                .map(n -> (VBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (HBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (StackPane) n)
                .flatMap(n -> n.getChildren().stream())
                .filter(n -> n.getId().equals("buttonSquare"))
                .map(n -> (ButtonSquare) n)
                .filter(ButtonSquare::isPresent)
                .forEach(s -> s.setOnMouseClicked(
                        mouseEvent -> {

                            s.setOnMouseEntered(null);
                            s.setOnMouseExited(null);
                            s.setOpacity(0.9);

                            target.append(((ButtonSquare) mouseEvent.getSource())
                                    .getColor().toLowerCase());

                            if (squareTarget.isSelected()) {

                                target.append("-")
                                        .append(((ButtonSquare) mouseEvent.getSource())
                                                .getSquareId());
                                target.append(" ");
                            } else {
                                target.append(")");
                                createDestination(root, board, effectType, target,
                                        destination);
                            }

                        }));
        squareTarget.setOnMouseClicked(mouseEvent -> {
            target.setLength(0);
            target.append("target(");
            confirm.setVisible(true);
            confirm.setMouseTransparent(false);
            playersAndMap.setVisible(true);
            board.setVisible(true);
            playersAndCheckBox.setVisible(false);
            playersAndMap.getChildren().clear();
            playersAndMap.getChildren().addAll(playersAndCheckBox, board);
        });
        squareTarget.setToggleGroup(radioButtonToggle);
        Label buttonLabel = new Label("Target: quadrato");
        lineOfRadioAndButton.getChildren().addAll(buttonLabel, squareTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndButton);
        buttonLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        HBox lineOfRadioAndRoom = new HBox();
        RadioButton roomTarget = new RadioButton();
        roomTarget.setOnMouseClicked(mouseEvent -> {
            target.setLength(0);
            target.append("target(");
            confirm.setMouseTransparent(true);
            confirm.setVisible(false);
            playersAndMap.setVisible(true);
            board.setVisible(true);
            playersAndCheckBox.setVisible(false);
            playersAndMap.getChildren().clear();
            playersAndMap.getChildren().addAll(playersAndCheckBox, board);
        });
        roomTarget.setToggleGroup(radioButtonToggle);
        Label roomLabel = new Label("Target: stanza");
        roomLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));
        lineOfRadioAndRoom.getChildren().addAll(roomLabel, roomTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndRoom);
        cardAndRadioButtons.getChildren().add(radioButtonAndLabel);
        confirm.setOnMouseClicked(mouseEvent -> {
            if (playerTarget.isSelected()) {
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
                createDestination(root, board, effectType, target, destination);
            } else {
                target.deleteCharAt(target.length() - 1);
                target.append(")");
                createDestination(root, board, effectType, target,
                        destination);
            }

        });
        endingButtons.getChildren().add(skipTarget);
        root.getChildren().addAll(cardAndRadioButtons, playersAndMap, endingButtons);
        shootStage.setScene(new Scene(root));
        shootStage.show();
    }

    private static void createDestination(VBox root, AnchorPane board, String effectType,
            StringBuilder target, StringBuilder destination) {

        root.getChildren().clear();
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        Label selectDestinationLabel = new Label();
        selectDestinationLabel.setText("Seleziona la destinazione (se necessario):");
        selectDestinationLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        Button skipButton = new Button();
        skipButton.setText("Salta");
        skipButton.setStyle("-fx-text-inner-color: white; -fx-font: 20px Silom");

        skipButton.setOnMouseClicked(
                mouseEvent -> thirdUseEffectScreen(root, effectType, target, destination));
        board.getChildren().stream()
                .filter(n -> n.getId().equals("squares"))
                .map(n -> (VBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (HBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (StackPane) n)
                .flatMap(n -> n.getChildren().stream())
                .filter(n -> n.getId().equals("buttonSquare"))
                .map(n -> (ButtonSquare) n)
                .filter(ButtonSquare::isPresent)
                .forEach(s -> {
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
        root.getChildren().addAll(selectDestinationLabel, board, skipButton);
        board.getChildren().stream()
                .filter(n -> n.getId().equals("squares"))
                .map(n -> (VBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (HBox) n)
                .flatMap(n -> n.getChildren().stream())
                .map(n -> (StackPane) n)
                .flatMap(n -> n.getChildren().stream())
                .filter(n -> n.getId().equals("buttonSquare"))
                .map(n -> (ButtonSquare) n)
                .filter(ButtonSquare::isPresent)
                .forEach(s -> s.setOnMouseClicked(
                        mouseEvent -> {

                            destination.append("destinazione(")
                                    .append(((ButtonSquare) mouseEvent.getSource())
                                            .getColor().toLowerCase())
                                    .append("-")
                                    .append(((ButtonSquare) mouseEvent.getSource())
                                            .getSquareId())
                                    .append(")");

                            thirdUseEffectScreen(root, effectType, target,
                                    destination);


                        }));


    }

    private static void thirdUseEffectScreen(VBox root, String effectType, StringBuilder target,
            StringBuilder destination) {

        StringBuilder paymentLine = new StringBuilder();

        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        if (!effectType.equals("askUsePowerUp")) {

            root.getChildren().clear();

            Label selectPaymentLabel = new Label();
            selectPaymentLabel.setText("Vuoi pagare con un powerup?");
            selectPaymentLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

            Button confirmButton = new GameButton("paga");

            Button noButton = new GameButton("no");

            HBox buttonsHBox = new HBox();

            buttonsHBox.setSpacing(200);

            buttonsHBox.getChildren().addAll(confirmButton, noButton);

            noButton.setOnMouseClicked(mouseEvent -> {
                JsonQueue.add("method", effectType);
                JsonQueue.add("line",
                        new StringBuilder().append(target.toString())
                                .append(destination.toString())
                                .toString());
                JsonQueue.send();
            });

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

                }
            });
        } else {

            //TODO cubesPayment Screen
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
