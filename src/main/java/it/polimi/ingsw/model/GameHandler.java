package it.polimi.ingsw.model;

import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import it.polimi.ingsw.presenter.ClientHandler;
import it.polimi.ingsw.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.presenter.exceptions.LoginException;
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

    private List<Player> playerList = new ArrayList<>();

    private Board board;
    private Map<Player, Integer> votes = new HashMap<>();

    private PointHandler pointHandler;
    private EffectHandler effectHandler = new EffectHandler();

    private Player activePlayer;

    public GameHandler(String gameId, int numberOfDeaths, boolean frenzy) {

        this.gameId = gameId;

        this.pointHandler = new PointHandler(this.playerList, numberOfDeaths);
        this.pointHandler.setFrenzy(frenzy);
    }

    public String getGameId() {

        return this.gameId;
    }

    public boolean isGameStarted() {

        return this.gameStarted;
    }

    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public void createBoard() {

        System.out.println(this.votes.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Entry::getValue))
                .map(x -> x.getKey() - 1));

        this.board = new Board.BoardBuilder(this.effectHandler).build(
                this.votes.values().stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream()
                        .max(Comparator.comparing(Entry::getValue))
                        .map(x -> x.getKey() - 1)
                        .orElse(ThreadLocalRandom.current().nextInt(0, 4)));
    }

    public Player addPlayer(String playerId, String character) throws LoginException {

        if (Color.ofCharacter(character) == null) {

            throw new LoginException("Il personaggio selezionato non esiste.");
        }

        if (this.playerList.stream()
                .anyMatch(x -> x.getColor().equals(Color.ofCharacter(character)))) {

            throw new LoginException("Il personaggio selezionato è già stato preso.");
        }

        Player player = new Player(playerId, Color.ofCharacter(character));

        this.playerList.add(player);

        if (this.playerList.size() == 3) {

            new Timer().schedule(

                    new TimerTask() {
                        @Override
                        public void run() {

                            ClientHandler.gameBroadcast(x -> true, GameHandler.this, "infoMessage", "La partita è iniziata!");

                            startGame();
                        }
                    },
                    60000
            );
        }

        return player;
    }

    public void voteBoard(Player player, int board) throws BoardVoteException {

        if (this.votes.containsKey(player)) {

            throw new BoardVoteException("Hai già votato per questa partita.");
        }

        this.votes.put(player, board);
    }

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    public synchronized void setActivePlayer(Player activePlayer) {

        while (this.playerList.stream().anyMatch(
                x -> x.isRespawn() || (x.isActivePlayer() && x.getCurrentPosition() == null))) {
            try {
                this.wait();
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
        if (this.activePlayer != null) {
            this.activePlayer.setActivePlayer(false);
        }
        this.activePlayer = activePlayer;
        this.activePlayer.setActivePlayer(true);

        this.effectHandler.setActivePlayer(activePlayer);

        if (this.activePlayer.getAdrenalin().equals(Adrenalin.SECONDFRENZY)) {

            this.activePlayer.setRemainingActions(1);
        } else {

            this.activePlayer.setRemainingActions(2);
        }
    }

    public void endOfTurn() throws EndGameException {

        this.activePlayer.endAction();
        this.activePlayer.setRemainingActions(-1);
        this.playerList.stream()
                .filter(Player::isDead)
                .forEach(x -> {
                    x.removePlayerFromBoard();
                    x.addPowerUp(this.board.getPowerUp());
                });

        this.pointHandler.checkIfDead();
        this.pointHandler.countKills();

        if (this.pointHandler.checkEndGame()) {

            throw new EndGameException(this.pointHandler.endGame());
        }

        this.board.fillBoard();
    }

    public void startGame() {

        //TODO Random int

        this.createBoard();

        this.gameStarted = true;
        this.playerList.forEach(x ->
                x.addPowerUp(this.board.getPowerUp()));
        this.playerList.forEach(x ->
                x.addPowerUp(this.board.getPowerUp()));
        this.playerList.get(0).setFirstPlayer(true);
        this.setActivePlayer(this.playerList.get(0));

        ClientHandler.gameBroadcast(x -> true, this, "showBoard", this.board.toJsonObject().toString());
    }

    public JsonObject toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.playerList.stream()
                .map(Player::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("gameId", this.gameId)
                .add("numberOfDeaths", this.pointHandler.getNumberOfDeaths())
                .add("frenzy", this.pointHandler.isFrenzy())
                .add("playerList", builder.build())
                .build();
    }
}
