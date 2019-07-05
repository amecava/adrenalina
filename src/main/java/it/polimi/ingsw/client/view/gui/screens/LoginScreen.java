package it.polimi.ingsw.client.view.gui.screens;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.handlers.JsonQueue;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Login screen for inserting the player's id.
 */
public class LoginScreen {

    /**
     * creates a login screen
     */
    private LoginScreen() {

        //
    }

    /**
     * generates the login screen in wiche the user must put his user name id
     */
    public static void generateScreen() {

        BorderPane borderPane = GUIView.createBorderPane(true, true);

        Label label = new Label("Inserisci un nome utente:");
        label.setFont(Font.font("Silom", 30));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);

        TextField userLogin = new TextField();
        userLogin.setFont(Font.font("Silom", FontWeight.BOLD, 70));
        userLogin.setAlignment(Pos.CENTER);
        userLogin.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white");
        userLogin.setOnKeyTyped(keyEvent -> {

            if (keyEvent.getCharacter().equals(" ")) {
                ((TextField) keyEvent.getSource()).deletePreviousChar();
            }
        });

        userLogin.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER) && !userLogin.getText().equals("")) {

                JsonQueue.add("method", "selectPlayerId");
                JsonQueue.add("playerId", userLogin.getText());

                JsonQueue.send();
            }
        });

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getChildren().addAll(new Text(), label, userLogin);

        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);

        Platform.runLater(() -> GUIView.changeScene(borderPane));
    }
}
