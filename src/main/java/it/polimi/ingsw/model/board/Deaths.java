package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.bridges.DamageBridge;
import it.polimi.ingsw.model.bridges.PointHandler;
import it.polimi.ingsw.model.bridges.Shots;
import java.util.ArrayList;
import java.util.List;

public class Deaths {
    private int numberOfDeaths;
    List<Shots> killStreak= new ArrayList<>();
    PointHandler pointHandler;

    public Deaths(int numberOfDeaths) {
        this.numberOfDeaths = numberOfDeaths;
    }
    public void setPointHandler(PointHandler pointHandler){
        this.pointHandler=pointHandler;
    }

    public List<Shots> getKillStreak() {
        List<Shots> temp = new ArrayList<>();
        temp.addAll(killStreak);
        return  temp;
    }
    public void addKill( Color color){
            killStreak.add(new Shots(color));
            if (killStreak.size()==this.numberOfDeaths)
                ;
            return false ;

    }

}
