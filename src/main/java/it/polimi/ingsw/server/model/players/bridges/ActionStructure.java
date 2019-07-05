package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.cards.effects.Effect;
import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * A structure to incapsulate the single action with its flag.
 */
public class ActionStructure implements Serializable {

    /**
     * id to identify the created action
     */
    private int id;

    /**
     * expresses if in the created action is possible to move
     */
    private Boolean move;

    /**
     * expresses if in the created action is possible to collect
     */
    private Boolean collect;

    /**
     * expresses if in the created action is possible to reload
     */
    private Boolean reload;

    /**
     * expresses if in the created action is possible to shoot
     */
    private Boolean shoot;

    /**
     * expresses how many steps can the player do if this action is selected
     */
    private Effect effect;

    /**
     * creates a specific action based on the player adrenalin and frenzy state
     *
     * @param builder a builder object that holds all the correct parameters for the creation of the
     * action
     */
    private ActionStructure(ActionStructureBuilder builder) {

        this.id = builder.id;

        this.move = builder.move;
        this.collect = builder.collect;
        this.reload = builder.reload;
        this.shoot = builder.shoot;

        this.effect = builder.effect;
    }

    /**
     * @return the unique id of the action
     */
    int getId() {

        return this.id;
    }

    /**
     * @return true if in this action is possible to move
     */
    public Boolean getMove() {

        return this.move;
    }

    /**
     * @return true if in this action is possible to collect
     */

    public Boolean isCollect() {

        return this.collect;
    }

    /**
     * @return true if in this action is possible to reload
     */
    public Boolean isReload() {

        return this.reload;
    }

    /**
     * @return true if in this action is possible to shoot
     */
    public Boolean isShoot() {

        return this.shoot;
    }

    public Effect getEffect() {

        return this.effect;
    }

    /**
     * sets as used the move effect
     */
    public void setEffectAsUsed() {

        this.effect.setUsed(false);
    }

    /**
     * ends the current action or sets as used some of its features such as reload or shoot
     *
     * @param limit number of parameter of the current action  that need to change state from free
     * to used or the opposite
     * @param change the state that the features need to go in
     */
    public void endAction(int limit, boolean change) {
        /**
         * index to keep track of the current feature
         */
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

        /**
         * a json object containing all the parameters for the features of the actions
         */
        private JsonObject jActionObject;

        /**
         * id to identify the created action
         */
        private int id;

        /**
         * expresses if the current action has the feature of moving
         */
        private Boolean move;
        /**
         * expresses if the current action has the feature of collecting
         */
        private Boolean collect;
        /**
         * expresses if the current action has the feature of reloading
         */
        private Boolean reload;
        /**
         * expresses if the current action has the feature of shooting
         */
        private Boolean shoot;
        /**
         * expresses how many steps can the player do in the current action
         */
        private Effect effect;

        /**
         * creates the action builder in order to create the actions
         *
         * @param jActionObject a json file where all the features af all actions are saved
         */
        ActionStructureBuilder(JsonObject jActionObject) {

            this.jActionObject = jActionObject;
        }

        /**
         * builds the action builder by taking all the parameters of the json file and using them to
         * create action features
         */
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

    /**
     * creates a representation of the object in a json object
     *
     * @return a json object that is a representation of the current object
     */
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("id", this.id)
                .add("move", move != null ? effect.getMaxDist() : 0)
                .add("collect", collect != null)
                .add("reload", reload != null)
                .add("shoot", shoot != null)
                .build();
    }
}
