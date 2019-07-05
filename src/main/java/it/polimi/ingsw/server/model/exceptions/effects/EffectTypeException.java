package it.polimi.ingsw.server.model.exceptions.effects;

/**
 * It is thrown when someone is trying to execute an effect that has a different type from the possible ones..
 */
public class EffectTypeException extends EffectException {

    /**
     * Creates the Exception.
     * @param message
     */
    public EffectTypeException(String message) {
        super(message);
    }

}
