package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.effects.TargetType;
import java.security.InvalidParameterException;

public enum AtomicType {

    DAMAGE,
    MARK,
    MOVE;

    private AtomicEffect movePlayer = new MovePlayer();
    private AtomicEffect playerDamage = new PlayerDamage();
    private AtomicEffect playerMark = new PlayerMark();
    private AtomicEffect roomDamage = new RoomDamage();
    private AtomicEffect squareDamage = new SquareDamage();
    private AtomicEffect squareMark = new SquareMark();

    public AtomicEffect getAtomicEffect(TargetType targetType) {

        switch (targetType) {

            case MOVE:
            case RECOIL:
            case PLAYER:

                if (this.equals(DAMAGE)) {

                    return this.playerDamage;
                }

                if (this.equals(MARK)) {

                    return this.playerMark;
                }

                if (this.equals(MOVE)) {

                    return this.movePlayer;
                }

                throw new InvalidParameterException();

            case SQUARE:

                if (this.equals(DAMAGE)) {

                    return this.squareDamage;
                }

                if (this.equals(MARK)) {

                    return this.squareMark;
                }

                throw new InvalidParameterException();

            case ROOM:

                if (this.equals(DAMAGE)) {

                    return this.roomDamage;
                }

                throw new InvalidParameterException();
        }

        throw new InvalidParameterException();
    }
}
