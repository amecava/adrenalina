package it.polimi.ingsw.presenter;

import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.virtual.VirtualPresenter;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiPresenter implements Presenter, VirtualPresenter {

    private String playerId;

    private View skeleton;

    private LocalDateTime lastPingTime;

    private ClientHandler clientHandler;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    RmiPresenter(View skeleton, ClientHandler clientHandler) {

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
    public LocalDateTime getLastPingTime() {

        return this.lastPingTime;
    }

    @Override
    public void callRemoteMethod(String method, String value) {

        try {

            this.skeleton.getClass().getMethod(method, String.class)
                    .invoke(this.skeleton, value);

        } catch (ReflectiveOperationException e) {

            LOGGER.log(Level.SEVERE, "Reflective operation exception.", e);
        }
    }

    @Override
    public void pingServer() {

        this.lastPingTime = LocalDateTime.now();
    }

    @Override
    public void remoteDisconnect() throws RemoteException {

        this.clientHandler.removeClient(this);

        if (this.playerId == null) {

            LOGGER.log(Level.INFO, "RMI client disconnected from server.");
        } else {

            LOGGER.log(Level.INFO, this.playerId + " disconnected from server.");

            this.clientHandler.playerLogMessage(this.playerId, "disconnected from server.");
        }

        this.skeleton.disconnect("RMI");
    }

    @Override
    public void sendMessage(String value) throws RemoteException {

        this.skeleton.infoMessage("Received: " + value);
    }

    @Override
    public void login(String value) throws RemoteException {

        this.playerId = value;

        this.skeleton.login(value);

        this.clientHandler.playerLogMessage(this.playerId, "connected to server.");
    }
}
