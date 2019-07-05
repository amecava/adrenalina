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
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.json.JsonObject;

/**
 * main class for the gui .this class implements the method for changing the scene and
 * for interacting with the preloader.
 */
public class GUIView extends Application implements View, VirtualView {

    /**
     * player name
     */
    private static String playerId;
    /**
     * character chosen by the player
     */
    private static String character;
    /**
     * primary stage showing all the scenes
     */
    private static Stage currentStage;

    /**
     * variable necessary for the preloader to work
     */
    private static BooleanProperty ready = new SimpleBooleanProperty(false);

    /**
     *
     * @return the player idn
     */
    public static String getPlayerId() {

        return playerId;
    }

    /**
     * sets the player id
     * @param id of the player
     */
    public static void setPlayerId(String id) {

        playerId = id;
    }

    /**
     *
     * @return name of the character chosen by the player
     */
    public static String getCharacter() {

        return character;
    }

    /**
     * sets the character chosen by the player
     * @param player is the chosen character
     */
    public static void setCharacter(String player) {

        character = player;
    }

    /**
     *
     * @return the current stage showing the current scene
     */
    public static Stage getCurrentStage() {

        return currentStage;
    }

    /**
     * sets the current stage
     * @param stage the next current stage
     */
    private static void setCurrentStage(Stage stage) {

        currentStage = stage;
    }

    /**
     * necessary method for the preloader to work with javafx
     * @param guiView the view that will be launched after the preloader has finished
     */
    private static void initialize(GUIView guiView) {

        new Thread(() -> {

            Images.loadImages(guiView);

            ready.setValue(Boolean.TRUE);

            guiView.notifyPreloader(new StateChangeNotification(
                    StateChangeNotification.Type.BEFORE_START));
        }).start();
    }

    /**
     * the current scene will be changed for a new scene
     * @param root new scene that will be shown
     */
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

    /**
     * creating the first scene after the preloader has finished
     * @param adrenalina expresses if the image adrenalina can be shown
     * @param character expresses if the character image can be shown
     * @return the new current scene
     */
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

    /**
     * starts the gui view
     * @param stage current stage that will be shown
     */
    @Override
    public void start(Stage stage) {

        stage.setTitle("Adrenalina");
        stage.getIcons().add(new Image("images/adrenaline_icon.png"));

        initialize(this);
        Scene scene = new Scene(new BorderPane());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);

        stage.setOnCloseRequest(x -> {

            JsonQueue.add("method", "remoteDisconnect");
            JsonQueue.send();

            try {

                Thread.sleep(1000);

                System.exit(0);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        });

        stage.setMinWidth(750.0 + 485.0);
        stage.setMinHeight(565.0 + 240.0);

        stage.centerOnScreen();

        setCurrentStage(stage);
    }

    /////////////////////////////////////////////////////////////////////////

    /**
     * this method creates a bridge between the user and the gui ,
     * the gui takes user's input and puts it into a queue where a thread is waiting
     * to take information and send it to the server
     * @return the information that will be sent to the server
     */
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

    /**
     * initial screen on wich the user chooses rmi or socket connections , also in this method
     * the gui sends a udp packet to discover the server's ip adress
     * @param discoveryPort port on wich the client sends udp packets to find server's ip
     * @param rmiPort rmi port
     * @param socketPort socket port
     */
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
                    Button tcpButton = new GameButton(new ImageView(Images.imagesMap.get("tcp")));

                    rmiButton.setOnMouseClicked(x -> {

                        Entry<ImageView, Animation> entry = Explosion
                                .getExplosion(4, x);

                        entry.getValue().setOnFinished(actionEvent -> {

                            hBox.getChildren().remove(tcpButton);
                            rmiButton.setMouseTransparent(true);

                            RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), rmiButton);
                            rotateTransition.setAxis(Rotate.Y_AXIS);
                            rotateTransition.setFromAngle(0);
                            rotateTransition.setToAngle(360);
                            rotateTransition.setCycleCount(1000);
                            rotateTransition.play();

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

                    tcpButton.setOnMouseClicked(x -> {

                        Entry<ImageView, Animation> entry = Explosion
                                .getExplosion(4, x);

                        entry.getValue().setOnFinished(y -> {

                            hBox.getChildren().remove(rmiButton);
                            tcpButton.setMouseTransparent(true);

                            RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), tcpButton);
                            rotateTransition.setAxis(Rotate.Y_AXIS);
                            rotateTransition.setFromAngle(0);
                            rotateTransition.setToAngle(360);
                            rotateTransition.setCycleCount(1000);
                            rotateTransition.play();

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

    /**
     * creates the login screen
     */
    @Override
    public void loginScreen() {

        LoginScreen.generateScreen();
    }

    /**
     * creates the game List Screen
     */
    @Override
    public void gameListScreen() {

        GameListScreen.generateScreen();
    }

    /**
     * creates the game not started screen
     */
    @Override
    public void gameNotStartedScreen() {

        GameNotStartedScreen.generateScreen();
    }

    /**
     * creates the main board screen
     * @param object Json Object with the information of all the game
     */

    @Override
    public void boardScreen(JsonObject object) {

        BoardScreen.updateScreen(object);
    }

    /////////////////////////////////////////////////////////////////////////

    /**
     * method called by the server to broadcast messages to the end users
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void broadcast(String value) {

        Notifications.createNotification("broadcast", value);
    }

    /**
     * method called by the server for broadcasting messages to the end users in a specific game
     * @param value A serialized JsonObject that will be deserialized   using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public void gameBroadcast(String value) {

        Notifications.createNotification("broadcast", value);
    }

    /**
     * method called by the server for sending an  info message to a user
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void infoMessage(String value) {

        Notifications.createNotification("info", value);
    }

    /**
     * method called by the server for sending an  error message  to a user
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public void errorMessage(String value) {

        Notifications.createNotification("error", value);
    }

    /**
     * method used by the server to test if the client is still connected
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */


    @Override
    public void isConnected(String value) {

        //
    }

    /**
     * method called by the server after tthe client has completed the login screen
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
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

    /**
     * method used to verify the complete disconnection  from the server
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void completeDisconnect(String value) {

        System.exit(0);
    }

    /**
     * method used to update the game list screen when a new game is created
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void updateGameList(String value) {

        GameListScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called when a new game is created successfully
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void completeCreateGame(String value) {

        Notifications.createNotification("info", "Partita creata con nome " + value + ".");
    }

    /**
     * method called by the server after a game has been successfully selected
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void completeSelectGame(String value) {

        Notifications.createNotification("info",
                "Sei stato aggiunto con successo alla partita " + value + ".");

        this.gameNotStartedScreen();
    }

    /**
     * method called by the server after the player has entered successfully a game
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void updateGameNotStartedScreen(String value) {

        GameNotStartedScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called by the server after a client has successfully voted for a board in
     * the game not started screen
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void completeVoteBoard(String value) {

        Notifications
                .createNotification("info", "Hai votato per giocare con l'arena " + value + ".");
    }

    /**
     * method called by the server after the client has successfully selected an action in
     * the game started screen
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */
    @Override
    public void completeSelectAction(String value) {

        Notifications.createNotification("info", "Selezione azione completata.");
    }

    /**
     * method called by the server after the client has successfully ended an action
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public void completeEndAction(String value) {

        Notifications.createNotification("info", "Azione terminata.");
    }

    /**
     * method called by the server after the user has asked for a card info
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public void completeCardInfo(String value) {

        CardHandler.weaponCardInfo(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called by the server after a client has asked for an info on a power up
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public void completePowerUpInfo(String value) {

        CardHandler.powerUpCardInfo(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called by the server for updating the board of the client after something has
     * been changed
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public synchronized void updateBoard(String value) {

        BoardScreen.updateScreen(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called by the server for updating the client state , the state defines the
     * possible action a client can call on the server
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     */

    @Override
    public synchronized void updateState(String value) {

        StateHandler.updateState(JsonUtility.jsonDeserialize(value));
    }

    /**
     * method called by the server for finishing the game and showing the winners
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the model
     * @throws RemoteException if the client-server can't talk to each other with the
     * network
     */

    @Override
    public void endGameScreen(String value) throws RemoteException {

        EndGameScreen.generateScreen(JsonUtility.jsonDeserialize(value));
    }
}
