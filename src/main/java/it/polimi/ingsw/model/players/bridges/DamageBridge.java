package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

class DamageBridge {

    private List<Shots> shots = new ArrayList<>();
    private List<Shots> marks = new ArrayList<>();

    DamageBridge() {
    }

    List<Shots> getShots() {

        return this.shots;
    }

    void setShots(List<Shots> shots) {

        this.shots = shots;
    }

    List<Shots> getMarks() {

        return this.marks;
    }

    void appendShot(Color color, int quantity) {
        ListIterator<Shots> listIterator = this.marks.listIterator();

        while (listIterator.hasNext()) {
            if (listIterator.next().getColor() == color) {
                quantity++;
                listIterator.remove();
            }
        }
        for (int i = 0; i < quantity; i++) {
            if (this.shots.size() < 12) {
                this.shots.add(new Shots(color));
            }
        }
    }

    void appendMark(Color color, int quantity) {

        int tempMarker = 0;

        for (Shots shot : this.marks) {
            if (shot.getColor().equals(color)) {
                tempMarker++;
            }
        }

        for (int i = 0; (i < quantity) && i < (3 - tempMarker); i++) {
            this.marks.add(new Shots(color));
        }
    }

    boolean isDead() {

        return this.shots.size() >= 11;
    }

    void endOfGame() {

        for (Shots shot : this.marks) {
            this.appendShot(shot.getColor(), 1);
        }
    }

    Adrenalin checkAdrenalin() {

        if (this.shots.size() <= 2) {
            return Adrenalin.NORMAL;
        }

        if (this.shots.size() <= 5) {
            return Adrenalin.FIRSTADRENALIN;
        }

        return Adrenalin.SECONDADRENALIN;
    }
}
