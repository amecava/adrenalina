package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private CardHandler cardHandler;
    private List<Player> playerList;
    private Card currentCard;
    private Board board;
    private Player activePlayer;
    private List<Square> tmpList;
    private Room tmpRoom;
    public Model() {
        this.playerList = new ArrayList<Player>();
        this.tmpList = new ArrayList<>();
    }

    public void buildBoard(){

        this.board = new Board();
        this.board.addRooms(tmpRoom = new Room("Blue"));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.tmpList.add(new Square(tmpRoom, 3));
        this.board.getRoomsList().get(0).addSquaresList(tmpList);
        // per ogni squares e
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room("Red"));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.tmpList.add(new Square(tmpRoom, 3));
        this.board.getRoomsList().get(1).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room("Yellow"));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.board.getRoomsList().get(2).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room("White"));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.board.getRoomsList().get(3).addSquaresList(tmpList);
        this.tmpList.clear();

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
