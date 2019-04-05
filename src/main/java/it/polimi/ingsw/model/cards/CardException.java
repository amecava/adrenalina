package it.polimi.ingsw.model.cards;

public class CardException extends Exception {

    private String message;

    public CardException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
