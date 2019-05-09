package it.polimi.ingsw.presenter;

import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface Presenter {

    String getPlayerId();

    void disconnectPresenter();
    LocalDateTime getLastPingTime();

    void callRemoteMethod(String method, String value) throws RemoteException;
}
