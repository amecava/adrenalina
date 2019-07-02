package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualAccessPoint extends Remote {

    /**
     * It allows the client to expose a set of methods in RMI. It is used only as a "bridge" between
     * client and server, it is not the actual RMI object.
     */
    VirtualPresenter callBack(VirtualView skeleton) throws RemoteException;
}
