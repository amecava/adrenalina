package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DamageBridge {

    private Color color;

    private List<Shots> shots = new ArrayList<>();
    private List<Shots> markers = new ArrayList<>();
    private List<Shots> temp = new ArrayList<>();
    private DeathBridge deathBridge = new DeathBridge();

    public DamageBridge(Color color) {

        this.color = color;
    }

    public void setShots(List<Shots> shots) {

        this.shots = shots;
    }

    public Color getColor() {

        return this.color;
    }

    public int getNumberOfDeaths() {

        return deathBridge.getIndexOfDeath();
    }

    public void addDamage(Color color, int quantity) {

        boolean look = true;

        for (int i = 0; i < quantity; i++) {
            if (shots.size() < 12) {
                shots.add(new Shots(color));

                if (look) {
                    ListIterator<Shots> iterator = markers.listIterator();
                    while (iterator.hasNext()) {

                        if (iterator.next().getColor().equals(color)) {
                            i--;
                            iterator.remove();
                            look = false;
                        }
                    }
                }
            }
        }
    }

    public boolean checkIfDead() {

        return this.shots.size() >= 11;
    }

    public List<Shots> getMarkers() {

        List<Shots> tempList = new ArrayList<>();
        tempList.addAll(this.markers);

        return tempList;
    }

    public void addMarker(Color color, int quantity) {

        int tempMarker = 0;

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

    public Adrenalin checkAdrenalin() {

        if (this.shots.size() <= 2) {
            return Adrenalin.NORMAL;
        }

        if (this.shots.size() <= 5) {
            return Adrenalin.FIRSTADRENALIN;
        }

        return Adrenalin.SECONDADRENALIN;
    }

    public void setKill() {

        this.deathBridge.setIndexOfDeath();
    }

    public int getPoints() {

        return this.deathBridge.getPoints();
    }

    public void restorePoints() {

        this.deathBridge.restorePoints();
        this.shots.clear();
    }

    public void eog() {

        for (Shots shots1 : markers) {
            this.addDamage(shots1.getColor(), 1);
        }

        markers.clear();
    }
}
