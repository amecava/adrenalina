package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.List;

public class Deaths extends Bridge {

    private int maxNumberOfDeaths;

    public Deaths(int maxNumberOfDeaths) {

        super(Color.EOG, null);
        this.maxNumberOfDeaths = maxNumberOfDeaths;
        this.setKillStreakCount();
    }

    public List<Shots> getKillStreak() {

        return this.getShots();
    }

    public void addKill(Color color, boolean infer) {

        if (infer) {
            this.maxNumberOfDeaths++;
            this.appendShot(color);
        }

        this.appendShot(color);
    }

    public boolean isGameEnded() {

        return this.getShots().size() >= this.maxNumberOfDeaths;
    }
}
