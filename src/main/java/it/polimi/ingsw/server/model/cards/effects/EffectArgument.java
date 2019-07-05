package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Target;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class hold all the information needed to execute an effect, in every possible case.
 */
public class EffectArgument implements Serializable {

    /**
     * The destination of the Target in the targetList.
     */
    private Square destination;

    /**
     * The list of Target. It can be a list of Square, a list with one or more Player or a list with
     * one Room.
     */
    private List<Target> targetList;

    /**
     * A boolean that distinguishes weaponCards effect by effects of actions or power ups.
     */
    private boolean weaponCard = true;

    /**
     * Creates the EffectArgument.
     */
    public EffectArgument() {

        this.targetList = new ArrayList<>();
    }

    /**
     * Creates the EffectArgument with the destination Square.
     *
     * @param destination The square destination.
     */
    public EffectArgument(Square destination) {

        this.destination = destination;
        this.targetList = new ArrayList<>();
    }

    /**
     * Creates the EffectArgument with the list of Target.
     * @param targetList The list of Target.
     */
    public EffectArgument(List<Target> targetList) {

        this.targetList = targetList;
    }

    /**
     * Creates the EffectArgument with the destination Square and the list of Target.
     * @param destination  The destination Square.
     * @param targetList The list of Target.
     */
    public EffectArgument(Square destination, List<Target> targetList) {

        this.destination = destination;
        this.targetList = targetList;
    }

    /**
     * Gets the destination.
     * @return The Square destination.
     */
    public Square getDestination() {

        return this.destination;
    }

    /**
     * Sets the destination.
     * @param destination The destination.
     */
    public void setDestination(Square destination) {

        this.destination = destination;
    }

    /**
     * Gets the targetList.
     * @return The list of Target.
     */
    public List<Target> getTargetList() {

        return this.targetList;
    }

    /**
     * Sets the targetList.
     * @param targetList The List of Target.
     */
    public void setTargetList(List<Target> targetList) {

        this.targetList = targetList;
    }

    /**
     * Adds a target to the target list.
     * @param target The Target to add.
     */
    public void appendTarget(Target target) {

        this.targetList.add(target);
    }

    /**
     * Checks if is weaponCard.
     * @return The boolean value.
     */
    public boolean isWeaponCard() {

        return this.weaponCard;
    }

    /**
     * Sets the weaponCard value.
     * @param weaponCard The boolean value.
     */
    public void setWeaponCard(boolean weaponCard) {

        this.weaponCard = weaponCard;
    }

    /**
     * Gets the args needed for this EffectArgument.
     * @return The double value.
     */
    public double getArgs() {

        if (this.destination == null && this.targetList.isEmpty()) {

            return 0;
        }

        if (this.destination != null && this.targetList.isEmpty()) {

            return 0.5;
        }

        if (this.destination == null) {

            return 1;
        }

        return 2;
    }
}
