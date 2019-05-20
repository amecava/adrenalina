package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.presenter.GameHandler;
import it.polimi.ingsw.presenter.exceptions.BoardVoteException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

            ClientHandler.gameBroadcast(this.gameHandler, x -> !x.getValue().equals(this),
                    "gameBroadcast",
                    this.playerId + ": riconnesso alla partita " + this.gameHandler.getGameId()
                            + ".");

            ClientHandler
                    .broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                            "showGames",
                            ClientHandler.getGameHandlerJsonArray().toString());

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

            } else if (!ClientHandler.isGameHandlerPresent(object.getString("gameId"))) {

                this.callRemoteMethod("errorMessage", "Nome partita preso cazzo.");

            } else {

                this.gameHandler = ClientHandler.getGameHandler(x -> x.getGameId().equals(object.getString("gameId")));

                this.player = this.gameHandler.addPlayer(this.playerId, object.getString("character"));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                ClientHandler.gameBroadcast(this.gameHandler, x -> !x.getValue().equals(this),
                        "gameBroadcast",
                        this.playerId + ": connesso alla partita " + this.gameHandler
                                .getGameId()
                                + ".");

                ClientHandler.broadcast(
                        x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null,
                        "showGames",
                        ClientHandler.getGameHandlerJsonArray().toString());
            }


        } catch (LoginException e) {

            this.callRemoteMethod("errorMessage", e.getMessage());

        }
    }

    @Override
    public void voteBoard(String value) throws RemoteException {

        String pattern = "(\\s*)([1-4])(\\s*)";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(value);

        if (this.gameHandler == null) {

            this.callRemoteMethod("errorMessage", "Non sei connesso a nessuna partita.");

        } else if (this.gameHandler.isGameStarted()) {

            this.callRemoteMethod("errorMessage", "La partita è gia iniziata.");

        } else if (!m.find()) {

            this.callRemoteMethod("errorMessage", "Comando errato: inserisci un numero da 1 a 4 ");

        } else {

            try {

                this.gameHandler.voteBoard(this.player.getPlayerId(), Integer.valueOf(m.group(2)));

                this.callRemoteMethod("completeVoteBoard", m.group(2));

            } catch (BoardVoteException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());
            }
        }
    }

    JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}
