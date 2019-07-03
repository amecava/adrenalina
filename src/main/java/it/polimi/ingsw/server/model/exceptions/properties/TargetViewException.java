package it.polimi.ingsw.server.model.exceptions.properties;

public class TargetViewException extends PropertiesException {

    /**
     * It is thrown when someone's trying to execute an effect on a target he should see but can't,
     * or vice versa.
     *
     * @param message The related message.
     */
    public TargetViewException(String message) {

        super(message);
    }

}