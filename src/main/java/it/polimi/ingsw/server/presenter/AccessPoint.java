package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.virtual.VirtualPresenter;
import it.polimi.ingsw.virtual.VirtualAccessPoint;
import it.polimi.ingsw.virtual.VirtualView;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AccessPoint implements VirtualAccessPoint {

    @Override
    public synchronized VirtualPresenter callBack(VirtualView skeleton) throws RemoteException {

        RmiPresenter presenter = new RmiPresenter(skeleton);

        ClientHandler.addClient(presenter);

        return (VirtualPresenter) UnicastRemoteObject.exportObject(presenter, 0);
    }
}
