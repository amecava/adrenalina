package it.polimi.ingsw.server.model.points;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;

public class PointsOfDeath implements Serializable {

    private int value;
    private boolean used = false;

    public PointsOfDeath(int value) {

        this.value = value;
    }

    public boolean isUsed() {

        return this.used;
    }

    public void reset() {

        this.used = false;
    }

    public int getValueSetUsed() {

        this.used = true;

        return this.value;
    }

    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("used", this.used)
                .add("value", this.value)
                .build();
    }
}
