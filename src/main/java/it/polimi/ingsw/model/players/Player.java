package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.bridges.Bridge;
import it.polimi.ingsw.model.bridges.PointStructure;
import it.polimi.ingsw.model.bridges.Shots;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.MaxCardException;
import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private String playerId;
    private Color playerColor;

    private int points = 0;
    private Bridge bridge;

    private List<Card> weaponDeck = new ArrayList<>();

    private Square oldPosition;
    private Square currentPosition;


    public Player(String playerId, Color playerColor) {

        this.playerId = playerId;
        this.playerColor = playerColor;

        bridge = new Bridge(playerColor);
    }

    public String getPlayerId() {

        return this.playerId;
    }

    public Color getPlayerColor() {

        return this.playerColor;
    }

    public void setPoints(int points) {

        this.points += points;
    }

    public int getPoints() {

        return this.points;
    }

    public Bridge getBridge() {

        return this.bridge;
    }

    public Square getOldPosition() {

        return this.oldPosition;
    }

    public void setCurrentPosition(Square currentPosition) {

        this.currentPosition = currentPosition;
    }

    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    @Override
    public String toString() {

        return this.playerId;
    }

    public void movePlayer(Square destination) {

        this.oldPosition = this.currentPosition;
        this.currentPosition.removePlayer(this);

        destination.addPlayer(this);
    }

    public void setDamage(Color color, int quantity) {

        this.bridge.setDamage(color, quantity);
    }

    public void setMark(Color color, int quantity) {

        this.bridge.setMark(color, quantity);
    }

    public boolean checkIfdead() {

        return this.bridge.getDamageBridge().checkIfDead();
    }

    public void addCardToHand(Card card) throws CardException {

        if (weaponDeck.size() < 3) {
            weaponDeck.add(card);
        } else {
            throw new MaxCardException("You already have 3 cards in your hand!", card);
        }
    }

    public PointStructure countPoints(List<Shots> shots) {

        int tempPoints = 0;
        int firstShot = 0;
        int lastShot = 0;
        int counter = 0;

        boolean foundFirstShot = false;

        for (Shots shots1 : shots) {
            counter++;

            if (shots1.getColor().equals(this.getPlayerColor())) {
                tempPoints++;
                lastShot = counter;

                if (!foundFirstShot) {
                    firstShot = counter;
                    foundFirstShot = true;
                }
            }
        }

        return new PointStructure(this, tempPoints, firstShot, lastShot);
    }
}
