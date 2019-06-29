package it.polimi.ingsw.virtual;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {

    /**
     * This method sends a broadcast message to every client connected to the server.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void broadcast(String value) throws RemoteException;

    /**
     * This method sends a broadcast message to every client of a specific game connected to the
     * server.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void gameBroadcast(String value) throws RemoteException;

    /**
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void infoMessage(String value) throws RemoteException;

    /**
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void errorMessage(String value) throws RemoteException;

    /**
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void isConnected(String value) throws RemoteException;

    /**
     * This method completes the login action making it possible to the player to enter the
     * GameListScreen.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeLogin(String value) throws RemoteException;

    /**
     * This method completes the action of disconnecting the client who asked to be disconnected
     * from the server.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeDisconnect(String value) throws RemoteException;

    /**
     * This method updates the screen that shows to the player how many games have been created and
     * who is currently logged into every game (eve if the client is currently disconnected from the
     * server).
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void updateGameList(String value) throws RemoteException;

    /**
     * This method notifies that the action "create game" has been completed successfully.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeCreateGame(String value) throws RemoteException;

    /**
     * This method notifies that the action "select game" has been completed successfully.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeSelectGame(String value) throws RemoteException;

    /**
     * This method updates the screen that is shown to the player when he selected the game he wants
     * to be part of and he is waiting for the game to start.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void updateGameNotStartedScreen(String value) throws RemoteException;

    /**
     * This method notifies the player that the action "vote board" has been completed
     * successfully.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeVoteBoard(String value) throws RemoteException;

    /**
     * This method notifies the player that the action "select action" has been completed
     * successfully.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeSelectAction(String value) throws RemoteException;

    /**
     * This method notifies the player that the action "end action" has been completed
     * successfully.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeEndAction(String value) throws RemoteException;

    /**
     * This method parses the deserialized JsonObject in order to show to the information of the
     * weapon card asked by the player.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completeCardInfo(String value) throws RemoteException;

    /**
     * This method parses the deserialized JsonObject in order to show to the information of the
     * power up asked by the player.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void completePowerUpInfo(String value) throws RemoteException;

    /**
     * This method updates the board of every player whenever it is called by the server, so that
     * players can see how the match is evolving.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void updateBoard(String value) throws RemoteException;

    /**
     * This method updates the game state of a specific player, guiding him through the game by
     * showing him only what he can do in the moment of the game in which this method is called.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void updateState(String value) throws RemoteException;

    /**
     * This method shows the last screen when the game ends.
     *
     * @param value A serialized JsonObject that will be deserialized using
     * JsonUtility.jsonDeserialize(String value) method, containing all the information from the
     * server
     */
    void endGameScreen(String value) throws RemoteException;
}
