package it.polimi.ingsw.server.model.cards.effects;

/**
 * An enum to differentiate target types directly.
 */
public enum TargetType {

    /**
     * If the target of an effect is a Player.
     */
    PLAYER,

    /**
     * If the target of an effect is a Square.
     */
    SQUARE,

    /**
     * If the target of an effect is a Room.
     */
    ROOM,

    /**
     * If the effect consists in moving a player from a square to another.
     */
    MOVE,

    /**
     * If an effect consists in moving a Player after hitting him.
     */
    RECOIL
}
