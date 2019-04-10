package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    private Board board;

    private Player activePlayer;
    private List<Player> playerList = new ArrayList<>();

    public GameHandler() {
    }

    public void setBoard(int boardID) {

        this.board = new Board.BoardBuilder(boardID).build();
    }

    public void setPlayerList(List<Player> playersList) {

        this.playerList = playersList;
    }
}
