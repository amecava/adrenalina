package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
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

        game.presenter.buildBoard();
    }
}
