package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {

    void updateState(String value) throws RemoteException;

    void broadcast(String value) throws RemoteException;
    void gameBroadcast(String value) throws RemoteException;
    void infoMessage(String value) throws RemoteException;
    void errorMessage(String value) throws RemoteException;

    void isConnected(String value) throws RemoteException;

    void completeLogin(String value) throws RemoteException;
    void completeDisconnect(String value) throws RemoteException;

    void updateGameList(String value) throws RemoteException;
    void completeCreateGame(String value) throws RemoteException;
    void completeSelectGame(String value) throws RemoteException;

    void updateGameNotStartedScreen(String value) throws RemoteException;
    void completeVoteBoard(String value) throws RemoteException;
    void completeSelectAction(String value) throws RemoteException;
    void completeEndAction(String value) throws RemoteException;

    void completeCardInfo(String value) throws RemoteException;
    void completePowerUpInfo(String value) throws RemoteException;

    void updateBoard(String value) throws RemoteException;

}
