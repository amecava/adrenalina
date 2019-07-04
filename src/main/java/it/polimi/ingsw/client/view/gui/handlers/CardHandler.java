package it.polimi.ingsw.client.view.gui.handlers;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import it.polimi.ingsw.client.view.gui.buttons.GameButton;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.util.Duration;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class CardHandler {

    private CardHandler() {

        //
    }

    public static void weaponCardInfo(JsonObject jsonCard) {

        Platform.runLater(() -> {

            Stage infoCard = new Stage();
            infoCard.initModality(Modality.APPLICATION_MODAL);
            infoCard.initOwner(GUIView.getCurrentStage());
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            HBox elements = new HBox();
            elements.setAlignment(Pos.CENTER);
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
            name.setText(jsonCard.getString("name"));
            name.setFont(Font.font("Silom", FontWeight.NORMAL, 30));
            name.setTextFill(Color.WHITE);
            name.setWrapText(true);
            vBox.getChildren().addAll(name, new Label(), new Label());

            vBox.getChildren().add(elements);

            JsonObject primary = jsonCard.getJsonObject("primary");
            Label primaryEffect = new Label();
            primaryEffect.setText("Primario: " + primary.getString("name"));
            primaryEffect.setFont(Font.font("Silom", FontWeight.NORMAL, 25));
            primaryEffect.setTextFill(Color.WHITE);
            primaryEffect.setWrapText(true);
            text.getChildren().add(primaryEffect);

            Label primaryEffectDescription = new Label();
            primaryEffectDescription.setText(primary.getString("description"));
            primaryEffectDescription.setWrapText(true);
            primaryEffectDescription.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
            primaryEffectDescription.setTextFill(Color.WHITE);
            text.getChildren().add(primaryEffectDescription);

            if (jsonCard.get("alternative") != JsonValue.NULL) {

                JsonObject alternative = jsonCard.getJsonObject("alternative");
                Label alternativeEffect = new Label();
                alternativeEffect.setText("Alternativo: " + alternative.getString("name"));
                alternativeEffect.setFont(Font.font("Silom", FontWeight.NORMAL, 25));
                alternativeEffect.setTextFill(Color.WHITE);
                alternativeEffect.setWrapText(true);
                text.getChildren().add(alternativeEffect);
                Label alternativeEffectDescription = new Label();
                alternativeEffectDescription.setText(alternative.getString("description"));
                alternativeEffectDescription.setWrapText(true);
                alternativeEffectDescription.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
                alternativeEffectDescription.setTextFill(Color.WHITE);
                text.getChildren().add(alternativeEffectDescription);
            }

            if (jsonCard.get("optional1") != JsonValue.NULL) {

                JsonObject optional1 = jsonCard.getJsonObject("optional1");

                Label optional1Effect = new Label();
                optional1Effect.setText("Opzionale 1: " + optional1.getString("name"));
                optional1Effect.setFont(Font.font("Silom", FontWeight.NORMAL, 25));
                optional1Effect.setTextFill(Color.WHITE);
                optional1Effect.setWrapText(true);
                text.getChildren().add(optional1Effect);

                Label optional1EffectDescription = new Label();
                optional1EffectDescription.setText(optional1.getString("description"));
                optional1EffectDescription.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
                optional1EffectDescription.setTextFill(Color.WHITE);
                optional1EffectDescription.setWrapText(true);
                text.getChildren().add(optional1EffectDescription);
            }

            if (jsonCard.get("optional2") != JsonValue.NULL) {

                JsonObject optional2 = jsonCard.getJsonObject("optional2");

                Label optional2Effect = new Label();
                optional2Effect.setText("Opzionale 2: " + optional2.getString("name"));
                optional2Effect.setFont(Font.font("Silom", FontWeight.NORMAL, 25));
                optional2Effect.setTextFill(Color.WHITE);
                optional2Effect.setWrapText(true);
                text.getChildren().add(optional2Effect);

                Label optional2EffectDescription = new Label();
                optional2EffectDescription.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
                optional2EffectDescription.setTextFill(Color.WHITE);
                optional2EffectDescription.setText(optional2.getString("description"));
                optional2EffectDescription.setWrapText(true);
                text.getChildren().add(optional2EffectDescription);
            }

            if (jsonCard.getString("notes") != null) {
                Label notes = new Label();
                notes.setMinHeight(30);
                notes.setText(jsonCard.getString("notes"));
                notes.setWrapText(true);
                notes.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
                notes.setTextFill(Color.WHITE);
                vBox.getChildren().addAll(new Label(), new Label(), notes);
            }

            Button exit = new GameButton("chiudi");
            exit.setOnMouseClicked(x -> infoCard.close());
            elements.getChildren().add(text);
            vBox.getChildren().add(exit);
            vBox.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
            Scene infoCardScene = new Scene(vBox, 700, 500);
            infoCard.setScene(infoCardScene);
            PauseTransition delay = new PauseTransition(Duration.seconds(50));
            delay.setOnFinished(event -> infoCard.close());
            infoCard.show();
            delay.play();
        });
    }

    public static void specificWeaponCardInfo(JsonObject playerObject) {

        Platform.runLater(() -> {

            Stage infoCard = new Stage();
            infoCard.initModality(Modality.APPLICATION_MODAL);
            infoCard.initOwner(GUIView.getCurrentStage());

            VBox cardsAndClose = new VBox();
            cardsAndClose.setSpacing(20);
            cardsAndClose.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));

            HBox elements = new HBox();
            elements.setSpacing(20);
            elements.setBackground(new Background(
                    new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

            playerObject.getJsonArray("weapons").stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        if (x.getBoolean("isLoaded")) {

                            ImageView card = new ImageView(Images.weaponsMap.get(x.getInt("id")));
                            card.setFitWidth(200);
                            card.setFitHeight(300);

                            elements.getChildren()
                                    .add(card);

                        } else {

                            ImageView back = new ImageView(Images.weaponsMap.get(0));
                            back.setFitWidth(200);
                            back.setFitHeight(300);

                            elements.getChildren().add(back);
                        }
                    });

            Button exit = new GameButton("chiudi");
            exit.setOnMouseClicked(x -> infoCard.close());
            exit.setAlignment(Pos.CENTER);

            cardsAndClose.getChildren().addAll(elements, exit);
            cardsAndClose.setAlignment(Pos.CENTER);

            Scene infoCardScene = new Scene(cardsAndClose);
            infoCard.setScene(infoCardScene);

            PauseTransition delay = new PauseTransition(Duration.seconds(50));
            delay.setOnFinished(event -> infoCard.close());
            infoCard.show();
            delay.play();
        });

    }

    public static void powerUpCardInfo(JsonObject object) {

        Platform.runLater(() -> {

            Stage infoCard = new Stage();
            infoCard.initModality(Modality.APPLICATION_MODAL);
            infoCard.initOwner(GUIView.getCurrentStage());
            HBox elements = new HBox();
            AnchorPane.setRightAnchor(elements, 20.0);
            AnchorPane.setTopAnchor(elements, 20.0);
            AnchorPane.setLeftAnchor(elements, 20.0);
            AnchorPane.setBottomAnchor(elements, 20.0);
            elements.setSpacing(20);

            ImageView cardImage = new ImageView(
                    Images.powerUpsMap.get(new StringBuilder()
                            .append(object.getString("name"))
                            .append(" ")
                            .append(object.getString("color"))
                            .toString()));
            cardImage.setFitWidth(200);
            cardImage.setFitHeight(300);
            elements.getChildren().add(cardImage);
            VBox text = new VBox();
            text.setSpacing(7);

            Label description = new Label();
            description.setText(object.getString("info"));
            description.setFont(Font.font("Silom", FontWeight.NORMAL, 16));
            description.setTextFill(Color.WHITE);
            description.setWrapText(true);
            text.getChildren().add(description);

            Button exit = new GameButton("chiudi");
            exit.setOnMouseClicked(x -> infoCard.close());
            text.getChildren().add(exit);
            elements.getChildren().add(text);
            elements.setBackground(new Background(
                    new BackgroundImage(Images.imagesMap.get("background"), BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));

            Scene infoCardScene = new Scene(elements, 500, 500);
            infoCard.setScene(infoCardScene);
            PauseTransition delay = new PauseTransition(Duration.seconds(50));
            delay.setOnFinished(event -> infoCard.close());
            infoCard.show();
            delay.play();
        });
    }
}
