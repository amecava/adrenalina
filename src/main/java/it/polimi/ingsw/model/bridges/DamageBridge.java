package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameHandler;
import java.util.ArrayList;
import java.util.List;

public class DamageBridge  {
    private List<Shots> shots= new ArrayList<>();
    private List<Shots> markers=new ArrayList<>();
    private List<Shots> temp= new ArrayList<>();
    private List<Shots> deaths = new ArrayList<>();
    private boolean look;
    private GameHandler gameHandler;

    public DamageBridge(GameHandler gameHandler){
        this.gameHandler=gameHandler;
    }

    public void addDamage (Color color , int quantity){
        look=true;
        for (int i=0; i<quantity ; i++)
            if (shots.size()<=10) {
                shots.add(new Shots(color));
                if (look==true) {
                    for (Shots marker : markers) {
                        if (marker.getColor().equals(color)) {
                            i--;
                            markers.remove(marker);
                            look = false;

                        }
                    }
                }
            }
            else {
                deaths.add(new Shots(color));
                shots.clear();
                markers.clear();
                gameHandler.deathUpdate(this);

            }
    }

    public List<Shots> getShots() {
        temp.clear();
        temp.addAll(shots);
        return  temp;
    }

}
