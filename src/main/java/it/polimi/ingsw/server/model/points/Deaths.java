package it.polimi.ingsw.server.model.points;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.Bridge;
import java.io.Serializable;
import java.util.List;

public class Deaths extends Bridge implements Serializable {

    private int numberOfDeaths;

    private boolean frenzyEnabled;
    private Player firstFrenzyPlayer;

    public Deaths(int numberOfDeaths) {

        super(Color.ALL);

        this.numberOfDeaths = numberOfDeaths;
        this.setKillStreakCount();
    }

    public int getNumberOfDeaths() {

        return this.numberOfDeaths;
    }

    public boolean isFrenzy() {

        return this.frenzyEnabled;
    }

    public void setFrenzy(boolean frenzy) {

        this.frenzyEnabled = frenzy;
    }

    Player getFirstFrenzyPlayer() {

        return this.firstFrenzyPlayer;
    }

    void setFirstFrenzyPlayer(Player firstFrenzyPlayer) {

        this.firstFrenzyPlayer = firstFrenzyPlayer;
    }

    List<Color> getKillStreak() {

        return this.getShots();
    }

    void addKill(Color color, boolean infer) {

        if (infer) {

            this.numberOfDeaths++;
            this.appendShot(color, true);
        }

        this.appendShot(color, true);
    }

    boolean isGameEnded() {

        return this.getShots().size() >= this.numberOfDeaths;
    }
}
