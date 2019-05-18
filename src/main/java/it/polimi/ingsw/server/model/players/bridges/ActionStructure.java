package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.cards.effects.Effect;
import javax.json.JsonObject;

public class ActionStructure {

    private int id;

    private Boolean move;
    private Boolean collect;
    private Boolean reload;
    private Boolean shoot;

    private Effect effect;

    private ActionStructure(ActionStructureBuilder builder) {

        this.id = builder.id;

        this.move = builder.move;
        this.collect = builder.collect;
        this.reload = builder.reload;
        this.shoot = builder.shoot;

        this.effect = builder.effect;
    }

    int getId() {

        return this.id;
    }

    public Boolean getMove() {

        return this.move;
    }

    public Boolean isCollect() {

        return this.collect;
    }

    public Boolean isReload() {

        return this.reload;
    }

    public Boolean isShoot() {

        return this.shoot;
    }

    public Effect getEffect() {

        return this.effect;
    }

    public void setEffectAsUsed() {

        this.effect.setUsed(false);
    }

    public void endAction(int limit, boolean change) {

        int i = 0;

        if (this.getMove() != null) {

            this.move = change;
        }

        i++;
        if (i == limit) {
            return;
        }

        if (this.isCollect() != null) {

            this.collect = change;
        }

        i++;
        if (i == limit) {
            return;
        }

        if (this.isReload() != null) {

            this.reload = change;
        }

        i++;
        if (i == limit) {
            return;
        }

        if (this.isShoot() != null) {

            this.shoot = change;
        }
    }

    static class ActionStructureBuilder {

        private JsonObject jActionObject;

        private int id;

        private Boolean move;
        private Boolean collect;
        private Boolean reload;
        private Boolean shoot;

        private Effect effect;

        ActionStructureBuilder(JsonObject jActionObject) {

            this.jActionObject = jActionObject;
        }

        ActionStructure build() {

            this.id = this.jActionObject.getInt("id");

            if (this.jActionObject.containsKey("move")) {

                this.move = this.jActionObject.getBoolean("move");
            }

            if (this.jActionObject.containsKey("collect")) {

                this.collect = this.jActionObject.getBoolean("collect");
            }

            if (this.jActionObject.containsKey("reload")) {

                this.reload = this.jActionObject.getBoolean("reload");
            }

            if (this.jActionObject.containsKey("shoot")) {

                this.shoot = this.jActionObject.getBoolean("shoot");
            }

            if (this.jActionObject.containsKey("effect")) {

                this.effect = new Effect.EffectBuilder(
                        this.jActionObject.getJsonObject("effect")).build();
            }

            return new ActionStructure(this);
        }
    }
}
