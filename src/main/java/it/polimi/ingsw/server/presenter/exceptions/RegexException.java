package it.polimi.ingsw.server.presenter.exceptions;

public class RegexException extends  Exception {
    String errorString;

    public RegexException(String errorString){
        this.errorString=errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
