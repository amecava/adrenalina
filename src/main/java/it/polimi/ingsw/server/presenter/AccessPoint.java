package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.virtual.VirtualPresenter;
import it.polimi.ingsw.virtual.VirtualAccessPoint;
import it.polimi.ingsw.virtual.VirtualView;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AccessPoint implements VirtualAccessPoint {

    /**
     * It allows the client to expose a set of methods in RMI. It is used only as a "bridge" between
     * client and server, it is not the actual RMI object.
     *
     * @param skeleton The object that the client wants to expose to the server.
     * @return The object that the server exposes to the client.
     */
    @Override
    public synchronized VirtualPresenter callBack(VirtualView skeleton) throws RemoteException {

        RmiPresenter presenter = new RmiPresenter(skeleton);

        ClientHandler.addClient(presenter);

        return (VirtualPresenter) UnicastRemoteObject.exportObject(presenter, 0);
    }
}
