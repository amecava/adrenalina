package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.server.presenter.exceptions.SpawnException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * This class is responsible of furthering the game. It has the timers and all the logic that deals
 * with the match progress from the very beginning.
 */
public class GameHandler implements Serializable {

    /**
     * The id of the match handled by this GameHandler.
     */
    private String gameId;

    /**
     * An integer used to updated the status of the countdown until the game starts.
     */
    private int gameStarted = 60;

    /**
     * A map that contains the votes of every player who voted for the board to use in this match.
     */
    private Map<String, Integer> votes = new HashMap<>();

    /**
     * The model of the game.
     */
    private Model model;

    /**
     * Turn timer.
     */
    private transient Timer turn;

    /**
     * Creates the GameHandler.
     *
     * @param gameId The id of the game.
     * @param numberOfDeaths The number of deaths this game will have.
     * @param frenzy If the final frenzy turn will be played.
     */
    GameHandler(String gameId, int numberOfDeaths, boolean frenzy) {

        this.gameId = gameId;
        this.model = new Model(numberOfDeaths, frenzy);
    }

    /**
     * Gets the game id.
     *
     * @return The game id.
     */
    String getGameId() {

        return this.gameId;
    }

    /**
     * Checks if this game already started.
     */
    boolean isGameStarted() {

        return this.gameStarted == 0;
    }

    /**
     * Gets the list of players connected to this game.
     *
     * @return The list of Players.
     */
    List<Player> getPlayerList() {

        return this.model.getPlayerList();
    }

    /**
     * Gets the Model of this game.
     *
     * @return The Model of this game.
     */
    Model getModel() {

        return this.model;
    }

    /**
     * Creates the board of the game based on the most voted one, or chooses randomly.
     */
    private void createBoard() {

        this.model.createBoard(this.votes.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Entry::getValue))
                .map(x -> x.getKey() - 1)
                .orElse(ThreadLocalRandom.current().nextInt(0, 4)));
    }

    /**
     * Adds a player to the game and, as soon as there are three players connected, starts the
     * countdown.
     *
     * @param playerId The id of the player that will be added.
     * @param character The character chosen by the player.
     * @return The Player object.
     * @throws LoginException If the id has already been chosen.
     * @throws ColorException If the character has already been chosen.
     */
    Player addPlayer(String playerId, String character) throws LoginException, ColorException {

        Player player = this.model.addPlayer(playerId, character);

        if (this.model.getPlayerList().size() == 3 && this.gameStarted != 0) {

            Thread countdown = new Thread(() -> {

                try {

                    for (int i = 10; i >= 0; i--) {

                        this.gameStarted = i;

                        ClientHandler.gameBroadcast(
                                this, x -> true,
                                "updateGameNotStartedScreen",
                                this.toJsonObject().toString());

                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            });

            countdown.setDaemon(true);
            countdown.start();

            new Timer().schedule(

                    new TimerTask() {
                        @Override
                        public void run() {

                            startGame();
                        }
                    },
                    12000
            );
        }

        return player;
    }

    /**
     * Updates the votes map with the vote of the player playerId.
     *
     * @param playerId The id of the player who's voting.
     * @param board The int of the board voted by the player.
     * @throws BoardVoteException If the player already voted.
     */
    void voteBoard(String playerId, int board) throws BoardVoteException {

        if (this.votes.containsKey(playerId)) {

            throw new BoardVoteException("Hai già votato per questa partita.");
        }

        this.votes.put(playerId, board);
    }

    /**
     * Makes the player spawn automatically if he doesn't respect the timer.
     *
     * @param player The Player that has to spawn.
     */
    private void randomSpawn(Player player) {

        try {

            PowerUpCard powerUp = player.getPowerUpsList().get(0);

            this.spawnPlayer(player, powerUp.getName(), powerUp.getColor().toString());

        } catch (SpawnException e) {

            //
        }
    }

    /**
     * Performs the "spawn" action. The player spawns in the Square colored "colorName".
     *
     * @param player The Player that has to spawn.
     * @param cardId The name of the PowerUp the player wants to discard in order to spawn.
     * @param colorName The name of the color of the power up the player wants to discard in order
     * to spawn.
     * @throws SpawnException If there are some problems in the information the player chose to
     * spawn.
     */
    void spawnPlayer(Player player, String cardId, String colorName) throws SpawnException {

        try {

            Color color = Color.ofName(colorName);

            if (color == null) {

                throw new ColorException("Il colore selezionato non esiste.");
            }

            Square destination = this.model.getBoard().findSpawn(color);

            this.model.getBoard()
                    .getPowerUpDeck()
                    .addPowerUpCard(player.spawn(cardId, color));

            player.movePlayer(destination);

            synchronized (this) {

                if (player.isRespawn()) {

                    player.setRespawn(false);
                }

                if (player.isActivePlayer()) {

                    this.model.getEffectHandler().setActivePlayer(player);
                }

                notifyAll();
            }

        } catch (IllegalActionException | CardNotFoundException | SquareException | ColorException e) {

            throw new SpawnException(e.getMessage());
        }
    }

    /**
     * Furthers the game by setting the active player to the next one of the playersList and updates
     * states of every player.
     */
    private synchronized void nextPlayer() {

        this.model.nextPlayer();

        ClientHandler.gameBroadcast(this, x -> x.getKey().isActivePlayer(), "infoMessage",
                "È il tuo turno!");

        ClientHandler.gameBroadcast(this,
                x -> x.getKey().isActivePlayer() && x.getKey().getCurrentPosition() == null,
                "updateState",
                Presenter.state.get("spawnState").toString());

        ClientHandler.gameBroadcast(this,
                x -> x.getKey().isActivePlayer() && x.getKey().getCurrentPosition() != null,
                "updateState",
                StateHandler.createActivePlayerState(this.model.getActivePlayer(),
                        Presenter.state.get("activePlayerState")).toString());

        ClientHandler.gameBroadcast(this, x -> !x.getKey().isActivePlayer(), "updateState",
                Presenter.state.get("notActivePlayerState").toString());

        turn = new Timer();

        turn.schedule(new TimerTask() {
            @Override
            public void run() {

                synchronized (this) {

                    endOfTurn();
                }
            }
        }, 180000);
    }

    /**
     * Performs the "endOfTurn" action, checks if there are players that need to respawn. If there
     * are, waits for them to respawn (either automatically on not). It also catches the
     * EndGameException, and when it does so it starts the end game process.
     */
    void endOfTurn() {

        synchronized (this) {

            if (turn != null) {

                turn.cancel();
            }

            try {

                this.model.endOfTurn();

                Timer spawn = new Timer();

                Thread thread = new Thread(() -> {

                    try {
                        synchronized (GameHandler.this) {

                            while (this.model.getPlayerList().stream()
                                    .anyMatch(Player::isRespawn)) {

                                ClientHandler
                                        .gameBroadcast(GameHandler.this,
                                                x -> x.getKey().isRespawn(),
                                                "infoMessage",
                                                "Devi fare il respawn.");

                                ClientHandler
                                        .gameBroadcast(GameHandler.this,
                                                x -> x.getKey().isRespawn(),
                                                "updateState",
                                                Presenter.state.get("spawnState").toString());

                                ClientHandler
                                        .gameBroadcast(GameHandler.this,
                                                x -> !x.getKey().isRespawn(),
                                                "updateState",
                                                Presenter.state.get("notActivePlayerState")
                                                        .toString());

                                GameHandler.this.wait();
                            }

                            spawn.cancel();

                            GameHandler.this.nextPlayer();

                            ClientHandler.gameBroadcast(this, x -> true, "updateBoard",
                                    this.toJsonObject().toString());

                        }
                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }

                });

                spawn.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        synchronized (GameHandler.this) {

                            model.getPlayerList().stream()
                                    .filter(Player::isRespawn)
                                    .forEach(GameHandler.this::randomSpawn);

                        }
                    }
                }, 10000);

                thread.start();

            } catch (EndGameException e) {

                JsonArrayBuilder array = Json.createArrayBuilder();

                e.getWinner().stream()
                        .flatMap(Collection::stream)
                        .forEach(x ->

                                array.add(
                                        Json.createObjectBuilder()
                                                .add("playerId", x.getPlayerId())
                                                .add("character", x.getColor().getCharacter())
                                                .add("points", x.getPoints())
                                                .build())
                        );

                ClientHandler.gameBroadcast(
                        this,
                        x -> true,
                        "endGameScreen",
                        Json.createObjectBuilder().add("array", array.build()).build().toString());
            }
        }
    }

    /**
     * Initializes the game by creating the board and the playersList.
     */
    private void startGame() {

        this.createBoard();
        this.model.getBoard().fillBoard();

        this.model.getPlayerList().forEach(x ->
                x.addPowerUp(this.model.getBoard().getPowerUp()));
        this.model.getPlayerList().forEach(x ->
                x.addPowerUp(this.model.getBoard().getPowerUp()));

        this.nextPlayer();

        ClientHandler.gameBroadcast(this, x -> true, "updateBoard",
                this.toJsonObject().toString());
    }


    /**
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be
     * sent to the view when needed.
     *
     * @return The JsonObject containing all the information of this card.
     */
    public JsonObject toJsonObject() {

        return this.model.toJsonObject()
                .add("gameId", this.gameId)
                .add("countdown", this.gameStarted)
                .add("gameStarted", this.isGameStarted())
                .build();
    }
}
