package it.polimi.ingsw.view;

import it.polimi.ingsw.view.connection.RmiConnection;
import it.polimi.ingsw.view.connection.SocketConnection;
import it.polimi.ingsw.virtual.VirtualView;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ConsoleView implements View, VirtualView {

    private Scanner stdin = new Scanner(System.in);

    private static final String METHOD = "method";
    private static final String VALUE = "value";

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    @Override
    public JsonObject userInput() {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        while (true) {

            String[] parts = this.input().split(" ", 2);

            builder.add(VALUE, parts.length == 2 ? parts[1] : "");

            switch (parts[0]) {

                case "disconnetti":

                    return builder.add(METHOD, "remoteDisconnect").build();

                case "login":

                    return builder.add(METHOD, "selectPlayerId").build();

                default:

                    this.errorMessage("Selezione non disponibile, riprova o digita help.");
            }
        }
    }

    @Override
    public void adrenalinaSplashScreen() {

        this.output("    _   ___  ___ ___ _  _   _   _    ___ _  _   _   ");
        this.output("   /_\\ |   \\| _ \\ __| \\| | /_\\ | |  |_ _| \\| | /_\\  ");
        this.output("  / _ \\| |) |   / _|| .` |/ _ \\| |__ | || .` |/ _ \\ ");
        this.output(" /_/ \\_\\___/|_|_\\___|_|\\_/_/ \\_\\____|___|_|\\_/_/ \\_\\\n");
    }

    @Override
    public Runnable selectConnection(InetAddress inetAddress, int rmiPort, int socketPort) {

        this.output("Seleziona il tipo di connessione da utilizzare:");
        this.output("    1. RMI");
        this.output("    2. Socket");

        while (true) {

            String line = this.input().toLowerCase();

            if (line.contains("rmi") && !line.contains("socket")) {

                return new RmiConnection(inetAddress, rmiPort, this);

            } else if (line.contains("socket") && !line.contains("rmi")) {

                return new SocketConnection(inetAddress, socketPort, this);

            } else {

                this.errorMessage("Selezione non disponibile, riprova.");
            }
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

        this.output("ERROR: " + value);
    }

    @Override
    public void isConnected(String value) {

        //
    }

    @Override
    public void completeLogin(String value) {

        this.output("Login effettuato come " + value + ".");
    }

    @Override
    public void completeDisconnect(String value) {

        this.logMessage("Disconnected from server.");
    }

    private String input() {

        return this.stdin.nextLine();
    }

    private void output(String value) {

        System.out.println(value);
    }
}
