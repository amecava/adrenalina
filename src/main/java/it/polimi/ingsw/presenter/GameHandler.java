package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.ClientHandler;
import it.polimi.ingsw.server.presenter.Presenter;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

public class GameHandler {

    private String gameId;
    private boolean gameStarted = false;

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

        return this.gameStarted;
    }

    public List<Player> getPlayerList() {

        return this.model.getPlayerList();
    }

    public void createBoard() {

        this.model.createBoard(this.votes.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Entry::getValue))
                .map(x -> x.getKey() - 1)
                .orElse(ThreadLocalRandom.current().nextInt(0, 4)));
    }

    public Player addPlayer(String playerId, String character) throws LoginException {

        Player player = this.model.addPlayer(playerId, character);

        if (this.model.getPlayerList().size() == 3) {

            new Timer().schedule(

                    new TimerTask() {
                        @Override
                        public void run() {

                            startGame();
                        }
                    },
                    60000
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

    public Player getActivePlayer() {

        return this.model.getActivePlayer();
    }

    public synchronized void setActivePlayer(Player activePlayer) {

        this.model.setActivePlayer(activePlayer);
    }

    public void endOfTurn() throws EndGameException {

        this.model.endOfTurn();
    }

    public void startGame() {

        //TODO Random int

        this.createBoard();

        this.gameStarted = true;
        this.model.startGame();

        ClientHandler.gameBroadcast(this, x -> true, "showBoard",
                this.model.getBoard().toJsonObject().toString());
    }

    public synchronized void canFinishTurn(Player activePlayer) {

        long elapsedTime = 0;
        long remainingTimeToWait = 20000;
        LocalDateTime startingTime;
        long oldNumberOfPlayerInRespawn;
        ClientHandler.gameBroadcast(this,
                x -> x.getKey().isRespawn() || (x.getKey().isActivePlayer()
                        && x.getKey().getCurrentPosition() == null), "infoMessage",
                "Devi fare il respawn, utilizza il comando spawn nomePowerUp colorePowerUp.");
        while (this.numberOfPlayerInRespawn() > 0) {
            try {
                oldNumberOfPlayerInRespawn = this.numberOfPlayerInRespawn();
                startingTime = LocalDateTime.now();

                if (remainingTimeToWait > 0) {
                    this.wait(remainingTimeToWait);
                }
                if (oldNumberOfPlayerInRespawn > this
                        .numberOfPlayerInRespawn()) {

                    elapsedTime = Duration.between(startingTime, LocalDateTime.now()).toMillis();
                    remainingTimeToWait = remainingTimeToWait - elapsedTime;

                } else {

                    this.model.getPlayerList().stream()
                            .filter(x -> x.isRespawn() || (x.isActivePlayer()
                                    && x.getCurrentPosition() == null)).forEach(x -> {

                        Presenter presenter = ClientHandler
                                .getPresenter(this, y -> y.getKey().equals(x));

                        //presenter.spawn("random");
                        //presenter needs to see if it's called with the keyWord random

                        ClientHandler.gameBroadcast(this, y -> y.getKey().equals(x), "infoMessage",
                                "Sei stato respawnato in modo casuale per via del timer scaduto.");


                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setActivePlayer(activePlayer);
    }

    public int numberOfPlayerInRespawn() {

        return (int) this.model.getPlayerList().stream()
                .filter(x -> x.isRespawn() || (x.isActivePlayer()
                        && x.getCurrentPosition() == null)).count();

    }


    public JsonObject toJsonObject() {

        return this.model.toJsonObject()
                .add("gameId", this.gameId)
                .build();
    }
}
