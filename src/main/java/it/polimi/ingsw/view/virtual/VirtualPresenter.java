package it.polimi.ingsw.view.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualPresenter extends Remote {

    void pingServer() throws RemoteException;
    void remoteDisconnect() throws RemoteException;

    void login(String value) throws RemoteException;
    void sendMessage(String value) throws RemoteException;
}
