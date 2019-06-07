package it.polimi.ingsw.client.view.gui;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUIPreloader extends Preloader {
    ProgressBar bar;
    Stage stage;
    boolean noLoadingProgress = true;

    private Scene createPreloaderScene() {
        bar = new ProgressBar(0);
        BorderPane p = new BorderPane();
        p.setCenter(bar);
        return new Scene(p, 300, 150);
    }

    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloaderScene());
        stage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        //application loading progress is rescaled to be first 50%
        //Even if there is nothing to load 0% and 100% events can be
        // delivered
        if (pn.getProgress() != 1.0 || !noLoadingProgress) {
            bar.setProgress(pn.getProgress()/2);
            if (pn.getProgress() > 0) {
                noLoadingProgress = false;
            }
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        //ignore, hide after application signals it is ready
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof ProgressNotification) {
            //expect application to send us progress notifications
            //with progress ranging from 0 to 1.0
            double v = ((ProgressNotification) pn).getProgress();
            if (!noLoadingProgress) {
                //if we were receiving loading progress notifications
                //then progress is already at 50%.
                //Rescale application progress to start from 50%
                v = 0.5 + v/2;
            }
            bar.setProgress(v);
        } else if (pn instanceof StateChangeNotification) {
            //hide after get any state update from application
            stage.hide();
        }
    }
}