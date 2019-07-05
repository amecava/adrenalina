package it.polimi.ingsw.server.model.points;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.List;

/**
 * structure that every player has when making damage to another player . In this structure
 * it is saved the first damage the player did to the other one , the last damage , and the
 * number of damage.
 */
public class PointStructure implements Serializable {

    /**
     * reference to the player that owns this points
     */
    private Player player;
    /**
     * number of shots that that the player did to the dead player
     */
    private int numberDamage;
    /**
     * first damage that the player did to the dead player
     */
    private int firstDamage;
    /**
     * last damage that the player did to the dead player
     */
    private int lastDamage;

    /**
     * creating the point structure for the referenced  player
     * @param player is the player for which we need to count the points after
     * a death of a player or after the end game
     */
    public PointStructure(Player player) {

        this.player = player;
    }

    /**
     * gets the referenced player
     * @return the referenced player
     */
    Player getPlayer() {

        return this.player;
    }

    /**
     * gets the number of damages done by the referenced player to the dead player
     * @return the number of damages done by the referenced player to the dead player
     */
    int getNumberDamage() {

        return this.numberDamage;
    }

    /**
     * sets the number of damages done by the referenced player
     * @param numberDamage are the damages done by the referenced player
     * @return point structure of the referenced player
     */
    PointStructure setNumberDamage(int numberDamage) {

        this.numberDamage = numberDamage;

        return this;
    }

    /**
     * gets the first damage done by the referenced player to the dead player
     * @return the first damage
     */
    int getFirstDamage() {

        return this.firstDamage;
    }

    /**
     * gets the last damage done by referenced player to the dead player
     * @return the last damage
     */
    int getLastDamage() {

        return this.lastDamage;
    }

    /**
     * sets the last damage done by the referenced player to the dead player
     * @param lastDamage is the last damage done by the referenced player
     */
    void setLastDamage(int lastDamage) {

        this.lastDamage = lastDamage;
    }

    /**
     * creates the point structure of the referenced player
     * @param shots is the list of all damages taken by  the dead player
     * @return the point structure of the player linked with the list of damages taken by
     * the dead player
     */
    public PointStructure createPointStructure(List<Color> shots) {

        this.numberDamage = 0;


        boolean foundFirstShot = false;

        for (int i = 0; i < shots.size(); i++) {

            if (shots.get(i).equals(this.player.getColor())) {
                this.numberDamage++;
                this.lastDamage = i + 1;

                if (!foundFirstShot) {
                    this.firstDamage = i + 1;
                    foundFirstShot = true;
                }
            }
        }

        return this;
    }
}
