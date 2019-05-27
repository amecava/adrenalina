package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.connection.RmiConnection;
import it.polimi.ingsw.client.view.connection.SocketConnection;
import it.polimi.ingsw.virtual.VirtualView;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class GUIView extends Application implements View, VirtualView {

    private Scene currentScene;
    private Scene nextScene;
    private static Stage currentStage;
    private ScrollPane gameList;
    private VBox selectedGame;


    @Override
    public void start(Stage stage) throws Exception {
        Image gameImage = new Image("adrenalina.jpg");
        ImageView imageView = new ImageView(gameImage);
        imageView.setPreserveRatio(false);
        BorderPane borderPane = new BorderPane();
        GridPane immagePane = new GridPane();
        immagePane.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(immagePane.widthProperty());
        imageView.fitHeightProperty().bind(immagePane.heightProperty());
        this.currentScene = new Scene(borderPane);
        currentStage = stage;
        currentStage.setMinWidth(1000);
        currentStage.setMinHeight(600);
        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        currentStage.setScene(currentScene);
        borderPane.setCenter(immagePane);
        currentStage.show();


    }

    public static void initialize() {
        GUIView.launch();
    }


    public void changeScene(Parent parent) {
        while (currentStage == null || !currentStage.isShowing()) {
            ;
        }
        currentStage.getScene().setRoot(parent);


    }

    //////////////////////////////////////////////////////////////////


    @Override
    public JsonObject userInput() {

        synchronized (JsonQueue.queue) {

            try {

                while (JsonQueue.queue.peek() == null) {

                    JsonQueue.queue.wait();
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        return JsonQueue.queue.remove();
    }

    @Override
    public void initialScreen(int discoveryPort, int rmiPort, int socketPort) {
        try {
            InetAddress inetAddress = Client.discoverServer(discoveryPort);
            BorderPane borderPane = new BorderPane();
            borderPane.setPrefHeight(615);
            borderPane.setPrefWidth(429);
            borderPane.setBackground(new Background(
                    new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
            Text adrenalina = new Text("adrenalina");
            adrenalina.setFont(Font.font("verdana", 35));
            adrenalina.setFill(Color.WHITE);
            borderPane.setTop(adrenalina);
            BorderPane.setAlignment(adrenalina, Pos.TOP_CENTER);
            HBox hBox = new HBox();
            hBox.setSpacing(20);
            MenuItem socketConnection = new MenuItem("connessione socket");
            socketConnection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (View.queue) {
                        View.queue.add(new SocketConnection(inetAddress, socketPort, GUIView.this));
                        View.queue.notifyAll();
                    }
                }
            });
            MenuItem rmiConnection = new MenuItem("connessione rmi");
            rmiConnection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (View.queue) {
                        View.queue.add(new RmiConnection(inetAddress, rmiPort, GUIView.this));
                        View.queue.notifyAll();
                    }
                }
            });
            MenuButton menuBar = new MenuButton("Connessione", null, rmiConnection,
                    socketConnection);
            hBox.getChildren().addAll(menuBar);
            borderPane.setCenter(hBox);
            BorderPane.setAlignment(hBox, Pos.CENTER_RIGHT);
            hBox.setAlignment(Pos.CENTER);
            Platform.runLater(() -> changeScene(borderPane));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void loginScreen() {
        Text loginText = new Text("login screen");
        loginText.setFont(Font.font("verdana", 35));
        loginText.setFill(Color.WHITE);
        Label connectionLabel = new Label("per favore immetti il nome per fare il login");
        connectionLabel.setFont(Font.font("verdana", 15));
        connectionLabel.setTextFill(Color.WHITE);
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
        GridPane gridPane = new GridPane();
        TextField userLogin = new TextField();
        Button enter = new Button("enter");
        enter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                JsonQueue.add("method", "selectPlayerId");
                JsonQueue.add("playerId", userLogin.getText());
                JsonQueue.send();
            }
        });
        userLogin.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    enter.fire();
                }
            }
        });
        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(connectionLabel, userLogin, enter);
        GridPane.setRowIndex(hBox, 4);
        GridPane.setRowIndex(hBox, 4);
        gridPane.getChildren().addAll(hBox);
        gridPane.setAlignment(Pos.CENTER);
        borderPane.setTop(loginText);
        borderPane.setCenter(gridPane);
        BorderPane.setAlignment(loginText, Pos.TOP_CENTER);
        Platform.runLater(() -> changeScene(borderPane));
    }

    @Override
    public void gamesListScreen() {
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
        Text adrenalina = new Text("gameListScreen");
        adrenalina.setFont(Font.font("verdana", 35));
        adrenalina.setFill(Color.WHITE);
        borderPane.setTop(adrenalina);
        BorderPane.setAlignment(adrenalina, Pos.TOP_CENTER);
        this.gameList = new ScrollPane();
        gameList.setBackground(new Background(
                new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
        gameList.setMaxHeight(200);
        gameList.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        gameList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        borderPane.setCenter(gameList);
        HBox newGame = new HBox();
        newGame.setSpacing(20);
        HBox show = new HBox();
        show.setSpacing(20);
        Button createGame = new Button("Crea Partita");
        createGame.setMinWidth(200);
        createGame.setTextFill(Color.BLACK);
        Label gameName = new Label("nome partita: ");
        gameName.setTextFill(Color.WHITE);
        gameName.setWrapText(true);
        TextField insertGameName = new TextField();
        insertGameName.setPrefSize(80, 30);
        Label numeroMorti = new Label("numero morti nella partita: ");
        numeroMorti.setTextFill(Color.WHITE);
        numeroMorti.setWrapText(true);
        TextField insertNumberOdDeaths = new TextField();
        insertNumberOdDeaths.setPrefSize(60, 20);
        Label frenzy = new Label("frensia finale");
        frenzy.setTextFill(Color.WHITE);
        frenzy.setWrapText(true);
        CheckBox checkBoxFrenzy = new CheckBox();
        Button confirmGame = new Button("crea!");
        confirmGame.setMinWidth(50);
        /////////////////////////////////////////////
        selectedGame = new VBox();
        selectedGame.setSpacing(20);
        Label selectedGameName = new Label("id partita");
        selectedGameName.setTextFill(Color.WHITE);
        TextField game = new TextField();
        Label selectPlayer = new Label("seleziona giocatore");
        selectPlayer.setTextFill(Color.WHITE);
        TextField insertSelectedPlayer = new TextField();
        Button enterGame = new Button("gioca!");
        enterGame.setTextFill(Color.GRAY);
        enterGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game.getText());
                JsonQueue.add("character", insertSelectedPlayer.getText());
                JsonQueue.send();
            }
        });
        selectedGame.getChildren()
                .addAll(selectedGameName, game, selectPlayer, insertSelectedPlayer, enterGame);
        selectedGame.setVisible(false);
        borderPane.setRight(selectedGame);
        selectedGame.setAlignment(Pos.CENTER);
        /////////////////////////////////////////////
        confirmGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "askCreateGame");
                JsonQueue.add("gameId", insertGameName.getText());
                JsonQueue.add("numberOfDeaths", insertNumberOdDeaths.getText());
                JsonQueue.add("frenzy", checkBoxFrenzy.isSelected() ? "frenesia" : "");
                JsonQueue.send();
            }
        });
        confirmGame.setTextFill(Color.BLACK);
        show.getChildren()
                .addAll(gameName, insertGameName, numeroMorti, insertNumberOdDeaths, frenzy,
                        checkBoxFrenzy, confirmGame);
        show.setVisible(false);
        show.setMinWidth(500);
        newGame.getChildren().addAll(createGame, show);
        createGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (show.isVisible()) {
                    show.setVisible(false);
                } else {
                    show.setVisible(true);
                }
            }
        });
        borderPane.setBottom(newGame);
        BorderPane.setAlignment(newGame, Pos.BOTTOM_LEFT);
        createGame.prefHeightProperty().bind(show.prefHeightProperty());
        Platform.runLater(() -> changeScene(borderPane));
    }

    @Override
    public void gameNotStartedScreen() {
        return;
    }

    @Override
    public void boardScreen() {
        return;
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void broadcast(String value) throws RemoteException {
        return;
    }

    @Override
    public void gameBroadcast(String value) throws RemoteException {
        return;
    }

    @Override
    public void infoMessage(String value) throws RemoteException {
        return;
    }

    @Override
    public void errorMessage(String value) throws RemoteException {
        Platform.runLater(() -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initOwner(currentStage);
            VBox dialogVbox = new VBox();
            Text title = new Text("ERROR MESSAGE:");
            title.setFont(Font.font("verdana", 20));
            title.setFill(Color.YELLOW);
            Text message = new Text(value);
            message.setFill(Color.WHITE);
            dialogVbox.getChildren().addAll(title, message);
            dialogVbox.setSpacing(20);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialogVbox.setBackground(new Background(new BackgroundFill( Color.rgb(25,  31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
            dialog.setScene(dialogScene);
            dialogVbox.setAlignment(Pos.CENTER);
            dialog.show();
            dialogVbox.requestFocus();
            dialogVbox.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    dialog.close();
                }
            });
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> dialog.close());
            delay.play();
        });
    }


    @Override
    public void isConnected(String value) throws RemoteException {
        return;
    }

    @Override
    public void completeLogin(String value) throws RemoteException {

        this.gamesListScreen();
    }

    @Override
    public void completeDisconnect(String value) throws RemoteException {
        return;
    }

    @Override
    public void updateGameList(String value) throws RemoteException {

        try (JsonReader reader = Json.createReader(new StringReader(value))) {
            JsonArray jsonArray = reader.readArray();
            VBox games = new VBox();
            games.setSpacing(40);
            jsonArray.stream().map(JsonValue::asJsonObject).forEach(x -> {
                HBox hBox = new HBox();
                Background hboxBackground = new Background(
                        new BackgroundFill(Color.rgb(217, 217, 217), CornerRadii.EMPTY,
                                Insets.EMPTY));
                hBox.setBackground(hboxBackground);
                hBox.setSpacing(20);
                Label gameId = new Label("nome della partita: " + x.getString("gameId"));
                gameId.setWrapText(true);
                hBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (selectedGame.isVisible()) {
                            selectedGame.setVisible(false);
                        } else {
                            selectedGame.setVisible(true);
                            ((TextField) (selectedGame.getChildren().get(1)))
                                    .setText(gameId.getText().replace("nome della partita: ", ""));
                        }

                    }
                });
                Label numberofDeaths = new Label(
                        "numero max morti: " + x.getInt("numberOfDeaths"));
                numberofDeaths.setWrapText(true);
                Label frenzy = new Label("frenesia finale:  " + x.getBoolean("frenzy"));
                frenzy.setWrapText(true);
                Label connectedPlayers = new Label("giocatori :");
                HBox players = new HBox();
                players.setSpacing(20);
                hBox.getChildren()
                        .addAll(gameId, numberofDeaths, frenzy, connectedPlayers, players);

                (x.getJsonArray("playerList")).stream().map(JsonValue::asJsonObject).forEach(y -> {
                    Label IdGiocatoreLable = new Label("id giocatore : " + y.getString("playerId"));
                    IdGiocatoreLable.setWrapText(true);
                    Label playerConnesso = new Label("connesso: " + y.getBoolean("connected"));
                    playerConnesso.setWrapText(true);
                    players.getChildren().addAll(IdGiocatoreLable, playerConnesso);
                });
                games.getChildren().add(hBox);

            });
            Platform.runLater(() -> gameList.setContent(games));


        }

        return;
    }

    @Override
    public void completeCreateGame(String value) throws RemoteException {
        return;
    }

    @Override
    public void completeSelectGame(String value) throws RemoteException {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new Text("sei in complete selectGame"));
        Platform.runLater(() -> GUIView.this.changeScene(borderPane));
    }

    @Override
    public void updateGameNotStartedScreen(String value) throws RemoteException {
        return;
    }

    @Override
    public void completeVoteBoard(String value) throws RemoteException {
        return;
    }

    @Override
    public void updateBoard(String value) throws RemoteException {
        return;
    }

    JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}
