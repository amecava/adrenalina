package it.polimi.ingsw;

import java.rmi.Remote;

public interface View extends Remote {

    String printToScreen();

}
