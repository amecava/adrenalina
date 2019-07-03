package it.polimi.ingsw.server.model.exceptions.properties;

public class SquareDistanceException extends PropertiesException {

    /**
     * It is thrown when an effect is trying to be executed on a target that is in a square too far
     * away or too close to the player.
     * @param message The related message.
     */
    public SquareDistanceException(String message) {

        super(message);
    }

}