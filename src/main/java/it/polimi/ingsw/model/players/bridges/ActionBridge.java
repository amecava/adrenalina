package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

class ActionBridge {

    private Adrenalin adrenalin;
    private ActionStructure currentAction;

    private boolean firstPlayer;
    private boolean frenzyActions;

    private int remainingActions;
    private WeaponCard currentWeaponCard;

    private List<ActionStructure> possibleActions;
    private List<ActionStructure> actionStructureList;

    private ActionBridge(ActionBridgeBuilder builder) {

        this.adrenalin = builder.adrenalin;
        this.currentAction = builder.currentAction;

        this.firstPlayer = builder.firstPlayer;
        this.frenzyActions = builder.frenzyActions;

        this.remainingActions = builder.remainingActions;
        this.currentWeaponCard = builder.currentWeaponCard;

        this.possibleActions = builder.possibleActions;
        this.actionStructureList = builder.actionStructureList;

        this.changePossibleActions();
    }

    void setAdrenalin(Adrenalin adrenalin) {

        if (!this.adrenalin.equals(adrenalin)) {

            this.adrenalin = adrenalin;
            this.changePossibleActions();
        }
    }

    ActionStructure getCurrentAction() {

        return this.currentAction;
    }

    boolean isFirstPlayer() {

        return this.firstPlayer;
    }

    void setFirstPlayer(boolean firstPlayer) {

        this.firstPlayer = firstPlayer;
    }

    boolean isFrenzyActions() {

        return this.frenzyActions;
    }

    void setFrenzyActions(boolean frenzyActions) {

        this.frenzyActions = frenzyActions;
    }

    int getRemainingActions() {

        return this.remainingActions;
    }

    void setRemainingActions(int remainingActions) {

        this.remainingActions = remainingActions;
    }

    WeaponCard getCurrentWeaponCard() {

        return this.currentWeaponCard;
    }

    void setCurrentWeaponCard(WeaponCard weaponCard) {

        this.currentWeaponCard = weaponCard;
    }

    void selectAction(int number) throws IllegalActionException {

        if (number >= this.possibleActions.size()) {

            throw new IllegalActionException("Action not present!");
        }

        this.currentAction = this.possibleActions.get(number);
    }

    void endAction() {

        if (this.currentAction != null) {

            this.currentAction.endAction(4, true);

            this.currentAction = null;
            this.currentWeaponCard = null;
        }
    }

    private ActionStructure findAction(int id) {

        return this.actionStructureList.stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElse(null);
    }

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

    public static class ActionBridgeBuilder {

        private Adrenalin adrenalin = Adrenalin.NORMAL;
        private ActionStructure currentAction;

        private boolean firstPlayer = false;
        private boolean frenzyActions = false;

        private int remainingActions = 0;
        private WeaponCard currentWeaponCard;

        private List<ActionStructure> possibleActions = new ArrayList<>();
        private List<ActionStructure> actionStructureList = new ArrayList<>();

        public ActionBridge build() {

            this.readActionsFromJason();

            return new ActionBridge(this);
        }

        private void readActionsFromJason() {

            try (JsonReader reader = Json.createReader(
                    new FileReader("lib/actions/PlayerActions.json"))) {

                JsonArray jActionsArray = reader.readArray();

                jActionsArray.stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(x ->
                                this.actionStructureList.add(
                                        new ActionStructure.ActionStructureBuilder(x).build())
                        );
            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }
    }
}
