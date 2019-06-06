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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
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
import javafx.scene.text.Text;
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
import javax.swing.border.Border;

public class GUIView extends Application implements View, VirtualView {

    private static Map<Integer, Image> weaponsMap = new HashMap();
    private static Map<String, Image> powerUpsMap = new HashMap<>();
    private static Map<String, Image> bridgesMap = new HashMap<>();
    private static Map<String, Image> playersMap = new HashMap<>();
    private static Map<String, Image> ammoTilesMap = new HashMap<>();
    private static Map<String, Image> dropsMap = new HashMap<>();
    private static Map<String, Image> possibleActionsMap = new HashMap<>();

    private static ImageView imageView0;
    private static ImageView imageView1;
    private static ImageView imageView2;
    private static ImageView imageView3;
    private static Image background;
    private static ImageView adrenalina;

    private Scene currentScene;
    private Scene nextScene;

    public static Stage currentStage;

    private ScrollPane gameList;

    private VBox selectedGame;
    private VBox collectiveButtons = new VBox();

    private List<Stage> notifications = new ArrayList<>();

    private static EventHandler<KeyEvent> noSpace;
    private static EventHandler<MouseEvent> bigger;
    private static EventHandler<MouseEvent> smaller;

    private static ScaleTransition stBig;
    private static ScaleTransition stSmall;

    private static JsonArray jsonArray;

    private List<ButtonSquare> squareList = new ArrayList<>();
    private List<ButtonPowerUp> powerUpList = new ArrayList<>();
    private List<ButtonWeapon> weaponsList = new ArrayList<>();
    private List<ButtonWeapon> weaponsInSpawnSquare = new ArrayList<>();
    private List<Integer> actionsList = new ArrayList<>();

    private static ProgressBar progressBar;
    private static boolean ramLoaded = false;
    private String character;

    private String playerIdView;

    //todo every button should be bigger when ohvered
    @Override
    public void start(Stage stage) throws Exception {
        StackPane imageAndStatusBar = new StackPane();
        Image gameImage = new Image("adrenalina.jpg");
        ImageView imageView = new ImageView(gameImage);
        imageView.setPreserveRatio(false);
        imageView.fitWidthProperty().bind(imageAndStatusBar.widthProperty());
        imageView.fitHeightProperty().bind(imageAndStatusBar.heightProperty());
        progressBar = new ProgressBar();
        imageAndStatusBar.getChildren().addAll(imageView, progressBar);
        this.currentScene = new Scene(imageAndStatusBar);
        currentStage = stage;

        /*
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        currentStage.setMinWidth(width);
        currentStage.setMinHeight(height);

         */
        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        currentStage.setScene(currentScene);
        currentStage.setMaximized(true);
        currentStage.setResizable(true);
        currentStage.show();
    }

    public static void initialize() {
        GUIView.launch();
    }


    public void changeScene(Parent parent) {

        while (currentStage == null || !currentStage.isShowing()) {
            ;
        }
        if (!ramLoaded) {
            double progress = 0;
            progressBar.setProgress(progress);
            InputStream in = GUIView.class.getClassLoader()
                    .getResourceAsStream("Boards/Board.json");

            jsonArray = Json.createReader(in).readArray();
            //////////////////////////////////////////////////////////////

            background = new Image("players/background.png");
            adrenalina = new ImageView("adrenalinaScritta.png");

            ammoTilesMap.put("BLUBLU", new Image("ammoTiles/BLUBLU.png"));
            ammoTilesMap.put("BLUGIALLOGIALLO", new Image("ammoTiles/BLUGIALLOGIALLO.png"));
            ammoTilesMap.put("BLUROSSOROSSO", new Image("ammoTiles/BLUROSSOROSSO.png"));
            ammoTilesMap.put("GIALLOBLU", new Image("ammoTiles/GIALLOBLU.png"));
            ammoTilesMap.put("GIALLOBLUBLU", new Image("ammoTiles/GIALLOBLUBLU.png"));
            ammoTilesMap.put("GIALLOGIALLO", new Image("ammoTiles/GIALLOGIALLO.png"));
            ammoTilesMap.put("GIALLOROSSO", new Image("ammoTiles/GIALLOROSSO.png"));
            ammoTilesMap.put("GIALLOROSSOROSSO", new Image("ammoTiles/GIALLOROSSOROSSO.png"));
            ammoTilesMap.put("ROSSOBLU", new Image("ammoTiles/ROSSOBLU.png"));
            ammoTilesMap.put("ROSSOBLUBLU", new Image("ammoTiles/ROSSOBLUBLU.png"));
            ammoTilesMap.put("ROSSOGIALLOGIALLO", new Image("ammoTiles/ROSSOGIALLOGIALLO.png"));
            ammoTilesMap.put("ROSSOROSSO", new Image("ammoTiles/ROSSOROSSO.png"));

            weaponsMap.put(0, new Image("cardsImages/AD_weapons_IT_0225.png"));
            weaponsMap.put(1, new Image("cardsImages/1.png"));
            weaponsMap.put(2, new Image("cardsImages/2.png"));
            weaponsMap.put(3, new Image("cardsImages/3.png"));
            weaponsMap.put(4, new Image("cardsImages/4.png"));
            weaponsMap.put(5, new Image("cardsImages/5.png"));
            weaponsMap.put(6, new Image("cardsImages/6.png"));
            weaponsMap.put(7, new Image("cardsImages/7.png"));
            weaponsMap.put(8, new Image("cardsImages/8.png"));
            weaponsMap.put(9, new Image("cardsImages/9.png"));
            weaponsMap.put(10, new Image("cardsImages/10.png"));
            weaponsMap.put(11, new Image("cardsImages/11.png"));
            weaponsMap.put(12, new Image("cardsImages/12.png"));
            weaponsMap.put(13, new Image("cardsImages/13.png"));
            weaponsMap.put(14, new Image("cardsImages/14.png"));
            weaponsMap.put(15, new Image("cardsImages/15.png"));
            weaponsMap.put(16, new Image("cardsImages/16.png"));
            weaponsMap.put(17, new Image("cardsImages/17.png"));
            weaponsMap.put(18, new Image("cardsImages/18.png"));
            weaponsMap.put(19, new Image("cardsImages/19.png"));
            weaponsMap.put(20, new Image("cardsImages/20.png"));
            weaponsMap.put(21, new Image("cardsImages/21.png"));
            progressBar.setProgress(progress + 0.2);

            powerUpsMap.put("GRANATAVENOM BLU", new Image("cardsImages/GRANATAVENOMBLUE.png"));
            powerUpsMap.put("GRANATAVENOM ROSSO", new Image("cardsImages/GRANATAVENOMRED.png"));
            powerUpsMap.put("GRANATAVENOM GIALLO", new Image("cardsImages/GRANATAVENOMYELLOW.png"));
            powerUpsMap.put("TELETRASPORTO BLU", new Image("cardsImages/TELETRASPORTOBLUE.png"));
            powerUpsMap.put("TELETRASPORTO ROSSO", new Image("cardsImages/TELETRASPORTORED.png"));
            powerUpsMap
                    .put("TELETRASPORTO GIALLO", new Image("cardsImages/TELETRASPORTOYELLOW.png"));
            powerUpsMap.put("MIRINO BLU", new Image("cardsImages/MIRINOBLUE.png"));
            powerUpsMap.put("MIRINO ROSSO", new Image("cardsImages/MIRINORED.png"));
            powerUpsMap.put("MIRINO GIALLO", new Image("cardsImages/MIRINOYELLOW.png"));
            powerUpsMap.put("RAGGIOCINETICO BLU", new Image("cardsImages/RAGGIOCINETICOBLUE.png"));
            powerUpsMap.put("RAGGIOCINETICO ROSSO", new Image("cardsImages/RAGGIOCINETICORED.png"));
            powerUpsMap.put("RAGGIOCINETICO GIALLO",
                    new Image("cardsImages/RAGGIOCINETICOYELLOW.png"));
            progressBar.setProgress(progress + 0.2);

            bridgesMap.put("Bansheefalse", new Image("playerboards/Bansheefalse.png"));
            bridgesMap.put("Bansheetrue", new Image("playerboards/Bansheetrue.png"));

            bridgesMap.put("Dozerfalse", new Image("playerboards/Dozerfalse.png"));
            bridgesMap.put("Dozertrue", new Image("playerboards/Dozertrue.png"));

            bridgesMap.put("Violettafalse", new Image("playerboards/Violettafalse.png"));
            bridgesMap.put("Violettatrue", new Image("playerboards/Violettatrue.png"));

            bridgesMap.put("Sprogfalse", new Image("playerboards/Sprogfalse.png"));
            bridgesMap.put("Sprogtrue", new Image("playerboards/Sprogtrue.png"));

            bridgesMap.put(":D-strutt-OR3false",
                    new Image("playerboards/D-strutt-OR3_false_frenesia.png"));
            bridgesMap
                    .put(":D-strutt-OR3true",
                            new Image("playerboards/D-strutt-OR3_true_frenesia.png"));
            progressBar.setProgress(progress + 0.2);

            playersMap.put(":D-strutt-OR3", new Image("players/distruttore.png"));
            playersMap.put("Sprog", new Image("players/sprog.png"));
            playersMap.put("Violetta", new Image("players/violetta.png"));
            playersMap.put("Dozer", new Image("players/dozer.png"));
            playersMap.put("Banshee", new Image("players/banshee.png"));
            playersMap.put("adrenalinaText", new Image("players/adrenaline_text.png"));
            playersMap.put("adrenalinaIcon", new Image("players/adrenaline_icon.png"));
            progressBar.setProgress(progress + 0.2);

            dropsMap.put("GIALLO", new Image("Drop/drop-yellow.png"));
            dropsMap.put("VERDE", new Image("Drop/drop-green.png"));
            dropsMap.put("AZZURRO", new Image("Drop/drop-blue.png"));
            dropsMap.put("VIOLA", new Image("Drop/drop-violet.png"));
            dropsMap.put("GRIGIO", new Image("Drop/drop-gray.png"));
            dropsMap.put("morte", new Image("Drop/teschio.jpg"));

            possibleActionsMap.put("Banshee0", new Image("azioniDisponibili/Banshee0.png"));
            possibleActionsMap.put("Banshee1", new Image("azioniDisponibili/Banshee1.png"));
            possibleActionsMap.put("Banshee2", new Image("azioniDisponibili/Banshee2.png"));
            possibleActionsMap.put("Banshee3", new Image("azioniDisponibili/Banshee3.png"));
            possibleActionsMap.put("Banshee4", new Image("azioniDisponibili/Banshee4.png"));
            possibleActionsMap.put("Banshee5", new Image("azioniDisponibili/Banshee5.png"));
            possibleActionsMap.put("Banshee6", new Image("azioniDisponibili/Banshee6.png"));
            possibleActionsMap.put("Banshee7", new Image("azioniDisponibili/Banshee7.png"));
            possibleActionsMap.put("Banshee8", new Image("azioniDisponibili/Banshee8.png"));
            possibleActionsMap.put("Banshee9", new Image("azioniDisponibili/Banshee9.png"));
            possibleActionsMap.put("Banshee10", new Image("azioniDisponibili/Banshee10.png"));
            possibleActionsMap
                    .put(":D-strutt-OR30", new Image("azioniDisponibili/D-strutt-OR30.png"));
            possibleActionsMap
                    .put(":D-strutt-OR31", new Image("azioniDisponibili/D-strutt-OR31.png"));
            possibleActionsMap
                    .put(":D-strutt-OR32", new Image("azioniDisponibili/D-strutt-OR32.png"));
            possibleActionsMap
                    .put(":D-strutt-OR33", new Image("azioniDisponibili/D-strutt-OR33.png"));
            possibleActionsMap
                    .put(":D-strutt-OR34", new Image("azioniDisponibili/D-strutt-OR34.png"));
            possibleActionsMap
                    .put(":D-strutt-OR35", new Image("azioniDisponibili/D-strutt-OR35.png"));
            //possibleActionsMap
            // .put(":D-strutt-OR36", new Image("azioniDisponibili/D-strutt-OR36.png"));
            //possibleActionsMap
            // .put(":D-strutt-OR37", new Image("azioniDisponibili/D-strutt-OR38.png"));
            //possibleActionsMap
            //.put(":D-strutt-OR38", new Image("azioniDisponibili/D-strutt-OR38.png"));
            //possibleActionsMap
            // .put(":D-strutt-OR39", new Image("azioniDisponibili/D-strutt-OR39.png"));
            //possibleActionsMap
            //  .put(":D-strutt-OR310", new Image("azioniDisponibili/D-strutt-OR310.png"));
            possibleActionsMap.put("Dozer0", new Image("azioniDisponibili/Dozer0.png"));
            possibleActionsMap.put("Dozer1", new Image("azioniDisponibili/Dozer1.png"));
            possibleActionsMap.put("Dozer2", new Image("azioniDisponibili/Dozer2.png"));
            possibleActionsMap.put("Dozer3", new Image("azioniDisponibili/Dozer3.png"));
            possibleActionsMap.put("Dozer4", new Image("azioniDisponibili/Dozer4.png"));
            possibleActionsMap.put("Dozer5", new Image("azioniDisponibili/Dozer5.png"));
            //possibleActionsMap.put("Dozer6", new Image("azioniDisponibili/Dozer6.png"));
            //possibleActionsMap.put("Dozer7", new Image("azioniDisponibili/Dozer7.png"));
            //possibleActionsMap.put("Dozer8", new Image("azioniDisponibili/Dozer8.png"));
            //possibleActionsMap.put("Dozer9", new Image("azioniDisponibili/Dozer9.png"));
            //possibleActionsMap.put("Dozer10", new Image("azioniDisponibili/Dozer10.png"));
            possibleActionsMap.put("Sprog0", new Image("azioniDisponibili/Sprog0.png"));
            possibleActionsMap.put("Sprog1", new Image("azioniDisponibili/Sprog1.png"));
            possibleActionsMap.put("Sprog2", new Image("azioniDisponibili/Sprog2.png"));
            possibleActionsMap.put("Sprog3", new Image("azioniDisponibili/Sprog3.png"));
            possibleActionsMap.put("Sprog4", new Image("azioniDisponibili/Sprog4.png"));
            possibleActionsMap.put("Sprog5", new Image("azioniDisponibili/Sprog5.png"));
            //possibleActionsMap.put("Sprog6", new Image("azioniDisponibili/Sprog6.png"));
            //possibleActionsMap.put("Sprog7", new Image("azioniDisponibili/Sprog7.png"));
            //possibleActionsMap.put("Sprog8", new Image("azioniDisponibili/Sprog8.png"));
            //possibleActionsMap.put("Sprog9", new Image("azioniDisponibili/Sprog9.png"));
            //possibleActionsMap.put("Spro10", new Image("azioniDisponibili/Sprog10.png"));
            possibleActionsMap.put("Violetta0", new Image("azioniDisponibili/Violetta0.png"));
            possibleActionsMap.put("Violetta1", new Image("azioniDisponibili/Violetta1.png"));
            possibleActionsMap.put("Violetta2", new Image("azioniDisponibili/Violetta2.png"));
            possibleActionsMap.put("Violetta3", new Image("azioniDisponibili/Violetta3.png"));
            possibleActionsMap.put("Violetta4", new Image("azioniDisponibili/Violetta4.png"));
            possibleActionsMap.put("Violetta5", new Image("azioniDisponibili/Violetta5.png"));
            //possibleActionsMap.put("Violetta6", new Image("azioniDisponibili/Violetta6.png"));
            //possibleActionsMap.put("Violetta7", new Image("azioniDisponibili/Violetta7.png"));
            //possibleActionsMap.put("Violetta8", new Image("azioniDisponibili/Violetta8.png"));
            //possibleActionsMap.put("Violetta9", new Image("azioniDisponibili/Violetta9.png"));
            //possibleActionsMap.put("Violetta10", new Image("azioniDisponibili/Violetta10.png"));

            //////////////////////////////////////////////////////////////event handler for no spaces
            noSpace = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    if (keyEvent.getCharacter().equals(" ")) {
                        ((TextField) keyEvent.getSource()).deletePreviousChar();
                    }
                }
            };
            progressBar.setProgress(1.0);

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
            Image board0Image = new Image("Boards/0 - UPUP.png");
            imageView0 = new ImageView(board0Image);
            imageView0.setFitHeight(240);
            imageView0.setFitWidth(300);
            Image board1Image = new Image("Boards/1 - DOWNDOWN.png");
            imageView1 = new ImageView(board1Image);
            imageView1.setFitHeight(240);
            imageView1.setFitWidth(300);
            Image board2Image = new Image("Boards/2 - UPDOWN.png");
            imageView2 = new ImageView(board2Image);
            imageView2.setFitWidth(300);
            imageView2.setFitHeight(240);
            Image board3Image = new Image("Boards/3 - DOWNUP.png");
            imageView3 = new ImageView(board3Image);
            imageView3.setFitWidth(300);
            imageView3.setFitHeight(240);
            ramLoaded = true;
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

            Text text = new Text(" ");
            text.setFont(Font.font("verdana"));
            borderPane.setTop(text);
            BorderPane.setAlignment(text, Pos.TOP_CENTER);

            HBox hBox = new HBox();
            hBox.setSpacing(20);

            MenuItem socketConnection = new MenuItem("Socket");
            socketConnection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (View.connection) {
                        View.connection
                                .add(new SocketConnection(inetAddress, socketPort, GUIView.this));
                        View.connection.notifyAll();
                    }
                }
            });

            MenuItem rmiConnection = new MenuItem("RMI");
            rmiConnection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (View.connection) {
                        View.connection.add(new RmiConnection(inetAddress, rmiPort, GUIView.this));
                        View.connection.notifyAll();
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

        ImageView text = adrenalina;

        Label connectionLabel = new Label("Effettua il login:");
        connectionLabel.setFont(Font.font("verdana", 15));
        connectionLabel.setTextFill(Color.WHITE);

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundImage(background, BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        GridPane gridPane = new GridPane();
        TextField userLogin = new TextField();
        userLogin.setOnKeyTyped(noSpace);

        Button enter = new Button("Enter");
        enter.setOnMouseEntered(bigger);
        enter.setOnMouseExited(smaller);

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
        borderPane.setTop(text);
        borderPane.setCenter(gridPane);
        BorderPane.setAlignment(text, Pos.TOP_CENTER);
        Platform.runLater(() -> changeScene(borderPane));
    }

    @Override
    public void gamesListScreen() {

        ImageView upperText = new ImageView("adrenalinaScritta.png");

        BorderPane borderPane = new BorderPane();

        borderPane.setBackground(new Background(
                new BackgroundImage(background, BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
        borderPane.setTop(upperText);

        BorderPane.setAlignment(upperText, Pos.TOP_CENTER);

        this.gameList = new ScrollPane();

        gameList.setMaxHeight(200);
        gameList.setHbarPolicy(ScrollBarPolicy.NEVER);
        gameList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        gameList.setMaxWidth(800);

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
        selectedGame = new VBox();
        selectedGame.setSpacing(20);

        Label selectedGameName = new Label("Id:");
        selectedGameName.setTextFill(Color.WHITE);

        TextField game = new TextField();

        Label selectPlayer = new Label("Seleziona personaggio:");
        selectPlayer.setTextFill(Color.WHITE);

        TextField insertSelectedPlayer = new TextField();
        insertSelectedPlayer.setOnKeyTyped(noSpace);

        Button enterGame = new Button("Gioca");
        enterGame.setOnMouseEntered(bigger);
        enterGame.setOnMouseExited(smaller);
        enterGame.setTextFill(Color.GRAY);

        enterGame.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {

                if (insertSelectedPlayer.getCharacters().toString().length() == 0) {

                    createNotifications("Errore:", "Inserisci un personaggio valido.");

                } else {

                    JsonQueue.add("method", "selectGame");
                    JsonQueue.add("gameId", game.getText());
                    JsonQueue.add("character", insertSelectedPlayer.getText());
                    JsonQueue.send();
                }
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

    @Override
    public void gameNotStartedScreen() {

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundImage(background, BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        AnchorPane upTxt = new AnchorPane(adrenalina);
        AnchorPane.setBottomAnchor(upTxt, 0.0);

        borderPane.setPrefHeight(150);
        borderPane.setTop(upTxt);
        BorderPane.setAlignment(upTxt, Pos.TOP_CENTER);

        HBox images1 = new HBox();
        images1.setMouseTransparent(false);

        Button board0 = new Button("", imageView2);
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

        Button board1 = new Button("", imageView0);
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

        Button board2 = new Button("", imageView1);
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

        Button board3 = new Button("", imageView3);
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

        images1.getChildren().addAll(board0, board1);
        HBox images2 = new HBox();
        images2.setMouseTransparent(false);
        images2.getChildren().addAll(board2, board3);
        VBox images = new VBox();
        images.getChildren().addAll(images1, images2);
        borderPane.setCenter(images);
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
        return;
    }

    @Override
    public void completeLogin(String value) throws RemoteException {

        this.playerIdView = JsonUtility.jsonDeserialize(value).getString("playerId");
        this.createNotifications("Login completato!", "Benvenuto " + this.playerIdView);
        this.gamesListScreen();
    }

    @Override
    public void completeDisconnect(String value) throws RemoteException {
        this.createNotifications("disconnesso", " sei stato disconnesso dal server");
    }

    @Override
    public void updateGameList(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        JsonArray jsonArray = object.getJsonArray("gameList");

        VBox games = new VBox();

        games.setBackground(new Background(
                new BackgroundImage(background, BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));

        games.setPrefWidth(gameList.getMaxWidth());

        if (jsonArray.isEmpty()) {

            Button game = new Button();
            game.setBackground(new Background(
                    new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            game.setPrefWidth(games.getMaxWidth());
            game.setPrefHeight(200);
            game.setText("Non sono ancora state create partite.\n"
                    + "Per crearne una clicca il pulsante qui sotto.");
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
            game.setPrefWidth(games.getMaxWidth());
            game.setPrefHeight(200);
            game.setOpacity(0.6);

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

                    if (selectedGame.isVisible()) {
                        selectedGame.setVisible(false);
                    } else {
                        selectedGame.setVisible(true);
                        ((TextField) (selectedGame.getChildren().get(1)))
                                .setText(game.getId());
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

        VBox playersConnected = new VBox();
        playersConnected.setMaxWidth(150);

        JsonReader reader = Json.createReader(new StringReader(value));
        JsonObject readObject = reader.readObject();
        Label countDown;

        int count = readObject.getInt("countdown");

        if (count < 10) {

            countDown = new Label(
                    "Tra " + count + " secondi avrà inizio la partita!");

        } else {

            countDown = new Label(
                    "Non appena si saranno collegati 3 giocatori inizierà il countdown...");
        }

        countDown.setMinHeight(200);
        countDown.setWrapText(true);
        countDown.setTextFill(Color.WHITE);
        countDown.setFont(Font.font("verdana", 15));

        playersConnected.getChildren().addAll(countDown);

        readObject.getJsonArray("playerList").stream()
                .map(JsonValue::asJsonObject)
                .forEach(x -> {

                    VBox player = new VBox();
                    player.setMinHeight(60);
                    player.setSpacing(3);

                    Label playerId = new Label("Giocatore : " + x.getString("playerId"));
                    playerId.setWrapText(true);
                    playerId.setFont(Font.font("verdana", 10));
                    playerId.setTextFill(Color.WHITE);

                    Label characterLabel = new Label("Personaggio: " + x.getString("character"));
                    characterLabel.setFont(Font.font("verdana", 10));
                    characterLabel.setWrapText(true);
                    characterLabel.setTextFill(Color.YELLOW);

                    player.getChildren().addAll(playerId, characterLabel);
                    playersConnected.getChildren().add(player);
                });

        Platform.runLater(() -> {

            BorderPane borderPane = (BorderPane) currentStage.getScene().getRoot();

            borderPane
                    .setPrefWidth(((BorderPane) currentStage.getScene().getRoot()).getPrefWidth());
            AnchorPane right = new AnchorPane();
            right.getChildren().add(playersConnected);
            AnchorPane.setLeftAnchor(right, 0.0);
            borderPane.setRight(right);
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
            ImageView cardImage = new ImageView(weaponsMap.get(jsonCard.getInt("id")));
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

    @Override
    public synchronized void updateBoard(String value) throws RemoteException {

        JsonObject jGameHandlerObject = JsonUtility.jsonDeserialize(value);

        int board = jGameHandlerObject.getJsonObject("board").getInt("boardId");

        Platform.runLater(() -> {
            ImageView boardImage = new ImageView();
            ((BorderPane) currentStage.getScene().getRoot()).getChildren().clear();
            switch (board) {
                case 0:
                    boardImage = imageView2;
                    break;
                case 1:
                    boardImage = imageView0;
                    break;

                case 2:
                    boardImage = imageView1;
                    break;

                case 3:
                    boardImage = imageView3;
                    break;


            }
            /////////////////////////////////////////////////////centro set quadrati!
            this.squareList.clear();
            VBox pannelloCentrale = new VBox();
            BorderPane borderPane = new BorderPane();
            borderPane.setBackground(new Background(
                    new BackgroundImage(background, BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
            GridPane imagePane = new GridPane();
            boardImage.setFitWidth(990);
            boardImage.setFitHeight(565);
            imagePane.setPrefHeight(990);
            imagePane.setPrefWidth(565);
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefSize(990, 565);
            VBox squares = new VBox();
            AnchorPane.setTopAnchor(squares, 120.0);
            AnchorPane.setBottomAnchor(squares, 25.0);
            AnchorPane.setLeftAnchor(squares, 160.0);
            AnchorPane.setRightAnchor(squares, 145.0);
            HBox line;
            line = new HBox();
            line.setSpacing(0);

            JsonObject jsonBoard = jsonArray.stream().map(JsonValue::asJsonObject)
                    .filter(x -> x.getInt("id") == board).findFirst()
                    .orElseThrow(IllegalArgumentException::new);

            jsonBoard.getJsonArray("values").stream().map(JsonValue::asJsonObject)
                    .forEach(x -> {
                                ButtonSquare buttonSquare = new ButtonSquare(true);
                                if (x.getBoolean("present")) {
                                    buttonSquare = new ButtonSquare(
                                            x.getString("color"), x.getString("squareId"));
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
                                buttonSquare.setPrefHeight(125);
                                buttonSquare.setPrefWidth(175);
                                StackPane stackPane = new StackPane();
                                stackPane.setPrefHeight(125);
                                stackPane.setPrefWidth(175);
                                stackPane.getChildren().add(buttonSquare);
                                squareList.add(buttonSquare);
                                line.getChildren().add(stackPane);
                                if (line.getChildren().size() == 4) {
                                    HBox row = new HBox();
                                    row.getChildren().addAll(line.getChildren());
                                    squares.getChildren().add(row);
                                    line.getChildren().clear();
                                }
                            }
                    );
            anchorPane.getChildren().add(squares);
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

            jGameHandlerObject.getJsonObject("board")
                    .getJsonArray("arrays").stream()
                    .flatMap(x -> x.asJsonArray().stream())
                    .map(JsonValue::asJsonObject).filter(x -> !x.containsKey("empty"))
                    .filter(x -> x.getBoolean("isSpawn")).forEach(x -> {

                String color = x.getString("color");

                x.getJsonArray("tools").stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(y -> {

                            ImageView cardImage = new ImageView(weaponsMap.get(y.getInt("id")));
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

            imagePane.getChildren().addAll(boardImage, anchorPane);
            anchorPane.setMouseTransparent(false);

            ////////////////////////////////////////////////////  metto giocatori e ammotiles nei quadrati!!
            jGameHandlerObject.getJsonObject("board").getJsonArray("arrays").stream()
                    .flatMap(x -> x.asJsonArray().stream())
                    .map(JsonValue::asJsonObject)
                    .filter(x -> !x.containsKey("empty"))
                    .forEach(z -> {

                        String color = z.getString("color").toLowerCase();
                        String id = String.valueOf(z.getInt("squareId"));

                        HBox playersInSquare = new HBox();
                        //was 175 125
                        //playersInSquare.setPrefSize(175, 125);

                        HBox tilesInSquare = new HBox();

                        if (!z.getBoolean("isSpawn")) {

                            z.getJsonArray("tools").stream()
                                    .map(JsonValue::asJsonObject)
                                    .forEach(s -> {

                                        ImageView tile = new ImageView(
                                                ammoTilesMap.get(s.getJsonArray("colors").stream()
                                                        .map(JsonValue::toString)
                                                        .map(p -> p.substring(1, p.length() - 1))
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

                                        ImageView player = new ImageView(
                                                playersMap.get(t.getString("character")));

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
                                                            .getButtonSquareId()
                                                            .equals(id))
                                                    .findAny().get().setCurrentPosition(true);
                                        }
                                    });
                        }

                        ButtonSquare tmp = this.squareList.stream().filter(k -> k.getPresent())
                                .filter(s -> s.getColor().equals(color) && s.getButtonSquareId()
                                        .equals(id))
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
                                dropsMap.get(x.toString().substring(1, x.toString().length() - 1)));
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

            /////////////////////////////////////////////////////////////////////////// centro powerUp e armi
            weaponsList.clear();
            powerUpList.clear();
            HBox cards = new HBox();
            cards.setPrefWidth(990);
            cards.setPrefHeight((565 / 3) + 10);
            JsonObject thisPlayerObject = jGameHandlerObject
                    .getJsonArray("playerList").stream()
                    .map(JsonValue::asJsonObject)
                    .filter(x -> x.getString("playerId").equals(playerIdView))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

            thisPlayerObject.getJsonArray("weapons").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        ImageView card;

                        if (x.getBoolean("isLoaded")) {
                            card = new ImageView(weaponsMap.get(x.getInt("id")));
                        } else {
                            card = new ImageView(weaponsMap.get(0));
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
                                powerUpsMap.get(new StringBuilder()
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
            pannelloCentrale.getChildren().addAll(imagePane, cards);
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
                                bridgesMap.get(new StringBuilder()
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
                        HBox kills = new HBox();
                        int numberOfBridge = bridges.getChildren().indexOf(characterBridge);
                        x.getJsonObject("bridge").getJsonArray("deathBridgeArray").stream()
                                .map(JsonValue::asJsonObject).filter(z -> z.getBoolean("used"))
                                .forEach(z -> {

                                    ImageView kill = new ImageView(dropsMap.get("morte"));
                                    kill.setFitHeight(25);
                                    kill.setFitWidth(25);
                                    kills.getChildren().add(kill);
                                });
                        x.getJsonObject("bridge").getJsonObject("damageBridge")
                                .getJsonArray("shots").stream()
                                .forEach(z -> {
                                    ImageView shot = new ImageView(dropsMap.get(
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
            collectiveButtons.setSpacing(14);
            rightAnchorPane.getChildren().add(collectiveButtons);
            borderPane.setRight(rightAnchorPane);

            /////////////////////////////////////////////////
            changeScene(borderPane);

            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            if (primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() > 1950) {
                currentStage.sizeToScene();
            } else {
                currentStage.setMaximized(true);
            }

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

                    Button infoCarte = new Button("info carte");
                    infoCarte.setPrefWidth(200);
                    infoCarte.setPrefHeight(36);
                    infoCarte.setTextFill(Color.BLACK);
                    infoCarte.setOnMouseExited(smaller);
                    infoCarte.setOnMouseEntered(bigger);
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
                                ImageView card = new ImageView(weaponsMap.get(i));
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
                            "per favore clicca su un powerUp per fare lo spawn");
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
                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseAlternative")) {
                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseOptional1")) {
                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("askUseOptional2")) {
                        } else if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("endAction")) {
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
                                    JsonQueue.add("squareId", destination.getButtonSquareId());
                                    JsonQueue.send();


                                }

                            });
                            this.squareList.forEach(s->s.update());
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
                                        //TODO clicca sullo square e segli arma con wizard

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

                            Button activateCardButton = new Button("clicca un'arma per attivarla!");
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
                            Button endAction = new Button("fine azione!");
                            endAction.setPrefSize(200, 36);
                            endAction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    JsonQueue.add("method", "endAction");
                                    JsonQueue.add("cardId", "");
                                    JsonQueue.send();
                                }
                            });
                            endAction.setOnMouseEntered(bigger);
                            endAction.setOnMouseExited(smaller);
                            collectiveButtons.getChildren().add(endAction);
                            this.resizeButtons();
                        }

                    });

                default:

                    object.getJsonArray("methods").forEach(x -> {

                        if (x.toString().substring(1, x.toString().length() - 1)
                                .equals("selectAction")) {

                            Button selezionaAzione = new Button("seleziona azione");
                            selezionaAzione.setPrefWidth(200);
                            selezionaAzione.setPrefHeight(36);
                            selezionaAzione.setOnMouseEntered(bigger);
                            selezionaAzione.setOnMouseExited(smaller);
                            selezionaAzione.setTextFill(Color.BLACK);
                            collectiveButtons.getChildren().add(selezionaAzione);
                            selezionaAzione.setAlignment(Pos.CENTER);
                            selezionaAzione.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    Stage chooseAction = new Stage();
                                    VBox root = new VBox();
                                    root.setSpacing(10);
                                    HBox actions = new HBox();
                                    actions.setSpacing(0);
                                    GUIView.this.actionsList.stream().forEach(k -> {
                                        ImageView action = new ImageView(possibleActionsMap
                                                .get(GUIView.this.character + Integer.toString(k)));
                                        int valueOfAction;
                                        switch (k) {
                                            case 0:
                                                valueOfAction = 4;

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

                            Button fineTurno = new Button("fine turno");
                            fineTurno.setPrefWidth(200);
                            fineTurno.setPrefHeight(36);
                            fineTurno.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    JsonQueue.add("method", "endOfTurn");
                                    JsonQueue.add("endOfTurn", "");
                                    JsonQueue.send();
                                }
                            });
                            fineTurno.setOnMouseExited(smaller);
                            fineTurno.setOnMouseEntered(bigger);
                            fineTurno.setTextFill(Color.BLACK);
                            collectiveButtons.getChildren().add(fineTurno);
                            fineTurno.setAlignment(Pos.CENTER);
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
                    .forEach(x -> x.prefHeight((212 + numberOfButtons * 14) / numberOfButtons));
        }
    }

    @Override
    public void completePowerUpInfo(String value) throws RemoteException {
        return;
    }

}
