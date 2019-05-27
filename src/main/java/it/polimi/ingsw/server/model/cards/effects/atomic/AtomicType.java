package it.polimi.ingsw.server.model.cards.effects.atomic;

public enum AtomicType {

    DAMAGE,
    MARK,
    MOVE;

    private AtomicEffect movePlayer = new AtomicMove();
    private AtomicEffect playerDamage = new AtomicDamage();
    private AtomicEffect playerMark = new AtomicMark();

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
