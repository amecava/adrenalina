package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {

    void logMessage(String value) throws RemoteException;
    void infoMessage(String value) throws RemoteException;
    void errorMessage(String value) throws RemoteException;

    void isConnected(String value) throws RemoteException;

    void completeLogin(String value) throws RemoteException;
    void completeDisconnect(String value) throws RemoteException;

    void showGames(String value) throws RemoteException;
    void completeCreateGame(String value) throws RemoteException;

    void completeSelectGame(String value) throws RemoteException;

    void completeEndOfTurn(String value) throws  RemoteException;
}
