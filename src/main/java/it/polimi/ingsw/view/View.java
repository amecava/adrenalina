package it.polimi.ingsw.view;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.json.JsonObject;

public interface View extends Remote {

    JsonObject userInteraction() throws RemoteException;
    void serverInteraction(JsonObject object) throws RemoteException;

    void logMessage(String value) throws RemoteException;
    void infoMessage(String value) throws RemoteException;
    void errorMessage(String value) throws RemoteException;

    void welcomeScreen() throws RemoteException;
    Runnable connect(String ip, int rmiPort, int socketPort) throws RemoteException;

    void isConnected(String value) throws RemoteException;
    void login(String value) throws RemoteException;
    void disconnect(String value) throws RemoteException;
}