package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player implements Target{
    private String playerID;
    private CardHandler cardHandler;
    private List<Card> weaponDeck;
    private Color playerColor;

    public Player(String playerID, Color playerColor) {

        this.playerID = playerID;
        this.playerColor = playerColor;
        this.weaponDeck = new ArrayList<Card>();
    }

    public void setCardHandler(CardHandler cardHandler){
        this.cardHandler = cardHandler;
        System.out.println(cardHandler);
    }

    public String getPlayerID() {
        return playerID;
    }

    public void addCardToDeck(Card card){
        this.weaponDeck.add(card);
    }

    public Color getPlayerColor() {
        return this.playerColor;
    }

    public void movePlayer(Square destination) {
        System.out.println("Player moved!");
    }

    public void setDamage(Player enemy, int quantity) {
        System.out.println("Damaged by" + enemy.getPlayerID());
        System.out.println("Enemy color" + enemy.getPlayerColor());
        System.out.println("Quantity" + quantity);
    }

    public void setMark(Player enemy, int quantity) {
        System.out.println("Marked by" + enemy.getPlayerID());
        System.out.println("Enemy color" + enemy.getPlayerColor());
        System.out.println("Quantity" + quantity);
    }
}
