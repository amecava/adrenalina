package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.virtual.VirtualAccessPoint;
import it.polimi.ingsw.view.virtual.VirtualPresenter;
import it.polimi.ingsw.view.virtual.VirtualView;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

public class RmiConnection implements Connection {

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
    public void connect() {

        try {

            Registry registry = LocateRegistry.getRegistry(this.ip, this.port);
            VirtualAccessPoint access = (VirtualAccessPoint) registry.lookup("AccessPoint");

            VirtualView skeleton = (VirtualView) UnicastRemoteObject.exportObject((VirtualView) this.view, 0);
            VirtualPresenter stub = access.callBack(skeleton);

            LOGGER.log(Level.INFO, "Connected to RMI server.");

            while (Thread.currentThread().isAlive()) {

                JsonObject object = this.view.userInput();

                switch (object.getString("method")) {

                    case "disconnetti":

                        stub.remoteDisconnect("RMI");
                        break;

                    case "login":

                        stub.selectPlayerId(object.getString("value"));
                        break;

                    default:

                        this.view.userOutput(Json.createObjectBuilder().add("method", "errorMessage").add("value", "Selezione non disponiile, riprova oppure help.").build());
                }
            }
        } catch (RemoteException | NotBoundException e) {

            LOGGER.log(Level.SEVERE, "RMI connection exception.", e);
        }
    }
}
