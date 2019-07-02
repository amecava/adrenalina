package it.polimi.ingsw.server.model.board.rooms;

public enum Connection {

    /**
     * If between two adjacent squares there is a wall. It means you can't move from one square to
     * the other.
     */
    WALL,

    /**
     * If between two adjacent squares there is not a wall (so it is possible to move/shoot from one
     * to the other) but they are in different rooms.
     */
    DOOR,

    /**
     * When, following a direction from a square you reach the end of the map. "The next square in
     * this direction is null".
     */
    ENDMAP,

    /**
     * If two squares are adjacent and they are in the same room.
     */
    SQUARE
}
