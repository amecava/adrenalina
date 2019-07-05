package it.polimi.ingsw.server.model.points;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.Bridge;
import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * main class for storing the kill streak of all the game among
 * with the player's color who did the kill shot
 */
public class Deaths extends Bridge implements Serializable {

    /**
     * number of deaths in order to finish the game
     */
    private int numberOfDeaths;

    /**
     * checks if frenzy is enabled
     */
    private boolean frenzyEnabled;
    /**
     * refrences the player that started the frenzy mode of the game
     */
    private Player firstFrenzyPlayer;

    /**
     * creates the number of deaths of  the game
     * @param numberOfDeaths number of deaths in order to finish the game
     */

    public Deaths(int numberOfDeaths) {

        super(Color.ALL);

        this.numberOfDeaths = numberOfDeaths;
        this.setKillStreakCount();
    }

    /**
     * returns the number of time players have died during the game
     * @return number of time players have died during game
     */
    public int getNumberOfDeaths() {

        return this.numberOfDeaths;
    }

    /**
     * checks if  the game  is in  frenzy mode
     * @return true if the game  is in frenzy mode
     */
    public boolean isFrenzy() {

        return this.frenzyEnabled;
    }

    /**
     * sets the frenzy of the game  based on the parameter frenzy
     * @param frenzy specifies  if  the game  should be in frenzy mode or not
     */
    public void setFrenzy(boolean frenzy) {

        this.frenzyEnabled = frenzy;
    }

    /**
     *gets referenced player that started frenzy mode
     * @return the referenced player that started frenzy mode
     */
    Player getFirstFrenzyPlayer() {

        return this.firstFrenzyPlayer;
    }

    /**
     * sets the player that started the frenzy mode
     * @param firstFrenzyPlayer player that started frenzy mode
     */
    void setFirstFrenzyPlayer(Player firstFrenzyPlayer) {

        this.firstFrenzyPlayer = firstFrenzyPlayer;
    }

    /**
     *
     * @return a list of all the time a player has died linked with the player that did the
     * kill shot
     */
    List<Color> getKillStreak() {

        return this.getShots();
    }

    /**
     * another player has been killed so it is added to the general kill streak
     * of the game
     * @param color color of the player that did the kill shot
     * @param infer indicates if the dead player has got a 12th shot
     */
    void addKill(Color color, boolean infer) {

        if (infer) {

            this.numberOfDeaths++;
            this.appendShot(color, true);
        }

        this.appendShot(color, true);
    }

    /**
     *
     * @return true if the game has exceeded the number of deaths possible
     * during the game
     */
    boolean isGameEnded() {

        return this.getShots().size() >= this.numberOfDeaths;
    }


    /**
     * creates a json object of the current structure
     * @return a json object of the current structure
     */
    public JsonObject toJsonDeaths() {

        return Json.createObjectBuilder(this.toJsonObject())
                .add("numberOfDeaths", this.numberOfDeaths)
                .add("frenzyEnabled", this.frenzyEnabled)
                .add("firstFrenzyPlayer",
                        this.firstFrenzyPlayer != null ? this.firstFrenzyPlayer.getPlayerId() : "")
                .build();
    }
}
