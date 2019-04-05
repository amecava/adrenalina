package it.polimi.ingsw.model.cards.effects.properties;

public class PropertiesException extends Exception {

    private String message;

    public PropertiesException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
