package it.polimi.ingsw.model.exceptions;

public class IllegalActionException extends Exception{
    private String string;
    public IllegalActionException(String string){
        this.string=string;
    }

}
