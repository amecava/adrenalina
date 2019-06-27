package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.client.Client;
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
import javax.json.JsonObject;

public class GameHandler implements Serializable {

    private String gameId;
    private int gameStarted = 60;

    private Map<String, Integer> votes = new HashMap<>();

    private Model model;

    GameHandler(String gameId, int numberOfDeaths, boolean frenzy) {

        this.gameId = gameId;
        this.model = new Model(numberOfDeaths, frenzy);
    }

    String getGameId() {

        return this.gameId;
    }

    boolean isGameStarted() {

        return this.gameStarted == 0;
    }

    List<Player> getPlayerList() {

        return this.model.getPlayerList();
    }

    Model getModel() {

        return this.model;
    }

    private void createBoard() {

        this.model.createBoard(this.votes.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Entry::getValue))
                .map(x -> x.getKey() - 1)
                .orElse(ThreadLocalRandom.current().nextInt(0, 4)));
    }

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

    void voteBoard(String playerId, int board) throws BoardVoteException {

        if (this.votes.containsKey(playerId)) {

            throw new BoardVoteException("Hai già votato per questa partita.");
        }

        this.votes.put(playerId, board);
    }

    private void randomSpawn(Player player) {

        try {

            PowerUpCard powerUp = player.getPowerUpsList().get(0);

            this.spawnPlayer(player, powerUp.getName(), powerUp.getColor().toString());

        } catch (SpawnException e) {

            //
        }
    }

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

    private synchronized void nextPlayer() {

        this.model.nextPlayer();

        ClientHandler.gameBroadcast(this, x -> x.getKey().isActivePlayer(), "infoMessage",
                "È il tuo turno, digita aiuto per vedere la lista di comandi.");

        ClientHandler.gameBroadcast(this, x -> x.getKey().isActivePlayer() && x.getKey().getCurrentPosition() == null, "updateState",
                Presenter.state.get("spawnState").toString());

        ClientHandler.gameBroadcast(this, x -> x.getKey().isActivePlayer() && x.getKey().getCurrentPosition() != null, "updateState",
                StateHandler.createActivePlayerState(this.model.getActivePlayer(), Presenter.state.get("activePlayerState")).toString());

        ClientHandler.gameBroadcast(this, x -> !x.getKey().isActivePlayer(), "updateState",
                Presenter.state.get("notActivePlayerState").toString());
    }

    void endOfTurn() throws EndGameException {

        this.model.endOfTurn();

        Timer timer = new Timer();

        Thread thread = new Thread(() -> {

            try {
                synchronized (GameHandler.this) {

                    while (this.model.getPlayerList().stream().anyMatch(Player::isRespawn)) {

                        ClientHandler.gameBroadcast(GameHandler.this, x -> x.getKey().isRespawn(),
                                "infoMessage",
                                "Devi fare il respawn.");

                        ClientHandler.gameBroadcast(GameHandler.this, x -> x.getKey().isRespawn(), "updateState",
                                Presenter.state.get("spawnState").toString());

                        ClientHandler.gameBroadcast(GameHandler.this, x -> !x.getKey().isRespawn(), "updateState",
                                Presenter.state.get("notActivePlayerState").toString());

                        GameHandler.this.wait();
                    }

                    timer.cancel();

                    GameHandler.this.nextPlayer();

                    ClientHandler.gameBroadcast(this, x -> true, "updateBoard", this.toJsonObject().toString());

                }
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

        });

        timer.schedule(new TimerTask() {
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
    }

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

    public JsonObject toJsonObject() {

        return this.model.toJsonObject()
                .add("gameId", this.gameId)
                .add("countdown", this.gameStarted)
                .add("gameStarted", this.isGameStarted())
                .build();
    }
}
