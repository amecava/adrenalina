package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.bridges.Shots;
import it.polimi.ingsw.model.points.PointStructure;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Target;
import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private String playerId;
    private Color playerColor;

    private Square oldPosition;
    private Square currentPosition;

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

    public Square getOldPosition() {

        return this.oldPosition;
    }

    @Override
    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    public Bridge getBridge() {

        return this.bridge;
    }

    public PointStructure getPointStructure() {

        return this.pointStructure;
    }

    public Card getWeaponCard(int index) {

        return this.weaponHand.get(index);
    }

    public void setWeaponHand(List<Card> weaponHand) {

        this.weaponHand = weaponHand;
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

        if (this.currentPosition != null) {

            this.oldPosition = this.currentPosition;
            this.oldPosition.removePlayer(this);
        }

        this.currentPosition = destination;
        this.currentPosition.addPlayer(this);
    }

    public void damagePlayer(Color color) {

        this.bridge.appendShot(color);
    }

    public void markPlayer(Color color) {

        this.bridge.appendMark(color);
    }
}
