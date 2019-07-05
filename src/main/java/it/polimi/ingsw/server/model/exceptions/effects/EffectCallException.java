package it.polimi.ingsw.server.model.exceptions.effects;

public class EffectCallException extends EffectException {

    /**
     * It is thrown when someone's trying to execute a wrong effect.
     * @param message The related message.
     */
    public EffectCallException(String message) {
        super(message);
    }

}