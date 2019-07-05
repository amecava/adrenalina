package it.polimi.ingsw.client.view.gui.handlers;

import it.polimi.ingsw.client.view.gui.GUIView;
import it.polimi.ingsw.client.view.gui.animations.Images;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * main class for showing and creating notifications
 */
public class Notifications {

    /**
     * List of all the notifications shown on screen
     */
    private static List<Stage> dialogList = new ArrayList<>();

    /**
     * Private constructor
     */
    private Notifications() {

        //
    }

    /**
     * method for creating the notifications and shifting all the other notifications
     * shown on the stage
     * @param title The title of the notifications
     * @param value The text that will be shown
     */
    public static synchronized void createNotification(String title, String value) {

        Platform.runLater(() -> {
            ImageView imageType = new ImageView(Images.notifications.get(title));
            imageType.setFitWidth(453.0);
            imageType.setFitHeight(107);
            imageType.setOpacity(0.9);
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.initOwner(GUIView.getCurrentStage());
            AnchorPane root = new AnchorPane();
            Text message = new Text(value);
            message.autosize();
            message.maxHeight(100);
            message.setFill(Color.WHITE);
            message.setWrappingWidth(440);
            AnchorPane.setTopAnchor(imageType, 13.0);
            AnchorPane.setLeftAnchor(imageType, 0.0);
            AnchorPane.setLeftAnchor(message, 75.0);
            AnchorPane.setTopAnchor(message, 48.0);
            root.getChildren().addAll(imageType, message);

            Scene dialogScene = new Scene(root, 453, 120);
            dialogScene.setFill(Color.TRANSPARENT);
            root.setBackground(Background.EMPTY);
            root.setFocusTraversable(false);
            dialog.setScene(dialogScene);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            dialog.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth());
            dialog.setY(primaryScreenBounds.getMinY());

            double currentX = dialog.getX();
            DoubleProperty xProperty = new SimpleDoubleProperty(currentX);
            xProperty.addListener((obs, oldX, newX) -> dialog.setX(newX.doubleValue()));
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200),
                    new KeyValue(xProperty, currentX - 453.0));
            Timeline animation = new Timeline(keyFrame);
            animation.play();

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                dialogList.remove(dialog);
                dialog.close();
            });
            dialogList.forEach(x -> {

                double currentY = x.getY();

                DoubleProperty yProperty = new SimpleDoubleProperty(currentY);
                yProperty.addListener((obs, oldY, newY) -> x.setY(newY.doubleValue()));

                KeyFrame kf = new KeyFrame(Duration.millis(200),
                        new KeyValue(yProperty, currentY + 120));

                Timeline an = new Timeline(kf);
                an.play();
            });
            dialogList.add(dialog);
            dialog.show();
            delay.play();
        });
    }
}
