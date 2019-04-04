package it.polimi.ingsw;

//import it.polimi.ingsw.View.View;

import java.util.List;

public class Controller {

    private GameHandler gameHandler;
    //private View view;

    public Controller() {
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
