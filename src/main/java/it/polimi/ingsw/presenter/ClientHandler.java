package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    private List<Presenter> clientList = new ArrayList<>();

    private Map<GameHandler, Map<Player, Presenter>> map = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    synchronized void addClient(Presenter presenter) {

        this.clientList.add(presenter);
    }

    synchronized void removeClient(Presenter presenter) {

        presenter.disconnectPresenter();
        this.clientList.remove(presenter);
    }

    public synchronized void removeDisconnected() {

        List<Presenter> removeList = new ArrayList<>();

        this.clientList.forEach(x -> {

            try {

                x.pingConnection();

            } catch (RemoteException e) {

                removeList.add(x);
            }

        });

        removeList.forEach(x -> {

            x.disconnectPresenter();
            this.clientList.remove(x);
        });

        if (!removeList.isEmpty()) {

            removeList.forEach(x -> {

                if (x.getPlayerId() != null) {

                    this.broadcast("infoMessage", x.getPlayerId() + ": disconnected from server.");
                }
            });
        }
    }

    void broadcast(String method, String value) {

        this.clientList.forEach(x -> {

            try {

                x.callRemoteMethod(method, value);

            } catch (RemoteException e) {

                LOGGER.log(Level.SEVERE, "Disconnected client in client list.", e);
            }
        });
    }
}
