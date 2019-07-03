package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.players.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class ClientHandler {

    private static List<Presenter> clientList = new ArrayList<>();

    private static Map<GameHandler, HashMap<Player, Presenter>> map = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    private ClientHandler(){

        //
    }

    static synchronized void addClient(Presenter presenter) {

        clientList.add(presenter);

        try {

            presenter.callRemoteMethod("updateState", Presenter.state.get("loginState").toString());

        } catch (RemoteException e) {

            //
        }

        LOGGER.log(Level.INFO, "Client connected to server.");
    }

    static synchronized void removeClient(Presenter presenter) {

        if (clientList.contains(presenter)) {

            clientList.remove(presenter);

            if (presenter.getGameHandler() != null && presenter.getPlayer() != null) {

                map.get(presenter.getGameHandler())
                        .replace(presenter.getPlayer().setConnected(false), null);
            }

            LOGGER.log(Level.INFO, "{0} disconnected from server.", presenter.getPlayerId());

            if (!presenter.getPlayerId().equals("Client")) {

                broadcast(x -> true, "broadcast",
                        presenter.getPlayerId() + ": disconnesso dal server.");
            }

            presenter.disconnectPresenter();
        }
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

    public static synchronized void gameBroadcast(GameHandler gameHandler,
            Predicate<Entry<Player, Presenter>> filter,
            String method, String value) {

        List<Presenter> disconnected = new ArrayList<>();

        map.get(gameHandler).entrySet().stream()
                .filter(x -> x.getValue() != null)
                .filter(filter)
                .forEach(x -> {

                    try {

                        x.getValue().callRemoteMethod(method, value);

                    } catch (RemoteException e) {

                        disconnected.add(x.getValue());

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
                .orElseThrow(NoSuchElementException::new)
                .setConnected(true);
    }


    public static synchronized Presenter getPresenter(GameHandler gameHandler,
            Predicate<Entry<Player, Presenter>> filter) {

        return map.get(gameHandler).entrySet().stream()
                .filter(filter).map(Entry::getValue)
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

    static synchronized JsonObject getGameHandlerJsonArray() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        map.keySet().forEach(x -> builder.add(x.toJsonObject()));

        return Json.createObjectBuilder().add("gameList", builder.build()).build();
    }

    public static void save(String path) {

        try {

            LOGGER.log(Level.INFO, "Saving to file...");

            map.values().forEach(x ->

                    x.forEach((key, value) -> {

                        key.setConnected(false);

                        if (value != null) {

                            value.disconnectPresenter();
                        }

                        x.replace(key, null);
                    })
            );

            File file = new File(path);
            file.createNewFile();

            try (FileOutputStream fileStream = new FileOutputStream(file);
                    ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {

                objectStream.writeObject((HashMap<GameHandler, HashMap<Player, Presenter>>) map);
            }

            LOGGER.log(Level.INFO, "Saving to file successful.");

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "Saving to file failed.", e);

        }
    }

    public static void load(String path) {

        try (FileInputStream fileStream = new FileInputStream(path);
                ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {

            LOGGER.log(Level.INFO, "Loading from file...");

            map = (HashMap<GameHandler, HashMap<Player, Presenter>>) objectStream.readObject();

            LOGGER.log(Level.INFO, "Loading from file successful.");


        } catch (IOException | ClassNotFoundException e) {

            map = new HashMap<>();
        }
    }
}
