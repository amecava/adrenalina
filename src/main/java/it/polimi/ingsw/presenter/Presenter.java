package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class Presenter {

    private GameHandler gameHandler;
    //private View view;

    public Presenter() {
        this.gameHandler = new GameHandler();
        //this.view = new View();
    }

    public void buildBoard() {
        this.gameHandler.buildBoard();
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
