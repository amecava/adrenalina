package it.polimi.ingsw.model.exceptions.jacop;

public class FrenzyRegenerationException extends Throwable {
    String description;
    public FrenzyRegenerationException (String description){
        this.description=description;
    }
}
