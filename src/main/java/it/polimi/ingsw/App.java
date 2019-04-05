package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.Presenter;
import java.util.ArrayList;
import java.util.List;

public class App {

    private Presenter presenter;
    private List<Player> players;

    public App() {
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public static void main(String[] args) {

        App game = new App();

        Player playerOne = new Player("Jacop", Color.VIOLET);
        Player playerTwo = new Player("Amedeo", Color.GRAY);
        Player playerThree = new Player("Federico", Color.GREEN);

        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
        game.addPlayer(playerThree);

        /*
        game.presenter = new Presenter();
        game.presenter.buildBoard();
        game.presenter.setPlayersList(App.players);
        game.presenter.displayPlayers();
        game.presenter.initCardHandler();
        */
    }
}
