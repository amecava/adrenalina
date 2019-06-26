package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.connection.RmiConnection;
import it.polimi.ingsw.client.view.connection.SocketConnection;
import it.polimi.ingsw.client.view.console.terminal.BoardDrawer;
import it.polimi.ingsw.client.view.console.terminal.JsonRegex;
import it.polimi.ingsw.client.view.console.terminal.Terminal;
import it.polimi.ingsw.virtual.JsonUtility;
import it.polimi.ingsw.virtual.VirtualView;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class ConsoleView implements View, VirtualView {

    private String id;

    public ConsoleView() {

        Terminal.addShutDownHook();

        Thread terminal = new Thread(Terminal::terminalRefresh);
        Thread input = new Thread(Terminal::inputReader);

        terminal.setDaemon(true);
        input.setDaemon(true);

        terminal.start();
        input.start();
    }

    @Override
    public JsonObject userInput() {

        while (true) {

            try {

                return JsonRegex.toJsonObject(Terminal.input().split(" ", 2));

            } catch (NoSuchElementException e) {

                this.errorMessage(e.getMessage());
            }
        }
    }

    @Override
    public void initialScreen(int discoveryPort, int rmiPort, int socketPort) {

        Terminal.output("Ricerca del server ADRENALINA.");

        try {

            InetAddress inetAddress = Client.discoverServer(discoveryPort);

            Terminal.output("");
            Terminal.output("Seleziona il tipo di connessione da utilizzare:");
            Terminal.output("    1. RMI");
            Terminal.output("    2. Socket");

            boolean found = false;

            while (!found) {

                String line = Terminal.input().toLowerCase();

                if (line.contains("rmi") && !line.contains("socket")) {

                    synchronized (connection) {

                        connection.add(new RmiConnection(inetAddress, rmiPort, this));

                        found = true;

                        connection.notifyAll();
                    }

                } else if (line.contains("socket") && !line.contains("rmi")) {

                    synchronized (connection) {

                        connection.add(new SocketConnection(inetAddress, socketPort, this));

                        found = true;

                        connection.notifyAll();
                    }
                } else {

                    this.errorMessage("Selezione non disponibile, riprova.");
                }
            }

            Terminal.clearScreen();
            Terminal.clearResponse();

            Terminal.output("");
            Terminal.output("Connessione in corso...");

        } catch (IOException e) {

            Terminal.clearScreen();

            Terminal.output("Server ADRENALINA non disponibile");
        }
    }

    @Override
    public void loginScreen() {

        Terminal.toggleMessages();

        this.splashScreen();

        Terminal.output(
                "Adrenalina porta i classici videogiochi “spara-tutto” direttamente sul vostro tavolo.");
        Terminal.output(
                "Costruisci il tuo arsenale per un turno micidiale, la risoluzione dei combattimenti è facile, ma senza dadi..");
        Terminal.output("Prendi fucile e munizioni ed inizia a sparare!");
        Terminal.output("");
        Terminal.output(
                "Come prima cosa effettua il login scrivendo la parola chiave \"login\" seguita dal tuo playerId.");
    }

    @Override
    public void gameListScreen() {

        this.splashScreen();
    }

    @Override
    public void gameNotStartedScreen() {

        this.splashScreen();
        Terminal.output(
                "Vota l'arena con il comando \"votaarena\" seguito dal numero dell'arena che vuoi utilizzare.");

        Terminal.output("");
        Terminal.output(
                "1: \u001b[34m█ █ █  \u001b[0m  2:\u001b[34m █ █ █ \u001b[32m█\u001b[0m  3: \u001b[31m█ \u001b[34m█ █  \u001b[0m  4: █ █ █ █\u001b[0m");
        Terminal.output(
                "   \u001b[31m█ █ █ \u001b[33m█\u001b[0m     \u001b[31m█ █\u001b[33m █ █\u001b[0m     \u001b[31m█ \u001b[35m█ █ \u001b[33m█\u001b[0m     █ █ █ █\u001b[0m");
        Terminal.output(
                "   \u001b[37m  █ █ \u001b[33m█\u001b[0m     \u001b[37m  █ \u001b[33m█ █\u001b[0m     \u001b[37m█ █ █ \u001b[33m█\u001b[0m     █ █ █ █\u001b[0m");
        Terminal.output("");
    }

    @Override
    public void boardScreen(JsonObject object) {

        Terminal.clearScreen();

        StringBuilder[] builder = BoardDrawer.drawBoard(object, this.id);

        Arrays.stream(builder).map(StringBuilder::toString).forEach(Terminal::output);
    }

    @Override
    public void broadcast(String value) {

        Terminal.broadcast(value);
    }

    @Override
    public void gameBroadcast(String value) {

        Terminal.gameBroadcast(value);
    }

    @Override
    public void infoMessage(String value) {

        Terminal.info(value);
    }

    @Override
    public void errorMessage(String value) {

        Terminal.error(value);
    }

    @Override
    public void isConnected(String value) {

        //
    }

    @Override
    public void completeLogin(String value) {

        JsonObject jsonObject = JsonUtility.jsonDeserialize(value);

        this.id = jsonObject.getString("playerId");

        if (jsonObject.getBoolean("gameStarted") && jsonObject.containsKey("gameId")) {

            this.infoMessage("Login effettuato come " + jsonObject.getString("playerId") +
                    " e riconnesso alla partita " + jsonObject.getString("gameId"));
        } else {

            this.infoMessage("Login effettuato come " + jsonObject.getString("playerId"));
        }
    }

    @Override
    public void completeDisconnect(String value) {

        //
    }

    @Override
    public void updateGameList(String value) {

        JsonObject object = JsonUtility.jsonDeserialize(value);

        JsonArray jsonArray = object.getJsonArray("gameList");

        this.gameListScreen();

        if (jsonArray.isEmpty()) {

            Terminal.output("Non sono ancora commands create partite.");
            Terminal.output("Creane una con il comando \"creapartita nomePartita(nome) "
                    + "numeroMorti(numero intero) frenesia(o niente se vuoi giocare senza)\".");

        } else {

            jsonArray.stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {

                        Terminal.output("Nome partita: " + x.getString("gameId")
                                + ", Numero morti: " + x.getInt("numberOfDeaths")
                                + ", Frenesia finale: " + (x.getBoolean("frenzy") ? "Sì"
                                : "No"));

                        Terminal.output(
                                "---> Giocatori connessi: " + x.getJsonArray("playerList")
                                        .stream()
                                        .map(JsonValue::asJsonObject)
                                        .map(y -> y.getString("playerId") + ": " + y
                                                .getString("character") + (
                                                y.getBoolean("connected")
                                                        ? "" : " (disconnesso)"))
                                        .collect(Collectors.toList()));
                    });
        }

    }

    @Override
    public void completeCreateGame(String value) {

        Terminal.info("Partita creata con nome " + value + ".");
    }

    @Override
    public void completeSelectGame(String value) {

        this.gameNotStartedScreen();

        Terminal.info("Sei stato aggiunto con successo alla partita " + value + ".");
    }

    @Override
    public void updateGameNotStartedScreen(String value) {

        JsonObject jsonObject = JsonUtility.jsonDeserialize(value);

        this.gameNotStartedScreen();

        Terminal.output("Giocatori connessi: " + jsonObject.getJsonArray("playerList")
                .stream()
                .map(JsonValue::asJsonObject)
                .map(y -> y.getString("playerId") + ": " + y
                        .getString("character") + (y.getBoolean("connected")
                        ? "" : " (disconnesso)"))
                .collect(Collectors.toList()));

        Terminal.output("");

        int countdown = jsonObject.getInt("countdown");

        switch (countdown) {

            case 60:

                Terminal.output(
                        "Raggiunto il numero minimo di giocatori, la partita inizierà dopo un minuto.\n\n\n\n\n");
                break;

            case 5:

                Terminal.output(
                        "     _____  \n    | ____| \n    | |__   \n    |___ \\  \n     ___) | \n    |____/  ");
                break;

            case 4:

                Terminal.output(
                        "     _  _    \n    | || |   \n    | || |_  \n    |__   _| \n       | |   \n       |_|   ");
                break;

            case 3:

                Terminal.output(
                        "     ____   \n    |___ \\  \n      __) | \n     |__ <  \n     ___) | \n    |____/  ");
                break;

            case 2:

                Terminal.output(
                        "     ___   \n    |__ \\  \n       ) | \n      / /  \n     / /_  \n    |____| ");
                break;

            case 1:

                Terminal.output(
                        "     __  \n    /_ | \n     | | \n     | | \n     | | \n     |_| ");
                break;

            case 0:

                Terminal.output(
                        "      ___   \n     / _ \\  \n    | | | | \n    | | | | \n    | |_| | \n     \\___/  ");
                break;

            default:

                Terminal.output("La partita inizierà tra " + countdown + " secondi.\n\n\n\n\n");
        }


    }

    @Override
    public void completeVoteBoard(String value) {

        Terminal.info("Hai votato per giocare con l'arena " + value + ".");
    }

    @Override
    public void completeSelectAction(String value) {

        JsonObject jSelectedAction = JsonUtility.jsonDeserialize(value);

        StringBuilder line = new StringBuilder()
                .append("Con l'azione selezionata puoi scrivere:\n");

        if (jSelectedAction.getInt("move") != 0) {

            line.append("- \"muovi/corri\" + coloreQuadrato + idQuadrato destinazione\n");
        }
        if (jSelectedAction.getBoolean("collect")) {

            line.append(
                    "- \"raccogli\" + (eventualmente) idCarta da raccogliere + (eventualmente) idCarta da scartare\n");

        }
        if (jSelectedAction.getBoolean("shoot")) {

            line.append("- \"selezionaarma\" + idCarta (da definire)\n");
        }
        if (jSelectedAction.getBoolean("reload")) {

            line.append("- \"ricarica\" + idCarta + (eventualmente)");
        }

        Terminal.info(line.toString());

    }

    @Override
    public void completeCardInfo(String value) {

        JsonObject jCardObject = JsonUtility.jsonDeserialize(value);

        StringBuilder info = new StringBuilder();

        info.append("Nome: ").append(jCardObject.getString("name")).append(" \n");

        info.append("Effetto primario: ").append(" \n");
        info.append(" -> Nome: ").append(jCardObject.getJsonObject("primary").getString("name"))
                .append(" \n");
        info.append(" -> Descrizione: ")
                .append(jCardObject.getJsonObject("primary").getString("description"))
                .append(" \n");

        if (jCardObject.get("alternative") != JsonValue.NULL) {

            info.append("Effetto alternativo: ").append(" \n");
            info.append(" -> Nome: ")
                    .append(jCardObject.getJsonObject("alternative").getString("name"))
                    .append(" \n");
            info.append(" -> Descrizione: ")
                    .append(jCardObject.getJsonObject("alternative").getString("description"))
                    .append(" \n");

            if (jCardObject.getJsonObject("alternative").get("cost") != null) {

                info.append(" -> Costo: ")
                        .append(jCardObject.getJsonObject("alternative").getString("cost"))
                        .append(" \n");
            }
        }

        if (jCardObject.get("optional1") != JsonValue.NULL) {

            info.append("Effetto opzionale: ").append(" \n");
            info.append(" -> Nome: ")
                    .append(jCardObject.getJsonObject("optional1").getString("name"))
                    .append(" \n");
            info.append(" -> Descrizione: ")
                    .append(jCardObject.getJsonObject("optional1").getString("description"))
                    .append(" \n");
            info.append(" -> Costo: ")
                    .append(jCardObject.getJsonObject("optional1").getString("cost"))
                    .append(" \n");
        }

        if (jCardObject.get("optional2") != JsonValue.NULL) {

            info.append("Effetto opzionale: ").append(" \n");
            info.append(" -> Nome: ")
                    .append(jCardObject.getJsonObject("optional2").getString("name"))
                    .append(" \n");
            info.append(" -> Descrizione: ")
                    .append(jCardObject.getJsonObject("optional2").getString("description"))
                    .append(" \n");
            info.append(" -> Costo: ")
                    .append(jCardObject.getJsonObject("optional2").getString("cost"))
                    .append(" \n");
        }

        info.append("Note: ").append(jCardObject.getString("notes")).append(" \n");

        Terminal.info(info.toString());
    }

    @Override
    public void completePowerUpInfo(String value) throws RemoteException {

        JsonObject jPowerUpObject = JsonUtility.jsonDeserialize(value);

        Terminal.info(new StringBuilder()
                .append("Nome: ")
                .append(jPowerUpObject.getString("name"))
                .append("\n")
                .append("Info specifiche da scrivere ... ")
                .toString());
    }

    @Override
    public void completeEndAction(String value) {

        JsonObject jActionBridgeObject = JsonUtility.jsonDeserialize(value);

        if (jActionBridgeObject.getInt("remainingActions") == 1) {

            Terminal.info("Ti rimane una sola azione.");

        } else {

            Terminal.info(
                    "Ti rimangono " + jActionBridgeObject.getInt("remainingActions") + " azioni.");
        }

    }

    @Override
    public void updateBoard(String value) {

        this.boardScreen(JsonUtility.jsonDeserialize(value));
    }

    @Override
    public void updateState(String value) {

        JsonRegex.updateState(JsonUtility.jsonDeserialize(value));
    }

    private void splashScreen() {

        Terminal.clearScreen();

        Terminal.output("");
        Terminal.output("    _   ___  ___ ___ _  _   _   _    ___ _  _   _   ");
        Terminal.output("   /_\\ |   \\| _ \\ __| \\| | /_\\ | |  |_ _| \\| | /_\\  ");
        Terminal.output(
                " \u001b[31m / _ \\| |) |   /\u001b[36m _\u001b[31m||\u001b[36m .` \u001b[31m|/ _ \u001b[36m\\| |__ | || .` |/ _ \\ ");
        Terminal.output(" \u001b[0m/_/ \\_\\___/|_|_\\___|_|\\_/_/ \\_\\____|___|_|\\_/_/ \\_\\\n");
    }
}
