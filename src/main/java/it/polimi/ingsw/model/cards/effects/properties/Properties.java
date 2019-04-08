package it.polimi.ingsw.model.cards.effects.properties;

import it.polimi.ingsw.model.ammo.Ammo;
import java.util.List;

public class Properties {

    private List<Ammo> cost;
    private Boolean activated;

    private Integer maxTargets;
    private List<Boolean> sameAsFather;
    private Boolean sameAsPlayer;
    private Boolean targetView;
    private Boolean seenByActive;

    private Integer minDist;
    private Integer maxDist;
    private boolean cardinal;
    private boolean throughWalls;
    private boolean differentSquares;

    public List<Ammo> getCost() {

        return this.cost;
    }

    public Boolean getActivated() {

        return this.activated;
    }

    public void setActivated(Boolean activated) {

        this.activated = activated;
    }

    public Integer getMaxTargets() {

        return this.maxTargets;
    }

    public List<Boolean> getSameAsFather() {

        return this.sameAsFather;
    }

    public boolean getSameAsPlayer() {

        return this.sameAsPlayer;
    }

    public Boolean getTargetView() {

        return this.targetView;
    }

    public Boolean getSeenByActive() {

        return this.seenByActive;
    }

    public Integer getMinDist() {

        return this.minDist;
    }

    public Integer getMaxDist() {

        return this.maxDist;
    }

    public boolean isCardinal() {

        return this.cardinal;
    }

    public boolean isThroughWalls() {

        return this.throughWalls;
    }

    public boolean isDifferentSquares() {

        return this.differentSquares;
    }

    private Properties(PropertiesBuilder builder) {

        this.cost = builder.cost;
        this.activated = builder.activated;

        this.maxTargets = builder.maxTargets;
        this.sameAsFather = builder.sameAsFather;
        this.sameAsPlayer = builder.sameAsPlayer;
        this.targetView = builder.targetView;
        this.seenByActive = builder.seenByActive;

        this.minDist = builder.minDist;
        this.maxDist = builder.maxDist;
        this.cardinal = builder.cardinal;
        this.throughWalls = builder.throughWalls;
        this.differentSquares = builder.differentSquares;
    }

    public static class PropertiesBuilder {

        private List<Ammo> cost;
        private Boolean activated = null;

        private Integer maxTargets = null;
        private List<Boolean> sameAsFather = null;
        private boolean sameAsPlayer = false;
        private Boolean targetView = null;
        private Boolean seenByActive = null;

        private Integer minDist = null;
        private Integer maxDist = null;
        private boolean cardinal = false;
        private boolean throughWalls = false;
        private boolean differentSquares = false;

        public PropertiesBuilder() {
        }

        public PropertiesBuilder setCost(List<Ammo> cost) {

            this.cost = cost;
            return this;
        }

        public PropertiesBuilder setActivated(Boolean activated) {

            this.activated = activated;
            return this;
        }

        public PropertiesBuilder setMaxTargets(Integer maxTargets) {

            this.maxTargets = maxTargets;
            return this;
        }

        public PropertiesBuilder setSameAsFather(List<Boolean> sameAsFather) {

            this.sameAsFather = sameAsFather;
            return this;
        }

        public PropertiesBuilder setSameAsPlayer(boolean sameAsPlayer) {

            this.sameAsPlayer = sameAsPlayer;
            return this;
        }

        public PropertiesBuilder setTargetView(Boolean targetView) {

            this.targetView = targetView;
            return this;
        }

        public PropertiesBuilder setSeenByActive(Boolean seenByActive) {

            this.seenByActive = seenByActive;
            return this;
        }

        public PropertiesBuilder setMinDist(Integer minDist) {

            this.minDist = minDist;
            return this;
        }

        public PropertiesBuilder setMaxDist(Integer maxDist) {

            this.maxDist = maxDist;
            return this;
        }

        public PropertiesBuilder setCardinal(boolean cardinal) {

            this.cardinal = cardinal;
            return this;
        }

        public PropertiesBuilder setThroughWalls(boolean throughWalls) {

            this.throughWalls = throughWalls;
            return this;
        }

        public PropertiesBuilder setDifferentSquares(boolean differentSquares) {

            this.differentSquares = differentSquares;
            return this;
        }

        public Properties build() {

            return new Properties(this);
        }
    }
}
