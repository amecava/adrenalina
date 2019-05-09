package it.polimi.ingsw.presenter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

public class SocketPresenter implements Presenter, Runnable {

    private String playerId;

    private Scanner in;
    private PrintWriter out;

    private LocalDateTime lastPingTime;

    private ClientHandler clientHandler;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public SocketPresenter(Socket socket, ClientHandler clientHandler) throws IOException {

        this.clientHandler = clientHandler;

        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {

        LOGGER.log(Level.INFO, "Socket client connected to server.");

        this.clientHandler.addClient(this);

        try {
            while (Thread.currentThread().isAlive()) {

                String line = this.in.nextLine();

                if (line.equals("ping")) {

                    this.lastPingTime = LocalDateTime.now();

                } else if (line.equals("disconnetti")) {

                    this.callRemoteMethod("disconnect", "socket");

                } else if (line.startsWith("login")) {

                    this.playerId = line.substring(6);

                    this.callRemoteMethod("login", line.substring(6));

                    this.clientHandler.playerLogMessage(this.playerId, "connected to server.");

                } else {

                    this.callRemoteMethod("infoMessage", "Received: " + line);
                }
            }
        } catch (RemoteException | NoSuchElementException e) {

            this.clientHandler.removeClient(this);

            if (this.playerId != null) {

                this.clientHandler.playerLogMessage(this.playerId, "disconnected from server.");
            }
        }
    }

    @Override
    public String getPlayerId() {

        return this.playerId;
    }

    @Override
    public void disconnectPresenter() {

        this.in.close();
        this.out.close();

        if (this.playerId == null) {

            LOGGER.log(Level.INFO, "Socket client disconnected from server.");
        } else {

            LOGGER.log(Level.INFO, this.playerId + " disconnected from server.");
        }
    }

    @Override
    public LocalDateTime getLastPingTime() {

        return this.lastPingTime;
    }

    @Override
    public void callRemoteMethod(String method, String value) throws RemoteException {

        try {

            this.out.println(this.jsonSerialize(method, value));
            this.out.flush();

        } catch (NoSuchElementException e) {

            throw new RemoteException();
        }

    }

    private String jsonSerialize(String method, String value) {

        JsonObject object = Json.createObjectBuilder()
                .add("method", method)
                .add("value", value)
                .build();

        return object.toString();
    }
}