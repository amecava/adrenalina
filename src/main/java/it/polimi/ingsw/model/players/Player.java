package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.bridges.Shots;
import it.polimi.ingsw.model.points.PointStructure;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.MaxCardException;
import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private String playerId;
    private Color playerColor;

    private Bridge bridge;
    private PointStructure pointStructure;

    private List<Card> weaponHand = new ArrayList<>();

    public Player(String playerId, Color playerColor) {

        this.playerId = playerId;
        this.playerColor = playerColor;
        this.bridge = new Bridge(playerColor);
        this.pointStructure = new PointStructure(this);
    }

    public String getPlayerId() {

        return this.playerId;
    }

    public Color getPlayerColor() {

        return this.playerColor;
    }

    public Bridge getBridge() {

        return this.bridge;
    }

    public Card getWeaponCard(int index) {

        return this.weaponHand.get(index);
    }

    public void setWeaponHand(List<Card> weaponHand) {

        this.weaponHand = weaponHand;
    }

    public void addCardToHand(Card card) throws CardException {

        if (weaponHand.size() < 3) {
            weaponHand.add(card);
        } else {

            throw new MaxCardException("You already have 3 cards in your hand!", card);
        }
    }

    public int getPoints() {

        return this.bridge.getPoints();
    }

    public void setPoints(int points) {

        this.bridge.setPoints(points);
    }

    public PointStructure createPointStructure(List<Shots> shots) {

        return this.pointStructure.createPointStructure(shots);
    }

    public void setFrenzy() {
        this.bridge.setFrenzy();
        this.bridge.setKillStreakCount();
    }

    public List<Shots> getShots() {

        return this.bridge.getShots();
    }

    public List<Shots> getMarks() {

        return this.bridge.getMarks();
    }

    public boolean isDead() {

        return this.bridge.isDead();
    }

    public void movePlayer(Square destination) {

        this.bridge.setOldPosition(this.bridge.getCurrentPosition());
        this.bridge.getCurrentPosition().removePlayer(this);

        destination.addPlayer(this);
    }

    public void damagePlayer(Color color, int quantity) {

        this.bridge.appendShot(color, quantity);
    }

    public void markPlayer(Color color, int quantity) {

        this.bridge.appendMark(color, quantity);
    }

    public Square getOldPosition() {

        return this.bridge.getOldPosition();
    }

    @Override
    public Square getCurrentPosition() {

        return this.bridge.getCurrentPosition();
    }

    public void setCurrentPosition(Square currentPosition) {

        this.bridge.setCurrentPosition(currentPosition);
    }
}
