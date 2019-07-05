package it.polimi.ingsw.server.model.exceptions.effects;

public class EffectUsedException extends EffectException {

    /**
     * It is thrown when someone is trying to use an effect that has already been used.
     * @param message The related message.
     */
    public EffectUsedException(String message) {
        super(message);
    }

}