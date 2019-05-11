package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.view.View;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualAccessPoint extends Remote {

    VirtualPresenter callBack(VirtualView skeleton) throws RemoteException;
}
