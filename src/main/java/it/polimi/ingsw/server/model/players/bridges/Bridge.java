package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Main class for storing all the damages , marks , kills  and  possible
 * actions of the linked player.

 */
public class Bridge implements Serializable {

    /**
     * the player color is also the bridge color
     */
    private Color color;

    /**
     * all the  points earned by the player during the current game
     */
    private int points = 0;

    /**
     * creates a death bridge to save all the times the player has died and
     * to correctly give points to every player each time the player dies
     *
     */
    private DeathBridge deathBridge = new DeathBridge();
    /**
     * creates a damage bridge to store every damage and mark taken by the player
     */
    private DamageBridge damageBridge = new DamageBridge();
    /**
     * creates an  action  bridge to store all the possible actions that a player can do
     * during his turn
     */
    private ActionBridge actionBridge = new ActionBridge.ActionBridgeBuilder().build();

    /**
     * creates a player's bridge with the players linked color
     * @param color bridge's color
     */
    public Bridge(Color color) {

        this.color = color;
    }

    /**
     *gets the color of the linked  player's bridge
     * @return the color of the linked  player's bridge
     */

    public Color getColor() {

        return this.color;
    }

    /**
     * gets the total points earned by the player
     * @return the total points earned by the player
     */
    public int getPoints() {

        return this.points;
    }

    /**
     * assigns new points to the linked player
     * @param points new points that need to be added to the linked player
     */
    public void setPoints(int points) {

        this.points += points;
    }

    /**
     * the linked player has died one more time so
     * another kill needs to be placed in his death bridge
     */
    public void addKill() {

        this.deathBridge.addKill();
    }

    /**
     * changes the frenzy state of the linked player
     */
    public void setFrenzy() {

        this.deathBridge.setFrenzy();
    }

    /**
     * after the linked player has died all his damages need to be cleared
     * and all his death points need to be reset  so that after they can be given
     * to different players
     */
    public void setPointsUsed() {

        this.deathBridge.setPointsUsed();
        this.damageBridge.clearShots();
    }

    /**
     * takes the right amount of points that afterwards will be given to a player
     * @return the points that the player who damaged the dead player deserves
     */
    public int assignPoints() {

        return this.deathBridge.assignPoints();
    }

    /**
     * checks if the player's first shot needs to be counted as
     * a first blood shot
     * @return true if the player's first shot needs to be counted as
     * a first blood shot
     */
    public boolean isKillStreakCount() {

        return this.damageBridge.isKillStreakCount();
    }

    /**
     * sets the player's first shot as a first blod shot
     */
    public void setKillStreakCount() {

        this.damageBridge.setKillStreakCount();
    }

    /**
     *gets all the damages taken by the player
     * @return all the damages taken by the player
     */
    public List<Color> getShots() {

        return this.damageBridge.getShots();
    }

    /**
     *gets all the marks taken by the player
     * @return all the marks taken by the player
     */
    public List<Color> getMarks() {

        return this.damageBridge.getMarks();
    }

    /**
     * appends another shot to the damage bridge of the player
     * @param color the color of the player who did the damage
     * @param checkMarks checks if the new shot should transform all the marks of the same
     * player into damages
     */
    public void appendShot(Color color, boolean checkMarks) {

        this.damageBridge.appendShot(color, checkMarks);
    }

    /**
     * appends another mark to the damage bridge of the player
     * @param color the color of the player who did the mark
     */
    public void appendMark(Color color) {

        this.damageBridge.appendMark(color);
    }

    /**
     * checks if the player is dead at the end of the turn
     * @return true uf the player is dead at the end of the turn
     */
    public boolean isDead() {

        return this.damageBridge.isDead();
    }

    /**
     *gets the adrenalin state of the linked player
     * @return the adrenalin state of the linked player
     */
    public Adrenalin getAdrenalin() {

        if (this.actionBridge.getAdrenalin().equals(Adrenalin.FIRSTFRENZY) ||
                this.actionBridge.getAdrenalin().equals(Adrenalin.SECONDFRENZY)) {

            return this.actionBridge.getAdrenalin();
        }

        return this.damageBridge.getAdrenalin();
    }

    /**
     * changes the adrenalin state of the linked player based on his damages and on
     * the game state
     * @param adrenalin the adrenalin state of the linked player
     */
    public void setAdrenalin(Adrenalin adrenalin) {

        this.actionBridge.setAdrenalin(adrenalin);
    }

    /**
     *gets the current action selected by the player during his turn
     * @return the current action selected by the player during his turn
     */
    public ActionStructure getCurrentAction() {

        return this.actionBridge.getCurrentAction();
    }

    /**
     * gets the current action selected by the player is a shoot action
     * @return true if the current action selected by the player is a shoot action
     */
    public boolean isShooting() {

        return this.actionBridge.isShooting();
    }

    /**
     *checks id the linked player is the one that started the game
     * @return true if the linked player is the one that started the game
     */
    public boolean isFirstPlayer() {

        return this.actionBridge.isFirstPlayer();
    }

    /**
     * sets if the linked player will be the one to start the game
     * @param firstPlayer true if the linked player should start the game , false otherwise
     */
    public void setFirstPlayer(boolean firstPlayer) {

        this.actionBridge.setFirstPlayer(firstPlayer);
    }

    /**
     * checks if the linked player has gotten access  to the frenzy actions
     * @return true if the linked player has gotten access  to the frenzy actions
     */
    public boolean isFrenzyActions() {

        return this.actionBridge.isFrenzyActions();
    }

    /**
     * sets if the linked player should have access to the frenzy actions
     * @param frenzyActions expresses if the linked player should have access to
     * the frenzy actions
     */
    public void setFrenzyActions(boolean frenzyActions) {

        this.actionBridge.setFrenzyActions(frenzyActions);
    }

    /**
     * checks if the player is dead and can respawn
     * @return true if the player is dead and can respawn
     */
    public boolean isRespawn() {

        return this.actionBridge.isRespawn();
    }

    /**
     * sets if the player can respawn or not
     * @param respawn true if the player must respawn
     */
    public void setRespawn(boolean respawn) {

        this.actionBridge.setRespawn(respawn);
    }

    /**
     *
     * @return the remaining actions of the linked player during his turn
     */
    public int getRemainingActions() {

        return this.actionBridge.getRemainingActions();
    }

    /**
     * sets the remaining actions of the player in his turn
     * @param remainingActions number of remaining actions in the player's turn
     */
    public void setRemainingActions(int remainingActions) {

        this.actionBridge.setRemainingActions(remainingActions);
    }

    /**
     *
     * @return the current weapon card selected for shooting by the linked player
     */
    public WeaponCard getCurrentWeaponCard() {

        return this.actionBridge.getCurrentWeaponCard();
    }

    /**
     * sets the current selected weapon card by the linked player
     * @param weaponCard the weapon card choose by the player to shoot
     */
    public void setCurrentWeaponCard(WeaponCard weaponCard) {

        this.actionBridge.setCurrentWeaponCard(weaponCard);
    }

    /**
     * @return all the possible actions that a player can do during his turn
     */
    public List<ActionStructure> getActions() {

        return this.actionBridge.getActions();
    }

    /**
     * selects the current action  chosen by the player
     * @param id id of the action the player wants to choose
     * @throws IllegalActionException if the player has no remaning actions or
     * the selected action is not available
     */
    public void selectAction(int id) throws IllegalActionException {

        this.actionBridge.selectAction(id);
    }

    /**
     * ends the current action of the linked player
     */
    public void endAction() {

        this.actionBridge.endAction();
    }

    /**
     *
     * @return a copy of the current object as a json object
     */
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("damageBridge", this.damageBridge.toJsonObject())
                .add("actionBridge", this.actionBridge.toJsonObject())
                .add("deathBridgeArray", this.deathBridge.toJsonArray())
                .build();
    }
}
