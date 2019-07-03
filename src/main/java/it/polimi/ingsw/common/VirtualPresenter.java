package it.polimi.ingsw.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualPresenter extends Remote {

    /**
     * Method that is called when the user wants to disconnect himself from the server.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    void remoteDisconnect(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to set his player id.
     *
     * @param value A serialized JsonObject with the name that the user chose.
     */
    void selectPlayerId(String value) throws RemoteException;

    /**
     * Method that is called when the user asks to create a new game.
     *
     * @param value A serialized JsonObject with the name of the new game, the number of deaths and
     * a boolean that says if the user wanted to play with the final frenzy mode.
     */
    void askCreateGame(String value) throws RemoteException;

    /**
     * Method that is called when the user asks to enter in a game that has already been created
     * either by himself or someone else.
     *
     * @param value A serialized JsonObject with the name of the game and the character he wants to
     * be.
     */
    void selectGame(String value) throws RemoteException;

    /**
     * Method that is called when the user asks to vote the board he prefers.
     *
     * @param value A serialized JsonObject with the integer of the board he wants to use.
     */
    void voteBoard(String value) throws RemoteException;

    /**
     * Method that is called when the user asks to spawn in a certain square, either in the first
     * turn or after he dies.
     *
     * @param value A serialized JsonObject with the name of the power uo he wants to discard and
     * its color.
     */
    void spawn(String value) throws RemoteException;

    /**
     * Method that is called when the user needs to decide which action he wants to perform (run,
     * collect, etc...).
     *
     * @param value A serialized JsonObject with the integer of the action he wants to perform.
     */
    void selectAction(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the "move" action and wants to
     * perform it.
     *
     * @param value A serialized JsonObject with color and the integer that identify the square the
     * user wants to reach.
     */
    void moveAction(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the "collect" action and wants to
     * perform it.
     *
     * @param value A serialized JsonObject with an optional integer that says which card the user
     * wants to collect, that (if it's present) can be followed by an optional integer that says
     * which card the user wants to discard, and eventually a String that says with which power ups
     * the user wants to pay the cost of the card
     */
    void askCollect(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the "shoot" action and wants to
     * activate the weapon he wants to use.
     *
     * @param value A serialized JsonObject with the integer of the card that the user wants to
     * activate.
     */
    void askActivateWeapon(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * primary effect.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    void askUsePrimary(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * alternative effect.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    void askUseAlternative(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * first optional effect.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    void askUseOptional1(String value) throws RemoteException;

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * second optional effect.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    void askUseOptional2(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to use a power up.
     *
     * @param value A serialized JsonObject with all the information needed to use the said power
     * up: target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * paga(colorOfAmmoCube). All these are optional, and need to be written only if necessary.
     */
    void askUsePowerUp(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to reload a weapon.
     *
     * @param value A serialized JsonObject with all the information needed to reload: the id of the
     * card that the user wants to reload, and an optional list of power ups he wants to use instead
     * of his ammo cubes.
     */
    void askReload(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to end the action he is performing now.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    void endAction(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to know some information about a specific card.
     *
     * @param value A serialized JsonObject with an integer: the id of the card.
     */
    void askCardInfo(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to know some information about a specific power
     * up.
     *
     * @param value A serialized JsonObject with the name and color of the power up.
     */
    void askInfoPowerUp(String value) throws RemoteException;

    /**
     * Method that is called when the user wants to end his turn.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    void endOfTurn(String value) throws RemoteException;
}
