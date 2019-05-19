package it.polimi.ingsw.client.view;

import java.net.InetAddress;
import javax.json.JsonObject;

public interface View {

    JsonObject userInput();

    void searchingForServer();
    Runnable selectConnection(InetAddress inetAddress, int rmiPort, int socketPort);
    void connectingToServer();

    void splashScreen();
    void loginScreen();
    //void gameNotStartedScreen();
    //void gameStartedScreen();
}