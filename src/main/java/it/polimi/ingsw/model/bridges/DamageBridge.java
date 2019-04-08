package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DamageBridge {

    private List<Shots> shots = new ArrayList<>();
    private List<Shots> markers = new ArrayList<>();
    private List<Shots> temp = new ArrayList<>();
    private DeathBridge deathBridge = new DeathBridge();
    private Color color;

    public void setShots(List<Shots> shots) {
        this.shots = shots;
    }

    public DamageBridge(Color color) {
        this.color=color;
    }

    public Color getColor() {
        return color;
    }
    public int getNumberOfDeaths(){
        return (deathBridge.getIndexOfDeath());
    }

    public void addDamage(Color color, int quantity) {
        boolean look;
        look = true;
        for (int i = 0; i < quantity; i++) {
            if (shots.size() < 12) {
                shots.add(new Shots(color));
                if (look == true) {
                    ListIterator<Shots> iterator= markers.listIterator();
                        while (iterator.hasNext()) {
                        if (iterator.next().getColor().equals(color)) {
                            i--;
                            System.out.println("Found mark on the player Bridge ");
                            iterator.remove();
                            look = false;

                        }
                    }
                }
            }
        }
    }

    public boolean checkIfDead() {
        if (this.shots.size() >= 11) {
            System.out.println("player " + this.color+ " is dead ");
            return true;
        }
        return false;
    }
    public List<Shots> getMarkers(){
        List<Shots> temp=new ArrayList<>();
        temp.addAll(this.markers);
        return temp;
    }

    public void addMarker(Color color, int quantity) {
        int tempMarker=0;
        for (Shots shots1 : markers) {
            if (shots1.getColor().equals(color)) {
                tempMarker++;
            }
        }
        for (int i = 0; (i < quantity) && i < (3 - tempMarker); i++) {
            markers.add(new Shots(color));
        }
    }

    public List<Shots> getShots() {
        temp.clear();
        temp.addAll(shots);
        return temp;
    }
    public Adrenalin checkAdrenalin (){
        if (this.shots.size()<=2)
            return Adrenalin.NORMAL;
        else if (this.shots.size()<=5)
            return  Adrenalin.FIRSTADRENALIN;
        else return Adrenalin.SECONDADRENALIN;
    }

    public void setKill() {
        this.deathBridge.setIndexOfDeath();
    }

    public int getPoints() {
        return (this.deathBridge.getPoints());
    }

    public void restorePoints() {
        this.deathBridge.restorePoints();
        this.shots.clear();
    }
    public void EOG (){
        for (Shots shots1: markers){
            this.addDamage(shots1.getColor(), 1);
        }
        markers.clear();
    }
}
