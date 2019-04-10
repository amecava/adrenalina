package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class Presenter {

    private GameHandler gameHandler;

    public Presenter() {

        this.gameHandler = new GameHandler();
    }

    public void buildBoard() {

        this.gameHandler.setBoard(0);
    }

    public void setPlayersList(List<Player> list) {

        this.gameHandler.setPlayerList(list);
    }
}
