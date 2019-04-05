package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    private CardHandler cardHandler;
    private List<Player> playerList;
    private Card currentCard;
    private Board board;
    private Player activePlayer;
    private List<Square> tmpList;
    private Room tmpRoom;

    public GameHandler() {
        this.playerList = new ArrayList<Player>();
        this.tmpList = new ArrayList<>();
    }

    public void buildBoard() {

        this.board = new Board();
        this.board.addRooms(tmpRoom = new Room(Color.BLUE));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.tmpList.add(new Square(tmpRoom, 3));
        this.board.getRoomsList().get(0).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room(Color.RED));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.tmpList.add(new Square(tmpRoom, 3));
        this.board.getRoomsList().get(1).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room(Color.YELLOW));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.board.getRoomsList().get(2).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.addRooms(tmpRoom = new Room(Color.WHITE));
        this.tmpList.add(new Square(tmpRoom, 1));
        this.tmpList.add(new Square(tmpRoom, 2));
        this.board.getRoomsList().get(3).addSquaresList(tmpList);
        this.tmpList.clear();
        this.board.connectSquares();
    }

    public void initCardHandler() {

        this.cardHandler = new CardHandler();
        for (Player p : playerList) {
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

    public void setPlayerList(List<Player> playersList) {
        this.playerList = playersList;
    }

    public void displayPlayers() {

        for (Player p : this.playerList) {
            System.out.println("Ciao " + p.getPlayerID());
        }
        this.playerList.stream().forEach(System.out::println);
    }
}
