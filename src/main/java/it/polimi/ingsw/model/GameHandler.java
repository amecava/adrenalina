package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ammo.Color;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    private String gameId;

    private List<Player> playerList = new ArrayList<>();

    public GameHandler(String gameId) {

        this.gameId = gameId;
    }

    public String getGameId() {

        return this.gameId;
    }

    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public Player addPlayer(String playerId, String character) throws LoginException {

        if (this.playerList.stream().anyMatch(x -> x.getPlayerId().equals(playerId))) {

            throw new LoginException("PlayerId already used.");
        }

        Color color;

        switch(character) {

            case "sprog":

                color = Color.GRAY;
                break;

            default:

                throw new LoginException("Character doesn't exists.");
        }

        if (this.playerList.stream().anyMatch(x -> x.getColor().equals(color))) {

            throw new LoginException("Character already used.");
        }

        Player player = new Player(playerId, color);

        this.playerList.add(player);

        return player;
    }
}
