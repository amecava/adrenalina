package it.polimi.ingsw.presenter;

import it.polimi.ingsw.view.virtual.VirtualPresenter;
import it.polimi.ingsw.view.virtual.VirtualView;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiPresenter implements Presenter, VirtualPresenter {

    private String playerId;

    private VirtualView skeleton;

    private ClientHandler clientHandler;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    RmiPresenter(VirtualView skeleton, ClientHandler clientHandler) {

        this.skeleton = skeleton;

        this.clientHandler = clientHandler;
    }

    @Override
    public String getPlayerId() {

        return this.playerId;
    }

    @Override
    public void disconnectPresenter() {

        try {

            UnicastRemoteObject.unexportObject(this, false);

        } catch (NoSuchObjectException e) {

            if (this.playerId != null) {

                LOGGER.log(Level.WARNING, "RMI client already disconnected from server.");
            }
        }

        if (this.playerId == null) {

            LOGGER.log(Level.INFO, "RMI client disconnected from server.");
        } else {

            LOGGER.log(Level.INFO, this.playerId + " disconnected from server.");
        }
    }

    @Override
    public void pingConnection() throws RemoteException {

        this.skeleton.isConnected("ping");
    }

    @Override
    public void callRemoteMethod(String method, String value) throws RemoteException {

        try {

            this.pingConnection();

            this.skeleton.getClass().getMethod(method, String.class)
                    .invoke(this.skeleton, value);

        } catch (ReflectiveOperationException e) {

            LOGGER.log(Level.SEVERE, "Reflective operation exception.", e);
        }

    }

    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.clientHandler.removeClient(this);

        this.clientHandler.broadcast("infoMessage", this.playerId + ": disconnected from server.");

        this.skeleton.disconnect(value);
    }

    @Override
    public void selectPlayerId(String value) throws RemoteException {

        if (value.replace(" ", "").length() == 0) {

            this.skeleton.errorMessage("PlayerId vuoto, riprova.");

        } else if (this.playerId != null) {

            this.skeleton.errorMessage("Login già effettuato, prima esegui logout.");

        } else {

            this.playerId = value;

            this.skeleton.login(value);

            this.clientHandler.broadcast("infoMessage", this.playerId + ": connected to server.");

        }
    }
}
