package it.polimi.ingsw.model.players.bridges;


import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class ActionBridge {

    private Adrenalin adrenalin;
    private List<ActionStructure> actionStructureList;
    private List<ActionStructure> nonUsableActions;
    private List<ActionStructure> returnList;
    private ActionStructure currentAction;


    private ActionBridge(ActionBridgeBuilder builder) {
        this.adrenalin = builder.adrenalin;
        this.actionStructureList = builder.actionStructureList;
        this.nonUsableActions = builder.nonUsableActions;
        this.returnList = builder.returnList;
        this.currentAction = builder.currentAction;
        this.changePossibleActions();
    }

    public List<ActionStructure> getActionStructureList() {
        returnList.clear();
        returnList.addAll(actionStructureList);
        return returnList;
    }

    public void selectAction(int number) throws IllegalActionException {
        if (number < actionStructureList.size()) {
            currentAction = actionStructureList.get(number);
        } else {
            throw new IllegalActionException(
                    "non valid number of action  please insert a number from 1 to "
                            + actionStructureList.size());
        }
    }

    public ActionStructure getCurrentAction() {
        return currentAction;
    }


    public void endAction() {
        if (this.currentAction != null) {
            this.currentAction.endAction(4, true);
            this.currentAction = null;
        }
    }


    public void setAdrenalin(
            Adrenalin adrenalin) {// at the start of every turn the turnHandler will set the right adrenalin

        if (!this.adrenalin.equals(adrenalin)) {

            this.adrenalin = adrenalin;

            this.changePossibleActions();
        }
    }

    private void changePossibleActions() {
        switch (this.adrenalin) {
            case NORMAL:
                this.actionStructureList.clear();
                this.actionStructureList.add(this.findAction(1));
                this.actionStructureList.add(this.findAction(2));
                this.actionStructureList.add(this.findAction(3));
                this.actionStructureList.add(this.findAction(0));
                break;
            case FIRSTADRENALIN:
                this.actionStructureList
                        .set(this.actionStructureList.indexOf(this.findAction(2)),
                                this.findAction(4));
                break;
            case SECONDADRENALIN:
                if (this.actionStructureList.indexOf(this.findAction(2)) != -1) {
                    this.actionStructureList
                            .set(this.actionStructureList.indexOf(this.findAction(2)),
                                    this.findAction(4));
                }
                this.actionStructureList
                        .set(this.actionStructureList.indexOf(this.findAction(3)),
                                this.findAction(5));
                break;
            case FIRSTFRENZY:
                this.actionStructureList.clear();
                this.actionStructureList.add(this.findAction(6));
                this.actionStructureList.add(this.findAction(7));
                this.actionStructureList.add(this.findAction(8));
                break;
            case SECONDFRENZY:
                this.actionStructureList.clear();
                this.actionStructureList.add(this.findAction(9));
                this.actionStructureList.add(this.findAction(10));
                break;
        }

    }

    public  ActionStructure findAction(int id) {
        return this.nonUsableActions.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public static class ActionBridgeBuilder {

        private Adrenalin adrenalin = Adrenalin.NORMAL;
        private List<ActionStructure> actionStructureList = new ArrayList<>();
        private List<ActionStructure> nonUsableActions = new ArrayList<>();
        private List<ActionStructure> returnList = new ArrayList<>();
        private ActionStructure currentAction;


        public ActionBridge build() {
            this.readActionsFromJason();
            this.currentAction = null;
            return new ActionBridge(this);
        }

        private void readActionsFromJason() {

            try (JsonReader reader = Json
                    .createReader(new FileReader("lib/actions/PlayerActions.json"))) {

                JsonArray jActionsArray = reader.readArray();

                jActionsArray.stream()
                        .map(JsonValue::asJsonObject)
                        .forEach(x -> {
                            this.currentAction = new ActionStructure();
                            this.currentAction.setId(x.getInt("id"));
                            if (x.containsKey("move")) {
                                this.currentAction.setMove(x.getBoolean("move"));
                            }
                            if (x.containsKey("collect")) {
                                this.currentAction.setCollect(x.getBoolean("collect"));
                            }
                            if (x.containsKey("shoot")) {
                                this.currentAction.setShoot(x.getBoolean("shoot"));
                            }
                            if (x.containsKey("effect")) {
                                this.currentAction.setEffect(
                                        new Effect.EffectBuilder(x.getJsonObject("effect"))
                                                .build());
                            }

                            this.nonUsableActions.add(this.currentAction);
                        });

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }
    }
}
