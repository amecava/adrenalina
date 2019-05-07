package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.TurnHandler;

public class TurnChanger extends Thread {

    TurnHandler turnHandler;

    public TurnChanger(TurnHandler turnHandler) {
        this.turnHandler = turnHandler;
    }

    @Override
    public void run() {
        turnHandler.setActivePlayer(turnHandler.getPlayerList()
                .get(turnHandler.getNextPlayer(turnHandler.getActivePlayer())));
    }

}
