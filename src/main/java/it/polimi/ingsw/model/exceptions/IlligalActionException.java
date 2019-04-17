package it.polimi.ingsw.model.exceptions;

public class IlligalActionException   extends Exception{
    private String string;
    public IlligalActionException (String string){
        this.string=string;
    }

}
