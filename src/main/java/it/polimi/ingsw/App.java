package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.bridges.DamageBridge;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.Presenter;
import java.util.ArrayList;
import java.util.List;

public class App {

    private Presenter presenter;


    public App() {
        this.presenter = new Presenter();
    }


    public static void main(String[] args) {

        App game = new App();
        List<Player> players = new ArrayList<>();

        Player playerOne = new Player("Jacop", Color.VIOLET);
        Player playerTwo = new Player("Amedeo", Color.GRAY);
        Player playerThree = new Player("Federico", Color.GREEN);

        players.add(playerOne);
        players.add(playerTwo);
        players.add(playerThree);

        game.presenter = new Presenter();
        game.presenter.buildBoard();
        game.presenter.setPlayersList(players);
        game.presenter.initCardHandler();


    }
}
