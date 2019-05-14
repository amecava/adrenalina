package it.polimi.ingsw.presenter;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.JsonArray;

public abstract class Presenter implements VirtualPresenter {

    private String playerId;

    private Player player;
    private GameHandler gameHandler;

    abstract void disconnectPresenter();

    abstract void callRemoteMethod(String method, String value) throws RemoteException;

    String getPlayerId() {

        return this.playerId;
    }

    void setPlayerId(String playerId) {

        this.playerId = playerId;
    }

    public Player getPlayer() {

        return this.player;
    }

    public GameHandler getGameHandler() {

        return this.gameHandler;
    }

    @Override
    public void remoteDisconnect(String value) throws RemoteException {

        this.callRemoteMethod("completeDisconnect", value);

        ClientHandler.removeClient(this);
    }

    @Override
    public void selectPlayerId(String value) throws RemoteException {

        //TODO regex
        if (!this.playerId.equals("RMI client") && !this.playerId.equals("Socket client")) {

            this.callRemoteMethod("errorMessage", "Login già effettuato, prima esegui logout.");

        } else if (value.replace(" ", "").length() == 0) {

            this.callRemoteMethod("errorMessage", "PlayerId vuoto, riprova.");

        } else if (ClientHandler.isClientPresent(value)) {

            this.callRemoteMethod("errorMessage", "PlayerId già preso.");

        } else if (ClientHandler.isPlayerPresent(value)) {

            this.gameHandler = ClientHandler.getGameHandler(
                    x -> x.getPlayerList().stream().anyMatch(y -> y.getPlayerId().equals(value)));

            this.player = ClientHandler
                    .getPlayer(this.gameHandler, x -> x.getPlayerId().equals(value));

            ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

            this.playerId = value;

            this.callRemoteMethod("completeLogin",
                    "Login effettuato come " + value + " e riconnesso alla partita "
                            + this.gameHandler.getGameId() + ".");

            ClientHandler.gameBroadcast(x -> !x.equals(this), this.gameHandler, "infoMessage",
                    this.playerId + ": riconnesso alla partita " + this.gameHandler.getGameId()
                            + ".");

        } else {

            this.playerId = value;

            this.callRemoteMethod("completeLogin", "Login effettuato come " + value + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "infoMessage",
                    this.playerId + ": connesso al server.");
        }
    }

    @Override
    public void askGames(String value) throws RemoteException {

        JsonArray jsonArray = ClientHandler.getGameHandlerJsonArray();

        this.callRemoteMethod("showGames", jsonArray.toString());
    }

    @Override
    public void askCreateGame(String value) throws RemoteException {

        //TODO frenesia o niente

        String pattern = "(\\s*)([a-zA-Z_0-9]+)(\\s*)([5-8])(\\s*)(vero|falso)";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(value);

        if (!m.find()) {

            this.callRemoteMethod("errorMessage",
                    "Comando errato, riprova scrivendo \"creapartita nomePartita(nome) numeroMorti(numero intero da 5 a 8) frenesia(vero/falso)\".");

        } else if (ClientHandler.isGameHandlerPresent(m.group(2))) {

            this.callRemoteMethod("errorMessage",
                    "Nome partita già esistente, ripeti con un nuovo nome.");

        } else {

            ClientHandler.addGameHandler(m.group(2), Integer.valueOf(m.group(4)),
                    m.group(6).equals("vero"));

            this.callRemoteMethod("completeCreateGame", m.group(2));

            ClientHandler.broadcast(x -> !x.equals(this), "infoMessage",
                    "Partita " + m.group(2) + " creata.");
        }
    }

    @Override
    public void selectGame(String value) throws RemoteException {

        String pattern = "(\\s*)([a-zA-Z_0-9]+)(\\s*)(\\S+)(\\s*)";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(value);

        if (this.playerId.equals("RMI client") || this.playerId.equals("Socket client")) {

            this.callRemoteMethod("errorMessage",
                    "Effettua il login prima di selezionare una partita (comando: login nomeUtente).");

        } else if (this.gameHandler != null && this.player != null) {

            this.callRemoteMethod("errorMessage", "Prima logout.");

        } else if (!m.find()) {

            this.callRemoteMethod("errorMessage", "REGEX.");

        } else if (!ClientHandler.isGameHandlerPresent(m.group(2))) {

            this.callRemoteMethod("errorMessage", "Nome partita preso cazzo.");

        } else {

            try {

                this.gameHandler = ClientHandler
                        .getGameHandler(x -> x.getGameId().equals(m.group(2)));

                this.player = this.gameHandler.addPlayer(this.playerId, m.group(4));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                ClientHandler.gameBroadcast(x -> !x.equals(this), this.gameHandler, "infoMessage",
                        this.playerId + ": connesso alla partita " + this.gameHandler.getGameId()
                                + ".");

            } catch (LoginException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
        }
    }
}
