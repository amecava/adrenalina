package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.players.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class DamageBridge {

    private boolean killStreakCount = false;

    private List<Shots> shots = new ArrayList<>();
    private List<Shots> marks = new ArrayList<>();


    boolean isKillStreakCount() {

        return this.killStreakCount;
    }

    void setKillStreakCount() {

        this.killStreakCount = true;
    }

    List<Shots> getShots() {

        return new ArrayList<>(this.shots);
    }

    List<Shots> getMarks() {

        return new ArrayList<>(this.marks);
    }

    void clearShots() {

        this.shots.clear();
    }

    void appendShot(Color color, boolean checkMarks) {

        if (this.shots.size() < 12) {
            this.shots.add(new Shots(color));
        }

        if (checkMarks) {

            this.marks = this.updateMarksToShots(color);
        }
    }

    void appendMark(Color color) {

        if (this.marks.stream().filter(x -> x.getColor().equals(color)).count() < 3) {

            this.marks.add(new Shots(color));
        }
    }

    boolean isDead() {

        return this.shots.size() >= 11;
    }

    Adrenalin getAdrenalin() {

        if (this.shots.size() <= 2) {

            return Adrenalin.NORMAL;
        }

        if (this.shots.size() <= 5) {

            return Adrenalin.FIRSTADRENALIN;
        }

        return Adrenalin.SECONDADRENALIN;
    }

    private List<Shots> updateMarksToShots(Color color) {

        return this.marks.stream()
                .filter(x -> {
                    if (x.getColor().equals(color)) {

                        this.shots.add(x);

                        return false;
                    }

                    return true;
                }).collect(Collectors.toList());
    }
}
