package it.polimi.ingsw.client.view.gui.animation;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * A sample that demonstrates how to add or remove a change listener on a node
 * (for example, a Rectangle node) for some property (for example,
 * Rectangle.hover). Once you add a listener, the text field  shows the hover
 * property change.
 *
 * @see javafx.beans.value.ChangeListener
 * @see javafx.beans.InvalidationListener
 * @see javafx.beans.value.ObservableValue
 */
public class ChangeListenerSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Group root = new Group();
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, 400,80);
        primaryStage.setScene(scene);

        //rect.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                Circle circle = new Circle(event.getSceneX(), event.getSceneY(),30);
                circle.setFill(Color.YELLOW);
                root.getChildren().add(circle);
            }
        });
        //root.getChildren().add(circle);
        primaryStage.show();

    }
}