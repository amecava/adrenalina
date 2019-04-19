package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.exceptions.IlligalActionException;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.model.exceptions.cards.SquareTypeException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

class ActionBridge {

    private WeaponCard weaponCard;
    private Adrenalin adrenalin;
    private List<ActionStructure> actionStructureList;
    private List<ActionStructure> nonUsableActions;
    private List<ActionStructure> returnList;
    private ActionStructure currentAction;
    private EffectHandler effectHandler;


    private ActionBridge(ActionBridgeBuilder builder) {

        this.weaponCard = builder.weaponCard;
        this.adrenalin = builder.adrenalin;
        this.actionStructureList = builder.actionStructureList;
        this.nonUsableActions = builder.nonUsableActions;
        this.returnList = builder.returnList;
        this.currentAction = builder.currentAction;
        this.effectHandler = builder.effectHandler;
        this.changePossibleActions();
    }

    public List<ActionStructure> getActionStructureList() {
        returnList.clear();
        returnList.addAll(actionStructureList);
        return returnList;
    }

    public void setEffectHandler(EffectHandler effectHandler) {
        this.effectHandler = effectHandler;
    }

    public void selectAction(int number) throws IlligalActionException {
        if (number <= actionStructureList.size()) {
            currentAction = actionStructureList.get(number);
        } else {
            throw new IlligalActionException(
                    "non valid number of action  please insert a number from 0 to "
                            + actionStructureList.size());
        }
    }

    public void activateCard(WeaponCard weaponCard) throws CardException, IlligalActionException {
        if (currentAction.isShoot() == null || !currentAction.isShoot()) {
            throw new IlligalActionException(" you can't shoot");
        }
        weaponCard.activateCard();
        this.weaponCard = weaponCard;
        this.currentAction.endAction(4, false);
    }

    public void useCard(EffectType effectType, AtomicTarget atomicTarget)
            throws PropertiesException, EffectException {
        this.weaponCard.useCard(effectType, atomicTarget);
    }

    public void reload(Card card) throws IlligalActionException {
        if (this.currentAction.isReload() == null || !this.currentAction.isReload()) {
            throw new IlligalActionException(" you can't reload!!");
        } else {
            // card.reload();
            this.currentAction.endAction(3, false);
        }
    }

    public AmmoTile collectAmmo() throws IlligalActionException, SquareTypeException, EmptySquareException {
        if (this.currentAction.isCollect() == null || !this.currentAction.isCollect()) {
            throw new IlligalActionException(" you can't collect from square with the chosen action!!!");
        }
        AmmoTile ammoTile=this.effectHandler.getActivePlayer().collect();// turn handler must put it back in the deck at the end of the turn
        this.currentAction.endAction(2, false);
        return ammoTile;
    }

    public void collectweapon (int cardId )
            throws IlligalActionException, EmptySquareException, SquareTypeException, FullHandException {
        if (this.currentAction.isCollect() == null || !this.currentAction.isCollect()) {
            throw new IlligalActionException(" you can't collect from square with the chosen action!!!");
        }
        this.effectHandler.getActivePlayer().collect(cardId);
        this.currentAction.endAction(2, false);
    }

    public void collectAndDiscard ( int discardCardId, int collectCard )
            throws IlligalActionException, SquareTypeException, EmptySquareException, CardNotFoundException {
        if (this.currentAction.isCollect() == null || !this.currentAction.isCollect()) {
            throw new IlligalActionException(" you can't collect from square with the chosen action!!!");
        }
        Card playerCard=this.effectHandler.getActivePlayer().removeCardFromHand(discardCardId);
        this.effectHandler.getActivePlayer().collect(playerCard, collectCard);
        this.currentAction.endAction(2, false);
    }
    public void moove(AtomicTarget atomicTarget)
            throws IlligalActionException, EffectException, PropertiesException {
        if (this.currentAction.getMove() == null || !this.currentAction.getMove()) {
            throw new IlligalActionException(" you can't move yourself right now!!!");
        }
        effectHandler.useEffect(this.currentAction.getEffect(), atomicTarget);
        this.currentAction.setEffectAsUsed();
        this.currentAction.endAction(1, false);
    }

    public void endFirstAction() {
        this.currentAction.endAction(4, true);
    }


    public void setAdrenalin(Adrenalin adrenalin) {// at the start of every turn the turnHandler will set the right adrenalin

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
                break;
            case FIRSTADRENALIN:
                this.actionStructureList
                        .set(this.actionStructureList.indexOf(this.findAction(2)),
                                this.findAction(4));
                break;
            case SECONDADRENALIN:
                if (this.actionStructureList.indexOf(this.findAction(2))!=-1) {
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

    public ActionStructure findAction(int id) {
        return this.nonUsableActions.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public static class ActionBridgeBuilder {

        private WeaponCard weaponCard;
        private Adrenalin adrenalin = Adrenalin.NORMAL;
        private List<ActionStructure> actionStructureList = new ArrayList<>();
        private List<ActionStructure> nonUsableActions = new ArrayList<>();
        private List<ActionStructure> returnList = new ArrayList<>();
        private ActionStructure currentAction;
        private EffectHandler effectHandler;

        public ActionBridgeBuilder(EffectHandler effectHandler) {
            this.effectHandler = effectHandler;
        }

        public ActionBridge build() {
            this.readActionsFromJason();
            return new ActionBridge(this);
        }

        public void readActionsFromJason() {

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
