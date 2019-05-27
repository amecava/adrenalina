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

public class GameHandler {

    private String gameId;
    private int gameStarted = 60;

    private Map<String, Integer> votes = new HashMap<>();

    private Model model;

    public GameHandler(String gameId, int numberOfDeaths, boolean frenzy) {

        this.gameId = gameId;
        this.model = new Model(numberOfDeaths, frenzy);
    }

    public String getGameId() {

        return this.gameId;
    }

    public boolean isGameStarted() {

        return this.gameStarted == 0;
    }

    public List<Player> getPlayerList() {

        return this.model.getPlayerList();
    }

    public Model getModel() {

        return this.model;
    }

    public void createBoard() {

        this.model.createBoard(this.votes.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Entry::getValue))
                .map(x -> x.getKey() - 1)
                .orElse(ThreadLocalRandom.current().nextInt(0, 4)));
    }

    public Player addPlayer(String playerId, String character) throws LoginException, ColorException {

        Player player = this.model.addPlayer(playerId, character);

        if (this.model.getPlayerList().size() == 3 && this.gameStarted != 0) {

            Thread countdown = new Thread(() -> {

                try {

                    for (int i = 60; i >= 0; i--) {

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
                    62000
            );
        }

        return player;
    }

    public void voteBoard(String playerId, int board) throws BoardVoteException {

        if (this.votes.containsKey(playerId)) {

            throw new BoardVoteException("Hai già votato per questa partita.");
        }

        this.votes.put(playerId, board);
    }

    public void randomSpawn(Player player) {

        try {

            PowerUpCard powerUp = player.getPowerUpsList().get(0);

            this.spawnPlayer(player, powerUp.getName(), powerUp.getColor().toString());

        } catch (SpawnException e) {

            //
        }
    }

    public void spawnPlayer(Player player, String cardId, String colorName) throws SpawnException {

        try {

            Color color = Color.ofName(colorName);

            Square destination = this.model.getBoard().findSpawn(color);

            this.model.getBoard()
                    .getPowerUpDeck()
                    .addPowerUpCard(player.spawn(cardId, color));

            player.movePlayer(destination);

            synchronized (this) {

                if (player.isRespawn()) {

                    player.setRespawn(false);
                }

                notifyAll();
            }

        } catch (IllegalActionException | CardNotFoundException | SquareException | ColorException e) {

            throw new SpawnException(e.getMessage());
        }
    }

    public synchronized void nextPlayer() {

        this.model.nextPlayer();

        ClientHandler.gameBroadcast(this, x -> x.getKey().isActivePlayer(), "infoMessage",
                "È il tuo turno, digita aiuto per vedere la lista di comandi.");
    }

    public void endOfTurn() throws EndGameException {

        this.model.endOfTurn();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                synchronized (GameHandler.this) {

                    model.getPlayerList().stream()
                            .filter(Player::isRespawn)
                            .forEach(GameHandler.this::randomSpawn);
                }
            }
        }, 60000);

        new Thread(() -> {

            try {
                synchronized (GameHandler.this) {

                    while (this.model.getPlayerList().stream().anyMatch(Player::isRespawn)) {

                        ClientHandler.gameBroadcast(GameHandler.this, x -> x.getKey().isRespawn(),
                                "infoMessage",
                                "Devi fare il respawn.");

                        GameHandler.this.wait();
                    }

                    timer.cancel();

                    GameHandler.this.nextPlayer();
                }
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

        }).start();
    }

    public void startGame() {

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
                .build();
    }
}
