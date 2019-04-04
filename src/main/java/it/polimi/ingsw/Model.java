package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private CardHandler cardHandler;
    private List<Player> playerList;
    private Card currentCard;
    private Board board;
    private Player activePlayer;


    public Model() {
        this.playerList = new ArrayList<Player>();
    }

    public void buildBoard(){
        this.board = new Board();
        this.board.addRoomsList(new Room("Blue"));
        this.board.addRoomsList(new Room("Red"));
        this.board.addRoomsList(new Room("Yellow"));
        this.board.addRoomsList(new Room("Green"));
    }

    public void initCardHandler(){

        this.cardHandler = new CardHandler(this.board);
        for(Player p: playerList){
            p.setCardHandler(this.cardHandler);
        }
    }

    /*
    public void setActivePlayer(Player p){

        this.activePlayer = p;
    }

    public void giveCardToPlayer(){
        this.activePlayer.addCardToDeck(new WeaponCard("Machine Gun", "Blue"));
    }
    */

    public void setPlayerList(List<Player> playersList){
        this.playerList = playersList;
        this.board.setPlayersList(playerList);
    }

    public void displayPlayers(){

        for(Player p: this.playerList){
            System.out.println("Ciao " + p.getPlayerID());
        }
        this.playerList.stream().forEach(System.out::println);
    }
}
