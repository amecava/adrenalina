package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.connection.RmiConnection;
import it.polimi.ingsw.client.view.connection.SocketConnection;
import it.polimi.ingsw.virtual.JsonUtility;
import it.polimi.ingsw.virtual.VirtualView;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.security.Guard;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Screen;
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

    private static Stage currentStage;

    private ScrollPane gameList;

    private VBox collectiveButtons = new VBox();

    private List<Stage> notifications = new ArrayList<>();

    private static EventHandler<KeyEvent> noSpace;
    public static EventHandler<MouseEvent> bigger;
    public static EventHandler<MouseEvent> smaller;

    private static ScaleTransition stBig;
    private static ScaleTransition stSmall;

    private static JsonArray jsonArray;

    private List<ButtonSquare> squareList = new ArrayList<>();
    private List<ButtonPowerUp> powerUpList = new ArrayList<>();
    private List<ButtonWeapon> weaponsList = new ArrayList<>();
    private List<ButtonWeapon> weaponsInSpawnSquare = new ArrayList<>();
    private List<Integer> actionsList = new ArrayList<>();
    private List<String> playersInGame = new ArrayList<>();
    private List<ButtonSquare> squareListForShootState = new ArrayList<>();

    private String character;

    private String playerIdView;
    private int boardId;
    private int activatedWeapon;

    private static BooleanProperty ready = new SimpleBooleanProperty(false);

    private void initialize() {

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                notifyPreloader(new ProgressNotification(0));
                InputStream in = GUIView.class.getClassLoader()
                        .getResourceAsStream("Boards/Board.json");

                jsonArray = Json.createReader(in).readArray();
                //////////////////////////////////////////////////////////////

                Images.loadImages(GUIView.this);

                //////////////////////////////////////////////////////////////event handler for no spaces
                noSpace = new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        if (keyEvent.getCharacter().equals(" ")) {
                            ((TextField) keyEvent.getSource()).deletePreviousChar();
                        }
                    }
                };
                ////////////////////////////////////////////////////////////////
                stBig = new ScaleTransition();
                stSmall = new ScaleTransition();
                stBig.setFromX(1.0);
                stBig.setFromY(1.0);
                stBig.setToX(1.2);
                stBig.setToY(1.2);
                stSmall.setFromX(1.0);
                stSmall.setToX(1.0);
                stSmall.setFromY(1.0);
                stSmall.setToX(1.0);
                stBig.setDuration(new Duration(20));
                stSmall.setDuration(new Duration(20));
                bigger = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stBig.setNode((Button) mouseEvent.getSource());
                        stBig.play();
                        currentStage.getScene().setCursor(Cursor.HAND);
                    }
                };
                smaller = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stSmall.setNode((Button) mouseEvent.getSource());
                        stSmall.play();
                        currentStage.getScene().setCursor(Cursor.DEFAULT);

                    }
                };
                ////////////////////////////////////////////////////////////

                ready.setValue(Boolean.TRUE);

                notifyPreloader(new StateChangeNotification(
                        StateChangeNotification.Type.BEFORE_START));

                return null;
            }
        };
        new Thread(task).start();
    }

    //todo every button should be bigger when ohvered
    @Override
    public void start(Stage stage) {

        initialize();

        currentStage = stage;
        currentScene = new Scene(new BorderPane());
        currentStage.setScene(currentScene);

        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                JsonQueue.add("method", "remoteDisconnect");
                JsonQueue.send();
            }
        });
        currentStage.setWidth(1920);
        currentStage.setHeight(1080);
        //currentStage.setResizable(false);

    }

    public void changeScene(Parent parent) {

        currentStage.getScene().setRoot(parent);

        if (!currentStage.isShowing()) {

            currentStage.show();
        }
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

        ready.addListener(new ChangeListener<Boolean>() {
            public void changed(
                    ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                if (Boolean.TRUE.equals(t1)) {

                    try {

                        InetAddress inetAddress = Client.discoverServer(discoveryPort);

                        BorderPane borderPane = new BorderPane();
                        borderPane.setPrefHeight(615);
                        borderPane.setPrefWidth(429);

                        borderPane.setBackground(new Background(
                                new BackgroundImage(Images.imagesMap.get("background"),
                                        BackgroundRepeat.REPEAT,
                                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                                        BackgroundSize.DEFAULT)));

                        borderPane.setPadding(new Insets(50, 0, 0, 0));
                        ImageView imageAdrenalina = new ImageView(
                                Images.imagesMap.get("adrenalina"));
                        imageAdrenalina.setFitHeight(125);
                        imageAdrenalina.setPreserveRatio(true);
                        borderPane.setTop(imageAdrenalina);
                        BorderPane.setAlignment(imageAdrenalina, Pos.BOTTOM_CENTER);

                        HBox hBox = new HBox();
                        hBox.setSpacing(50);

                        Button rmiButton = new Button("",
                                new ImageView(Images.imagesMap.get("rmi")));
                        rmiButton.setOnMouseEntered(bigger);
                        rmiButton.setOnMouseExited(smaller);
                        rmiButton.setBackground(new Background(
                                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY,
                                        Insets.EMPTY)));

                        rmiButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                synchronized (View.connection) {
                                    View.connection.add(new RmiConnection(inetAddress, rmiPort,
                                            GUIView.this));
                                    View.connection.notifyAll();
                                }
                            }
                        });

                        Button tcpButtom = new Button("",
                                new ImageView(Images.imagesMap.get("tcp")));
                        tcpButtom.setOnMouseEntered(bigger);
                        tcpButtom.setOnMouseExited(smaller);
                        tcpButtom.setBackground(new Background(
                                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY,
                                        Insets.EMPTY)));

                        tcpButtom.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                synchronized (View.connection) {
                                    View.connection
                                            .add(new SocketConnection(inetAddress, socketPort,
                                                    GUIView.this));
                                    View.connection.notifyAll();
                                }
                            }
                        });

                        hBox.getChildren().addAll(rmiButton, tcpButtom);
                        borderPane.setCenter(hBox);
                        BorderPane.setAlignment(hBox, Pos.CENTER_RIGHT);
                        hBox.setAlignment(Pos.CENTER);
                        ImageView distruttoreImage = new ImageView(
                                Images.imagesMap.get("distructor"));
                        distruttoreImage.setPreserveRatio(true);
                        distruttoreImage.setFitHeight(300);
                        borderPane.setBottom(distruttoreImage);
                        BorderPane.setAlignment(distruttoreImage, Pos.BOTTOM_LEFT);

                        Platform.runLater(() -> changeScene(borderPane));

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void loginScreen() {

        Label label = new Label("Inserisci un nome utente:");
        label.setFont(Font.font("Silom", 30));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        TextField userLogin = new TextField();
        userLogin.setFont(Font.font("Silom", FontWeight.BOLD, 70));
        userLogin.setAlignment(Pos.CENTER);
        userLogin.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white");
        userLogin.setOnKeyTyped(noSpace);

        userLogin.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER) && !userLogin.getText().equals("")) {
                    JsonQueue.add("method", "selectPlayerId");
                    JsonQueue.add("playerId", userLogin.getText());
                    JsonQueue.send();
                }
            }
        });

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getChildren().addAll(new Text(), label, userLogin);

        borderPane.setPadding(new Insets(50, 0, 0, 0));
        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);
        ImageView imageAdrenalina = new ImageView(Images.imagesMap.get("adrenalina"));
        imageAdrenalina.setFitHeight(125);
        imageAdrenalina.setPreserveRatio(true);
        borderPane.setTop(imageAdrenalina);
        BorderPane.setAlignment(imageAdrenalina, Pos.TOP_CENTER);
        ImageView distruttoreImage = new ImageView(Images.imagesMap.get("distructor"));
        distruttoreImage.setPreserveRatio(true);
        distruttoreImage.setFitHeight(300);
        borderPane.setBottom(distruttoreImage);
        BorderPane.setAlignment(distruttoreImage, Pos.BOTTOM_LEFT);
        Platform.runLater(() -> changeScene(borderPane));
    }

    @Override
    public void gamesListScreen() {

        BorderPane borderPane = new BorderPane();

        borderPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        borderPane.setPadding(new Insets(50, 0, 0, 0));

        ImageView imageAdrenalina = new ImageView(Images.imagesMap.get("adrenalina"));
        imageAdrenalina.setFitHeight(125);
        imageAdrenalina.setPreserveRatio(true);
        borderPane.setTop(imageAdrenalina);
        BorderPane.setAlignment(imageAdrenalina, Pos.TOP_CENTER);

        this.gameList = new ScrollPane();

        gameList.setMaxHeight(200);
        gameList.setHbarPolicy(ScrollBarPolicy.NEVER);
        gameList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        gameList.setMaxWidth(800);
        gameList.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        borderPane.setCenter(gameList);

        HBox newGame = new HBox();
        newGame.setSpacing(20);

        HBox show = new HBox();
        show.setSpacing(20);

        Button createGame = new Button("Crea Partita");
        createGame.setOnMouseEntered(bigger);
        createGame.setOnMouseExited(smaller);
        createGame.setMinWidth(200);
        createGame.setTextFill(Color.BLACK);

        Label gameName = new Label("Nome:");
        gameName.setTextFill(Color.WHITE);
        gameName.setWrapText(true);

        TextField insertGameName = new TextField();
        insertGameName.setOnKeyTyped(noSpace);
        insertGameName.setPrefSize(80, 30);

        Label numberOfDeaths = new Label("Morti:");
        numberOfDeaths.setTextFill(Color.WHITE);
        numberOfDeaths.setWrapText(true);

        TextField insertNumberOdDeaths = new TextField();
        insertNumberOdDeaths.setOnKeyTyped(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent keyEvent) {

                if (insertNumberOdDeaths.getCharacters().toString().length() > 1) {

                    insertNumberOdDeaths.deletePreviousChar();

                } else if (!insertNumberOdDeaths.getCharacters().toString().matches("([5-8])")) {

                    insertNumberOdDeaths.deletePreviousChar();
                    createNotifications("Errore:", "Metti un numero da 5 a 8");
                }
            }
        });
        insertNumberOdDeaths.setPrefSize(60, 20);
        Label frenzy = new Label("Frensia finale:");
        frenzy.setTextFill(Color.WHITE);
        frenzy.setWrapText(true);

        CheckBox checkBoxFrenzy = new CheckBox();
        Button confirmGame = new Button("Crea");
        confirmGame.setOnMouseEntered(bigger);
        confirmGame.setOnMouseExited(smaller);
        confirmGame.setMinWidth(50);

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
                .addAll(gameName, insertGameName, numberOfDeaths, insertNumberOdDeaths, frenzy,
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

    private void selectCharacterScreen(String game) {

        BorderPane borderPane = new BorderPane();

        borderPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        borderPane.setPadding(new Insets(50, 0, 0, 0));
        ImageView imageAdrenalina = new ImageView(Images.imagesMap.get("adrenalina"));
        imageAdrenalina.setFitHeight(125);
        imageAdrenalina.setPreserveRatio(true);
        borderPane.setTop(imageAdrenalina);
        BorderPane.setAlignment(imageAdrenalina, Pos.TOP_CENTER);

        Button back = new Button("Indietro");
        back.setOnMouseEntered(bigger);
        back.setOnMouseExited(smaller);
        back.setMinWidth(200);
        back.setTextFill(Color.BLACK);

        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                gamesListScreen();
            }
        });

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

        vBox.getChildren().addAll(label, characters);

        ////////////////////////////////////////////////////////////////////////////

        ImageView distructor = new ImageView(Images.playersMap.get(":D-strutt-OR3"));
        distructor.setPreserveRatio(true);
        distructor.setFitHeight(200);
        Button button1 = new Button("", distructor);
        button1.setOnMouseEntered(bigger);
        button1.setOnMouseExited(smaller);
        button1.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        button1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game);
                JsonQueue.add("character", ":D-strutt-OR3");
                JsonQueue.send();
            }
        });

        ImageView sprog = new ImageView(Images.playersMap.get("Sprog"));
        sprog.setPreserveRatio(true);
        sprog.setFitHeight(200);
        Button button2 = new Button("", sprog);
        button2.setOnMouseEntered(bigger);
        button2.setOnMouseExited(smaller);
        button2.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game);
                JsonQueue.add("character", "Sprog");
                JsonQueue.send();
            }
        });

        ImageView violetta = new ImageView(Images.playersMap.get("Violetta"));
        violetta.setPreserveRatio(true);
        violetta.setFitHeight(200);
        Button button3 = new Button("", violetta);
        button3.setOnMouseEntered(bigger);
        button3.setOnMouseExited(smaller);
        button3.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        button3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game);
                JsonQueue.add("character", "Violetta");
                JsonQueue.send();
            }
        });

        ImageView dozer = new ImageView(Images.playersMap.get("Dozer"));
        dozer.setPreserveRatio(true);
        dozer.setFitHeight(200);
        Button button4 = new Button("", dozer);
        button4.setOnMouseEntered(bigger);
        button4.setOnMouseExited(smaller);
        button4.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        button4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game);
                JsonQueue.add("character", "Dozer");
                JsonQueue.send();
            }
        });

        ImageView banshee = new ImageView(Images.playersMap.get("Banshee"));
        banshee.setPreserveRatio(true);
        banshee.setFitHeight(200);
        Button button5 = new Button("", banshee);
        button5.setOnMouseEntered(bigger);
        button5.setOnMouseExited(smaller);
        button5.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        button5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "selectGame");
                JsonQueue.add("gameId", game);
                JsonQueue.add("character", "Banshee");
                JsonQueue.send();
            }
        });

        ////////////////////////////////////////////////////////////////////////////

        characters.getChildren().addAll(button1, button2, button3, button4, button5);
        characters.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);

        Platform.runLater(() -> changeScene(borderPane));
    }

    @Override
    public void gameNotStartedScreen() {

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        borderPane.setPadding(new Insets(50, 40, 0, 0));

        ImageView imageAdrenalina = new ImageView(Images.imagesMap.get("adrenalina"));
        imageAdrenalina.setFitHeight(125);
        imageAdrenalina.setPreserveRatio(true);
        borderPane.setTop(imageAdrenalina);
        BorderPane.setAlignment(imageAdrenalina, Pos.TOP_CENTER);

        VBox center = new VBox();
        center.setSpacing(30);
        center.setAlignment(Pos.CENTER);

        HBox boards = new HBox();
        boards.setMouseTransparent(false);
        boards.setAlignment(Pos.CENTER);

        ImageView image0 = new ImageView(Images.boardsMap.get("board3").getValue());
        image0.setFitHeight(240);
        image0.setFitWidth(300);
        Button board0 = new Button("", image0);
        board0.setOnMouseExited(smaller);
        board0.setOnMouseEntered(bigger);
        board0.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        board0.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "voteBoard");
                JsonQueue.add("vote", "1");
                JsonQueue.send();
            }
        });

        ImageView image1 = new ImageView(Images.boardsMap.get("board1").getValue());
        image1.setFitHeight(240);
        image1.setFitWidth(300);
        Button board1 = new Button("", image1);
        board1.setOnMouseEntered(bigger);
        board1.setOnMouseExited(smaller);
        board1.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        board1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "voteBoard");
                JsonQueue.add("vote", "2");
                JsonQueue.send();
            }
        });

        ImageView image2 = new ImageView(Images.boardsMap.get("board2").getValue());
        image2.setFitHeight(240);
        image2.setFitWidth(300);
        Button board2 = new Button("", image2);
        board2.setOnMouseEntered(bigger);
        board2.setOnMouseExited(smaller);
        board2.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        board2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "voteBoard");
                JsonQueue.add("vote", "3");
                JsonQueue.send();
            }
        });

        ImageView image3 = new ImageView(Images.boardsMap.get("board4").getValue());
        image3.setFitHeight(240);
        image3.setFitWidth(300);
        Button board3 = new Button("", image3);
        board3.setOnMouseEntered(bigger);
        board3.setOnMouseExited(smaller);
        board3.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        board3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "voteBoard");
                JsonQueue.add("vote", "4");
                JsonQueue.send();
            }
        });

        boards.getChildren().addAll(board0, board1, board2, board3);

        HBox characters = new HBox();
        characters.setSpacing(60);
        characters.setAlignment(Pos.CENTER);

        Images.playersMap.forEach((key, value) -> {

            ImageView desaturated = new ImageView(value);
            desaturated.setPreserveRatio(true);
            desaturated.setFitHeight(180);
            desaturated.setOpacity(0.2);
            ColorAdjust desaturate = new ColorAdjust();
            desaturate.setSaturation(-1);
            desaturated.setEffect(desaturate);

            Label label = new Label();
            label.setFont(Font.font("Silom", 30));
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(20);

            vBox.getChildren().addAll(desaturated, label);
            vBox.setId(key);

            characters.getChildren().add(vBox);
        });

        Label countDown = new Label();
        countDown.setWrapText(true);
        countDown.setTextFill(Color.WHITE);
        countDown.setFont(Font.font("Silom", 20));
        countDown.setAlignment(Pos.CENTER);

        center.getChildren().addAll(boards, characters, countDown);

        borderPane.setCenter(center);
        BorderPane.setAlignment(center, Pos.CENTER);

        Platform.runLater(() -> changeScene(borderPane));
    }


    @Override
    public void boardScreen() {
        return;
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void broadcast(String value) throws RemoteException {
        this.createNotifications("broadcast", value);
    }

    @Override
    public void gameBroadcast(String value) throws RemoteException {
        this.createNotifications("Game Brodcast", value);
    }

    @Override
    public void infoMessage(String value) throws RemoteException {
        this.createNotifications("Informazione", value);
    }

    @Override
    public void errorMessage(String value) throws RemoteException {
        this.createNotifications("Errore:", value);

    }


    @Override
    public void isConnected(String value) throws RemoteException {

        //
    }

    @Override
    public void completeLogin(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        this.playerIdView = object.getString("playerId");
        this.createNotifications("Login completato!", "Benvenuto " + this.playerIdView);

        if (object.containsKey("gameId")) {

            if (object.getBoolean("gameStarted")) {

                this.boardScreen();

            } else {

                this.gameNotStartedScreen();
            }

        } else {

            this.gamesListScreen();
        }

    }

    @Override
    public void completeDisconnect(String value) throws RemoteException {

        System.exit(0);
    }

    @Override
    public void updateGameList(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        JsonArray jsonArray = object.getJsonArray("gameList");

        VBox games = new VBox();

        games.setPrefWidth(gameList.getMaxWidth());
        games.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        if (jsonArray.isEmpty()) {

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

            game.setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    ((Button) (mouseEvent.getSource())).setOpacity(100);
                    currentStage.getScene().setCursor(Cursor.HAND);
                }
            });

            game.setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    ((Button) (mouseEvent.getSource())).setOpacity(0.6);
                    currentStage.getScene().setCursor(Cursor.DEFAULT);
                }
            });

            game.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    if (!x.getBoolean("gameStarted")) {

                        selectCharacterScreen(game.getId());

                    } else {

                        createNotifications("Errore:", "La partita è già iniziata!");
                    }

                }
            });

            games.getChildren().add(game);

        });

        Platform.runLater(() -> gameList.setContent(games));
    }

    @Override
    public void completeCreateGame(String value) throws RemoteException {
        this.createNotifications("partita creata!",
                "ora seleziona la partita nella quale vuoi entrare");
    }

    @Override
    public void completeSelectGame(String value) throws RemoteException {
        this.gameNotStartedScreen();
    }

    @Override
    public void updateGameNotStartedScreen(String value) throws RemoteException {

        JsonObject readObject = JsonUtility.jsonDeserialize(value);

        Platform.runLater(() -> {

            BorderPane borderPane = (BorderPane) currentStage.getScene().getRoot();
            HBox characters = (HBox) ((VBox) borderPane.getCenter()).getChildren().get(1);

            readObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        VBox characterVBox = characters.getChildren().stream()
                                .map(y -> (VBox) y)
                                .filter(y -> y.getId().equals(x.getString("character")))
                                .findFirst()
                                .orElse(null);

                        if (characterVBox != null
                                && characterVBox.getChildren().get(0).getOpacity() == 0.2) {

                            ImageView imageView = (ImageView) characterVBox.getChildren().get(0);
                            imageView.setFitHeight(200);
                            imageView.setOpacity(1);
                            ((ColorAdjust) imageView.getEffect()).setSaturation(0);

                            RotateTransition rotator = new RotateTransition(Duration.millis(300),
                                    imageView);
                            rotator.setAxis(Rotate.Y_AXIS);
                            rotator.setFromAngle(0);
                            rotator.setToAngle(360);
                            rotator.setInterpolator(Interpolator.LINEAR);
                            rotator.setCycleCount(1);

                            rotator.play();

                            /*
                            ScaleTransition scaleTransition = new ScaleTransition();
                            scaleTransition.setDuration(Duration.millis(500));
                            scaleTransition.setNode(imageView);
                            scaleTransition.setByY(1.05);
                            scaleTransition.setByX(1.05);
                            scaleTransition.setCycleCount(1);
                            scaleTransition.setAutoReverse(false);

                            scaleTransition.play();
                            */

                            Label label = (Label) characterVBox.getChildren().get(1);
                            label.setText(x.getString("playerId"));
                        }
                    });

            ////////
            int count = readObject.getInt("countdown");

            if (count < 10) {

                ((Label) ((VBox) borderPane.getCenter()).getChildren().get(2))
                        .setText("La partita inizierà tra " + count + " secondi.");

            } else {

                ((Label) ((VBox) borderPane.getCenter()).getChildren().get(2))
                        .setText("In attesa di tre giocatori connessi.");
            }
        });

    }

    @Override
    public void completeVoteBoard(String value) throws RemoteException {
        this.createNotifications("voto mappa", " hai votato correttamente");
    }

    @Override
    public void completeSelectAction(String value) throws RemoteException {
        this.createNotifications("selezione azione completata", "");
    }

    @Override
    public void completeEndAction(String value) throws RemoteException {
        this.createNotifications("fine azione", "");
    }

    @Override
    public void completeCardInfo(String value) throws RemoteException {
        Platform.runLater(() -> {

            JsonObject jsonCard = JsonUtility.jsonDeserialize(value);
            Stage infocard = new Stage();
            AnchorPane anchorPane = new AnchorPane();
            HBox elements = new HBox();
            AnchorPane.setRightAnchor(elements, 20.0);
            AnchorPane.setTopAnchor(elements, 20.0);
            AnchorPane.setLeftAnchor(elements, 20.0);
            AnchorPane.setBottomAnchor(elements, 20.0);
            elements.setSpacing(20);
            ImageView cardImage = new ImageView(Images.weaponsMap.get(jsonCard.getInt("id")));
            cardImage.setFitWidth(200);
            cardImage.setFitHeight(300);
            elements.getChildren().add(cardImage);
            VBox text = new VBox();
            text.setSpacing(7);
            Label name = new Label();
            name.setText("Nome :" + jsonCard.getString("name"));
            name.setTextFill(Color.YELLOW);
            text.getChildren().add(name);
            Label carica = new Label();
            carica.setText("Carica: " + (jsonCard.getBoolean("isLoaded") ? "Sì" : "No"));
            carica.setTextFill(Color.YELLOW);
            text.getChildren().add(carica);

            if (jsonCard.getString("notes") != null) {
                Label notes = new Label();
                notes.setMinHeight(30);
                notes.setText("Note: " + jsonCard.getString("notes"));
                notes.setWrapText(true);
                text.getChildren().add(notes);
                notes.setTextFill(Color.YELLOW);
            }

            JsonObject primary = jsonCard.getJsonObject("primary");
            Label effettoprimario = new Label();
            effettoprimario.setText("Effetto primario :" + primary.getString("name"));
            effettoprimario.setTextFill(Color.YELLOW);
            text.getChildren().add(effettoprimario);
            Label descrizionePrimario = new Label();
            descrizionePrimario.setMinHeight(30);
            descrizionePrimario
                    .setText("Descrizione effetto primario: " + primary.getString("description"));
            descrizionePrimario.setWrapText(true);
            descrizionePrimario.setTextFill(Color.YELLOW);
            text.getChildren().add(descrizionePrimario);

            if (jsonCard.get("alternative") != JsonValue.NULL) {
                JsonObject alternativo = jsonCard.getJsonObject("alternative");
                Label effettoAlternativo = new Label();
                effettoAlternativo.setText("Effetto alternativo :" + primary.getString("name"));
                effettoAlternativo.setTextFill(Color.YELLOW);
                text.getChildren().add(effettoAlternativo);
                Label descrizioneAlternativo = new Label();
                descrizionePrimario.setMinHeight(30);
                descrizionePrimario.setText(
                        "Descrizione effetto alternativo: " + primary.getString("description"));
                descrizioneAlternativo.setWrapText(true);
                descrizioneAlternativo.setTextFill(Color.YELLOW);
                text.getChildren().add(descrizioneAlternativo);
            }

            if (jsonCard.get("optional1") != JsonValue.NULL) {

                JsonObject optional1 = jsonCard.getJsonObject("optional1");
                Label effettoOptional1 = new Label();
                effettoOptional1.setText("Effetto opzionale 1:" + optional1.getString("name"));

                effettoOptional1.setTextFill(Color.YELLOW);
                text.getChildren().add(effettoOptional1);

                Label descrizioneOptional1 = new Label();
                descrizioneOptional1.setTextFill(Color.YELLOW);
                descrizioneOptional1.setMinHeight(30);
                descrizioneOptional1.setText(
                        "Descrizione effetto opzionale 1: " + optional1.getString("description"));
                descrizioneOptional1.setWrapText(true);
                text.getChildren().add(descrizioneOptional1);
            }

            if (jsonCard.get("optional2") != JsonValue.NULL) {

                JsonObject optional2 = jsonCard.getJsonObject("optional2");
                Label effettoOptional2 = new Label();

                effettoOptional2.setText("Effetto opzionale 2:" + optional2.getString("name"));

                effettoOptional2.setTextFill(Color.YELLOW);
                text.getChildren().add(effettoOptional2);

                Label descrizioneEffettoOptional2 = new Label();
                descrizioneEffettoOptional2.setMinHeight(30);
                descrizioneEffettoOptional2.setTextFill(Color.YELLOW);
                descrizioneEffettoOptional2.setText(
                        "Descrizione effetto opzionale 2: " + optional2.getString("description"));
                descrizioneEffettoOptional2.setWrapText(true);
                text.getChildren().add(descrizioneEffettoOptional2);
            }

            Button exit = new Button("Exit");
            exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    infocard.close();
                }
            });

            exit.setTextFill(Color.BLACK);
            exit.setOnMouseEntered(bigger);
            exit.setOnMouseExited(smaller);
            text.getChildren().add(exit);
            elements.getChildren().add(text);
            elements.setBackground(new Background(
                    new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
            Scene infocardScene = new Scene(elements, 500, 500);
            infocard.setScene(infocardScene);
            PauseTransition delay = new PauseTransition(Duration.seconds(50));
            delay.setOnFinished(event -> {
                infocard.close();
            });
            infocard.show();
            delay.play();
        });
    }

    public synchronized Node createBoard(boolean isUpdateBoard, double xScaleFactor) {
        double yScaleFactor = xScaleFactor;
        ImageView boardImage = new ImageView();

        switch (boardId) {
            case 0:
                boardImage = new ImageView(Images.boardsMap.get("board3").getKey());
                break;
            case 1:
                boardImage = new ImageView(Images.boardsMap.get("board1").getKey());
                break;

            case 2:
                boardImage = new ImageView(Images.boardsMap.get("board2").getKey());
                break;

            case 3:
                boardImage = new ImageView(Images.boardsMap.get("board4").getKey());
                break;


        }
        /////////////////////////////////////////////////////centro set quadrati!
        if (isUpdateBoard) {
            this.squareList.clear();
        } else {
            this.squareListForShootState.clear();
        }
        boardImage.setFitWidth(990 / xScaleFactor);
        boardImage
                .setFitHeight(565 / yScaleFactor);//1.321 rapporto tra lunghezza e altezza originale
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(990 / xScaleFactor, 565 / yScaleFactor);
        VBox squares = new VBox();
        AnchorPane.setTopAnchor(squares, 120.0 / yScaleFactor);
        AnchorPane.setBottomAnchor(squares, 25.0 / yScaleFactor);
        AnchorPane.setLeftAnchor(squares, 160.0 / xScaleFactor);
        AnchorPane.setRightAnchor(squares, 145.0 / xScaleFactor);
        HBox line;
        line = new HBox();
        line.setSpacing(0);

        JsonObject jsonBoard = jsonArray.stream().map(JsonValue::asJsonObject)
                .filter(x -> x.getInt("id") == boardId).findFirst()
                .orElseThrow(IllegalArgumentException::new);

        jsonBoard.getJsonArray("values").stream().map(JsonValue::asJsonObject)
                .forEach(x -> {
                            ButtonSquare buttonSquare = new ButtonSquare(true);
                            if (x.getBoolean("present")) {
                                buttonSquare = new ButtonSquare(
                                        x.getString("color"), Integer.valueOf(x.getString("squareId")));
                                buttonSquare.setBackground(new Background(
                                        new BackgroundFill(Color.rgb(217, 217, 217),
                                                CornerRadii.EMPTY,
                                                Insets.EMPTY)));
                                buttonSquare.setSpawn(x.getBoolean("spawn"));
                                buttonSquare.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        ((ButtonSquare) (mouseEvent.getSource())).setOpacity(0.3);
                                        currentStage.getScene().setCursor(Cursor.HAND);
                                    }
                                });
                                buttonSquare.setOnMouseExited(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        ((ButtonSquare) (mouseEvent.getSource())).setOpacity(0.0);
                                        currentStage.getScene().setCursor(Cursor.DEFAULT);
                                    }
                                });

                            } else {
                                buttonSquare = new ButtonSquare(false);

                            }
                            buttonSquare.setPrefHeight(125 / yScaleFactor);
                            buttonSquare.setPrefWidth(175 / xScaleFactor);
                            StackPane stackPane = new StackPane();
                            stackPane.setPrefHeight(125 / yScaleFactor);
                            stackPane.setPrefWidth(175 / xScaleFactor);
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

    @Override
    public synchronized void updateBoard(String value) throws RemoteException {

        JsonObject jGameHandlerObject = JsonUtility.jsonDeserialize(value);

        boardId = jGameHandlerObject.getJsonObject("board").getInt("boardId");

        Platform.runLater(() -> {
            VBox pannelloCentrale = new VBox();
            BorderPane borderPane = new BorderPane();
            borderPane.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
            ((BorderPane) currentStage.getScene().getRoot()).getChildren().clear();
            AnchorPane anchorPane = (AnchorPane) this.createBoard(true, 1);

            /////////////////////////////////////////////////metto armi nello slot armi
            weaponsInSpawnSquare.clear();
            HBox weaponsTop = new HBox();
            weaponsTop.setMouseTransparent(false);
            VBox weaponsDx = new VBox();
            weaponsDx.setMouseTransparent(false);
            VBox weaponsSx = new VBox();
            weaponsSx.setMouseTransparent(false);

            weaponsSx.setSpacing(5);
            weaponsDx.setSpacing(5);
            weaponsTop.setSpacing(5);

            AnchorPane.setTopAnchor(weaponsTop, 0.0);
            AnchorPane.setLeftAnchor(weaponsTop, 520.0);
            AnchorPane.setRightAnchor(weaponsTop, 150.0);

            AnchorPane.setLeftAnchor(weaponsSx, 0.0);
            AnchorPane.setBottomAnchor(weaponsSx, 105.0);
            AnchorPane.setTopAnchor(weaponsSx, 205.0);

            AnchorPane.setTopAnchor(weaponsDx, 320.0);
            AnchorPane.setRightAnchor(weaponsDx, 0.0);
            AnchorPane.setBottomAnchor(weaponsDx, 0.0);
            //////////////////////////////////////////////////////////

            jGameHandlerObject.getJsonObject("board")
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
                                    definiteRotateImage.setFitWidth(120);
                                    definiteRotateImage.setFitHeight(65);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            y.getString("name"),
                                            definiteRotateImage);
                                    rotatedButton.setColorOfSpawn("rosso");
                                    weaponsSx.getChildren().add(rotatedButton);

                                    break;

                                case "BLU":

                                    cardImage.setRotate(180);
                                    SnapshotParameters paramsBlue = new SnapshotParameters();
                                    paramsBlue.setFill(Color.TRANSPARENT);
                                    Image rotatedImage180 = cardImage.snapshot(paramsBlue, null);
                                    definiteRotateImage = new ImageView(rotatedImage180);
                                    definiteRotateImage.setFitWidth(85);
                                    definiteRotateImage.setFitHeight(100);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            y.getString("name"),
                                            definiteRotateImage);
                                    rotatedButton.setColorOfSpawn("blu");

                                    weaponsTop.getChildren().add(rotatedButton);

                                    break;

                                case "GIALLO":

                                    cardImage.setRotate(270);
                                    SnapshotParameters paramsGiallo = new SnapshotParameters();
                                    paramsGiallo.setFill(Color.TRANSPARENT);
                                    Image rotatedImage270 = cardImage.snapshot(paramsGiallo, null);
                                    definiteRotateImage = new ImageView(rotatedImage270);
                                    definiteRotateImage.setFitHeight(65);
                                    definiteRotateImage.setFitWidth(120);
                                    rotatedButton = new ButtonWeapon(y.getInt("id"),
                                            y.getString("name"),
                                            definiteRotateImage);
                                    rotatedButton.setColorOfSpawn("giallo");
                                    weaponsDx.getChildren().add(rotatedButton);

                                    break;

                            }
                            weaponsInSpawnSquare.add(rotatedButton);
                            rotatedButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    JsonQueue.add("method", "askCardInfo");
                                    JsonQueue.add("cardId", Integer.toString(
                                            ((ButtonWeapon) mouseEvent.getSource()).cardId));
                                    JsonQueue.send();
                                }
                            });
                            rotatedButton.setOnMouseEntered(bigger);
                            rotatedButton.setOnMouseExited(smaller);

                        });

            });

            anchorPane.getChildren().addAll(weaponsDx, weaponsSx, weaponsTop);

            anchorPane.setMouseTransparent(false);

            ////////////////////////////////////////////////////  metto giocatori e ammotiles nei quadrati!!
            playersInGame.clear();
            jGameHandlerObject.getJsonObject("board").getJsonArray("arrays").stream()
                    .flatMap(x -> x.asJsonArray().stream())
                    .map(JsonValue::asJsonObject)
                    .filter(x -> !x.containsKey("empty"))
                    .forEach(z -> {

                        String color = z.getString("color").toLowerCase();
                        int id = z.getInt("squareId");

                        HBox playersInSquare = new HBox();
                        //was 175 125
                        //playersInSquare.setPrefSize(175, 125);

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
                                        if (t.getString("playerId").equals(playerIdView)) {
                                            this.squareList.stream().filter(k -> k.getPresent())
                                                    .filter(s -> s.getColor().equals(color) && s
                                                            .getButtonSquareId() == id)
                                                    .findAny().get().setCurrentPosition(true);
                                        }
                                    });
                        }

                        ButtonSquare tmp = this.squareList.stream().filter(k -> k.getPresent())
                                .filter(s -> s.getColor().equals(color)
                                        && s.getButtonSquareId() == id)
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
            jGameHandlerObject.getJsonObject("deaths").getJsonArray("deathBridgeArray").stream()
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
            this.actionsList.clear();
            JsonObject characterJson = jGameHandlerObject.getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(playerIdView)).findFirst().get();
            character = characterJson.getString("character");
            characterJson.getJsonObject("bridge").getJsonObject("actionBridge")
                    .getJsonArray("possibleActionsArray").stream().map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        this.actionsList.add(x.getInt("id"));

                    });

            /////////////////////////////////////////////////////////////////////////// centro powerUp e armi e tuoi cubes
            weaponsList.clear();
            powerUpList.clear();

            HBox myPlayerCubes = new HBox();
            myPlayerCubes.setPrefHeight(30);

            HBox cards = new HBox();
            cards.setPrefWidth(990);
            cards.setPrefHeight((565 / 3) + 10);

            JsonObject thisPlayerObject = jGameHandlerObject
                    .getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(playerIdView))
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

                        if (x.getBoolean("isLoaded")) {
                            card = new ImageView(Images.weaponsMap.get(x.getInt("id")));
                        } else {
                            card = new ImageView(Images.weaponsMap.get(0));
                        }
                        card.setFitWidth(130);
                        card.setFitHeight((565 / 3) + 15);
                        ButtonWeapon imageButton = new ButtonWeapon(
                                x.getInt("id"),
                                x.getString("name"),
                                card);
                        imageButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                JsonQueue.add("method", "askCardInfo");
                                JsonQueue.add("cardId", Integer.toString(
                                        ((ButtonWeapon) mouseEvent.getSource()).cardId));
                                JsonQueue.send();
                            }
                        });
                        weaponsList.add(imageButton);
                        imageButton.setOnMouseEntered(bigger);
                        imageButton.setOnMouseExited(smaller);
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
                        powerUp.setFitHeight((565 / 3) + 15);
                        ButtonPowerUp powerUpButton = new ButtonPowerUp(x.getString("name"),
                                x.getString("color"), powerUp);
                        powerUpList.add(powerUpButton);
                        powerUpButton.setOnMouseEntered(bigger);
                        powerUpButton.setOnMouseExited(smaller);
                        cards.getChildren().add(powerUpButton);
                    });
            pannelloCentrale.getChildren().addAll(anchorPane, cards);

            anchorPane.getChildren().add(myPlayerCubes);

            AnchorPane.setLeftAnchor(myPlayerCubes, 10.0);
            AnchorPane.setBottomAnchor(myPlayerCubes, 0.0);

            pannelloCentrale.setSpacing(0);
            borderPane.setCenter(pannelloCentrale);
            /////////////////////////////////////////////////destra metto plance giocatori
            AnchorPane rightAnchorPane = new AnchorPane();
            VBox bridges = new VBox();
            AnchorPane.setTopAnchor(bridges, 0.0);
            bridges.setSpacing(0);
            jGameHandlerObject.getJsonArray("playerList").stream()
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
                        bridge.setFitWidth(990 / 3 + 150);
                        bridge.setFitHeight(565 / 5);
                        bridges.getChildren().add(bridge);


                    });
            rightAnchorPane.getChildren().add(bridges);
            /////////////////////////////metto segnalini danno su giocatori e segnalini morti
            jGameHandlerObject.getJsonArray("playerList").stream()
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
                                .getJsonArray("shots").stream()
                                .forEach(z -> {
                                    ImageView shot = new ImageView(Images.dropsMap.get(
                                            z.toString().substring(1, z.toString().length() - 1)));

                                    shot.setFitHeight(35);
                                    shot.setFitWidth(25);
                                    shots.getChildren().add(shot);

                                });
                        AnchorPane.setTopAnchor(kills,
                                (double) numberOfBridge * (565 / 5) + 115 - 30);
                        AnchorPane.setLeftAnchor(kills, 103.0);
                        AnchorPane.setTopAnchor(shots, (double) numberOfBridge * (565 / 5) + 35);
                        AnchorPane.setLeftAnchor(shots, 40.0);
                        AnchorPane.setRightAnchor(shots, 120.0);
                        rightAnchorPane.getChildren().addAll(shots, kills);
                    });
            ///////////////////////////// bottoni

            AnchorPane.setTopAnchor(collectiveButtons, 565.0);
            AnchorPane.setLeftAnchor(collectiveButtons, 130.0);
            collectiveButtons.setSpacing(10);
            rightAnchorPane.getChildren().add(collectiveButtons);
            borderPane.setRight(rightAnchorPane);

            /////////////////////////////////////////////////
            changeScene(borderPane);

            /*
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            if (primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() > 1950) {
                currentStage.sizeToScene();
            } else {
                currentStage.setMaximized(true);
            }
            */

        });

    }

    private synchronized void createNotifications(String stringTitle, String value) {

        Platform.runLater(() -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.initOwner(currentStage);
            VBox dialogVbox = new VBox();
            Text title = new Text(stringTitle);
            title.setFont(Font.font("verdana", 20));
            title.setFill(Color.YELLOW);
            Text message = new Text(value);
            message.setFill(Color.WHITE);
            message.setWrappingWidth(200);
            dialogVbox.getChildren().addAll(title, message);
            dialogVbox.setSpacing(20);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialogVbox.setBackground(new Background(
                    new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY,
                            Insets.EMPTY)));
            dialog.setScene(dialogScene);
            dialogVbox.setAlignment(Pos.CENTER);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            dialog.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 300);
            dialog.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 200);
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                notifications.remove(dialog);
                dialog.close();
            });
            this.notifications.stream().forEach(x -> x.setY(x.getY() - 200));
            this.notifications.add(dialog);
            dialog.show();
            delay.play();
        });
    }

    private void elimanteSetOnMouseClicked() {
        ButtonPowerUp.setOnMouse(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ;
            }
        });
        this.powerUpList.stream().forEach(x -> x.update());

        ButtonWeapon.setOnMouse(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "askCardInfo");
                JsonQueue.add("cardId",
                        Integer.toString(((ButtonWeapon) mouseEvent.getSource()).cardId));
                JsonQueue.send();
            }
        });
        this.weaponsList.stream().forEach(x -> x.update());
        this.weaponsInSpawnSquare.forEach(x -> x.update());

        ButtonSquare.setOnMouse(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ;
            }
        });
        ButtonSquare.setOnMouseCollect(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ;
            }
        });
        this.squareList.stream().forEach(x -> x.update());

    }

    @Override
    public synchronized void updateState(String value) throws RemoteException {

        Platform.runLater(() -> {

            this.elimanteSetOnMouseClicked();
            JsonObject object = JsonUtility.jsonDeserialize(value);
            collectiveButtons.getChildren().clear();

            ////////////////////////////////////////////////////bottoni sempre disponibli infocarte , info powerup

            object.getJsonArray("info").forEach(x -> {

                if (x.toString().substring(1, x.toString().length() - 1).equals("askCardInfo")) {

                    Button infoCarte = new GameButton("info carte",
                            new ImageView(Images.imagesMap.get("button")));

                    infoCarte.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            Stage infoCarteStage = new Stage();
                            BorderPane centre = new BorderPane();
                            ScrollPane images = new ScrollPane();
                            HBox twoCrads = new HBox();
                            twoCrads.setSpacing(80);
                            VBox allcards = new VBox();
                            allcards.setSpacing(30);
                            for (int i = 1; i < 22; i++) {
                                ImageView card = new ImageView(Images.weaponsMap.get(i));
                                card.setFitWidth(150);
                                card.setFitHeight(250);
                                ButtonWeapon buttonWeapon = new ButtonWeapon(i, "name", card);
                                buttonWeapon.setOnMouseEntered(bigger);
                                buttonWeapon.setOnMouseExited(smaller);
                                buttonWeapon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        JsonQueue.add("method", "askCardInfo");
                                        JsonQueue.add("cardId", Integer.toString(
                                                ((ButtonWeapon) mouseEvent.getSource()).cardId));
                                        JsonQueue.send();
                                    }
                                });
                                twoCrads.getChildren().add(buttonWeapon);

                                if (twoCrads.getChildren().size() == 2) {

                                    allcards.getChildren().add(twoCrads);
                                    twoCrads = new HBox();
                                    twoCrads.setSpacing(80);


                                }
                            }
                            images.setContent(allcards);
                            images.setVbarPolicy(ScrollBarPolicy.ALWAYS);
                            Scene imagesScene = new Scene(new StackPane(images), 450, 500);
                            infoCarteStage.setScene(imagesScene);
                            infoCarteStage.show();

                        }

                    });
                    infoCarte.setAlignment(Pos.CENTER);
                    collectiveButtons.getChildren().add(infoCarte);
                    this.resizeButtons();

                } else if (x.toString().substring(1, x.toString().length() - 1)
                        .equals("askInfoPowerUp")) {

                    //TODO infopowerup

                }
            });

            ////////////////////////////////////////////////////////////////////state che impongono

            switch (object.getString("state")) {

                case "spawnState":

                    ButtonPowerUp.setOnMouse(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {

                            JsonQueue.add("method", "spawn");
                            JsonQueue.add("name",
                                    ((ButtonPowerUp) mouseEvent.getSource()).getName());
                            JsonQueue.add("color",
                                    ((ButtonPowerUp) mouseEvent.getSource())
                                            .getColor());
                            JsonQueue.send();
                        }


                    });

                    powerUpList.forEach(y -> y.update());
                    Button spawnButton = new Button(
                            "Spawn");
                    spawnButton.setPrefSize(200, 36);
                    spawnButton.setWrapText(true);
                    spawnButton.setTextFill(Color.BLACK);
                    collectiveButtons.getChildren().add(spawnButton);
                    spawnButton.setAlignment(Pos.CENTER);
                    this.resizeButtons();
                    break;

                case "shootState":

                    object.getJsonArray("methods").forEach(x -> {

                        if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUsePrimary")) {

                            Button effettoPrimario = new GameButton("effetto primario",
                                    new ImageView(Images.imagesMap.get("button")));

                            effettoPrimario.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    createShootStage(true, "askUsePrimary");

                                }
                            });
                            collectiveButtons.getChildren().add(effettoPrimario);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseAlternative")) {

                            Button effettoAlternativo = new GameButton("effetto alternativo",
                                    new ImageView(Images.imagesMap.get("button")));

                            effettoAlternativo.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    createShootStage(false, "askUseAlternative");
                                }
                            });
                            collectiveButtons.getChildren().add(effettoAlternativo);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseOptional1")) {

                            Button opzionale1 = new GameButton("effetto opzionale 1",
                                    new ImageView(Images.imagesMap.get("button")));

                            opzionale1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    createShootStage(false, "askUseOptional1");
                                }
                            });
                            collectiveButtons.getChildren().add(opzionale1);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseOptional2")) {

                            Button opzionale2 = new GameButton("effetto opzionale 2",
                                    new ImageView(Images.imagesMap.get("button")));

                            opzionale2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    createShootStage(false, "askUseOptional2");
                                }
                            });
                            collectiveButtons.getChildren().add(opzionale2);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("endAction")) {
                            Button endAction = this.createEndActionButton();
                            collectiveButtons.getChildren().addAll(endAction);
                            this.resizeButtons();
                        }
                    });
                    break;

                case "actionState":
                    object.getJsonArray("methods").forEach(x -> {

                        if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("moveAction")) {

                            Button moveActionButton = new Button(
                                    "clicca un quadrato per muoverti!");
                            ButtonSquare.setOnMouse(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    ButtonSquare destination = ((ButtonSquare) mouseEvent
                                            .getSource());
                                    JsonQueue.add("method", "moveAction");
                                    JsonQueue.add("squareColor", destination.getColor());
                                    JsonQueue.add("squareId",
                                            String.valueOf(destination.getButtonSquareId()));
                                    JsonQueue.send();


                                }

                            });
                            this.squareList.forEach(s -> s.update());
                            moveActionButton.setId("move");
                            moveActionButton.setPrefSize(200, 36);
                            moveActionButton.setAlignment(Pos.CENTER);
                            moveActionButton.setTextFill(Color.BLACK);
                            collectiveButtons.getChildren().add(moveActionButton);
                            this.resizeButtons();


                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askCollect")) {
                            Button collectButton = new Button("clicca il tuo quadrato e raccoogli");
                            collectButton.setPrefSize(200, 36);
                            collectButton.setAlignment(Pos.CENTER);
                            collectButton.setTextFill(Color.BLACK);
                            ButtonSquare.setOnMouseCollect(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    ButtonSquare destination = ((ButtonSquare) mouseEvent
                                            .getSource());

                                    if (destination.isSpawn) {

                                        Stage collectStage = new Stage();

                                        VBox root = new VBox();
                                        root.setSpacing(20);

                                        ToggleGroup cardsInSquareGroup = new ToggleGroup();

                                        HBox cardsInSquare = new HBox();

                                        HBox toggleBox = new HBox();
                                        toggleBox.setSpacing(100);

                                        weaponsInSpawnSquare.stream()
                                                .filter(c -> c.getColorOfSpawn()
                                                        .equals(destination.getColor()))
                                                .forEach(c -> {

                                                    ImageView card = new ImageView(
                                                            Images.weaponsMap.get(c.getCardId()));
                                                    card.setFitWidth(100);
                                                    card.setFitHeight(150);

                                                    Button cardButton = new Button("", card);

                                                    RadioButton radioButton = new RadioButton();
                                                    radioButton.setUserData(
                                                            String.valueOf(c.getCardId()));

                                                    cardsInSquare.getChildren().add(cardButton);
                                                    radioButton.setToggleGroup(cardsInSquareGroup);

                                                    toggleBox.getChildren().add(radioButton);

                                                    cardButton.setOnMouseEntered(
                                                            new EventHandler<MouseEvent>() {
                                                                @Override
                                                                public void handle(
                                                                        MouseEvent mouseEvent) {
                                                                    collectStage.getScene()
                                                                            .setCursor(Cursor.HAND);
                                                                }
                                                            });
                                                    cardButton.setOnMouseExited(
                                                            new EventHandler<MouseEvent>() {
                                                                @Override
                                                                public void handle(
                                                                        MouseEvent mouseEvent) {
                                                                    collectStage.getScene()
                                                                            .setCursor(
                                                                                    Cursor.DEFAULT);
                                                                }
                                                            });
                                                    cardButton.setOnMouseClicked(
                                                            new EventHandler<MouseEvent>() {
                                                                @Override
                                                                public void handle(
                                                                        MouseEvent mouseEvent) {
                                                                    cardsInSquareGroup.selectToggle(
                                                                            radioButton);
                                                                }
                                                            });
                                                });
                                        root.getChildren().addAll(cardsInSquare, toggleBox);

                                        HBox myPlayerCards = new HBox();
                                        HBox playerToggles = new HBox();
                                        playerToggles.setSpacing(100);
                                        ToggleGroup playerCardsGroup = new ToggleGroup();
                                        weaponsList.stream().forEach(w -> {

                                            ImageView weaponImage = new ImageView(
                                                    Images.weaponsMap.get(w.getCardId()));
                                            weaponImage.setFitHeight(150);
                                            weaponImage.setFitWidth(100);
                                            Button weaponCardButton = new Button("", weaponImage);

                                            RadioButton weaponRadioButton = new RadioButton();
                                            weaponRadioButton.setUserData(
                                                    String.valueOf(w.getCardId()));
                                            myPlayerCards.getChildren().add(weaponCardButton);
                                            weaponRadioButton.setToggleGroup(playerCardsGroup);
                                            playerToggles.getChildren().add(weaponRadioButton);

                                            weaponCardButton.setOnMouseEntered(
                                                    new EventHandler<MouseEvent>() {
                                                        @Override
                                                        public void handle(
                                                                MouseEvent mouseEvent) {
                                                            collectStage.getScene()
                                                                    .setCursor(Cursor.HAND);
                                                        }
                                                    });
                                            weaponCardButton.setOnMouseExited(
                                                    new EventHandler<MouseEvent>() {
                                                        @Override
                                                        public void handle(
                                                                MouseEvent mouseEvent) {
                                                            collectStage.getScene()
                                                                    .setCursor(
                                                                            Cursor.DEFAULT);
                                                        }
                                                    });
                                            weaponCardButton.setOnMouseClicked(
                                                    new EventHandler<MouseEvent>() {
                                                        @Override
                                                        public void handle(
                                                                MouseEvent mouseEvent) {
                                                            playerCardsGroup.selectToggle(
                                                                    weaponRadioButton);
                                                        }
                                                    });


                                        });
                                        root.getChildren().addAll(myPlayerCards, playerToggles);

                                        HBox myPowerUps = new HBox();
                                        HBox powerUpCheckBox = new HBox();
                                        powerUpCheckBox.setSpacing(100);
                                        powerUpList.stream().forEach(p -> {

                                            ImageView powerUpImage = new ImageView(
                                                    Images.powerUpsMap
                                                            .get(new StringBuilder().append(p.name)
                                                                    .append(" ").append(p.color)
                                                                    .toString()));
                                            powerUpImage.setFitWidth(100);
                                            powerUpImage.setFitHeight(150);
                                            Button powerUpButton = new Button("", powerUpImage);

                                            CheckBox checkBox = new CheckBox();
                                            checkBox.setId(new StringBuilder().append(p.name)
                                                    .append("-").append(p.color.toLowerCase())
                                                    .toString());

                                            myPowerUps.getChildren().addAll(powerUpButton);
                                            powerUpCheckBox.getChildren().addAll(checkBox);

                                            powerUpButton.setOnMouseEntered(
                                                    new EventHandler<MouseEvent>() {

                                                        @Override
                                                        public void handle(
                                                                MouseEvent mouseEvent) {

                                                            collectStage.getScene()
                                                                    .setCursor(Cursor.HAND);
                                                        }
                                                    });

                                            powerUpButton.setOnMouseExited(
                                                    new EventHandler<MouseEvent>() {

                                                        @Override
                                                        public void handle(
                                                                MouseEvent mouseEvent) {

                                                            collectStage.getScene()
                                                                    .setCursor(
                                                                            Cursor.DEFAULT);
                                                        }
                                                    });

                                            powerUpButton.setOnMouseClicked(
                                                    new EventHandler<MouseEvent>() {
                                                        @Override
                                                        public void handle(MouseEvent mouseEvent) {

                                                            checkBox.setSelected(
                                                                    !checkBox.isSelected());
                                                        }
                                                    });
                                        });

                                        root.getChildren().addAll(myPowerUps, powerUpCheckBox);

                                        Button enter = new Button("Conferma");
                                        enter.setPrefSize(80, 25);
                                        enter.setOnMouseEntered(bigger);
                                        enter.setOnMouseExited(smaller);

                                        enter.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                            @Override
                                            public void handle(MouseEvent mouseEvent) {

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

                                                    createNotifications("Attenzione!",
                                                            "Devi selezionare quale carta vuoi raccogliere.");
                                                }

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
                                }
                            });
                            this.squareList.stream().forEach(z -> z.update());
                            collectiveButtons.getChildren().add(collectButton);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askActivateWeapon")) {

                            Button activateCardButton = new Button("clicca arma per attivarla!");
                            ButtonWeapon.setOnMouse(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    GUIView.this.activatedWeapon = ((ButtonWeapon) mouseEvent
                                            .getSource()).getCardId();
                                    JsonQueue.add("method", "askActivateWeapon");
                                    JsonQueue.add("cardId", String.valueOf(
                                            ((ButtonWeapon) mouseEvent.getSource()).getCardId()));
                                    JsonQueue.send();
                                }
                            });
                            GUIView.this.weaponsList.stream().forEach(z -> z.update());

                            activateCardButton.setPrefSize(200, 36);
                            activateCardButton.setTextFill(Color.BLACK);
                            collectiveButtons.getChildren().add(activateCardButton);
                            this.resizeButtons();


                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askReload")) {
                            Button reloadWeapon = new Button("ricarica arma!");
                            reloadWeapon.setOnMouseEntered(bigger);
                            reloadWeapon.setOnMouseExited(smaller);
                            reloadWeapon.setTextFill(Color.BLACK);
                            reloadWeapon.setPrefSize(200, 36);
                            collectiveButtons.getChildren().add(reloadWeapon);
                            this.resizeButtons();
                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("endAction")) {
                            Button endAction = this.createEndActionButton();
                            collectiveButtons.getChildren().add(endAction);
                            this.resizeButtons();
                        }

                    });

                default:

                    object.getJsonArray("methods").forEach(x -> {

                        if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("selectAction")) {

                            Button selezionaAzione = new GameButton("seleziona azione",
                                    new ImageView(Images.imagesMap.get("button")));

                            collectiveButtons.getChildren().add(selezionaAzione);

                            selezionaAzione.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    Stage chooseAction = new Stage();
                                    VBox root = new VBox();
                                    root.setSpacing(10);
                                    HBox actions = new HBox();
                                    actions.setSpacing(0);
                                    GUIView.this.actionsList.stream().forEach(k -> {
                                        ImageView action = new ImageView(Images.possibleActionsMap
                                                .get(GUIView.this.character + Integer.toString(k)));
                                        int valueOfAction;
                                        switch (k) {
                                            case 0:
                                                valueOfAction = 4;
                                                break;

                                            case 4:
                                                valueOfAction = 2;
                                                break;

                                            case 5:
                                                valueOfAction = 3;
                                                break;

                                            case 6:
                                                valueOfAction = 1;
                                                break;
                                            case 7:
                                                valueOfAction = 2;
                                                break;

                                            case 8:
                                                valueOfAction = 3;
                                                break;

                                            case 9:
                                                valueOfAction = 1;
                                                break;

                                            case 10:
                                                valueOfAction = 2;
                                                break;

                                            default:
                                                valueOfAction = k;
                                        }
                                        String actionValueString = Integer.toString(valueOfAction);
                                        Button actionButton = new Button("", action);
                                        actionButton.setOnMouseEntered(bigger);
                                        actionButton.setOnMouseExited(smaller);
                                        actionButton.setBackground(new Background(
                                                new BackgroundFill(Color.TRANSPARENT,
                                                        CornerRadii.EMPTY, Insets.EMPTY)));
                                        actionButton.setOnMouseClicked(
                                                new EventHandler<MouseEvent>() {
                                                    @Override
                                                    public void handle(MouseEvent mouseEvent) {
                                                        JsonQueue.add("method", "selectAction");
                                                        JsonQueue.add("actionNumber",
                                                                actionValueString);
                                                        JsonQueue.send();
                                                        chooseAction.close();

                                                    }
                                                });
                                        action.setFitHeight(80);
                                        action.setFitWidth(50);
                                        actions.getChildren().add(actionButton);
                                    });
                                    Button quit = new Button("quit");
                                    quit.setOnMouseEntered(bigger);
                                    quit.setOnMouseExited(smaller);
                                    quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent mouseEvent) {
                                            chooseAction.close();
                                        }
                                    });
                                    root.getChildren().addAll(actions, quit);
                                    chooseAction.setScene(new Scene(root));
                                    chooseAction.show();

                                }
                            });

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUsePowerUp")) {

                            ButtonPowerUp.setOnMouse(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {

                                    //
                                }
                            });

                            powerUpList.forEach(y -> y.update());
                            //se schiacci powerUp usi powerup (usa effetto nuova finestra)
                            //setOnMouse

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("endOfTurn")) {

                            Button fineTurno = new GameButton("fine turno",
                                    new ImageView(Images.imagesMap.get("button")));

                            fineTurno.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    JsonQueue.add("method", "endOfTurn");
                                    JsonQueue.add("endOfTurn", "");
                                    JsonQueue.send();
                                }
                            });

                            collectiveButtons.getChildren().add(fineTurno);
                            this.resizeButtons();

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("moveAction")) {

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askCollect")) {

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askActivateWeapon")) {

                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askReload")) {

                        }

                    });
            }
        });

        /////////////////////////////////////////////////////////

    }

    public void resizeButtons() {
        int numberOfButtons = this.collectiveButtons.getChildren().size();

        if (numberOfButtons > 4) {
            this.collectiveButtons.getChildren().stream()
                    .forEach(x -> x.resize(36, (212 + numberOfButtons * 14) / numberOfButtons));
        }
    }

    public void createShootStage(Boolean isPrimaryEffect, String effectType) {

        VBox root = new VBox();
        root.setSpacing(5);
        StringBuilder target = new StringBuilder().append("");
        StringBuilder destination = new StringBuilder().append("");
        root.setBackground(new Background(
                new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        AnchorPane board = (AnchorPane) this.createBoard(false, 2);// scala della meta' in lunghezza
        HBox playersconnected = new HBox();
        HBox checkBoxes = new HBox();
        VBox playersAndCheckBox = new VBox();
        int numberOfPlayersConnected = (int) this.playersInGame.stream().count() - 1;
        int scaleFactor = numberOfPlayersConnected >= 3 ? numberOfPlayersConnected : 3;

        this.playersInGame.stream().filter(x -> !x.equals(character)).forEach(x -> {
            ImageView playerConnected = new ImageView(Images.playersMap.get(x));
            playerConnected.setFitWidth(495 / scaleFactor);
            playerConnected.setFitHeight(220);
            CheckBox playerCheckBox = new CheckBox();
            playerCheckBox.setPrefSize(30, 30);
            playerCheckBox.setId(x);
            checkBoxes.getChildren().add(playerCheckBox);
            Button playerConnectedButton = new Button(null, playerConnected);
            checkBoxes.setSpacing(playerConnectedButton.getWidth());
            playerConnectedButton.setBackground(new Background(
                    new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            playerConnectedButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    playerCheckBox.setSelected(playerCheckBox.isSelected() ? false : true);
                }
            });

            playersconnected.getChildren().add(playerConnectedButton);

        });

        StackPane playersAndMap = new StackPane();

        HBox skipAndConfirmButtons = new HBox();
        Button confirm = new Button("conferma");
        confirm.setStyle("-fx-text-inner-color: white; -fx-font: 20px Silom");
        confirm.setOnMouseEntered(bigger);
        confirm.setOnMouseExited(smaller);
        Button skipTarget = new Button("salta target");
        skipTarget.prefHeightProperty().bind(confirm.prefHeightProperty());
        skipTarget.prefWidthProperty().bind(confirm.prefWidthProperty());
        skipTarget.setOnMouseEntered(bigger);
        skipTarget.setOnMouseExited(smaller);
        skipTarget.setStyle("-fx-text-inner-color: white; -fx-font: 20px Silom");
        skipTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                GUIView.this.createDestination(root, board, effectType, target, destination);
            }
        });

        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (checkBoxes.getChildren().stream()
                        .anyMatch(c -> ((CheckBox) c).isSelected())) {

                    target.append("target(");
                    checkBoxes.getChildren().stream()
                            .filter(c -> ((CheckBox) c).isSelected())
                            .forEach(c -> {

                                target.append(c.getId().toLowerCase()).append(" ");
                            });
                    target.append(")");
                }

                GUIView.this.createDestination(root, board, effectType, target, destination);

            }
        });

        playersAndCheckBox.getChildren()
                .addAll(playersconnected, checkBoxes, confirm);

        playersAndMap.setVisible(false);
        playersAndMap.getChildren().addAll(board, playersAndCheckBox);
        Stage shootStage = new Stage();
        HBox cardAndRadioButtons = new HBox();
        ToggleGroup radioButtonToggle = new ToggleGroup();
        ImageView cardImage = new ImageView(Images.weaponsMap.get(activatedWeapon));
        cardImage.setFitWidth(100);
        cardImage.setFitHeight(150);
        cardAndRadioButtons.getChildren().add(cardImage);

        VBox radioButtonAndLabel = new VBox(30);
        HBox lineOfRadioAndPlayer = new HBox();
        RadioButton playerTarget = new RadioButton();
        playerTarget.setToggleGroup(radioButtonToggle);
        playerTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playersAndMap.setVisible(true);
                board.setVisible(false);
                playersAndCheckBox.setVisible(true);
                playersAndMap.getChildren().clear();
                playersAndMap.getChildren().addAll(board, playersAndCheckBox);
            }
        });
        Label playerLabel = new Label("Target: personaggio");
        lineOfRadioAndPlayer.getChildren().addAll(playerLabel, playerTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndPlayer);
        playerLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        HBox lineOfRadioAndButton = new HBox();
        RadioButton squareTarget = new RadioButton();
        GUIView.this.squareListForShootState.stream().filter(s -> s.getPresent())
                .forEach(s -> s.setOnMouseClicked(
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {

                                target.append("target(")
                                        .append(((ButtonSquare) mouseEvent.getSource())
                                                .getColor().toLowerCase());

                                if (squareTarget.isSelected()) {

                                    target.append("-")
                                            .append(((ButtonSquare) mouseEvent.getSource())
                                                    .getButtonSquareId());
                                }

                                target.append(")");
                                GUIView.this.createDestination(root, board, effectType, target,
                                        destination);
                            }
                        }));
        squareTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playersAndMap.setVisible(true);
                board.setVisible(true);
                playersAndCheckBox.setVisible(false);
                playersAndMap.getChildren().clear();
                playersAndMap.getChildren().addAll(playersAndCheckBox, board);

            }
        });
        squareTarget.setToggleGroup(radioButtonToggle);
        Label buttonLabel = new Label("Target: quadrato");
        lineOfRadioAndButton.getChildren().addAll(buttonLabel, squareTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndButton);
        buttonLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        HBox lineOfRadioAndRoom = new HBox();
        RadioButton roomTarget = new RadioButton();
        roomTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playersAndMap.setVisible(true);
                board.setVisible(true);
                playersAndCheckBox.setVisible(false);
                playersAndMap.getChildren().clear();
                playersAndMap.getChildren().addAll(playersAndCheckBox, board);
            }
        });
        roomTarget.setToggleGroup(radioButtonToggle);
        Label roomLabel = new Label("Target: stanza");
        lineOfRadioAndRoom.getChildren().addAll(roomLabel, roomTarget);
        radioButtonAndLabel.getChildren().add(lineOfRadioAndRoom);
        cardAndRadioButtons.getChildren().add(radioButtonAndLabel);
        roomLabel.setFont(Font.font("Silom", FontWeight.BOLD, 20));

        root.getChildren().addAll(cardAndRadioButtons, playersAndMap, skipTarget);
        shootStage.setScene(new Scene(root));
        shootStage.show();


    }

    @Override
    public void completePowerUpInfo(String value) throws RemoteException {

    }

    public Button createEndActionButton() {
        Button endAction = new GameButton("fine azione",
                new ImageView(Images.imagesMap.get("button")));

        endAction.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonQueue.add("method", "endAction");
                JsonQueue.add("cardId", "");
                JsonQueue.send();
            }
        });

        return endAction;
    }

    public void createDestination(VBox root, AnchorPane board, String effectType,
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

        skipButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                GUIView.this.thirdUseEffectScreen(root, effectType, target, destination);
            }
        });

        board.setVisible(true);
        root.getChildren().addAll(selectDestinationLabel, board, skipButton);
        GUIView.this.squareListForShootState.stream()
                .filter(s -> s.getPresent())
                .forEach(s -> s.setOnMouseClicked(
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                destination.append("destinazione(")
                                        .append(((ButtonSquare) mouseEvent.getSource())
                                                .getColor().toLowerCase())
                                        .append("-")
                                        .append(((ButtonSquare) mouseEvent.getSource())
                                                .getButtonSquareId())
                                        .append(")");

                                switch (effectType) {

                                    case "askUsePrimary":

                                        JsonQueue.add("method", effectType);
                                        JsonQueue.add("line",
                                                new StringBuilder().append(target.toString())
                                                        .append(destination.toString())
                                                        .toString());

                                        JsonQueue.send();
                                        break;

                                    default:

                                        GUIView.this.thirdUseEffectScreen(root, effectType, target,
                                                destination);

                                }


                            }
                        }));
    }

    public void thirdUseEffectScreen(VBox root, String effectType, StringBuilder target,
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

            Button confirmButton = new Button();
            confirmButton.setText("Paga");
            confirmButton.setStyle("-fx-text-inner-color: white; -fx-font: 15px Silom");

            Button noButton = new Button();
            noButton.setText("No");
            noButton.setStyle("-fx-text-inner-color: white; -fx-font: 15px Silom");

            HBox buttonsHBox = new HBox();

            buttonsHBox.setSpacing(400);

            buttonsHBox.getChildren().addAll(confirmButton, noButton);

            noButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    JsonQueue.add("method", effectType);
                    JsonQueue.add("line",
                            new StringBuilder().append(target.toString())
                                    .append(destination.toString())
                                    .toString());
                    JsonQueue.send();
                }
            });

            HBox powerUpButtonsHBox = new HBox();
            HBox checkBoxHBox = new HBox();

            checkBoxHBox.setLayoutX(20);

            checkBoxHBox.setSpacing(170);

            powerUpList.forEach(p -> {

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

                Button pButton = new Button("", powerUp);

                pButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {

                        powerUpCheckBox.setSelected(!powerUpCheckBox.isSelected());
                    }
                });

                checkBoxHBox.getChildren().add(powerUpCheckBox);
                powerUpButtonsHBox.getChildren().add(pButton);
            });

            root.getChildren()
                    .addAll(selectPaymentLabel, powerUpButtonsHBox, checkBoxHBox, buttonsHBox);

            confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    if (!checkBoxHBox.getChildren().stream()
                            .anyMatch(c -> ((CheckBox) c).isSelected())) {

                        JsonQueue.add("method", effectType);
                        JsonQueue.add("line",
                                new StringBuilder().append(target.toString())
                                        .append(destination.toString())
                                        .toString());
                        JsonQueue.send();
                    } else {

                        paymentLine.append("powerup(");

                        checkBoxHBox.getChildren().stream()
                                .forEach(c ->
                                        paymentLine.append(c.getId())
                                                .append(" ")
                                );

                        paymentLine.append(")");

                        JsonQueue.add("method", effectType);
                        JsonQueue.add("line",
                                new StringBuilder().append(target.toString())
                                        .append(destination.toString())
                                        .append(paymentLine.toString())
                                        .toString());
                        JsonQueue.send();

                    }
                }
            });
        } else {

            //TODO cubesPayment Screen
        }
    }

}
