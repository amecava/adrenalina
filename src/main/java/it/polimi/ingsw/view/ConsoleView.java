package it.polimi.ingsw.view;

import it.polimi.ingsw.view.connection.Connection;
import it.polimi.ingsw.view.connection.RmiConnection;
import it.polimi.ingsw.view.connection.SocketConnection;
import it.polimi.ingsw.view.virtual.VirtualView;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

public class ConsoleView implements View, VirtualView {

    private Scanner stdin = new Scanner(System.in);

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    @Override
    public JsonObject userInput() {

        String parts[] = this.input().split(" ", 2);

        return Json.createObjectBuilder()
                .add("method", parts[0])
                .add("value", parts.length == 2 ? parts[1] : "")
                .build();
    }

    @Override
    public void userOutput(JsonObject object) {

        try {

            this.serverInteraction(object);

        } catch (ReflectiveOperationException e) {

            LOGGER.log(Level.SEVERE, "Reflective operation exception.", e);
        }
    }

    @Override
    public void serverInteraction(JsonObject object) throws ReflectiveOperationException {

        this.getClass()
                .getMethod(object.getString("method"), String.class)
                .invoke(this, object.getString("value"));
    }

    @Override
    public void welcomeScreen() {

        this.output("    _   ___  ___ ___ _  _   _   _    ___ _  _   _   ");
        this.output("   /_\\ |   \\| _ \\ __| \\| | /_\\ | |  |_ _| \\| | /_\\  ");
        this.output("  / _ \\| |) |   / _|| .` |/ _ \\| |__ | || .` |/ _ \\ ");
        this.output(" /_/ \\_\\___/|_|_\\___|_|\\_/_/ \\_\\____|___|_|\\_/_/ \\_\\\n");
    }

    @Override
    public Connection selectConnection(String ip, int rmiPort, int socketPort) throws InterruptedException {

        this.output("Seleziona il tipo di connessione da utilizzare:");
        this.output("    1. RMI");
        this.output("    2. Socket");

        while (Thread.currentThread().isAlive()) {

            String line = this.input().toLowerCase();

            if (line.contains("rmi") && !line.contains("socket")) {

                return new RmiConnection(ip, rmiPort, this);
            } else if (line.contains("socket") && !line.contains("rmi")) {

                return new SocketConnection(ip, socketPort, this);
            } else {

                this.errorMessage("Selezione non disponibile, riprova.");
            }
        }

        throw new InterruptedException();
    }

    @Override
    public void logMessage(String value) {

        LOGGER.log(Level.INFO, value);
    }

    @Override
    public void infoMessage(String value) {

        this.output("INFO: " + value);
    }

    @Override
    public void errorMessage(String value) {

        this.output("ERROR: " + value);
    }

    @Override
    public void isConnected(String value) {

        //
    }

    @Override
    public void login(String value) {

        this.output("Login effettuato come " + value + ".");
    }

    @Override
    public void disconnect(String value) {

        this.logMessage("Disconnected from " + value + " server.");

        System.exit(0);
    }

    private String input() {

        return this.stdin.nextLine();
    }

    private void output(String value) {

        System.out.println(value);
    }
}
