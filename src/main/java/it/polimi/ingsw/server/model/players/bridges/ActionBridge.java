package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * The Action Bridge of the player.
 */
class ActionBridge implements Serializable {

    /**
     * indicates the adrenalin state of the player
     */
    private Adrenalin adrenalin;
    /**
     * indicates the current action selected by the player
     */
    private ActionStructure currentAction;
    /**
     * expresses if the linked player is the player that started the game
     */
    private boolean firstPlayer;
    /**
     * expresses if the player has access to the frenzy actions
     */
    private boolean frenzyActions;
    /**
     * expresses if the player must respawn
     */

    private boolean respawn;
    /**
     * indicates the number of remaining actions that the player has during the current
     * turn
     */
    private int remainingActions;
    /**
     * a reference to the selected weapon card for shooting
     */
    private WeaponCard currentWeaponCard;

    /**
     * a list of all the actions the player can choose from during his turn
     */
    private List<ActionStructure> possibleActions;
    /**
     * a list of all actions in the game
     */
    private List<ActionStructure> actionStructureList;

    /**
     * creating an action bridge with the possible actions for the linked player
     * @param builder an object containing all the information in  order
     * to create a valid action bridge
     */

    private ActionBridge(ActionBridgeBuilder builder) {

        this.adrenalin = builder.adrenalin;
        this.currentAction = builder.currentAction;

        this.firstPlayer = builder.firstPlayer;
        this.frenzyActions = builder.frenzyActions;

        this.respawn = builder.respawn;
        this.remainingActions = builder.remainingActions;
        this.currentWeaponCard = builder.currentWeaponCard;

        this.possibleActions = builder.possibleActions;
        this.actionStructureList = builder.actionStructureList;

        this.changePossibleActions();
    }

    /**
     * sets the adrenalin state of the player , if the state changes than all the
     * possible actions will change
     * @param adrenalin
     */
    void setAdrenalin(Adrenalin adrenalin) {

        if (!this.adrenalin.equals(adrenalin)) {

            this.adrenalin = adrenalin;
            this.changePossibleActions();
        }
    }

    /**
     *
     * @return the adrenalin state of the player
     */

    public Adrenalin getAdrenalin() {

        return this.adrenalin;
    }

    /**
     *
     * @return the current action selected by the player
     */

    ActionStructure getCurrentAction() {

        return this.currentAction;
    }

    /**
     *
     * @return true if the selected action has the feature of shooting
     */
    boolean isShooting() {

        return this.currentWeaponCard != null;
    }

    /**
     *
     * @return true if the player is the first player of the game
     */
    boolean isFirstPlayer() {

        return this.firstPlayer;
    }

    /**
     * sets if the linked player should start the game
     * @param firstPlayer expresses if the linked player is the first player
     * of the game
     */
    void setFirstPlayer(boolean firstPlayer) {

        this.firstPlayer = firstPlayer;
    }

    /**
     *
     * @return true if the linked player has access to the frenzy actions
     */
    boolean isFrenzyActions() {

        return this.frenzyActions;
    }

    /**
     * sets if the linked player should have access to the frenzy actions
     * @param frenzyActions
     */
    void setFrenzyActions(boolean frenzyActions) {

        this.frenzyActions = frenzyActions;
    }

    /**
     *
     * @return true if the linked must respawn
     */
    boolean isRespawn() {

        return this.respawn;
    }

    /**
     * sets if the linked player must respawn
     * @param respawn true if the player must respawn , false otherwise
     */
    void setRespawn(boolean respawn) {

        this.respawn = respawn;
    }

    /**
     *
     * @return the remaining actions of the player during his turn
     */
    int getRemainingActions() {

        return this.remainingActions;
    }

    /**
     * sets the remaining actions of the player during his turn
     * @param remainingActions number of actions the player can still do during his turn
     */
    void setRemainingActions(int remainingActions) {

        this.remainingActions = remainingActions;
    }

    /**
     *
     * @return the currently selected weapon card for shooting
     */
    WeaponCard getCurrentWeaponCard() {

        return this.currentWeaponCard;
    }

    /**
     * sets the current weapon card for shooting
     * @param weaponCard a reference to the weapon card the player has cosen
     */
    void setCurrentWeaponCard(WeaponCard weaponCard) {

        this.currentWeaponCard = weaponCard;
    }

    /**
     *
     * @return the list of all the actions the player can do during his turn
     */
    List<ActionStructure> getActions() {

        return this.possibleActions;
    }

    /**
     * selects an action and makes it the current action
     * @param number of the action the player wants to select
     * @throws IllegalActionException if the player has no remaining actions or the player
     * has chosen a non valid action
     */
    void selectAction(int number) throws IllegalActionException {

        if (number >= this.possibleActions.size()) {

            throw new IllegalActionException("L'azione selezionata non è disponibile adesso.");

        } else if (this.currentAction != null) {

            throw new IllegalActionException(
                    "Prima completa l'azione che hai selezionato in precedenza scrivendo \"fineazione\".");
        }

        this.currentAction = this.possibleActions.get(number);
    }
    /*
    ends the current action
     */
    void endAction() {

        if (this.currentAction != null) {

            this.currentAction.endAction(4, true);

            this.currentAction = null;
        }
        if (this.currentWeaponCard != null) {

            this.currentWeaponCard.setLoaded(false);
            this.currentWeaponCard = null;
        }
    }

    /**
     * finds the linked action with the parameter id
     * @param id the number of the action to be selected
     * @return the action structure selected
     */
    private ActionStructure findAction(int id) {

        return this.actionStructureList.stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElse(null);
    }

    /**
     * changes the possible actions of the player based on his andrenalin state and his
     * frenzy state
     */
    private void changePossibleActions() {

        switch (this.adrenalin) {

            case NORMAL:

                this.possibleActions.clear();
                this.possibleActions.add(this.findAction(1));
                this.possibleActions.add(this.findAction(2));
                this.possibleActions.add(this.findAction(3));
                this.possibleActions.add(this.findAction(0));
                break;

            case FIRSTADRENALIN:

                this.possibleActions.set(
                        this.possibleActions.indexOf(this.findAction(2)),
                        this.findAction(4));
                break;

            case SECONDADRENALIN:

                if (this.possibleActions.indexOf(this.findAction(2)) != -1) {

                    this.possibleActions.set(
                            this.possibleActions.indexOf(this.findAction(2)),
                            this.findAction(4));
                }

                this.possibleActions.set(
                        this.possibleActions.indexOf(this.findAction(3)),
                        this.findAction(5));
                break;

            case FIRSTFRENZY:

                this.possibleActions.clear();
                this.possibleActions.add(this.findAction(6));
                this.possibleActions.add(this.findAction(7));
                this.possibleActions.add(this.findAction(8));
                break;

            case SECONDFRENZY:

                this.possibleActions.clear();
                this.possibleActions.add(this.findAction(9));
                this.possibleActions.add(this.findAction(10));
                break;
        }

    }

    /**
     * creates a json object with all the parameters for all the actions
     * @return a json object for creating all the actions
     */
    public JsonObject toJsonObject() {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        this.possibleActions.stream().map(ActionStructure::toJsonObject).forEach(arrayBuilder::add);

        return Json.createObjectBuilder()
                .add("possibleActionsArray", arrayBuilder.build())
                .add("remainingActions", this.remainingActions)
                .add("adrenalinLevel", this.adrenalin.toString())
                .add("isFirstPlayer", firstPlayer)
                .build();
    }

    /**
     * builder class that holds all the parameters for creating
     * the ActionBridge class
     */
    public static class ActionBridgeBuilder {

        /**
         * reference to the adrenalin state of the player
         */
        private Adrenalin adrenalin = Adrenalin.NORMAL;
        /**
         * current action selected by the player
         */
        private ActionStructure currentAction;
        /**
         * expresses if the player is the first player
         */
        private boolean firstPlayer = false;
        /**
         * expresses if the player has access to the frenzy actions
         */
        private boolean frenzyActions = false;
        /**
         * expresses if the player must respawn
         */
        private boolean respawn = false;
        /**
         * number of remaining actions for the player
         */
        private int remainingActions = 0;
        /**
         * current weapon card selected by the player for shooting
         */
        private WeaponCard currentWeaponCard;

        /**
         * list of all possible actions that a player can choose from
         */
        private List<ActionStructure> possibleActions = new ArrayList<>();

        /**
         * list of all actions present in game
         */
        private List<ActionStructure> actionStructureList = new ArrayList<>();
        /*
        json array containing all the parameters for all the actions
         */
        private static JsonArray object;

        static {

            InputStream in = ActionBridgeBuilder.class.getClassLoader()
                    .getResourceAsStream("PlayerActions.json");

            object = Json.createReader(in).readArray();
        }

        /**
         *
         * @return builds an action bridge
         */

        public ActionBridge build() {

            this.readActionsFromJason();

            return new ActionBridge(this);
        }

        /**
         * streams the file in order to create a json array with all the necessary
         * information to build the actionBridge
         */
        private void readActionsFromJason() {

            object.stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x ->
                            this.actionStructureList.add(
                                    new ActionStructure.ActionStructureBuilder(x).build())
                    );
        }
    }
}
