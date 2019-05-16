package it.polimi.ingsw.presenter;

import it.polimi.ingsw.virtual.VirtualView;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiPresenter extends Presenter {

    private VirtualView skeleton;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    RmiPresenter(VirtualView skeleton) {

        this.skeleton = skeleton;

        this.setPlayerId("RMI client");
    }

    @Override
    public void disconnectPresenter() {

        try {

            UnicastRemoteObject.unexportObject(this, false);

        } catch (NoSuchObjectException e) {

            LOGGER.log(Level.WARNING, "RMI client already disconnected from server.");
        }
    }

    @Override
    public void callRemoteMethod(String method, String value) throws RemoteException {

        try {

            this.skeleton.getClass().getMethod(method, String.class)
                    .invoke(this.skeleton, value);

        } catch (ReflectiveOperationException e) {

            throw new RemoteException();
        }
    }

}
