package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import java.util.ArrayList;
import java.util.List;

public class EffectArgument {

    private Square destination;
    private List<Target> targetList;

    private boolean weaponCard = true;

    public EffectArgument() {

        this.targetList = new ArrayList<>();
    }

    public EffectArgument(Square destination) {

        this.destination = destination;
        this.targetList = new ArrayList<>();
    }

    public EffectArgument(List<Target> targetList) {

        this.targetList = targetList;
    }

    public EffectArgument(Square destination, List<Target> targetList) {

        this.destination = destination;
        this.targetList = targetList;
    }

    public Square getDestination() {

        return this.destination;
    }

    public void setDestination(Square destination) {

        this.destination = destination;
    }

    public List<Target> getTargetList() {

        return this.targetList;
    }

    public void setTargetList(List<Target> targetList) {

        this.targetList = targetList;
    }

    public void appendTarget(Target target) {

        this.targetList.add(target);
    }

    public boolean isWeaponCard() {

        return this.weaponCard;
    }

    public void setWeaponCard(boolean weaponCard) {

        this.weaponCard = weaponCard;
    }

    public int getArgs() {

        if (this.destination == null && this.targetList.isEmpty()) {

            return 0;
        }

        if (this.destination == null || this.targetList.isEmpty()) {

            return 1;
        }

        return 2;
    }
}
