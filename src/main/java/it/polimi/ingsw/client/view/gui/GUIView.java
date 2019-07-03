package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.connection.RmiConnection;
import it.polimi.ingsw.client.view.connection.SocketConnection;
import it.polimi.ingsw.client.view.gui.animations.Explosion;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.handlers.CardHandler;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import it.polimi.ingsw.client.view.gui.handlers.Notifications;
import it.polimi.ingsw.client.view.gui.screens.EndGameScreen;
import it.polimi.ingsw.client.view.gui.screens.boardscreen.BoardScreen;
import it.polimi.ingsw.client.view.gui.screens.GameListScreen;
import it.polimi.ingsw.client.view.gui.screens.GameNotStartedScreen;
import it.polimi.ingsw.client.view.gui.screens.LoginScreen;
import it.polimi.ingsw.client.view.gui.handlers.StateHandler;
import it.polimi.ingsw.common.JsonUtility;
import it.polimi.ingsw.common.VirtualView;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Map.Entry;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.json.JsonObject;

public class GUIView extends Application implements View, VirtualView {

    private static String playerId;
    private static String character;
    private static Stage currentStage;

    private static BooleanProperty ready = new SimpleBooleanProperty(false);

    public static String getPlayerId() {

        return playerId;
    }

    public static void setPlayerId(String id) {

        playerId = id;
    }

    public static String getCharacter() {

        return character;
    }

    public static void setCharacter(String player) {

        character = player;
    }

    public static Stage getCurrentStage() {

        return currentStage;
    }

    private static void setCurrentStage(Stage stage) {

        currentStage = stage;
    }

    private static void initialize(GUIView guiView) {

        new Thread(() -> {

            Images.loadImages(guiView);

            ready.setValue(Boolean.TRUE);

            guiView.notifyPreloader(new StateChangeNotification(
                    StateChangeNotification.Type.BEFORE_START));
        }).start();
    }

    public static void changeScene(BorderPane root) {

        root.addEventHandler(MouseEvent.MOUSE_CLICKED, x -> {

            Entry<ImageView, Animation> entry = Explosion.getExplosion(1, x);

            entry.getValue().setOnFinished(y -> root.getChildren().remove(entry.getKey()));

            entry.getValue().play();

            root.getChildren().add(entry.getKey());
        });

        currentStage.getScene().setRoot(root);

        if (!currentStage.isShowing()) {

            currentStage.show();
        }
    }

    public static BorderPane createBorderPane(boolean adrenalina, boolean character) {

        BorderPane borderPane = new BorderPane();

        borderPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        if (adrenalina) {

            ImageView imageAdrenalina = new ImageView(Images.imagesMap.get("adrenalina"));
            imageAdrenalina.setPreserveRatio(true);
            imageAdrenalina.setFitHeight(125);

            borderPane.setTop(imageAdrenalina);
            BorderPane.setAlignment(imageAdrenalina, Pos.TOP_CENTER);
            borderPane.setPadding(new Insets(50, 0, 0, 0));
        }

        if (character) {

            ImageView distructor = new ImageView(Images.imagesMap.get("distructor"));
            distructor.setPreserveRatio(true);
            distructor.setFitHeight(300);

            borderPane.setBottom(distructor);
            BorderPane.setAlignment(distructor, Pos.BOTTOM_LEFT);
        }

        return borderPane;
    }

    @Override
    public void start(Stage stage) {

        initialize(this);

        Scene scene = new Scene(new BorderPane());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);

        stage.setOnCloseRequest(x -> {

            JsonQueue.add("method", "remoteDisconnect");
            JsonQueue.send();
        });

        stage.setMinWidth(750.0 + 485.0);
        stage.setMinHeight(565.0 + 240.0);

        stage.centerOnScreen();

        setCurrentStage(stage);
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public JsonObject userInput() {

        synchronized (JsonQueue.getQueue()) {

            try {

                while (JsonQueue.getQueue().peek() == null) {

                    JsonQueue.getQueue().wait();
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        return JsonQueue.getQueue().remove();
    }

    @Override
    public void initialScreen(int discoveryPort, int rmiPort, int socketPort) {

        ready.addListener((observableValue, aBoolean, t1) -> {

            if (Boolean.TRUE.equals(t1)) {

                try {

                    InetAddress inetAddress = Client.discoverServer(discoveryPort);

                    BorderPane borderPane = createBorderPane(true, true);

                    HBox hBox = new HBox();
                    hBox.setSpacing(50);

                    Button rmiButton = new GameButton(new ImageView(Images.imagesMap.get("rmi")));

                    rmiButton.setOnMouseClicked(x -> {

                        Entry<ImageView, Animation> entry = Explosion
                                .getExplosion(4, x);

                        entry.getValue().setOnFinished(y -> {

                            borderPane.getChildren().remove(entry.getKey());

                            synchronized (View.connection) {

                                View.connection.add(new RmiConnection(inetAddress, rmiPort,
                                        GUIView.this));
                                View.connection.notifyAll();
                            }
                        });

                        entry.getValue().play();

                        borderPane.getChildren().add(entry.getKey());
                    });

                    Button tcpButton = new GameButton(new ImageView(Images.imagesMap.get("tcp")));

                    tcpButton.setOnMouseClicked(x -> {

                        Entry<ImageView, Animation> entry = Explosion
                                .getExplosion(4, x);

                        entry.getValue().setOnFinished(y -> {

                            borderPane.getChildren().remove(entry.getKey());

                            synchronized (View.connection) {

                                View.connection
                                        .add(new SocketConnection(inetAddress, socketPort,
                                                GUIView.this));
                                View.connection.notifyAll();
                            }
                        });

                        entry.getValue().play();

                        borderPane.getChildren().add(entry.getKey());
                    });

                    hBox.getChildren().addAll(rmiButton, tcpButton);
                    borderPane.setCenter(hBox);
                    BorderPane.setAlignment(hBox, Pos.CENTER_RIGHT);
                    hBox.setAlignment(Pos.CENTER);

                    Platform.runLater(() -> changeScene(borderPane));

                } catch (IOException e) {

                    //
                }
            }
        });
    }

    @Override
    public void loginScreen() {

        LoginScreen.generateScreen();
    }

    @Override
    public void gameListScreen() {

        GameListScreen.generateScreen();
    }

    @Override
    public void gameNotStartedScreen() {

        GameNotStartedScreen.generateScreen();
    }


    @Override
    public void boardScreen(JsonObject object) {

        BoardScreen.updateScreen(object);
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void broadcast(String value) {

        Notifications.createNotification("broadcast", value);
    }

    @Override
    public void gameBroadcast(String value) {

        Notifications.createNotification("broadcast", value);
    }

    @Override
    public void infoMessage(String value) {

        Notifications.createNotification("info", value);
    }

    @Override
    public void errorMessage(String value) {

        Notifications.createNotification("error", value);
    }


    @Override
    public void isConnected(String value) {

        //
    }

    @Override
    public void completeLogin(String value) {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        setPlayerId(object.getString("playerId"));

        Notifications.createNotification("info", "Benvenuto " + playerId);

        if (object.containsKey("gameId")) {

            if (object.getBoolean("gameStarted")) {

                this.boardScreen(object);

            } else {

                this.gameNotStartedScreen();
            }

        } else {

            this.gameListScreen();
        }
    }

    @Override
    public void completeDisconnect(String value) {

        System.exit(0);
    }

    @Override
    public void updateGameList(String value) {

        GameListScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public void completeCreateGame(String value) {

        Notifications.createNotification("info", "Partita creata con nome " + value + ".");
    }

    @Override
    public void completeSelectGame(String value) {

        Notifications.createNotification("info",
                "Sei stato aggiunto con successo alla partita " + value + ".");

        this.gameNotStartedScreen();
    }

    @Override
    public void updateGameNotStartedScreen(String value) {

        GameNotStartedScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public void completeVoteBoard(String value) {

        Notifications
                .createNotification("info", "Hai votato per giocare con l'arena " + value + ".");
    }

    @Override
    public void completeSelectAction(String value) {

        Notifications.createNotification("info", "Selezione azione completata.");
    }

    @Override
    public void completeEndAction(String value) {

        Notifications.createNotification("info", "Azione terminata.");
    }

    @Override
    public void completeCardInfo(String value) {

        CardHandler.weaponCardInfo(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public void completePowerUpInfo(String value) {

        CardHandler.powerUpCardInfo(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public synchronized void updateBoard(String value) {

        BoardScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public synchronized void updateState(String value) {

        StateHandler.updateState(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public void endGameScreen(String value) throws RemoteException {

        EndGameScreen.generateScreen(JsonUtility.jsonDeserialize(value));
    }
}
