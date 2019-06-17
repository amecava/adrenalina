package it.polimi.ingsw.client.view.gui.handlers;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class CardHandler {

    private CardHandler() {

        //
    }

    public static void weaponCardInfo(JsonObject jsonCard) {

        Platform.runLater(() -> {

            Stage infocard = new Stage();
            infocard.initModality(Modality.APPLICATION_MODAL);
            infocard.initOwner(GUIView.getCurrentStage());
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
                effettoAlternativo.setText("Effetto alternativo :" + alternativo.getString("name"));
                effettoAlternativo.setTextFill(Color.YELLOW);
                text.getChildren().add(effettoAlternativo);
                Label descrizioneAlternativo = new Label();
                descrizioneAlternativo.setMinHeight(30);
                descrizioneAlternativo.setText(
                        "Descrizione effetto alternativo: " + alternativo.getString("description"));
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

            if (jsonCard.getString("notes") != null) {
                Label notes = new Label();
                notes.setMinHeight(30);
                notes.setText("Note: " + jsonCard.getString("notes"));
                notes.setWrapText(true);
                text.getChildren().add(notes);
                notes.setTextFill(Color.YELLOW);
            }

            Button exit = new GameButton("Exit");
            exit.setOnMouseClicked(x -> infocard.close());
            text.getChildren().add(exit);
            elements.getChildren().add(text);
            elements.setBackground(new Background(
                    new BackgroundFill(Color.rgb(25, 31, 53), CornerRadii.EMPTY, Insets.EMPTY)));
            Scene infocardScene = new Scene(elements, 500, 500);
            infocard.setScene(infocardScene);
            PauseTransition delay = new PauseTransition(Duration.seconds(50));
            delay.setOnFinished(event -> infocard.close());
            infocard.show();
            delay.play();
        });
    }

    public static void powerUpCardInfo(JsonObject object) {

        //
    }
}
