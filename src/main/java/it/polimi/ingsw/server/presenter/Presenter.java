package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.GameHandler;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        this.callRemoteMethod("completeDisconnect", value);

        ClientHandler.removeClient(this);

        ClientHandler.broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null, "showGames",
                ClientHandler.getGameHandlerJsonArray().toString());
    }

    @Override
    public void selectPlayerId(String value) throws RemoteException {

        Pattern r = Pattern.compile("(\\s*)([a-zA-Z_0-9]+)(\\s*)");

        Matcher m = r.matcher(value);

        if (!this.playerId.equals("Client")) {

            this.callRemoteMethod("errorMessage", "Login già effettuato, prima esegui logout.");

        } else if (!m.find()) {

            this.callRemoteMethod("errorMessage", "PlayerId non valido, riprova.");

        } else if (ClientHandler.isClientPresent(m.group(2))) {

            this.callRemoteMethod("errorMessage", "PlayerId già preso.");

        } else if (ClientHandler.isPlayerPresent(m.group(2))) {

            this.gameHandler = ClientHandler.getGameHandler(
                    x -> x.getPlayerList().stream().anyMatch(y -> y.getPlayerId().equals(m.group(2))));

            this.player = ClientHandler
                    .getPlayer(this.gameHandler, x -> x.getPlayerId().equals(value))
                    .setConnected(true);

            ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

            this.playerId = m.group(2);

            this.callRemoteMethod("completeLogin",
                    "Login effettuato come " + m.group(2) + " e riconnesso alla partita "
                            + this.gameHandler.getGameId() + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": connesso al server.");

            ClientHandler.gameBroadcast(x -> !x.equals(this), this.gameHandler, "gameBroadcast",
                    this.playerId + ": riconnesso alla partita " + this.gameHandler.getGameId()
                            + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null, "showGames",
                    ClientHandler.getGameHandlerJsonArray().toString());

        } else {

            this.playerId = value;

            this.callRemoteMethod("completeLogin", "Login effettuato come " + value + ".");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals(this.playerId), "broadcast",
                    this.playerId + ": connesso al server.");

            ClientHandler.broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null, "showGames",
                    ClientHandler.getGameHandlerJsonArray().toString());
        }
    }

    @Override
    public void askCreateGame(String value) throws RemoteException {

        Pattern r = Pattern.compile("(\\s*)([a-zA-Z_0-9]+)(\\s*)([5-8])(\\s*)(frenesia|)(\\s*)");

        Matcher m = r.matcher(value);

        if (this.playerId.equals("Client")) {

            this.callRemoteMethod("errorMessage", "Come prima cosa effettua il login.");

        } else if (!m.find()) {

            this.callRemoteMethod("errorMessage",
                    "Comando errato. Riprova scrivendo creapartita nomePartita(nome) numeroTeschi(numero intero compreso tra 5 e 8) "
                            + "frenesia(o niente se non vuoi usare la modalità frenesia).");

        } else if (ClientHandler.isGameHandlerPresent(m.group(2))) {

            this.callRemoteMethod("errorMessage",
                    "Nome partita già esistente, ripeti con un nuovo nome.");

        } else {

            ClientHandler.addGameHandler(m.group(2), Integer.valueOf(m.group(4)),
                    m.group(6).equals("frenesia"));

            this.callRemoteMethod("completeCreateGame", m.group(2));

            ClientHandler.broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null, "showGames",
                    ClientHandler.getGameHandlerJsonArray().toString());
        }
    }

    @Override
    public void selectGame(String value) throws RemoteException {

        Pattern r = Pattern.compile("(\\s*)([a-zA-Z_0-9]+)(\\s*)(\\S+)(\\s*)");

        Matcher m = r.matcher(value);

        if (this.playerId.equals("Client")) {

            this.callRemoteMethod("errorMessage", "Come prima cosa effettua il login.");

        } else if (this.gameHandler != null && this.player != null) {

            this.callRemoteMethod("errorMessage", "Prima di selezionare una partita, effettua il loguot da quella in cui sei adesso.");

        } else if (!m.find()) {

            this.callRemoteMethod("errorMessage", "Comando errato: seleziona una partita digitando \"selezionapartita\" seguito dal nome della partita"
                    + "e dal personaggio che vuoi utilizzare.");

        } else if (!ClientHandler.isGameHandlerPresent(m.group(2))) {

            this.callRemoteMethod("errorMessage", "Comando errato: inserisci una partita valida.");

        } else {

            try {

                this.gameHandler = ClientHandler
                        .getGameHandler(x -> x.getGameId().equals(m.group(2)));

                this.player = this.gameHandler.addPlayer(this.playerId, m.group(4));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                ClientHandler.broadcast(x -> !x.getPlayerId().equals("Client") && x.getGameHandler() == null, "showGames",
                        ClientHandler.getGameHandlerJsonArray().toString());

                if (!this.gameHandler.isGameStarted()) {

                    ClientHandler.gameBroadcast(x -> true, this.gameHandler,"updateGameNotStartedScreen",
                            this.gameHandler.toJsonObject().toString());
                }

            } catch (LoginException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
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
}
