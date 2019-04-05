package it.polimi.ingsw.model.cards.effects.properties;

public class Properties {

    private EffectType effectType;

    private Integer maxTargets;
    private Boolean sameAsFather;
    private Boolean sameAsPlayer;
    private Boolean targetView;
    private Boolean seenByActive;

    private Integer minDist;
    private Integer maxDist;
    private Boolean cardinal;
    private Boolean throughWalls;

    public EffectType getEffectType() {
        return effectType;
    }

    public Integer getMaxTargets() {
        return maxTargets;
    }

    public Boolean getSameAsFather() {
        return sameAsFather;
    }

    public Boolean getSameAsPlayer() {
        return sameAsPlayer;
    }

    public Boolean getTargetView() {
        return targetView;
    }

    public Boolean getSeenByActive() {
        return seenByActive;
    }

    public Integer getMinDist() {
        return minDist;
    }

    public Integer getMaxDist() {
        return maxDist;
    }

    public Boolean getCardinal() {
        return cardinal;
    }

    public Boolean getThroughWalls() {
        return throughWalls;
    }

    public Properties(PropertiesBuilder builder) {
        this.effectType = builder.effectType;
        this.maxTargets = builder.maxTargets;
        this.sameAsFather = builder.sameAsFather;
        this.sameAsPlayer = builder.sameAsPlayer;
        this.targetView = builder.targetView;
        this.seenByActive = builder.seenByActive;
        this.minDist = builder.minDist;
        this.maxDist = builder.maxDist;
        this.cardinal = builder.cardinal;
        this.throughWalls = builder.throughWalls;
    }

    //Builder Class
    public static class PropertiesBuilder {

        private EffectType effectType;

        private Integer maxTargets = null;
        private Boolean sameAsFather = null;
        private Boolean sameAsPlayer = null;
        private Boolean targetView = null;
        private Boolean seenByActive = null;

        private Integer minDist = null;
        private Integer maxDist = null;
        private Boolean cardinal = false;
        private Boolean throughWalls = false;

        public PropertiesBuilder(Integer cost, EffectType effectType) {
            this.effectType = effectType;
        }

        public PropertiesBuilder setMaxTargets(Integer maxTargets) {
            this.maxTargets = maxTargets;
            return this;
        }

        public PropertiesBuilder setSameAsFather(Boolean sameAsFather) {
            this.sameAsFather = sameAsFather;
            return this;
        }

        public PropertiesBuilder setSameAsPlayer(Boolean sameAsPlayer) {
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

        public PropertiesBuilder setCardinal(Boolean cardinal) {
            this.cardinal = cardinal;
            return this;
        }

        public PropertiesBuilder setThroughWalls(Boolean throughWalls) {
            this.throughWalls = throughWalls;
            return this;
        }


        public Properties build() {
            return new Properties(this);
        }
    }
}
