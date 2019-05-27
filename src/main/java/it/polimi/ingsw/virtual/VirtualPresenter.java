package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualPresenter extends Remote {

    void remoteDisconnect(String value) throws RemoteException;
    void selectPlayerId(String value) throws RemoteException;

    void askCreateGame(String value) throws RemoteException;
    void selectGame(String value) throws RemoteException;

    void voteBoard(String value) throws RemoteException;

    void spawn(String value) throws RemoteException;

    void selectAction(String value) throws RemoteException;
    void moveAction(String value) throws RemoteException;
    void askCollect(String value) throws RemoteException;
    //void selectWeapon(String value) throws RemoteException;
    //void shootAction(String value) throws RemoteException;
    //void usePowerUp(String value) throws RemoteException;
    //void askReload(String value) throws RemoteException;
    //void endAction(String value) throws RemoteException;

    void askCardInfo(String value) throws RemoteException;
    //void askPowerUpInfo(String value) throws RemoteException;

    void endOfTurn(String value) throws RemoteException;
}
