package it.polimi.ingsw.client.view.connection;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.common.VirtualAccessPoint;
import it.polimi.ingsw.common.VirtualPresenter;
import it.polimi.ingsw.common.VirtualView;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;

/**
 * This class handles the RMI connection between the view and the server.
 */
public class RmiConnection implements Runnable {

    /**
     * The server InetAdress received with the UDP broadcast message.
     */
    private InetAddress inetAddress;
    /**
     * The server RMI port received with the UDP broadcast message.
     */
    private int port;

    /**
     * The view that the connection will use to communicate with the user (CLI or GUI).
     */
    private View view;

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * Constructor for the RMI connection.
     *
     * @param inetAddress The server InetAdress received with the UDP broadcast message.
     * @param port The server RMi port received with the UDP broadcast message.
     * @param view The view that the connection will use to communicate with the user (CLI or GUI).
     */
    public RmiConnection(InetAddress inetAddress, int port, View view) {

        this.inetAddress = inetAddress;
        this.port = port;

        this.view = view;
    }

    /**
     * This method connects to the server RMI registry and exports a VirtualView to be used on the
     * RMI callback. When the connection is established it waits for user inputs from the view to be
     * sent to the server.
     */
    @Override
    public void run() {

        try {

            Registry registry = LocateRegistry
                    .getRegistry(this.inetAddress.getHostAddress(), this.port);
            VirtualAccessPoint access = (VirtualAccessPoint) registry.lookup("AccessPoint");

            VirtualView skeleton = (VirtualView) UnicastRemoteObject
                    .exportObject((VirtualView) this.view, 0);
            VirtualPresenter stub = access.callBack(skeleton);

            this.view.loginScreen();

            while (Thread.currentThread().isAlive()) {

                JsonObject object = this.view.userInput();

                VirtualPresenter.class
                        .getMethod(object.getString("method"), String.class)
                        .invoke(stub, object.toString());

                if (object.getString("method").equals("remoteDisconnect")) {

                    UnicastRemoteObject.unexportObject((VirtualView) this.view, false);

                    break;
                }
            }

        } catch (RemoteException e) {

            LOGGER.log(Level.SEVERE, "RMI server not reachable.", e);

        } catch (NotBoundException e) {

            LOGGER.log(Level.SEVERE, "RMI access point not reachable.", e);

        } catch (NoSuchMethodException e) {

            LOGGER.log(Level.SEVERE, "Selected method does not exist.", e);

        } catch (IllegalAccessException e) {

            LOGGER.log(Level.SEVERE, "Method visibility qualifiers violated.", e);

        } catch (InvocationTargetException e) {

            LOGGER.log(Level.SEVERE, "Invocation target exception.", e);
        }
    }
}
