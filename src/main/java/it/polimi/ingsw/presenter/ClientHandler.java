package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class ClientHandler {

    public static List<Presenter> clientList = new ArrayList<>();

    public static Map<GameHandler, Map<Player, Presenter>> map = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public static synchronized void addClient(Presenter presenter) {

        clientList.add(presenter);

        LOGGER.log(Level.INFO, "{0} connected to server.", presenter.getPlayerId());
    }

    public static synchronized void removeClient(Presenter presenter) {

        clientList.remove(presenter);

        LOGGER.log(Level.INFO, "{0} disconnected from server.", presenter.getPlayerId());

        if (!presenter.getPlayerId().equals("RMI client") && !presenter.getPlayerId().equals("Socket client")) {

            broadcast(x -> true, "infoMessage", presenter.getPlayerId() + ": disconnected from server.");
        }

        presenter.disconnectPresenter();
    }

    public static synchronized void broadcast(Predicate<Presenter> filter, String method, String value) {

        List<Presenter> disconnected = new ArrayList<>();

        clientList.stream()
                .filter(filter)
                .forEach(x -> {

                    try {

                        x.callRemoteMethod(method, value);

                    } catch (RemoteException e) {

                        disconnected.add(x);
                    }
                });

        disconnected.forEach(ClientHandler::removeClient);
    }

    public static synchronized JsonArray getGameHandlerJsonArray() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        map.keySet().forEach(x -> builder.add(x.toJsonObject()));

        return builder.build();
    }
}
