package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.players.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

class DamageBridge {

    private boolean killStreakCount = false;

    private List<Color> shots = new ArrayList<>();
    private List<Color> marks = new ArrayList<>();

    boolean isKillStreakCount() {

        return this.killStreakCount;
    }

    void setKillStreakCount() {

        this.killStreakCount = true;
    }

    List<Color> getShots() {

        return new ArrayList<>(this.shots);
    }

    List<Color> getMarks() {

        return new ArrayList<>(this.marks);
    }

    void clearShots() {

        this.shots.clear();
    }

    void appendShot(Color color, boolean checkMarks) {

        if (this.shots.size() < 12) {
            this.shots.add(color);
        }

        if (checkMarks) {

            this.marks = this.updateMarksToShots(color);
        }
    }

    void appendMark(Color color) {

        if (this.marks.stream().filter(x -> x.equals(color)).count() < 3) {

            this.marks.add(color);
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

    private List<Color> updateMarksToShots(Color color) {

        return this.marks.stream()
                .filter(x -> {
                    if (x.equals(color)) {

                        this.shots.add(x);

                        return false;
                    }

                    return true;
                }).collect(Collectors.toList());
    }

    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("shots", Json.createArrayBuilder().add(this.shots.stream()
                        .map(Color::toString)
                        .collect(Collectors.joining(" ")))
                        .build())
                .add("marks", Json.createArrayBuilder().add(this.marks.stream()
                        .map(Color::toString)
                        .collect(Collectors.joining(" ")))
                        .build())
                .build();
    }
}
