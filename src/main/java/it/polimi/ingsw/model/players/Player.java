package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.bridges.Bridge;
import it.polimi.ingsw.model.bridges.DamageBridge;
import it.polimi.ingsw.model.bridges.PointStructure;
import it.polimi.ingsw.model.bridges.Shots;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardHandler;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.MaxCardException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Player implements Target {

    private String playerID;
    private CardHandler cardHandler;
    private List<Card> weaponDeck;
    private Color playerColor;
    private Square currentPosition;
    private int points;
    private Bridge bridge = new Bridge();

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    public void setPoints(int points) {
        this.points=this.points+ points;
    }


    public CardHandler getCardHandler() {
        return cardHandler;
    }

    public int getPoints() {
        return points;
    }


    public Player(String playerID, Color playerColor) {

        this.playerID = playerID;
        this.playerColor = playerColor;
        this.weaponDeck = new ArrayList<>();
        this.points = 0;
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


    public void addCardToHand(Card card) throws CardException {
        if (weaponDeck.size() < 3) {
            weaponDeck.add(card);
        } else {
            throw new MaxCardException("You already have 3 cards in your hand!", card);
        }

    }

    public Color getPlayerColor() {
        return this.playerColor;
    }

    public void movePlayer(Square destination) {

        this.getCurrentPosition().removePlayer(this);
        destination.addPlayer(this);
    }

    public void setDamage(Player enemy, int quantity) {
        this.bridge.setDamage(enemy.getPlayerColor(), quantity);
        System.out.println("Damaged by " + enemy.getPlayerID());
        System.out.println("Enemy color " + enemy.getPlayerColor());
        System.out.println("Quantity " + quantity);
    }
    public void setMark(Player enemy, int quantity) {
        this.bridge.setMarker(enemy.getPlayerColor(),quantity);
        System.out.println("Marked by " + enemy.getPlayerID());
        System.out.println("Enemy color " + enemy.getPlayerColor());
        System.out.println("Quantity " + quantity);
    }
    public void setMark(Color color , int quantity) {
        this.bridge.setMarker(color ,quantity);
    }

    public PointStructure countPoints(List<Shots> shots )  {
        int tempPoints=0;
        int firstShot=0;
        int lastShot=0;
        int counter=0;
        boolean foundFirstShot=false;
        for (Shots shots1 : shots) {
            counter++;
            if (shots1.getColor().equals(this.getPlayerColor())) {
                tempPoints++;
                lastShot=counter;
                if (!foundFirstShot) {
                    firstShot = counter;
                    foundFirstShot = true;
                }
            }
        }
        return new PointStructure(this, tempPoints,firstShot, lastShot);


    }

}
