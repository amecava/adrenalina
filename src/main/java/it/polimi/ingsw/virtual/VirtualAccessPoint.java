package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualAccessPoint extends Remote {

    VirtualPresenter callBack(VirtualView skeleton) throws RemoteException;
}
