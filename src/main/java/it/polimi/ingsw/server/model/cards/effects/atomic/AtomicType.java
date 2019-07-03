package it.polimi.ingsw.server.model.cards.effects.atomic;

public enum AtomicType {

    /**
     * An atomic effect can be a damage: when the player gets one damage.
     */
    DAMAGE,

    /**
     * An atomic effect can be a mark: when the player gets one mark.
     */
    MARK,

    /**
     * An atomic effect can be a move: when the player is moved from a square to another.
     */
    MOVE;

    /**
     * The object corresponding to "MOVE".
     */
    private AtomicEffect movePlayer = new AtomicMove();

    /**
     * The object corresponding to "DAMAGE".
     */
    private AtomicEffect playerDamage = new AtomicDamage();

    /**
     * The object corresponding to "MARK".
     */
    private AtomicEffect playerMark = new AtomicMark();

    /**
     * Gets the AtomicEffect object corresponding to the enum value.
     *
     * @return The AtomicEffect object corresponding to the enum value.
     */
    public AtomicEffect getAtomicEffect() {

        if (this.equals(DAMAGE)) {

            return this.playerDamage;
        }

        if (this.equals(MARK)) {

            return this.playerMark;
        }

        return this.movePlayer;
    }
}
