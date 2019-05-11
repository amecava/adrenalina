package it.polimi.ingsw.presenter;

import java.rmi.RemoteException;

public interface Presenter {

    String getPlayerId();
    void disconnectPresenter();

    void pingConnection() throws RemoteException;
    void callRemoteMethod(String method, String value) throws RemoteException;
}
