package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class Presenter {

    private GameHandler gameHandler;
    //private View view;
    private int boardID;


    public Presenter() {
        this.gameHandler = new GameHandler();
        //this.view = new View();
    }

    public void buildBoard() {
        this.boardID = 0;
        this.gameHandler.buildBoard(boardID);

    }

    public void initCardHandler() {
        this.gameHandler.initCardHandler();
    }

    public void setPlayersList(List<Player> list) {
        this.gameHandler.setPlayerList(list);
    }


    public void displayPlayers() {
        this.gameHandler.displayPlayers();
    }
}
