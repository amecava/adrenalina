package it.polimi.ingsw.view;

import it.polimi.ingsw.view.connection.Connection;
import javax.json.JsonObject;

public interface View {

    JsonObject userInput();
    void userOutput(JsonObject jsonObject);

    void serverInteraction(JsonObject object) throws ReflectiveOperationException;

    void welcomeScreen();
    Connection selectConnection(String ip, int rmiPort, int socketPort) throws InterruptedException;
}