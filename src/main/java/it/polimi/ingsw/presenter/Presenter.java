package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class Presenter {

    private GameHandler gameHandler;
    private int boardID;

    public Presenter() {

        this.gameHandler = new GameHandler();
    }

    public void buildBoard() {

        this.boardID = 0;
        this.gameHandler.buildBoard(boardID);
    }

    public void setPlayersList(List<Player> list) {

        this.gameHandler.setPlayerList(list);
    }

    public void displayPlayers() {

        this.gameHandler.displayPlayers();
    }
}
