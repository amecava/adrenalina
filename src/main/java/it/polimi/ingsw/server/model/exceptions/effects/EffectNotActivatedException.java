package it.polimi.ingsw.server.model.exceptions.effects;

/**
 * It is thrown when someone is trying to execute an effect that has not been activated.
 */
public class EffectNotActivatedException extends EffectException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public EffectNotActivatedException(String message) {
        super(message);
    }

}