package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.players.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * This class stores all the damages
 * and marks of the linked player
 * among with the color of the player who did them
 */
class DamageBridge implements Serializable {

    /**
     * indicates if  the first damage of the linked player
     * needs to be counted as a first blood shot
     */
    private boolean killStreakCount = false;

    /**
     * a list of all shots taken by the player linked with the color
     * of the player who did the shot
     */
    private List<Color> shots = new ArrayList<>();

    /**
     * list of marks taken by the payer linked with
     * the color of the player who did the mark
     */
    private List<Color> marks = new ArrayList<>();

    /**
     *checks if the player's first shot needs to be counted as
     * a first blood shot
     * @return true if the player's first shot needs to be counted as
     * a first blood shot
     */
    boolean isKillStreakCount() {

        return this.killStreakCount;
    }

    /**
     * sets the player's first shot as a first blood shot
     */
    void setKillStreakCount() {

        this.killStreakCount = true;
    }

    /**
     * gets the list of all damages taken by the player
     * @return the list of all damages taken by the player
     */
    List<Color> getShots() {

        return new ArrayList<>(this.shots);
    }

    /**
     * gets the list of all marks taken by the player
     * @return a list of all marks taken by the player
     */
    List<Color> getMarks() {

        return new ArrayList<>(this.marks);
    }

    /**
     * clears all the damages taken by the player so that after the player's death
     * there are no more damages in his damage bridge
     */
    void clearShots() {

        this.shots.clear();
    }

    /**
     * the linked player has taken another shot so it needs to be added
     * to his damage bridge
     * @param color the color of the player who did the damage
     * @param checkMarks indicates if the most recent damage taken by the player
     * should transform all the marks of the same color into damages
     */
    void appendShot(Color color, boolean checkMarks) {

        if (this.shots.size() < 12) {
            this.shots.add(color);
        }

        if (checkMarks) {

            this.marks = this.updateMarksToShots(color);
        }
    }

    /**
     * appends another mark to the linked player damage bridge
     * @param color indicates the color of the player who did the mark to the linked player
     */
    void appendMark(Color color) {

        if (this.marks.stream().filter(x -> x.equals(color)).count() < 3) {

            this.marks.add(color);
        }
    }

    /**
     * idicates if the player at the end of the turn is dead or not
     * @return true if the player at the end of the turn is dead
     */
    boolean isDead() {

        return this.shots.size() >= 11;
    }

    /**
     * gets the adrenalin state of the linked player
     * @return the adrenalin state of the linked player
     */
    Adrenalin getAdrenalin() {

        if (this.shots.size() <= 2) {

            return Adrenalin.NORMAL;
        }

        if (this.shots.size() <= 5) {

            return Adrenalin.FIRSTADRENALIN;
        }

        return Adrenalin.SECONDADRENALIN;
    }


    /**
     * transforms all the marks done by a given player to damages done by the same player
     * @param color indicates the color of marks that should be transformed into damages
     * @return the list of marks without the transformed ones
     */
    private List<Color> updateMarksToShots(Color color) {

        return this.marks.stream()
                .filter(x -> {
                    if (x.equals(color)) {

                        this.shots.add(x);

                        return false;
                    }

                    return true;
                }).collect(Collectors.toList());
    }

    /**
     * creates the json object for this structure
     * @return the json object of this damage bridge
     */
    public JsonObject toJsonObject() {

        JsonArrayBuilder shotsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder marksBuilder = Json.createArrayBuilder();

        this.shots.stream().map(Color::toString).forEach(shotsBuilder::add);
        this.marks.stream().map(Color::toString).forEach(marksBuilder::add);

        return Json.createObjectBuilder()
                .add("shots", shotsBuilder.build())
                .add("marks", marksBuilder.build())
                .add("isFinalFrenzy", this.killStreakCount)
                .build();
    }
}
