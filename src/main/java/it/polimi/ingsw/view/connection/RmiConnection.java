package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.virtual.VirtualAccessPoint;
import it.polimi.ingsw.view.virtual.VirtualPresenter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiConnection implements Runnable {

    private String ip;
    private int port;

    private View view;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public RmiConnection(String ip, int port, View view) {

        this.ip = ip;
        this.port = port;

        this.view = view;
    }

    @Override
    public void run() {

        try {

            Registry registry = LocateRegistry.getRegistry(this.ip, this.port);
            VirtualAccessPoint access = (VirtualAccessPoint) registry.lookup("AccessPoint");

            View skeleton = (View) UnicastRemoteObject.exportObject(this.view, 0);
            VirtualPresenter stub = access.callBack(skeleton);

            LOGGER.log(Level.INFO, "Connected to RMI server.");

            Thread ping = new Thread(() -> {

                while (Thread.currentThread().isAlive()) {

                    try {

                        Thread.sleep(1000);

                        stub.pingServer();

                    } catch (InterruptedException | RemoteException e) {

                        Thread.currentThread().interrupt();
                    }
                }
            });

            ping.setDaemon(true);
            ping.start();

            while (Thread.currentThread().isAlive()) {

                String line = this.view.userInteraction();

                if (line.equals("disconnetti")) {

                    stub.remoteDisconnect();

                } else if (line.startsWith("login")) {

                    stub.login(line.substring(6));

                } else {

                    stub.sendMessage(line);
                }
            }
        } catch (RemoteException | NotBoundException e) {

            LOGGER.log(Level.SEVERE, "RMI connection exception.", e);
        }
    }
}
