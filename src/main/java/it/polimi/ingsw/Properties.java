package it.polimi.ingsw;

public class Properties {
    private Integer cost;
    private String effectType;

    private Integer maxTargets = null;
    private Boolean sameAsFather = null;
    private Boolean targetView = null;
    private Boolean seenByActive = null;

    private Integer minDist = null;
    private Integer maxDist = null;
    private Boolean cardinal = false;
    private Boolean throughWalls = false;

    private Boolean doubleSelect = false;

    public Integer getCost() {
        return cost;
    }

    public String getEffectType() {
        return effectType;
    }

    public Integer getMaxTargets() {
        return maxTargets;
    }

    public Boolean getSameAsFather() {
        return sameAsFather;
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

    public Boolean getDoubleSelect() {
        return doubleSelect;
    }

    public Properties(PropertiesBuilder builder) {
        this.cost = builder.cost;
        this.effectType = builder.effectType;
        this.maxTargets = builder.maxTargets;
        this.sameAsFather = builder.sameAsFather;
        this.targetView = builder.targetView;
        this.seenByActive = builder.seenByActive;
        this.minDist = builder.minDist;
        this.maxDist = builder.maxDist;
        this.cardinal = builder.cardinal;
        this.throughWalls = builder.throughWalls;
        this.doubleSelect = builder.doubleSelect;
    }

    //Builder Class
    public static class PropertiesBuilder{
        private Integer cost;
        private String effectType;

        private Integer maxTargets = null;
        private Boolean sameAsFather = null;
        private Boolean targetView = null;
        private Boolean seenByActive = null;

        private Integer minDist = null;
        private Integer maxDist = null;
        private Boolean cardinal = false;
        private Boolean throughWalls = false;

        private Boolean doubleSelect = false;

        public PropertiesBuilder(Integer cost, String effectType) {
            this.cost = cost;
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

        public PropertiesBuilder setDoubleSelect(Boolean doubleSelect) {
            this.doubleSelect = doubleSelect;
            return this;
        }

        public Properties build(){
            return new Properties(this);
        }
    }
}
