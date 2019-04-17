package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.exceptions.IlligalActionException;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import java.util.ArrayList;
import java.util.List;

class ActionBridge {

    private WeaponCard weaponCard;
    private Adrenalin adrenalin=Adrenalin.NORMAL;
    private List<ActionStructure> actionStructureList = new ArrayList<>();
    private List<ActionStructure> nonUsableActions = new ArrayList<>();
    private List<ActionStructure> returnList = new ArrayList<>();
    private ActionStructure currentAction= new ActionStructure();
    private EffectHandler effectHandler;


    ActionBridge() {
        // crei tutte le azioni possibili
        // dopo di che setto le azioni che puoi fare in base al tuo stato di adrenalina!!
        this.changePossibleActions();
    }
    public List<ActionStructure> getActionStructureList(){
        returnList.clear();
        returnList.addAll(actionStructureList);
        return  returnList;
    }
    public void setEffectHandler (EffectHandler effectHandler){
        this.effectHandler=effectHandler;
    }
    public void selectAction ( int number ) throws IlligalActionException {
        if ( number <= actionStructureList.size()){
            currentAction=actionStructureList.get(number);
        }
        else throw  new IlligalActionException("non valid number of action  please insert a number from 0 to " + actionStructureList.size());
    }

    public void setCard(WeaponCard weaponCard) throws CardException, IlligalActionException {
        if ( currentAction.isShoot()==null || !currentAction.isShoot() ){
            throw new IlligalActionException(" you can't shoot");
        }
        weaponCard.activateCard();
        this.weaponCard = weaponCard;
        this.currentAction.endAction(4, false);
    }
    public void useEffectType(EffectType effectType, AtomicTarget atomicTarget)
            throws PropertiesException, EffectException {

    }

    public void  reload ( Card card) throws IlligalActionException {
        if (this.currentAction.isReload()==null ||  !this.currentAction.isReload()){
            throw new IlligalActionException(" you can't reload!!");
        }
        else {
           // card.reload();
            this.currentAction.endAction(3, false );
        }
    }
    public void  collect () throws IlligalActionException{
        if (this.currentAction.isCollect()==null || !this.currentAction.isCollect()){
            throw  new IlligalActionException(" you can't collect from square!!! ");
        }
        this.currentAction.endAction(2, false );
        //this.effectHandler.getActivePlayer().collect();
    }
    public void moove ( AtomicTarget atomicTarget) throws  IlligalActionException, EffectException, PropertiesException {
        if (this.currentAction.getMoove()==null || !this.currentAction.getMoove() ){
            throw new IlligalActionException(" you can't move yourself right now!!!");
        }
        effectHandler.useEffect(this.currentAction.getEffect(), atomicTarget);
        this.currentAction.setEffectAsUsed();
        this.currentAction.endAction(1, false );
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
        switch (this.adrenalin){
            case NORMAL:
                break;
            case FIRSTADRENALIN:
                break;
            case SECONDADRENALIN:
                break;
        }




    }
}
