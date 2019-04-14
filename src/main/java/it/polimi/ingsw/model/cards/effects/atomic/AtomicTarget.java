package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import java.util.ArrayList;
import java.util.List;

public class AtomicTarget {

    private Square destination;

    private List<Target> targetList;

    public AtomicTarget() {

        this.targetList = new ArrayList<>();
    }

    public AtomicTarget(Square destination) {

        this.destination = destination;
        this.targetList = new ArrayList<>();
    }

    public AtomicTarget(List<Target> targetList) {

        this.targetList = targetList;
    }

    public AtomicTarget(Square destination, List<Target> targetList) {

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
