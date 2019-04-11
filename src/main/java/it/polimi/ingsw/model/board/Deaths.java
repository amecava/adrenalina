package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.ArrayList;
import java.util.List;

public class Deaths {

    private int maxNumberOfDeaths;

    private List<Shots> killStreak = new ArrayList<>();

    public Deaths(int maxNumberOfDeaths) {

        this.maxNumberOfDeaths = maxNumberOfDeaths;
    }

    public List<Shots> getKillStreak() {

        return this.killStreak;
    }

    public void addKill(Color color, boolean infer) {

        if (infer) {
            this.maxNumberOfDeaths++;
            killStreak.add(new Shots(color));
        }

        this.killStreak.add(new Shots(color));
    }

    public boolean checkEndGame() {

        return this.killStreak.size() >= this.maxNumberOfDeaths;
    }
}
