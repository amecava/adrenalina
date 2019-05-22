package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.io.StringReader;
import java.rmi.RemoteException;
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

        ClientHandler
                .broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                        "showGames",
                        ClientHandler.getGameHandlerJsonArray().toString());
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
                    x -> x.getPlayerList().stream().anyMatch(y -> y.getPlayerId().equals(object.getString("playerId"))));

            this.player = ClientHandler
                    .getPlayer(this.gameHandler, x -> x.getPlayerId().equals(object.getString("playerId")));

            ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

            this.playerId = object.getString("playerId");

            this.callRemoteMethod("completeLogin",
                    "Login effettuato come " + object.getString("playerId") + " e riconnesso alla partita "
                            + this.gameHandler.getGameId() + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": connesso al server.");

            ClientHandler
                    .broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                            "showGames",
                            ClientHandler.getGameHandlerJsonArray().toString());

            ClientHandler.gameBroadcast(this.gameHandler, x -> true,
                    "showBoard",
                    this.gameHandler.toJsonObject().toString());

        } else {

            this.playerId = object.getString("playerId");

            this.callRemoteMethod("completeLogin", "Login effettuato come " + object.getString("playerId") + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": connesso al server.");

            ClientHandler
                    .broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                            "showGames",
                            ClientHandler.getGameHandlerJsonArray().toString());
        }
    }

    @Override
    public void askCreateGame(String value) throws RemoteException {

        JsonObject object = this.jsonDeserialize(value);

        if (ClientHandler.isGameHandlerPresent(object.getString("gameId"))) {

            this.callRemoteMethod("errorMessage",
                    "Nome partita già esistente, ripeti con un nuovo nome.");

        } else {

        ClientHandler.addGameHandler(object.getString("gameId"), Integer.valueOf(object.getString("numberOfDeaths")),
                object.getString("frenzy").equals("frenesia"));

            this.callRemoteMethod("completeCreateGame", object.getString("gameId"));

            ClientHandler
                    .broadcast(x -> !x.getPlayerId().equals("Client")
                                    && x.getGameHandler() == null,
                            "showGames",
                            ClientHandler.getGameHandlerJsonArray().toString());
        }
    }

    @Override
    public  void selectGame(String value) throws RemoteException {

        JsonObject object = this.jsonDeserialize(value);

        try {

            if (this.playerId.equals("Client")) {

                this.callRemoteMethod("errorMessage", "Come prima cosa effettua il login.");

            } else if (this.gameHandler != null && this.player != null) {

                this.callRemoteMethod("errorMessage", "Prima logout.");

            } else if (ClientHandler.getGameHandler(x -> x.getGameId().equals(object.getString("gameId"))).isGameStarted()) {

                this.callRemoteMethod("errorMessage", "La partita è già iniziata.");

            } else if (!ClientHandler.isGameHandlerPresent(object.getString("gameId"))) {

                this.callRemoteMethod("errorMessage", "Non esiste questa partita cazzo.");

            } else {

                this.gameHandler = ClientHandler.getGameHandler(x -> x.getGameId().equals(object.getString("gameId")));

                this.player = this.gameHandler.addPlayer(this.playerId, object.getString("character"));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                ClientHandler.broadcast(
                        x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                        "showGames",
                        ClientHandler.getGameHandlerJsonArray().toString());

                //ClientHandler.gameBroadcast(this.gameHandler, x -> !x.getValue().equals(this),
                //        "updateGameNotStartedScreen",
                //        ClientHandler.getGameHandlerJsonArray().toString());
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

                this.gameHandler.voteBoard(this.player.getPlayerId(), Integer.valueOf(object.getString("vote")));

                this.callRemoteMethod("completeVoteBoard", object.getString("vote"));

            } catch (BoardVoteException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}
