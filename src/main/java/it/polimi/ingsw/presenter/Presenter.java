package it.polimi.ingsw.presenter;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import it.polimi.ingsw.virtual.VirtualPresenter;
import java.rmi.RemoteException;
import java.util.ArrayList;
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

            ClientHandler
                    .gameBroadcast(this.gameHandler, x -> !x.getValue().equals(this), "infoMessage",
                            this.playerId + ": riconnesso alla partita " + this.gameHandler
                                    .getGameId()
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
        String returnString;
        int i = 0;
        List<String> regexList = new ArrayList<>();
        String regex1 = "(\\s*)(\\S+)(\\s*)";
        String regex2 = "(\\s*)([5-8])(\\s*)";
        String regex3 = "(\\s*)(frenesia|)(\\s*)";

        regexList.add(regex1);

        regexList.add(regex2);

        regexList.add(regex3);


        Pattern p = Pattern.compile(regexList.get(i));
        Matcher m = p.matcher(value);
        while (m.lookingAt()) {

            regexList.set(i, m.group(2));
            value = value.substring(m.end());
            i++;
            if (i >= regexList.size()) {
                break;
            }
            p = Pattern.compile(regexList.get(i));
            m = p.matcher(value);
        }
        if (i < regexList.size() || value.length() > 0) {
            switch (i) {
                case 0:
                    returnString = "scrivi un nome partita";
                    break;
                case 1:
                    returnString = "immetti un numero da 1-5";
                    break;

                default:
                    returnString = " metti frenesia o meno";
            }
            this.callRemoteMethod("errorMessage", "Errore di scrittura." + returnString);
        }




        //TODO frenesia o niente
        /*
        String pattern = "(\\s*)([a-zA-Z_0-9]+)(\\s*)([5-8])(\\s*)(vero|falso)";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(value);



        if (!m.find()) {

            this.callRemoteMethod("errorMessage",
                    "Comando errato, riprova scrivendo \"creapartita nomePartita(nome) numeroMorti(numero intero da 5 a 8) frenesia(vero/falso)\".");


         */
         else if (ClientHandler.isGameHandlerPresent(regexList.get(0))) {

            this.callRemoteMethod("errorMessage",
                    "Nome partita già esistente, ripeti con un nuovo nome.");

        } else {

            ClientHandler.addGameHandler(regexList.get(0), Integer.valueOf(regexList.get(1)),
                    regexList.get(2).equals("frenesia"));

            this.callRemoteMethod("completeCreateGame", regexList.get(0));

            ClientHandler.broadcast(x -> !x.equals(this), "infoMessage",
                    "Partita " + regexList.get(0) + " creata.");
        }
    }

    @Override
    public void selectGame(String value) throws RemoteException {
        int i = 0;
        List<String> regexList = new ArrayList<>();
        String returnString;
        String regex1 = "(\\s*)([a-zA-Z_0-9]+)(\\s*)";
        String regex2 = "(\\s*)(\\S+)(\\s*)";
        regexList.add(regex1);
        regexList.add(regex2);
        Pattern p = Pattern.compile(regexList.get(i));
        Matcher m = p.matcher(value);
        while (m.lookingAt()) {
            regexList.set(i, m.group(2));
            value = value.substring(m.end());
            i++;
            if (i >= regexList.size()) {
                break;
            }
            p = Pattern.compile(regexList.get(i));
            m = p.matcher(value);

        }
        if (i < regexList.size() || value.length() > 0) {
            switch (i) {
                case 0:
                    returnString = " per favore scrivi un nome partita corretto";
                    break;
                case 1:
                    returnString = " per favore scrivi uno dei personaggi validi";
                    break;
                default:
                    returnString = "";
            }
            this.callRemoteMethod("errorMessage", "Errore di scrittura." + returnString);
        }


        /*
        String pattern = "(\\s*)([a-zA-Z_0-9]+)(\\s*)()(\\s*)";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(value);

         */

        else if (this.playerId.equals("RMI client") || this.playerId.equals("Socket client")) {

            this.callRemoteMethod("errorMessage",
                    "Effettua il login prima di selezionare una partita (comando: login nomeUtente).");

        } else if (this.gameHandler != null && this.player != null) {

            this.callRemoteMethod("errorMessage", "Prima logout.");

         /*else if (!m.find()) {

            this.callRemoteMethod("errorMessage", "REGEX.");

          */

        } else if (!ClientHandler.isGameHandlerPresent(regexList.get(0))) {

            this.callRemoteMethod("errorMessage", "Nome partita preso cazzo.");

        } else {

            try {
                for (String string : regexList) {
                    System.out.println(string);
                }

                this.gameHandler = ClientHandler
                        .getGameHandler(x -> x.getGameId().equals(regexList.get(0)));

                this.player = this.gameHandler.addPlayer(this.playerId, regexList.get(1));

                ClientHandler.putPlayerPresenter(this.gameHandler, this.player, this);

                this.callRemoteMethod("completeSelectGame", this.gameHandler.getGameId());

                ClientHandler.gameBroadcast(this.gameHandler, x -> !x.getValue().equals(this),
                        "infoMessage",
                        this.playerId + ": connesso alla partita " + this.gameHandler.getGameId()
                                + ".");

            } catch (LoginException e) {

                this.callRemoteMethod("errorMessage", e.getMessage());

            }
        }
    }

    @Override
    public void endOfTurn(String value) throws RemoteException {
        if (!this.player.isActivePlayer()) {
            this.callRemoteMethod("errorMessage", "Non sei l'active player..");
        }
        try {
            this.gameHandler.endOfTurn();
            Thread canFinishTurn = new Thread(() -> this.gameHandler.canFinishTurn(this.player));
            canFinishTurn.start();
            this.callRemoteMethod("completeEndOfTurn", "");
        } catch (EndGameException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            this.callRemoteMethod("errorMessage", e.getMessage());
        }

    }
}
