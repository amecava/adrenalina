package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualPresenter extends Remote {

    void remoteDisconnect(String value) throws RemoteException;
    void selectPlayerId(String value) throws RemoteException;
}
