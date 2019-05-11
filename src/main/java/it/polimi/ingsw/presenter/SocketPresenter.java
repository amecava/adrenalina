package it.polimi.ingsw.presenter;

import it.polimi.ingsw.view.virtual.VirtualPresenter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

public class SocketPresenter implements Presenter, VirtualPresenter, Runnable {

    private String playerId;

    private Scanner in;
    private PrintWriter out;

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
    public String getPlayerId() {

        return this.playerId;
    }

    @Override
    public void disconnectPresenter() {

        if (this.playerId == null) {

            LOGGER.log(Level.INFO, "Socket client disconnected from server.");
        } else {

            LOGGER.log(Level.INFO, this.playerId + " disconnected from server.");
        }
    }

    @Override
    public void pingConnection() throws RemoteException {

        try {

            this.out.println(this.jsonSerialize("isConnected", "ping"));
            this.out.flush();

        } catch (NoSuchElementException e) {

            throw new RemoteException();
        }
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

    @Override
    public void run() {

        LOGGER.log(Level.INFO, "Socket client connected to server.");

        this.clientHandler.addClient(this);

        try {

            while (Thread.currentThread().isAlive()) {

                JsonObject object = this.jsonDeserialize(this.in.nextLine());

                switch (object.getString("method")) {

                    case "disconnetti":

                        this.remoteDisconnect("socket");
                        break;

                    case "login":

                        this.selectPlayerId(object.getString("value"));
                        break;

                    default:

                        this.callRemoteMethod("errorMessage", "Selezione non disponiile, riprova oppure help.");
                }
            }
        } catch (RemoteException | NoSuchElementException e) {

            this.clientHandler.removeClient(this);

            if (this.playerId != null) {

                this.clientHandler.broadcast("infoMessage", this.playerId + ": disconnected from server.");
            }
        }
    }

    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.clientHandler.removeClient(this);

        this.clientHandler.broadcast("infoMessage", this.playerId + ": disconnected from server.");

        this.callRemoteMethod("disconnect", value);

        this.in.close();
        this.out.close();
    }

    @Override
    public void selectPlayerId(String value) throws RemoteException {

        if (value.replace(" ", "").length() == 0) {

            this.callRemoteMethod("errorMessage", "PlayerId vuoto, riprova.");

        } else if (this.playerId != null) {

            this.callRemoteMethod("errorMessage", "Login già effettuato, prima esegui logout.");

        } else {

            this.playerId = value;

            this.callRemoteMethod("login", value);

            this.clientHandler.broadcast("infoMessage", this.playerId + ": connected to server.");

        }
    }

    private JsonObject jsonSerialize(String method, String value) {

        return Json.createObjectBuilder()
                .add("method", method)
                .add("value", value)
                .build();
    }

    private JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}