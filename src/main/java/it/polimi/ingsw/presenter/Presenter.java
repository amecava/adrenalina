package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.exceptions.FileException;
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
        try{

            this.gameHandler.buildBoard(boardID);
        } catch (FileException e){
            e.printStackTrace();
        }
    }

    public void setPlayersList(List<Player> list) {

        this.gameHandler.setPlayerList(list);
    }

    public void displayPlayers() {

        this.gameHandler.displayPlayers();
    }
}
