package it.polimi.ingsw.client.view;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.json.JsonObject;

/**
 * This class contains the view screens.
 */
public interface View {

    /**
     * This queue passes the selected connection to the Client class.
     */
    Queue<Runnable> connection = new ConcurrentLinkedQueue<>();

    /**
     * This method waits for a user input and returns the parsed JsonObject.
     *
     * @return The computed JsonObject.
     */
    JsonObject userInput();

    /**
     * The initial screen of the CLI. The user can choose between RMI connection and socket
     * connection.
     */
    void initialScreen(int discoveryPort, int rmiPort, int socketPort);

    /**
     * This is the login screen, the user can choose an username and login to the server.
     */
    void loginScreen();

    /**
     * This is the game list screen.
     */
    void gameListScreen();

    /**
     * This is the game not started screen.
     */
    void gameNotStartedScreen();

    /**
     * This is the board screen.
     *
     * @param object The update board JsonObject.
     */
    void boardScreen(JsonObject object);
}