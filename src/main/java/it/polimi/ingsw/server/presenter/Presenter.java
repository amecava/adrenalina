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
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.server.presenter.exceptions.SpawnException;
import it.polimi.ingsw.virtual.JsonUtility;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public abstract class Presenter implements VirtualPresenter {

    private String playerId = "Client";

    private Player player;
    private GameHandler gameHandler;

    static final Map<String, JsonObject> state = new HashMap<>();

    static {

        InputStream in = Presenter.class.getClassLoader().getResourceAsStream("GameState.json");

        JsonArray object = Json.createReader(in).readArray();

        object.stream()
                .map(JsonValue::asJsonObject)
                .forEach(x ->

                    state.put(x.getString("state"), x)
                );
    }

    abstract void disconnectPresenter();

    abstract void callRemoteMethod(String method, String value) throws RemoteException;

    String getPlayerId() {

        return this.playerId;
    }

    public Player getPlayer() {

        return this.player;
    }

    public GameHandler getGameHandler() {

        return this.gameHandler;
    }

    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.callRemoteMethod("completeDisconnect", "");

        ClientHandler.removeClient(this);

        this.updateLoginGame();
    }

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

            ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

            this.playerId = object.getString("playerId");

            this.callRemoteMethod("completeLogin",
                    Json.createObjectBuilder(object)
                            .add("gameId", this.gameHandler.getGameId() != null ? this.gameHandler
                                    .getGameId() : " ")
                            .add("isGameStarted", this.gameHandler.isGameStarted())
                            .build().toString());

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": riconnesso al server.");

            this.updateLoginGame();

        } else {

            this.playerId = object.getString("playerId");

            this.callRemoteMethod("completeLogin",
                    Json.createObjectBuilder(object)
                            .add("isGameStarted", this.gameHandler == null)
                            .build().toString());

            this.callRemoteMethod("updateState", state.get("noGameState").toString());

            ClientHandler.broadcast(
                    (x) -> !x.getPlayerId().equals(this.playerId),
                    "broadcast",
                    this.playerId + ": connesso al server.");

            this.updateLoginGame();
        }
    }

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

                this.callRemoteMethod("errorMessage", "Non esiste questa partita cazzo.");

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

                    this.callRemoteMethod("updateState", state.get("activePlayerState").toString());

                } else {

                    this.callRemoteMethod("updateState", state.get("notActivePlayerState").toString());
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

                //TODO end game
            }
        }
    }

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

                this.player
                        .selectAction(Integer.valueOf(object.getString("actionNumber")));

                this.callRemoteMethod("completeSelectAction",
                        this.player.toJsonObject().getJsonObject("bridge")
                                .getJsonObject("actionBridge")
                                .getJsonArray("possibleActionsArray")
                                .getJsonObject(
                                        Integer.valueOf(object.getString("actionNumber")) - 1)
                                .toString());

                this.callRemoteMethod("updateState", state.get("actionState").toString());

            } catch (IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }


    }

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

            } catch (ColorException | IllegalActionException | CardException | EffectException | PropertiesException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

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

                    this.player.collect(Integer.parseInt(object.getString("cardIdCollect")));

                } else {

                    this.player.collect(Integer.parseInt(object.getString("cardIdCollect")),
                            Integer.parseInt(object.getString("cardIdDiscard")));
                }

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());


            } catch (IllegalActionException |
                    CardException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
        }
    }

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

                this.callRemoteMethod("infoMessage", "Hai selezionato la carta da usare, "
                        + "adesso per usare un effetto puoi scrivere:\n"
                        + "usaeffetto tipoEffetto | Target(un personaggio, un quadrato o una stanza) | destinazione(colore-idQiuadrato) | eventualiPowerup(nome-colore)\n"
                        + "Esempio: usaeffetto primario | sprog dozer (oppure \"rosso\" per selezionare un'intera stanza) | rosso-1 | mirino-rosso");

                this.callRemoteMethod("updateState", state.get("shootState").toString());

            } catch (CardException | IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    @Override
    public void askUseEffect(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            EffectArgument effectArgument = new EffectArgument();

            try {

                String line = object.getString("line");

                EffectType effectType = EffectParser
                        .effectType(line.substring(0, line.indexOf("|")));

                line = EffectParser.updateString(line);

                EffectParser.target(this.gameHandler, line.substring(0, line.indexOf("|")))
                        .forEach(effectArgument::appendTarget);

                line = EffectParser.updateString(line);

                effectArgument.setDestination(EffectParser
                        .destination(this.gameHandler, line.substring(0, line.indexOf("|"))));

                line = EffectParser.updateString(line);

                line = line.replaceAll("|", "");

                List<PowerUpCard> powerUpCardList = EffectParser
                        .powerUps(this.player, line);

                this.player.useCard(effectType, effectArgument, powerUpCardList);

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

                /*
                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> quelli che sono nella targetList,
                        "infoMessage",
                        this.gameHandler.toJsonObject().toString());
                 */

                this.callRemoteMethod("infoMessage",
                        "Hai sparato con successo, adesso puoi usare un effetto opzionale oppure finire l'azione.");

            } catch (ColorException | EffectException | CardException | IllegalActionException | PropertiesException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }

    }

    @Override
    public void askUsePowerUp(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                String line = object.getString("name");

                if (!line.contains("|")) {

                    this.callRemoteMethod("errorMessage", "Per favore, per usare un power up scrivi:\n"
                            + "usapowerup nomePowerUp-colorePowerUp | eventualeTarget | eventualeColore.");
                }else {

                    EffectArgument effectArgument = new EffectArgument();

                    PowerUpCard powerUp = EffectParser.powerUps(this.player, line.substring(0, line.indexOf("|"))).get(0);

                    line = EffectParser.updateString(line);

                    EffectParser.target(this.gameHandler, line.substring(0, line.indexOf("|")))
                            .forEach(effectArgument::appendTarget);

                    line = EffectParser.updateString(line);
                    line = line.replaceAll("|", "");
                    line = line.trim();

                    Color color = Color.ofName(line);

                    if (color == null) {

                        powerUp.useCard(effectArgument);

                    } else {

                        powerUp.useCard(effectArgument, color);
                    }

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

    @Override
    public void askReload(String value) throws RemoteException {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                String line = object.getString("line");

                if (!line.contains("|")) {

                    this.callRemoteMethod("errorMessage", "Per favore, per usare un power up scrivi:\n"
                            + "usapowerup nomePowerUp-colorePowerUp | eventualeTarget | eventualeColore.");
                }else {

                    int id = EffectParser.cardId(line.substring(0, line.indexOf("|")));

                    line = EffectParser.updateString(line);
                    line = line.replaceAll("|", "");

                    this.player.reload(id, EffectParser
                            .powerUps(this.player, line));

                    ClientHandler.gameBroadcast(
                            this.gameHandler,
                            x -> true,
                            "updateBoard",
                            this.gameHandler.toJsonObject().toString());

                    this.callRemoteMethod("infoMessage",
                            "Carta ricaricata! Adesso puoi solo finire il tuo turno con il comando \"fineturno\".");
                }

            } catch (CardException | IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            } catch (NumberFormatException e) {

                this.callRemoteMethod("errorMessage", "Seleziona un id valido.");
            }
        }
    }

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

            this.callRemoteMethod("updateState", state.get("activePlayerState").toString());
        }
    }

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
