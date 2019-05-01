package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.model.exceptions.cards.SquareTypeException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.exceptions.endGameException.FrenzyRegenerationException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.List;

public class TurnHandler {

    private boolean frenzy;
    private boolean gameStarted;
    private Board board;
    private Player activePlayer;
    private Player firstFrenzyPlayer;
    private List<Player> playerList;
    private PointHandler pointHandler;
    private int remainingActions;
    private EffectHandler effectHandler;

    public TurnHandler(Board board,
            PointHandler pointHandler) {
        this.board = board;
        this.pointHandler = pointHandler;
        this.playerList = pointHandler.getPlayerList();
        this.frenzy = false;
        this.gameStarted = false;
        this.remainingActions = 2;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    private void frenzyRound() throws EndGameException {

        if (!this.frenzy) {

            this.frenzy = true;
            Player tempPlayer = this.playerList.get(this.getNextPlayer(this.activePlayer));

            while (!tempPlayer.isFirstPlayer()) {

                tempPlayer.setAdrenalin(Adrenalin.FIRSTFRENZY);
                tempPlayer = this.playerList.get(this.getNextPlayer(tempPlayer));
            }

            while (tempPlayer != this.activePlayer) {

                tempPlayer.setAdrenalin(Adrenalin.SECONDFRENZY);
                tempPlayer = playerList.get(this.getNextPlayer(tempPlayer));
            }

            this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
            this.firstFrenzyPlayer = activePlayer;
        } else {

            if (playerList.get(this.getNextPlayer(this.activePlayer)) == firstFrenzyPlayer) {

                throw new EndGameException("end game!!!!", pointHandler.getWinner());
            }
            this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        }
        if (this.activePlayer.getAdrenalin().equals(Adrenalin.FIRSTFRENZY)) {

            this.remainingActions = 2;
        } else {

            this.remainingActions = 1;
        }
    }

    private int getNextPlayer(Player activePlayer) {

        int nextPlayer = playerList.indexOf(activePlayer) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return nextPlayer;
    }

    // presenter needs to catch the end of game exception and block everything !!!!!!!!!!!!!!!!!!!!!
    //endOfTurn called by the presenter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void endOfTurn()
            throws EndGameException { //should be protected but for testing we use public!!
        this.endAction();
        try {
            pointHandler.checkIfDead();
        } catch (FrenzyRegenerationException e) {
            this.frenzyRound();
            return;
        }
        this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        this.remainingActions = 2;
    }

    public void endofTurnWithReload(WeaponCard weaponCard)
            throws EndGameException, IllegalActionException {
        this.activePlayer.reload(weaponCard);
        this.endOfTurn();
    }

    public void startGame(Player firstPlayer) throws IllegalAccessException {
        if (!gameStarted) {
            gameStarted = true;
            firstPlayer.setFirstPlayer(true);
            this.setActivePlayer(firstPlayer);
        } else {
            throw new IllegalAccessException("game already started!");
        }
    }

    //for all possible actions the presenter must see if the player that is  calling turnHandler is the activePlayer
    public void selectAction(int actionId) throws IllegalActionException {
        if (remainingActions > 0) {
            this.activePlayer.selectAction(actionId - 1);
            this.remainingActions--;
        } else {
            throw new IllegalActionException(
                    "no remaining actions, please type end of turn, and if you want the gun you would like to reload");
        }
    }

    public void activateCard(WeaponCard weaponCard)
            throws CardException, IllegalActionException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.activateCard(weaponCard);
    }

    public void useCard(EffectType effectType, EffectArgument effectArgument)
            throws PropertiesException, EffectException, IllegalActionException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.useCard(effectType, effectArgument);
    }

    public void reload(Card weaponCard) throws IllegalActionException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.reload(weaponCard);
    }

    public void collectAmmo()
            throws IllegalActionException, SquareTypeException, EmptySquareException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        AmmoTile ammoTile = this.activePlayer.collectAmmo();
        this.board.pushAmmoTile(ammoTile);
        try {
            this.activePlayer.getCurrentPosition().addTool(board.getAmmoTile());
        } catch (NullPointerException e) {
            return;
        }
    }

    public void collectWeapon(int cardId)
            throws IllegalActionException, EmptySquareException, SquareTypeException, FullHandException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.collectWeapon(cardId);
        try {
            this.activePlayer.getCurrentPosition().addTool(board.getWeaponCard());
        } catch (NullPointerException e) {
            return;
        }
    }

    public void collectAndDiscard(int discardCard, int getCard)
            throws IllegalActionException, CardException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.collectAndDiscard(discardCard, getCard);
    }

    public void move(EffectArgument effectArgument)
            throws IllegalActionException, EffectException, PropertiesException {
        if (this.activePlayer.getCurrentAction() == null) {
            throw new IllegalActionException(" please select the action you would like to use!!!");
        }
        this.activePlayer.move(effectArgument);
    }

    public void endAction() {
        this.activePlayer.endFirstAction();
    }


}
