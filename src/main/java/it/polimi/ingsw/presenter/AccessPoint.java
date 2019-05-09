package it.polimi.ingsw.presenter;

import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.virtual.VirtualPresenter;
import it.polimi.ingsw.view.virtual.VirtualAccessPoint;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessPoint implements VirtualAccessPoint {

    private ClientHandler clientHandler;

    private Map<VirtualPresenter, RmiPresenter> stubMap = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public AccessPoint(ClientHandler clientHandler) {

        this.clientHandler = clientHandler;
    }

    @Override
    public synchronized VirtualPresenter callBack(View skeleton) throws RemoteException {

        RmiPresenter presenter = new RmiPresenter(skeleton, this.clientHandler);
        VirtualPresenter stub = (VirtualPresenter) UnicastRemoteObject.exportObject(presenter, 0);

        LOGGER.log(Level.INFO, "RMI client connected to server.");

        this.clientHandler.addClient(presenter);

        return stub;
    }
}
