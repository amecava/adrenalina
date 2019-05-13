package it.polimi.ingsw.view;

import java.net.InetAddress;
import javax.json.JsonObject;

public interface View {

    JsonObject userInput();

    void adrenalinaSplashScreen();
    Runnable selectConnection(InetAddress inetAddress, int rmiPort, int socketPort);
}