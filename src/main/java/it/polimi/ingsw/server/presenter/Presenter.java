package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.server.presenter.exceptions.SpawnException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonObject;

public abstract class Presenter implements VirtualPresenter {

    private String playerId = "Client";

    private Player player;
    private GameHandler gameHandler;

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

        JsonObject object = this.jsonDeserialize(value);

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
                            .add("gameId",  this.gameHandler.getGameId() != null ? this.gameHandler.getGameId(): " ")
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

            ClientHandler.broadcast(
                    (x) -> !x.getPlayerId().equals(this.playerId),
                    "broadcast",
                    this.playerId + ": connesso al server.");

            this.updateLoginGame();
        }
    }

    @Override
    public void askCreateGame(String value) throws RemoteException {

        JsonObject object = this.jsonDeserialize(value);

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

        JsonObject object = this.jsonDeserialize(value);

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

                this.updateLoginGame();
            }

        } catch (LoginException e) {

            this.callRemoteMethod("errorMessage", e.getMessage());

        }
    }

    @Override
    public void voteBoard(String value) throws RemoteException {

        JsonObject object = this.jsonDeserialize(value);

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

        JsonObject object = this.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");

        } else {

            try {

                Color color = Color.valueOf(object.getString("color"));

                this.gameHandler.spawnPlayer(this.player, object.getString("name"), color);

                this.callRemoteMethod("infoMessage", "Lo spawn è andato a buon fine.\n"
                        + "Adesso puoi selezionare un'azione: scrivi \"selezionaazione \" seguito dal numero dell'azione che vuoi usare.\n"
                        + "1 - ricarica 2 - corri 3 - raccogli 4 - spara");

                ClientHandler.gameBroadcast(
                        this.gameHandler,
                        x -> true,
                        "updateBoard",
                        this.gameHandler.toJsonObject().toString());

            } catch (SpawnException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            } catch (IllegalArgumentException e) {

                this.callRemoteMethod("errorMessage", "Il colore selezionato non esiste.");
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

            } catch (EndGameException e) {

                //TODO end game
            }
        }
    }

    @Override
    public void selectAction(String value) throws RemoteException {

        JsonObject object = this.jsonDeserialize(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (!this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita non è ancora iniziata.");
        /*
        } else if (!this.player.isActivePlayer()) {

            this.callRemoteMethod("infoMessage",
                    "Per selezionare un'azione aspetta che sia il tuo turno");
        */
        } else {

            try {

                this.player
                        .selectAction(Integer.valueOf(object.getString("actionNumber")));

                this.callRemoteMethod("infoMessage", "Inserisci i dettagli per completare ");

            } catch (IllegalActionException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
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

    JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}
