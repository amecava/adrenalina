package it.polimi.ingsw.presenter;

import it.polimi.ingsw.virtual.VirtualPresenter;
import java.rmi.RemoteException;

public abstract class Presenter implements VirtualPresenter {

    private String playerId;

    abstract void disconnectPresenter();
    abstract void callRemoteMethod(String method, String value) throws RemoteException;

    String getPlayerId() {

        return this.playerId;
    }

    void setPlayerId(String playerId) {

        this.playerId = playerId;
    }

    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.callRemoteMethod("completeDisconnect", value);

        ClientHandler.removeClient(this);
    }

    @Override
    public void selectPlayerId(String value) throws RemoteException {

        if (value.replace(" ", "").length() == 0) {

            this.callRemoteMethod("errorMessage", "PlayerId vuoto, riprova.");

        } else if (!this.playerId.equals("RMI client") && !this.playerId.equals("Socket client")) {

            this.callRemoteMethod("errorMessage", "Login già effettuato, prima esegui logout.");

        } else {

            this.playerId = value;

            this.callRemoteMethod("completeLogin", value);

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "infoMessage", this.playerId + ": connected to server.");
        }
    }
}
