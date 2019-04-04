package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private String playerID;
    private CardHandler cardHandler;
    private List<Card> weaponDeck;
    private Color playerColor;
    private  Square currentPosition;

    public Player(String playerID, Color playerColor) {

        this.playerID = playerID;
        this.playerColor = playerColor;
        this.weaponDeck = new ArrayList<Card>();
    }

    public void setCurrentPosition(Square currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Square getCurrentPosition() {
        return currentPosition;
    }

    public void setCardHandler(CardHandler cardHandler) {
        this.cardHandler = cardHandler;
        System.out.println(cardHandler);
    }

    public String getPlayerID() {
        return this.playerID;
    }


    public void addCardToHand(Card card) throws MaxCardException {
        if (weaponDeck.size() < 3) {
            weaponDeck.add(card);
            return;
        } else {
            throw new MaxCardException("You already have 3 cards in your hand!!", card);
        }

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
