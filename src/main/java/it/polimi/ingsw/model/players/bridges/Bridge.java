package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Square;
import java.util.List;

public class Bridge {

    private Color color;

    private int points = 0;

    private DeathBridge deathBridge = new DeathBridge();
    private DamageBridge damageBridge = new DamageBridge();
    private ActionBridge actionBridge = new ActionBridge();

    public Bridge(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }

    public int getPoints() {

        return this.points;
    }

    public void setPoints(int points) {

        this.points += points;
    }

    public DeathBridge getDeathBridge() {

        return deathBridge;
    }

    public int getKills() {

        return this.deathBridge.getKills();
    }

    public void addKill() {

        this.deathBridge.addKill();
    }

    public int calculatePoints() {

        return this.deathBridge.calculatePoints();
    }

    public void setPointsUsed() {

        this.deathBridge.setPointsUsed();
        this.damageBridge.getShots().clear();
    }

    public void setFrenzy() {

        this.deathBridge.setFrenzy();
    }

    public List<Shots> getShots() {

        return this.damageBridge.getShots();
    }

    public void setShots(List<Shots> shots) {

        this.damageBridge.setShots(shots);
    }

    public List<Shots> getMarks() {

        return this.damageBridge.getMarks();
    }

    public boolean isDead() {

        return this.damageBridge.isDead();
    }

    public void appendShot(Color color, int quantity) {

        this.damageBridge.appendShot(color, quantity);
    }

    public void appendMark(Color color, int quantity) {

        this.damageBridge.appendMark(color, quantity);
    }

    public Square getOldPosition() {

        return this.actionBridge.getOldPosition();
    }

    public void setOldPosition(Square oldPosition) {

        this.actionBridge.setOldPosition(oldPosition);
    }

    public Square getCurrentPosition() {

        return this.actionBridge.getCurrentPosition();
    }

    public void setCurrentPosition(Square currentPosition) {

        this.actionBridge.setCurrentPosition(currentPosition);
    }
}
