package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.GameHandler;
import it.polimi.ingsw.server.model.players.Player;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class ClientHandler {

    private static List<Presenter> clientList = new ArrayList<>();

    private static Map<GameHandler, Map<Player, Presenter>> map = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    static synchronized void addClient(Presenter presenter) {

        clientList.add(presenter);

        LOGGER.log(Level.INFO, "Client connected to server.");
    }

    static synchronized void removeClient(Presenter presenter) {

        clientList.remove(presenter);

        if (presenter.getGameHandler() != null && presenter.getPlayer() != null) {

            map.get(presenter.getGameHandler()).replace(presenter.getPlayer().setConnected(false), null);
        }

        LOGGER.log(Level.INFO, "{0} disconnected from server.", presenter.getPlayerId());

        if (!presenter.getPlayerId().equals("Client")) {

            broadcast(x -> true, "broadcast",
                    presenter.getPlayerId() + ": disconnesso dal server.");
        }

        presenter.disconnectPresenter();
    }

    static synchronized boolean isClientPresent(String playerId) {

        return clientList.stream()
                .map(Presenter::getPlayerId)
                .anyMatch(x -> x.equals(playerId));
    }

    public static synchronized void broadcast(Predicate<Presenter> filter, String method,
            String value) {

        List<Presenter> disconnected = new ArrayList<>();

        clientList.stream()
                .filter(filter)
                .forEach(x -> {

                    try {

                        x.callRemoteMethod(method, value);

                    } catch (RemoteException e) {

                        disconnected.add(x);

                    } catch (NullPointerException e) {

                        //
                    }
                });

        disconnected.forEach(ClientHandler::removeClient);
    }

    static synchronized void gameBroadcast(Predicate<Presenter> filter,
            GameHandler gameHandler, String method, String value) {

        List<Presenter> disconnected = new ArrayList<>();

        map.get(gameHandler).values().stream()
                .filter(filter)
                .forEach(x -> {

                    try {

                        x.callRemoteMethod(method, value);

                    } catch (RemoteException e) {

                        disconnected.add(x);

                    } catch (NullPointerException e) {

                        //
                    }
                });

        disconnected.forEach(ClientHandler::removeClient);
    }

    static synchronized GameHandler getGameHandler(Predicate<GameHandler> filter) {

        return map.keySet().stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    static synchronized Player getPlayer(GameHandler gameHandler, Predicate<Player> filter) {

        return map.get(gameHandler).keySet().stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    static synchronized void addGameHandler(String gameId, int numberOdDeaths,
            boolean frenzy) {

        map.put(new GameHandler(gameId, numberOdDeaths, frenzy), new HashMap<>());
    }

    static synchronized boolean isGameHandlerPresent(String gameId) {

        return map.keySet().stream().map(GameHandler::getGameId).anyMatch(x -> x.equals(gameId));
    }

    static synchronized void putPlayerPresenter(GameHandler gameHandler, Player player,
            Presenter presenter) {

        if (map.get(gameHandler).keySet().contains(player)) {

            map.get(gameHandler).replace(player, presenter);
        } else {

            map.get(gameHandler).put(player, presenter);
        }
    }

    static synchronized boolean isPlayerPresent(String playerId) {

        return map.values().stream()
                .flatMap(x -> x.keySet().stream())
                .map(Player::getPlayerId)
                .anyMatch(x -> x.equals(playerId));
    }

    static synchronized JsonArray getGameHandlerJsonArray() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        map.keySet().forEach(x -> builder.add(x.toJsonObject()));

        return builder.build();
    }
}
