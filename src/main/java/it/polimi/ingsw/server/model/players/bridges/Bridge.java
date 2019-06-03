package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

public class Bridge implements Serializable {

    private Color color;

    private int points = 0;

    private DeathBridge deathBridge = new DeathBridge();
    private DamageBridge damageBridge = new DamageBridge();
    private ActionBridge actionBridge = new ActionBridge.ActionBridgeBuilder().build();

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

    public List<Color> getShots() {

        return this.damageBridge.getShots();
    }

    public List<Color> getMarks() {

        return this.damageBridge.getMarks();
    }

    public void appendShot(Color color, boolean checkMarks) {

        this.damageBridge.appendShot(color, checkMarks);
    }

    public void appendMark(Color color) {

        this.damageBridge.appendMark(color);
    }

    public boolean isDead() {

        return this.damageBridge.isDead();
    }

    public Adrenalin getAdrenalin() {

        return this.damageBridge.getAdrenalin();
    }

    public void setAdrenalin(Adrenalin adrenalin) {

        this.actionBridge.setAdrenalin(adrenalin);
    }

    public ActionStructure getCurrentAction() {

        return this.actionBridge.getCurrentAction();
    }

    public boolean isShooting() {

        return this.actionBridge.isShooting();
    }

    public boolean isFirstPlayer() {

        return this.actionBridge.isFirstPlayer();
    }

    public void setFirstPlayer(boolean firstPlayer) {

        this.actionBridge.setFirstPlayer(firstPlayer);
    }

    public boolean isFrenzyActions() {

        return this.actionBridge.isFrenzyActions();
    }

    public void setFrenzyActions(boolean frenzyActions) {

        this.actionBridge.setFrenzyActions(frenzyActions);
    }

    public boolean isRespawn() {

        return this.actionBridge.isRespawn();
    }

    public void setRespawn(boolean respawn) {

        this.actionBridge.setRespawn(respawn);
    }

    public int getRemainingActions() {

        return this.actionBridge.getRemainingActions();
    }

    public void setRemainingActions(int remainingActions) {

        this.actionBridge.setRemainingActions(remainingActions);
    }

    public WeaponCard getCurrentWeaponCard() {

        return this.actionBridge.getCurrentWeaponCard();
    }

    public void setCurrentWeaponCard(WeaponCard weaponCard) {

        this.actionBridge.setCurrentWeaponCard(weaponCard);
    }

    public List<ActionStructure> getActions() {

        return this.actionBridge.getActions();
    }

    public void selectAction(int id) throws IllegalActionException {

        this.actionBridge.selectAction(id);
    }

    public void endAction() {

        this.actionBridge.endAction();
    }

    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("damageBridge", this.damageBridge.toJsonObject())
                .add("actionBridge", this.actionBridge.toJsonObject())
                .add("deathBridgeArray", this.deathBridge.toJsonArray())
                .build();
    }
}
