package it.polimi.ingsw.server.model.board;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.bridges.Bridge;
import it.polimi.ingsw.server.model.players.bridges.Shots;
import java.util.List;

public class Deaths extends Bridge {

    private int maxNumberOfDeaths;

    public Deaths(int maxNumberOfDeaths) {

        super(Color.ALL);

        this.maxNumberOfDeaths = maxNumberOfDeaths;
        this.setKillStreakCount();
    }

    public int getNumberOfDeaths() {

        return this.maxNumberOfDeaths;
    }

    public List<Color> getKillStreak() {

        return this.getShots();
    }

    public void addKill(Color color, boolean infer) {

        if (infer) {
            this.maxNumberOfDeaths++;
            this.appendShot(color, true);
        }

        this.appendShot(color, true);
    }

    public boolean isGameEnded() {

        return this.getShots().size() >= this.maxNumberOfDeaths;
    }
}
