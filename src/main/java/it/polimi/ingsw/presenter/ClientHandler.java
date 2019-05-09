package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.LocalDateTime;
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

        LocalDateTime now = LocalDateTime.now();

        List<Presenter> removeList = new ArrayList<>();

        this.clientList.forEach(x -> {

            if (x.getLastPingTime() != null && Duration.between(x.getLastPingTime(), now)
                    .compareTo(Duration.ofMillis(5000)) > 0) {

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

                    this.playerLogMessage(x.getPlayerId(), "disconnected from server.");
                }
            });
        }
    }

    void playerLogMessage(String playerId, String message) {

        this.clientList.forEach(x -> {

            try {

                if (x.getPlayerId() != playerId) {

                    x.callRemoteMethod("infoMessage", playerId + ": " + message);
                }

            } catch (RemoteException e) {

                LOGGER.log(Level.SEVERE, "Disconnected client in client list.", e);
            }
        });
    }
}
