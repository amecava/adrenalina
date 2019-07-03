package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.server.presenter.exceptions.SpawnException;
import it.polimi.ingsw.common.JsonUtility;
import it.polimi.ingsw.common.VirtualPresenter;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public abstract class Presenter implements VirtualPresenter {

    /**
     * The player id. It is initialized at "Client" to differentiate active players and disconnected
     * ones.
     */
    private String playerId = "Client";

    /**
     * A reference to the user's Player object.
     */
    private Player player;

    /**
     * The GameHandler of the game the user's currently playing.
     */
    private GameHandler gameHandler;

    /**
     * The key of this Map is the player id, the value is the JsonObject with all the possible
     * actions of the state in which the user currently is.
     */
    static final Map<String, JsonObject> state = new HashMap<>();

    /**
     * Statically loads the state of the game.
     */
    static {

        InputStream in = Presenter.class.getClassLoader().getResourceAsStream("GameState.json");

        JsonArray object = Json.createReader(in).readArray();

        object.stream()
                .map(JsonValue::asJsonObject)
                .forEach(x ->

                        state.put(x.getString("state"), x)
                );
    }

    /**
     * Disconnects the Presenter.
     */
    abstract void disconnectPresenter();

    /**
     * The method is responsible of talking to the client. Declares this abstract method that will
     * be implemented by one of the two subclasses of Presenter: RmiPresenter or SocketPresenter.
     * Thanks to this design, "login" method are implemented only once, and the connection method
     * will be implemented by the subclasses.
     */
    abstract void callRemoteMethod(String method, String value) throws RemoteException;

    /**
     * Gets the id of the player of this presenter.
     * @return The player id.
     */
    String getPlayerId() {

        return this.playerId;
    }

    /**
     * Gets the player of this presenter.
     * @return The Player of this presenter.
     */
    Player getPlayer() {

        return this.player;
    }

    /**
     * Gets the GameHandler of this presenter.
     * @return The GameHandler of this presenter.
     */
    GameHandler getGameHandler() {

        return this.gameHandler;
    }

    /**
     * Method that is called when the user wants to disconnect himself from the server.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.callRemoteMethod("completeDisconnect", "");

        ClientHandler.removeClient(this);

        this.updateLoginGame();
    }

    /**
     * Method that is called when the user wants to set his player id.
     *
     * @param value A serialized JsonObject with the name that the user chose.
     */
    @Override
    public void selectPlayerId(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (!this.playerId.equals("Client")) {

            this.callRemoteMethod("errorMessage", "Login già effettuato, prima esegui logout.");

        } else if (ClientHandler.isClientPresent(object.getString("playerId"))) {

            this.callRemoteMethod("errorMessage", "PlayerId già preso.");

        } else if (ClientHandler.isPlayerPresent(object.getString("playerId"))) {

            this.gameHandler = ClientHandler.getGameHandler(
                    x -> x.getPlayerList().stream()
                            .anyMatch(y -> y.getPlayerId().equals(object.getString("playerId"))));

            this.player = ClientHandler
                    .getPlayer(this.gameHandler,
                            x -> x.getPlayerId().equals(object.getString("playerId")));

            this.playerId = object.getString("playerId");

            ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

            this.callRemoteMethod("completeLogin",
                    Json.createObjectBuilder(object)
                            .add("gameId", this.gameHandler.getGameId())
                            .add("gameStarted", this.gameHandler.isGameStarted())
                            .add("board",
                                    this.gameHandler.isGameStarted() ? this.gameHandler.getModel()
                                            .getBoard().toJsonObject() : JsonValue.NULL)
                            .build().toString());

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": riconnesso al server.");

            this.reconnectPlayer();

            this.updateLoginGame();

        } else {

            this.playerId = object.getString("playerId");

            this.callRemoteMethod("completeLogin",
                    Json.createObjectBuilder(object)
                            .add("gameStarted", this.gameHandler == null)
                            .build().toString());

            this.callRemoteMethod("updateState", state.get("noGameState").toString());

            ClientHandler.broadcast(
                    x -> !x.getPlayerId().equals(this.playerId),
                    "broadcast",
                    this.playerId + ": connesso al server.");

            this.updateLoginGame();
        }
    }

    /**
     * Method that is called when the user asks to create a new game.
     *
     * @param value A serialized JsonObject with the name of the new game, the number of deaths and
     * a boolean that says if the user wanted to play with the final frenzy mode.
     */
    @Override
    public void askCreateGame(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.playerId.equals("Client")) {

            this.callRemoteMethod("errorMessage", "Come prima cosa effettua il login.");

        } else if (ClientHandler.isGameHandlerPresent(object.getString("gameId"))) {

            this.callRemoteMethod("errorMessage",
                    "Nome partita già esistente, ripeti con un nuovo nome.");

        } else {

            ClientHandler.addGameHandler(object.getString("gameId"),
                    Integer.valueOf(object.getString("numberOfDeaths")),
                    object.getString("frenzy").equals("frenesia"));

            this.callRemoteMethod("completeCreateGame", object.getString("gameId"));

            this.updateLoginGame();
        }
    }

    /**
     * Method that is called when the user asks to enter in a game that has already been created
     * either by himself or someone else.
     *
     * @param value A serialized JsonObject with the name of the game and the character he wants to
     * be.
     */
    @Override
    public void selectGame(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        try {

            if (this.playerId.equals("Client")) {

                this.callRemoteMethod("errorMessage", "Come prima cosa effettua il login.");

            } else if (this.gameHandler != null && this.player != null) {

                this.callRemoteMethod("errorMessage", "Prima logout.");

            } else if (ClientHandler
                    .getGameHandler(x -> x.getGameId().equals(object.getString("gameId")))
                    .isGameStarted()) {

                this.callRemoteMethod("errorMessage", "La partita è già iniziata.");

            } else if (!ClientHandler.isGameHandlerPresent(object.getString("gameId"))) {

                this.callRemoteMethod("errorMessage", "Non esiste questa partita.");

            } else {

                this.gameHandler = ClientHandler
                        .getGameHandler(x -> x.getGameId().equals(object.getString("gameId")));

                this.player = this.gameHandler
                        .addPlayer(this.playerId, object.getString("character"));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                this.callRemoteMethod("updateState", state.get("gameNotStartedState").toString());

                this.updateLoginGame();
            }

        } catch (NoSuchElementException e) {

            this.callRemoteMethod("errorMessage", "Non esiste questa partita cazzo.");

        } catch (LoginException | ColorException e) {

            this.callRemoteMethod("errorMessage", e.getMessage());

        }
    }

    /**
     * Method that is called when the user asks to vote the board he prefers.
     *
     * @param value A serialized JsonObject with the integer of the board he wants to use.
     */
    @Override
    public void voteBoard(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita è gia iniziata.");

        } else {

            try {

                this.gameHandler.voteBoard(this.player.getPlayerId(),
                        Integer.valueOf(object.getString("vote")));

                this.callRemoteMethod("completeVoteBoard", object.getString("vote"));

            } catch (BoardVoteException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    /**
     * Method that is called when the user asks to spawn in a certain square, either in the first
     * turn or after he dies.
     *
     * @param value A serialized JsonObject with the name of the power uo he wants to discard and
     * its color.
     */
    @Override
    public void spawn(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                this.gameHandler.spawnPlayer(this.player, object.getString("name"),
                        object.getString("color"));

                this.callRemoteMethod("infoMessage", "Lo spawn è andato a buon fine.\n");

                if (this.player.isActivePlayer()) {

                    this.callRemoteMethod("updateState", StateHandler
                            .createActivePlayerState(this.player, state.get("activePlayerState"))
                            .toString());

                } else {

                    this.callRemoteMethod("updateState",
                            state.get("notActivePlayerState").toString());
                }

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

            } catch (SpawnException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
        }
    }

    /**
     * Method that is called when the user wants to end his turn.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    @Override
    public void endOfTurn(String value) throws RemoteException {

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else if (!this.player.isActivePlayer() || this.player.getRemainingActions() == -1) {

            this.callRemoteMethod("errorMessage", "Non è il tuo turno.");

        } else if (this.player.isActivePlayer() && this.player.getCurrentPosition() == null) {

            this.callRemoteMethod("errorMessage", "Devi fare lo spawn prima di finire il turno.");

        } else {

            try {

                this.gameHandler.endOfTurn();

                this.callRemoteMethod("infoMessage", "Turno finito.");

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

            } catch (EndGameException e) {

                JsonArrayBuilder array = Json.createArrayBuilder();

                e.getWinner().stream()
                        .flatMap(Collection::stream)
                        .forEach(x ->

                                array.add(
                                        Json.createObjectBuilder()
                                                .add("playerId", x.getPlayerId())
                                                .add("character", x.getColor().getCharacter())
                                                .add("points", x.getPoints())
                                                .build())
                        );

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "endGameScreen",
                        Json.createObjectBuilder().add("array", array.build()).build().toString());

            }
        }
    }

    /**
     * Method that is called when the user needs to decide which action he wants to perform (run,
     * collect, reload).
     *
     * @param value A serialized JsonObject with the integer of the action he wants to perform.
     */
    @Override
    public void selectAction(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else if (!this.player.isActivePlayer()) {

            this.callRemoteMethod("infoMessage",
                    "Per selezionare un'azione aspetta che sia il tuo turno");

        } else {

            try {

                this.player.selectAction(Integer.valueOf(object.getString("actionNumber")));

                this.callRemoteMethod("completeSelectAction",
                        this.player.toJsonObject().getJsonObject("bridge")
                                .getJsonObject("actionBridge")
                                .getJsonArray("possibleActionsArray")
                                .getJsonObject(
                                        Integer.valueOf(object.getString("actionNumber")) - 1)
                                .toString());

                this.callRemoteMethod("updateState",
                        StateHandler.createActionState(this.gameHandler.getModel().getBoard(), this.player, state.get("actionState"))
                                .toString());

            } catch (IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }


    }

    /**
     * Method that is called when the user already "activated" the "move" action and wants to
     * perform it.
     *
     * @param value A serialized JsonObject with color and the integer that identify the square the
     * user wants to reach.
     */
    @Override
    public void moveAction(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                this.player.move(new EffectArgument(this.gameHandler.getModel().getBoard()
                                .findSquare(object.getString("squareColor"), object.getString("squareId"))),
                        this.gameHandler.getModel().getEffectHandler());

                this.callRemoteMethod("infoMessage", "Ti sei mosso nel quadrato che hai scelto.");

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                this.callRemoteMethod("updateState",
                        StateHandler.createActionState(this.gameHandler.getModel().getBoard(), this.player, state.get("actionState"))
                                .toString());

            } catch (ColorException | IllegalActionException | CardException | EffectException | PropertiesException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    /**
     * Method that is called when the user already "activated" the "collect" action and wants to
     * perform it.
     *
     * @param value A serialized JsonObject with an optional integer that says which card the user
     * wants to collect, that (if it's present) can be followed by an optional integer that says
     * which card the user wants to discard, and eventually a String that says with which power ups
     * the user wants to pay the cost of the card
     */
    @Override
    public void askCollect(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                if (object.getString("cardIdCollect").equals("")) {

                    this.gameHandler.getModel().getBoard().getAmmoTilesDeck()
                            .addTile(this.player.collect());

                } else if (object.getString("cardIdDiscard").equals("")) {

                    this.player.collect(Integer.parseInt(object.getString("cardIdCollect")),
                            EffectParser.powerUps(this.player, object.getString("powerups")));

                } else {

                    this.player.collect(Integer.parseInt(object.getString("cardIdCollect")),
                            Integer.parseInt(object.getString("cardIdDiscard")),
                            EffectParser.powerUps(this.player, object.getString("powerups")));
                }

                if (!EffectParser.powerUps(this.player, object.getString("powerups")).isEmpty()) {

                    for (PowerUpCard powerUp : EffectParser
                            .powerUps(this.player, object.getString("powerups"))) {

                        this.gameHandler.getModel().getBoard().getPowerUpDeck().addPowerUpCard(
                                this.player.removePowerUp(powerUp));
                    }
                }

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                this.callRemoteMethod("updateState",
                        StateHandler.createActionState(this.gameHandler.getModel().getBoard(), this.player, state.get("actionState"))
                                .toString());


            } catch (IllegalActionException |
                    CardException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
        }
    }

    /**
     * Method that is called when the user already "activated" the "shoot" action and wants to
     * activate the weapon he wants to use.
     *
     * @param value A serialized JsonObject with the integer of the card that the user wants to
     * activate.
     */
    @Override
    public void askActivateWeapon(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                this.player.activateCard(Integer.parseInt(object.getString("cardId")));

                this.callRemoteMethod("infoMessage", "Hai selezionato la carta da usare!");

                this.callRemoteMethod("updateState",
                        StateHandler.createShootState(this.player, state.get("shootState"))
                                .toString());

            } catch (CardException | IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * primary effect. Calls askUseEffect with the specific EffectType.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    @Override
    public void askUsePrimary(String value) throws RemoteException {

        this.askUseEffect(EffectType.PRIMARY, value);
    }

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * alternative effect. Calls askUseEffect with the specific EffectType.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    @Override
    public void askUseAlternative(String value) throws RemoteException {

        this.askUseEffect(EffectType.ALTERNATIVE, value);
    }

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * first optional effect. Calls askUseEffect with the specific EffectType.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    @Override
    public void askUseOptional1(String value) throws RemoteException {

        this.askUseEffect(EffectType.OPTIONAL_1, value);
    }

    /**
     * Method that is called when the user already "activated" the weapon card and wants to use the
     * second optional effect. Calls askUseEffect with the specific EffectType.
     *
     * @param value A serialized JsonObject with all the information needed to use the said effect:
     * target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * powerup(powerUpName-color). All these are optional, and need to be written only if
     * necessary.
     */
    @Override
    public void askUseOptional2(String value) throws RemoteException {

        this.askUseEffect(EffectType.OPTIONAL_2, value);
    }

    /**
     * Method that is called when the user wants to use a power up.
     *
     * @param value A serialized JsonObject with all the information needed to use the said power
     * up: target(*name of the character*) destination(*colorOfSquare-numberOfSquare*)
     * paga(colorOfAmmoCube). All these are optional, and need to be written only if necessary.
     */
    @Override
    public void askUsePowerUp(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                String line = object.getString("line");

                if (!line.contains("powerup")) {

                    this.callRemoteMethod("errorMessage",
                            "Per favore, per usare un power up scrivi:\n"
                                    + "usapowerup powerup(nomePowerUp-colorePowerUp) target(eventualeTarget)  paga(eventualeColore).");
                } else {

                    PowerUpCard powerUpCard = EffectParser
                            .powerUps(this.player, line).get(0);

                    if (!line.contains("paga")) {

                        powerUpCard.useCard(EffectParser.effectArgument(this.gameHandler, line));


                    } else {

                        powerUpCard.useCard(EffectParser.effectArgument(this.gameHandler, line),
                                EffectParser.paymentCube(line));
                    }

                    this.gameHandler.getModel().getBoard().getPowerUpDeck()
                            .addPowerUpCard(this.player.removePowerUp(powerUpCard));

                    ClientHandler.gameBroadcast(
                            this.gameHandler,
                            x -> true,
                            "updateBoard",
                            this.gameHandler.toJsonObject().toString());

                }


            } catch (ColorException | EffectException | CardException | PropertiesException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    /**
     * Method that is called when the user wants to reload a weapon.
     *
     * @param value A serialized JsonObject with all the information needed to reload: the id of the
     * card that the user wants to reload, and an optional list of power ups he wants to use instead
     * of his ammo cubes.
     */
    @Override
    public void askReload(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                this.player.reload(Integer.parseInt(object.getString("id")), EffectParser
                        .powerUps(this.player, object.getString("powerup")));

                if (!EffectParser.powerUps(this.player, object.getString("powerup")).isEmpty()) {

                    for (PowerUpCard powerUp : EffectParser
                            .powerUps(this.player, object.getString("powerup"))) {

                        this.gameHandler.getModel().getBoard().getPowerUpDeck().addPowerUpCard(
                                this.player.getPowerUpsList()
                                        .remove(this.player.getPowerUpsList().indexOf(powerUp)));
                    }
                }

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                this.callRemoteMethod("infoMessage",
                        "Carta ricaricata! Adesso puoi solo finire il tuo turno con il comando \"fineturno\".");

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                this.callRemoteMethod("updateState",
                        StateHandler.createActionState(this.gameHandler.getModel().getBoard(), this.player, state.get("actionState"))
                                .toString());

            } catch (CardException | IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            } catch (NumberFormatException e) {

                this.callRemoteMethod("errorMessage", "Seleziona un id valido.");
            }
        }

    }

    /**
     * Method that is called when the user wants to end the action he is performing now.
     *
     * @param value A serialized JsonObject with the name of this method.
     */
    @Override
    public void endAction(String value) throws RemoteException {

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            this.player.endAction();

            this.callRemoteMethod("completeEndAction",
                    this.player.toJsonObject().getJsonObject("bridge").getJsonObject("actionBridge")
                            .toString());

            this.callRemoteMethod("updateState", StateHandler
                    .createActivePlayerState(this.player, state.get("activePlayerState"))
                    .toString());

            ClientHandler.gameBroadcast(
                    this.gameHandler,
                    x -> true,
                    "updateBoard",
                    this.gameHandler.toJsonObject().toString());
        }
    }

    /**
     * Method that is called when the user wants to know some information about a specific card.
     *
     * @param value A serialized JsonObject with an integer: the id of the card.
     */
    @Override
    public void askCardInfo(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Prima connettiti ad una partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "Aspetta che inizi la partita.");

        } else {

            this.callRemoteMethod("completeCardInfo", this.gameHandler.getModel().getBoard()
                    .getInfoCard(object.getString("cardId")).toString());

        }
    }

    /**
     * Method that is called when the user wants to know some information about a specific power
     * up.
     *
     * @param value A serialized JsonObject with the name and color of the power up.
     */
    @Override
    public void askInfoPowerUp(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Prima connettiti ad una partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "Aspetta che inizi la partita.");

        } else {

            try {

                if (this.gameHandler.getModel().getBoard().getInfoPowerUp(object.getString("name"))
                        != null) {

                    this.callRemoteMethod("completePowerUpInfo",
                            this.gameHandler.getModel().getBoard()
                                    .getInfoPowerUp(object.getString("name")).toString());
                } else {

                    this.callRemoteMethod("completePowerUpInfo",
                            this.gameHandler.getPlayerList().stream()
                                    .flatMap(x -> x.getPowerUpsList().stream())
                                    .filter(y -> JsonUtility
                                            .levenshteinDistance(object.getString("name"),
                                                    y.getName()) <= 3)
                                    .findFirst()
                                    .orElseThrow(IllegalArgumentException::new)
                                    .toJsonObject().toString());
                }
            } catch (IllegalArgumentException e) {

                this.callRemoteMethod("errorMessage", "Seleziona un power up valido.");
            }
        }
    }

    /**
     * Either "askUsePrimary", "askUseAlternative", "askUseOptional1", "askUseOptional2", call this
     * method, that is the method that actually uses the model to perform the action.
     */
    private void askUseEffect(EffectType effectType, String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (object.getString("line").chars().filter(c -> c == '(').count() != object
                .getString("line").chars().filter(c -> c == ')').count()) {

            this.callRemoteMethod("errorMessage", "Scrivi bene le parentesi.");

        } else if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                EffectArgument effectArgument = EffectParser
                        .effectArgument(this.gameHandler, object.getString("line"));
                List<PowerUpCard> powerUpList = EffectParser
                        .powerUps(this.player, object.getString("line"));

                this.player.useCard(effectType,
                        effectArgument,
                        powerUpList);

                if (!powerUpList.isEmpty()) {

                    for (PowerUpCard powerUp : powerUpList) {

                        this.gameHandler.getModel().getBoard().getPowerUpDeck().addPowerUpCard(
                                this.player.removePowerUp(powerUp));
                    }
                }

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                this.callRemoteMethod("infoMessage",
                        "L'effetto è stato eseguito con successo.");

                this.callRemoteMethod("updateState",
                        StateHandler.createShootState(this.player, state.get("shootState"))
                                .toString());

            } catch (ColorException | EffectException | CardException | IllegalActionException | PropertiesException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }

    }

    /**
     * This method updates the state of the game of a player who just reconnected to the game.
     */
    private void reconnectPlayer() throws RemoteException {

        if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("updateState", state.get("gameNotStartedState").toString());

        } else if ((this.player.isActivePlayer() && this.player.getCurrentPosition() == null)
                || this.player.isRespawn()) {

            this.callRemoteMethod("updateState", state.get("spawnState").toString());

        } else if (!this.player.isActivePlayer()) {

            this.callRemoteMethod("updateState", state.get("notActivePlayerState").toString());

        } else if (this.player.isShooting()) {

            this.callRemoteMethod("updateState",
                    StateHandler.createShootState(this.player, state.get("shootState")).toString());

        } else if (this.player.getCurrentAction() != null) {

            this.callRemoteMethod("updateState",
                    StateHandler.createActionState(this.gameHandler.getModel().getBoard(), this.player, state.get("actionState"))
                            .toString());

        } else {

            this.callRemoteMethod("updateState", StateHandler
                    .createActivePlayerState(this.player, state.get("activePlayerState"))
                    .toString());
        }
    }

    /**
     * This method updates the login state of a match by updating the proper screen.
     */
    private void updateLoginGame() {

        ClientHandler.broadcast(
                x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                "updateGameList",
                ClientHandler.getGameHandlerJsonArray().toString());

        if (this.gameHandler != null) {

            if (!this.gameHandler.isGameStarted()) {

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateGameNotStartedScreen",
                        this.gameHandler.toJsonObject().toString());

            } else {

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());
            }
        }
    }
}
