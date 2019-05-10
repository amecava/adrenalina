package it.polimi.ingsw.view;

import it.polimi.ingsw.view.connection.RmiConnection;
import it.polimi.ingsw.view.connection.SocketConnection;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

public class ConsoleView implements View {

    private Scanner stdin = new Scanner(System.in);

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * This method is for socket connections that requires sequential inputs.
     *
     * //@return
     */
    @Override
    public JsonObject userInteraction() throws RemoteException {

        String parts[] = this.input().split(" ", 2);

        if (parts[0] == null) {

            throw new RemoteException();
        }

        JsonObject object = Json.createObjectBuilder()
                .add("method", parts[0])
                .add("value", parts[1])
                .build();

        return object;
    }

    /**
     * This method is for socket connections that requires output deserialization.
     *
     * //@return
     */
    @Override
    public void serverInteraction(JsonObject object) throws RemoteException {

        try {

            this.getClass()
                    .getMethod(object.getString("method"), String.class)
                    .invoke(this, object.getString("value"));

        } catch (ReflectiveOperationException e) {

            throw new RemoteException();
        }
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

        this.output(value);
    }

    @Override
    public void welcomeScreen() {

        this.output("    _   ___  ___ ___ _  _   _   _    ___ _  _   _   ");
        this.output("   /_\\ |   \\| _ \\ __| \\| | /_\\ | |  |_ _| \\| | /_\\  ");
        this.output("  / _ \\| |) |   / _|| .` |/ _ \\| |__ | || .` |/ _ \\ ");
        this.output(" /_/ \\_\\___/|_|_\\___|_|\\_/_/ \\_\\____|___|_|\\_/_/ \\_\\\n");
    }

    @Override
    public Runnable connect(String ip, int rmiPort, int socketPort) {

        this.output("Seleziona il tipo di connessione da utilizzare:");
        this.output("    1. RMI");
        this.output("    2. Socket");

        while (true) {

            String line = this.input().toLowerCase();

            if (line.contains("rmi") && !line.contains("socket")) {

                return new RmiConnection(ip, rmiPort, this);
            } else if (line.contains("socket") && !line.contains("rmi")) {

                return new SocketConnection(ip, socketPort, this);
            } else {

                this.errorMessage("Selezione non disponibile, riprova.");
            }
        }
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
