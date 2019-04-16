package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.Color;
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

    public void addKill() {

        this.deathBridge.addKill();
    }

    public void setFrenzy() {

        this.deathBridge.setFrenzy();
    }

    public void setPointsUsed() {

        this.deathBridge.setPointsUsed();
        this.damageBridge.clearShots();
    }

    public int assignPoints() {

        return this.deathBridge.assignPoints();
    }

    public boolean isKillStreakCount() {

        return this.damageBridge.isKillStreakCount();
    }

    public void setKillStreakCount() {

        this.damageBridge.setKillStreakCount();
    }

    public List<Shots> getShots() {

        return this.damageBridge.getShots();
    }

    public List<Shots> getMarks() {

        return this.damageBridge.getMarks();
    }

    public void appendShot(Color color) {

        this.damageBridge.appendShot(color);
    }

    public void appendMark(Color color) {

        this.damageBridge.appendMark(color);
    }

    public boolean isDead() {

        return this.damageBridge.isDead();
    }
}
