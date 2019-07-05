package it.polimi.ingsw.server.model.exceptions.effects;

/**
 * It is thrown when someone's trying to execute a wrong effect.
 */
public class EffectCallException extends EffectException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public EffectCallException(String message) {
        super(message);
    }

}