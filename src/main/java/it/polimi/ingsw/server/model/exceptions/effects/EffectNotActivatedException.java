package it.polimi.ingsw.server.model.exceptions.effects;

public class EffectNotActivatedException extends EffectException {

    /**
     * It is thrown when someone is trying to execute an effect that has not been activated.
     * @param message The related message.
     */
    public EffectNotActivatedException(String message) {
        super(message);
    }

}