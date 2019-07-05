package it.polimi.ingsw.server.model.exceptions.effects;

public class EffectException extends Exception {

    /**
     * An effect related to the Effect class.
     * @param message A message that explains what went wrong.
     */
    public EffectException(String message) {

        super(message);
    }

}
