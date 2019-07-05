package it.polimi.ingsw.client.view.gui;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * preloader class for loading all the needed images in the ram
 */
public class GUIPreloader extends Preloader {

    /**
     * A progress bar to indicate the progress of all the images been loaded in the ram
     */
    private ProgressBar bar;
    /**
     * A temporary  stage for showing the preloader
     */
    private Stage stage;
    /**
     * Boolean that expresses if the loading has started
     */
    private boolean noLoadingProgress = true;

    /**
     * Initializing the stage for the preloader
     * @param stage the current stage
     */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        this.stage.initStyle(StageStyle.TRANSPARENT);

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox vBox = new VBox();
        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        ImageView image = new ImageView("images/adrenaline_icon.png");
        image.setPreserveRatio(true);
        image.setFitHeight(300);

        bar = new ProgressBar(0);
        bar.setStyle("-fx-accent: orange;");

        vBox.getChildren().addAll(image, bar);

        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.CENTER);

        Scene scene = new Scene(borderPane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.show();
    }

    /**
     * This method increments the progress bar indicator
     * @param pn node of the progress bar
     */
    @Override
    public void handleProgressNotification(ProgressNotification pn) {

        if (pn.getProgress() != 1.0 || !noLoadingProgress) {

            bar.setProgress(pn.getProgress() / 2);

            if (pn.getProgress() > 0) {

                noLoadingProgress = false;
            }
        }
    }

    /**
     * Needed to be implemented to respect the inheritance
     * @param evt parameter  needed for the method
     */
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {

        //
    }

    /**
     * method for handling the application notifications
     * @param pn Preloader notifications
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {

        if (pn instanceof ProgressNotification) {

            double v = ((ProgressNotification) pn).getProgress();

            if (!noLoadingProgress) {

                v = 0.5 + v / 2;
            }

            bar.setProgress(v);

        } else if (pn instanceof StateChangeNotification) {

            stage.hide();
        }
    }
}