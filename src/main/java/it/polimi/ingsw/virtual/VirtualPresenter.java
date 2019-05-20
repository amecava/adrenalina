package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.json.JsonObject;

public interface VirtualPresenter extends Remote {

    void remoteDisconnect(String value) throws RemoteException;
    void selectPlayerId(String value) throws RemoteException;

    void askCreateGame(String value) throws RemoteException;
    void selectGame(String value) throws RemoteException;

    void voteBoard(String value) throws RemoteException;
}
