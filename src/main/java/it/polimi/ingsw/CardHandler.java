package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class CardHandler {

    private Player activePlayer;
    private Square activeSquare;

    private Card card;
    private Board board;

    private List<Player> active = new ArrayList<>();
    private List<Player> inactive = new ArrayList<>();

    public CardHandler(Board board) {
        this.board = board;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void setCard(Card card) {
        this.card = card;
    }

}
