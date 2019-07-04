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

    /**
     * A list that holds the players that are waiting for a match to start.
     */
    private static List<Presenter> clientList = new ArrayList<>();

    /**
     * A Map that for every match holds a map containing every player of the match with his
     * Presenter.
     */
    private static Map<GameHandler, HashMap<Player, Presenter>> map = new HashMap<>();

    /**
     * The Logger that prints updates on the server.
     */
    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * Initializes the ClientHandler.
     */
    private ClientHandler() {

        //
    }

    /**
     * Adds a new Client to the server.
     *
     * @param presenter The presenter of that client.
     */
    static synchronized void addClient(Presenter presenter) {

        clientList.add(presenter);

        try {

            presenter.callRemoteMethod("updateState", Presenter.state.get("loginState").toString());

        } catch (RemoteException e) {

            //
        }

        LOGGER.log(Level.INFO, "Client connected to server.");
    }

    /**
     * Removes a client from the server.
     *
     * @param presenter The Presenter of the client that will be removed from the server.
     */
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

    /**
     * Check if a certain client is present in the list of players waiting for a game to start.
     *
     * @param playerId The id of the player.
     * @return A boolean that says if the player is present.
     */
    static synchronized boolean isClientPresent(String playerId) {

        return clientList.stream()
                .map(Presenter::getPlayerId)
                .anyMatch(x -> x.equals(playerId));
    }

    /**
     * Calls a method on the view of every player in the clientList based on a filter Predicate.
     *
     * @param filter The condition on which the method is called or not.
     * @param method The name of the method that will be called.
     * @param value The value that will be sent as a parameter.
     */
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

    /**
     * Calls a method on the view of every player in the map that is key of a GameHandler based on a
     * filter Predicate.
     *
     * @param gameHandler The GameHandler of the game on which you want to call the method.
     * @param filter The condition on which the method is called or not.
     * @param method The name of the method that will be called.
     * @param value The value that will be sent as a parameter.
     */
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

    /**
     * Gets a GameHandler based on a filter Predicate.
     * @param filter The Predicate that is the condition for getting th right GameHandler.
     * @return The GameHandler.
     */
    static synchronized GameHandler getGameHandler(Predicate<GameHandler> filter) {

        return map.keySet().stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Get a Player of a match based on a filter Predicate.
     * @param gameHandler The GameHandler of the match in which the player should be.
     * @param filter The filter Predicate.
     * @return The Player.
     */
    static synchronized Player getPlayer(GameHandler gameHandler, Predicate<Player> filter) {

        return map.get(gameHandler).keySet().stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .setConnected(true);
    }

    /**
     *
     * @param gameHandler
     * @param filter
     * @return
     */
    public static synchronized Presenter getPresenter(GameHandler gameHandler,
            Predicate<Entry<Player, Presenter>> filter) {

        return map.get(gameHandler).entrySet().stream()
                .filter(filter).map(Entry::getValue)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Adds a new GameHandler to the Map: creates a new Game.
     * @param gameId The id of the game that has been created.
     * @param numberOdDeaths The number of deaths of the new game.
     * @param frenzy A boolean that says if the created game will have the final frenzy turn.
     */
    static synchronized void addGameHandler(String gameId, int numberOdDeaths,
            boolean frenzy) {

        map.put(new GameHandler(gameId, numberOdDeaths, frenzy), new HashMap<>());
    }

    /**
     * Checks if a certain GameHandler is present in the maps: checks if a game has already been created.
     * @param gameId The id of the game you're searching.
     * @return A boolean that says if the gameHandler is present.
     */
    static synchronized boolean isGameHandlerPresent(String gameId) {

        return map.keySet().stream().map(GameHandler::getGameId).anyMatch(x -> x.equals(gameId));
    }

    /**
     * Adds a new Key-Value(Player-Presenter) tuple to the map which is value of a certain GameHandler: adds a player to a match.
     * @param gameHandler The GameHandler in which the player has to be added.
     * @param player The player that has to be added.
     * @param presenter The Presenter of the Player that has to be added.
     */
    static synchronized void putPlayerPresenter(GameHandler gameHandler, Player player,
            Presenter presenter) {

        if (map.get(gameHandler).keySet().contains(player)) {

            map.get(gameHandler).replace(player, presenter);
        } else {

            map.get(gameHandler).put(player, presenter);
        }
    }

    /**
     * Checks if a player is present in the map.
     * @param playerId The id of the player that has to be found.
     * @return A boolean that says if the player i present.
     */
    static synchronized boolean isPlayerPresent(String playerId) {

        return map.values().stream()
                .flatMap(x -> x.keySet().stream())
                .map(Player::getPlayerId)
                .anyMatch(x -> x.equals(playerId));
    }

    /**
     * Creates a JsonObject with the information of the games that have been created and the Players in.
     * @return The JsonObject with the information of the games that have been created and the Players in.
     */
    static synchronized JsonObject getGameHandlerJsonArray() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        map.keySet().forEach(x -> builder.add(x.toJsonObject()));

        return Json.createObjectBuilder().add("gameList", builder.build()).build();
    }

    /**
     * Saves a game to the saving file specified in the path string.
     * @param path The path of the saving file.
     */
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

    /**
     * Loads a game from the saving file specified in the path string
     * @param path The path of the saving file.
     */
    public static void load(String path) {

        try (FileInputStream fileStream = new FileInputStream(path);
                ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {

            LOGGER.log(Level.INFO, "Loading from file...");

            map = (HashMap<GameHandler, HashMap<Player, Presenter>>) objectStream.readObject();

            LOGGER.log(Level.INFO, "Loading from file successful.");


        } catch (IOException | ClassNotFoundException e) {

            LOGGER.log(Level.WARNING, "Loading from file unsuccessful.");

            map = new HashMap<>();
        }
    }
}
