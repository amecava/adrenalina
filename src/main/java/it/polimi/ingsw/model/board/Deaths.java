package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.bridges.DamageBridge;
import it.polimi.ingsw.model.bridges.PointHandler;
import it.polimi.ingsw.model.bridges.Shots;
import java.util.ArrayList;
import java.util.List;

public class Deaths {

    private int numberOfDeaths;
    List<Shots> killStreak = new ArrayList<>();
    PointHandler pointHandler;

    public Deaths(int numberOfDeaths, PointHandler pointHandler) {

        this.numberOfDeaths = numberOfDeaths;
        this.pointHandler = pointHandler;

    }

    public void setPointHandler(PointHandler pointHandler) {

        this.pointHandler = pointHandler;
    }

    public List<Shots> getKillStreak() {

        List<Shots> temp = new ArrayList<>();
        temp.addAll(killStreak);

        return temp;
    }

    public void endgame() {

        this.pointHandler.setEog(true);// count points forall the bridges with at least 1 damage!!!
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
