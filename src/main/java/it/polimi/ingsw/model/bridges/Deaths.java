package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import java.util.ArrayList;
import java.util.List;

public class Deaths {

    private int numberOfDeaths;
    private PointHandler pointHandler;

    private List<Shots> killStreak = new ArrayList<>();

    public Deaths(int numberOfDeaths, PointHandler pointHandler) {

        this.numberOfDeaths = numberOfDeaths;
        this.pointHandler = pointHandler;
    }

    public List<Shots> getKillStreak() {

        return this.killStreak;
    }

    public void endgame() {

        this.pointHandler.setEog(true);
        DamageBridge damageBridge = new DamageBridge(Color.EOG);
        damageBridge.setShots(killStreak);
        this.pointHandler.setKillStreakCount();
        this.pointHandler.deathUpdate(damageBridge);
    }

    public void addKill(Color color, Boolean twelve) {

        if (twelve) {
            this.numberOfDeaths++;
            killStreak.add(new Shots(color));
        }

        killStreak.add(new Shots(color));
    }

    public int remainingKills() {
        return (this.numberOfDeaths - this.killStreak.size());
    }

    public boolean checkEndGame (){
        if (killStreak.size() >= this.numberOfDeaths) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return killStreak.toString();
    }
}
