package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import java.util.List;

public class Bridge {

    private Color color;

    private int points = 0;

    private DeathBridge deathBridge = new DeathBridge();
    private DamageBridge damageBridge = new DamageBridge();
    private ActionBridge actionBridge;

    public Bridge(Color color, EffectHandler effectHandler) {

        this.color = color;
        this.actionBridge = new ActionBridge.ActionBridgeBuilder(effectHandler).build();
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

    public ActionBridge getActionBridge() {
        return this.actionBridge;
    }
    public Adrenalin getAdrenalin(){
        return  this.damageBridge.checkAdrenalin();
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

    public DamageBridge getDamageBridge() {
        return damageBridge;
    }

    public void appendShot(Color color) {
        this.damageBridge.appendShot(color);
    }

    public Adrenalin checkAdrenalin() {
        return this.damageBridge.checkAdrenalin();
    }

    public void appendMark(Color color) {

        this.damageBridge.appendMark(color);
    }

    public void setAdrenalin(Adrenalin adrenalin) {
        this.actionBridge.setAdrenalin(adrenalin);
    }

    public boolean isDead() {

        return this.damageBridge.isDead();
    }
}
