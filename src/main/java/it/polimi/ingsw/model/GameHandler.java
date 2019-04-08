package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardBuilder;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.bridges.DamageBridge;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameHandler {

    private CardHandler cardHandler;
    private List<Player> playerList;
    private Card currentCard;
    private BoardBuilder boardBuilder = new BoardBuilder();
    private Player activePlayer;
    private List<Square> tmpList;
    private Room tmpRoom;
    private Board board = new Board();


    public GameHandler() {
        this.playerList = new ArrayList<Player>();
        this.tmpList = new ArrayList<>();
    }

    public void buildBoard(int boardID) {
        this.board = boardBuilder.buildBoard(boardID);
    }

    public Board getBoard() {
        return this.boardBuilder.getBoard();
    }

    public void initCardHandler() {

        this.cardHandler = new CardHandler();
        for (Player p : playerList) {
            p.setCardHandler(this.cardHandler);
        }
    }

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
