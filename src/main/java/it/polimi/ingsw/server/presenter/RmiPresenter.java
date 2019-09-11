package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.common.VirtualView;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rmi extension of the Presenter (it implements ita abstract methods):
 * The presenter of the Model - Passive View - Presenter pattern. This class is responsible of
 * furthering the correct requests by calling the correct methods of the model (through the
 * GameHandler) and by preparing the proper JsonObjects for the View. Every method of this class not
 * only performs the action but also sends the proper notification to the client.
 */
public class RmiPresenter extends Presenter {

    /**
     * The skeleton of the RMI object exposed by the client in order to enable the server to call
     * some of his methods.
     */
    private VirtualView skeleton;

    /**
     * A Logger that prints updates on the server.
     */
    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * Creates the RmiPresenter.
     *
     * @param skeleton The skeleton of the RMI object exposed by the client in order to enable the
     * server to call some of his methods.
     */
    RmiPresenter(VirtualView skeleton) {

        this.skeleton = skeleton;
    }

    /**
     * Unexports the object exposed by the server to the client.
     */
    @Override
    public void disconnectPresenter() {

        try {

            UnicastRemoteObject.unexportObject(this, false);

        } catch (NoSuchObjectException e) {

            LOGGER.log(Level.WARNING, "RMI client already disconnected from server.");
        }
    }

    /**
     * Uses the Skeleton to call the method "method" on the object exported by the client, with the
     * parameter "value". Practical example: if method = "updateBoard", the method "updateBoard"
     * will be called on the client's View, and the board of that client will be updated.
     *
     * @param method The name of the method that will be called on the View.
     * @param value The value that will be given as a parameter to "method" method.
     * @throws RemoteException It is thrown as a response to this Exception:
     * ReflectiveOperationException.
     */
    @Override
    public void callRemoteMethod(String method, String value) throws RemoteException {

        final AtomicBoolean completed = new AtomicBoolean(false);

        Thread thread = new Thread(() -> {

            try {

                VirtualView.class
                        .getMethod(method, String.class)
                        .invoke(this.skeleton, value);

                completed.set(true);

            } catch (ReflectiveOperationException e) {

                //
            }
        });

        try {

            thread.setDaemon(true);

            thread.start();

            thread.join(1000);

            if (thread.isAlive() || !completed.get()) {

                throw new RemoteException();
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
