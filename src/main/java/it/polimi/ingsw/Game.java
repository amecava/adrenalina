package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Controller controller;
    private List<Player> players;


    public Game() {
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player p){
        this.players.add(p);
    }

   /* public static void main(String[] args) {

        Game game = new Game();


        Player playerOne = new Player("jacop");
        Player playerTwo = new Player("Amedeo");
        Player playerThree = new Player("Federico");


        game.addPlayer(playerOne);
        game.addPlayer(playerTwo);
        game.addPlayer(playerThree);

        game.controller = new Controller();
        game.controller.buildBoard();
        game.controller.setPlayersList(game.players);
        game.controller.displayPlayers();
        game.controller.initCardHandler();
    }
    */
}
