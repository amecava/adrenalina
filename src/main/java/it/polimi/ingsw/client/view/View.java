package it.polimi.ingsw.client.view;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.json.JsonObject;

public interface View {

    Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    JsonObject userInput();

    void initialScreen(int discoveryPort, int rmiPort, int socketPort);

    void loginScreen();
    void gamesListScreen();

    void gameNotStartedScreen();
    void boardScreen();
}