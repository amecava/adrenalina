package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    private Board board;
    private EffectHandler effectHandler = new EffectHandler();

    private Player activePlayer;
    private List<Player> playerList = new ArrayList<>();

    public GameHandler() {
    }

    public void setBoard(int boardID) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(boardID);
        this.board.fill();
    }

    public void setPlayerList(List<Player> playersList) {

        this.playerList = playersList;
    }
}
