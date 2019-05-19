package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.server.model.decks.WeaponDeck.WeaponDeckBuilder;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.connection.RmiConnection;
import it.polimi.ingsw.client.view.connection.SocketConnection;
import it.polimi.ingsw.virtual.VirtualView;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class ConsoleView implements View, VirtualView {

    final String METHOD = "method";
    final String VALUE = "value";

    //avevo tolto private static ma se non sbaglio l'avevamo fatto insieme
    private static Map<String, String> map = new HashMap<>();

    static {

        InputStream in = WeaponDeckBuilder.class.getClassLoader().getResourceAsStream("ConsoleInput.json");

        JsonArray object = Json.createReader(in).readArray();

        object.stream()
                .map(JsonValue::asJsonObject)
                .forEach(x ->

                        x.getJsonArray("input").stream()
                                .map(JsonValue::toString)
                                .forEach(y -> map.put(y, x.getString(METHOD)))
                );
    }

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

        JsonObjectBuilder builder = Json.createObjectBuilder();

        while (true) {

            String[] parts = Terminal.input().split(" ", 2);

            builder.add(VALUE, parts.length == 2 ? parts[1] : "");

            try {

                return builder.add(
                        METHOD,
                        map.entrySet().stream()
                                .filter(x -> levenshteinDistance(parts[0], x.getKey()) <= 3)
                                .map(Entry::getValue)
                                .findFirst()
                                .orElseThrow(NoSuchElementException::new)).build();

            } catch (NoSuchElementException e) {

                this.errorMessage("Selezione non disponibile, riprova o digita help.");
            }
        }
    }

    @Override
    public void searchingForServer() {

        Terminal.output("Ricerca del server ADRENALINA.");
    }

    @Override
    public Runnable selectConnection(InetAddress inetAddress, int rmiPort, int socketPort) {

        Terminal.output("");
        Terminal.output("Seleziona il tipo di connessione da utilizzare:");
        Terminal.output("    1. RMI");
        Terminal.output("    2. Socket");

        while (true) {

            String line = Terminal.input().toLowerCase();

            if (line.contains("rmi") && !line.contains("socket")) {

                return new RmiConnection(inetAddress, rmiPort, this);

            } else if (line.contains("socket") && !line.contains("rmi")) {

                return new SocketConnection(inetAddress, socketPort, this);

            } else {

                this.errorMessage("Selezione non disponibile, riprova.");
            }
        }
    }

    @Override
    public void connectingToServer() {

        Terminal.clearScreen();
        Terminal.clearResponse();

        this.searchingForServer();

        Terminal.output("");
        Terminal.output("Connessione in corso...");
    }

    @Override
    public void splashScreen() {

        Terminal.clearScreen();

        Terminal.output("");
        Terminal.output("    _   ___  ___ ___ _  _   _   _    ___ _  _   _   ");
        Terminal.output("   /_\\ |   \\| _ \\ __| \\| | /_\\ | |  |_ _| \\| | /_\\  ");
        Terminal.output("  / _ \\| |) |   / _|| .` |/ _ \\| |__ | || .` |/ _ \\ ");
        Terminal.output(" /_/ \\_\\___/|_|_\\___|_|\\_/_/ \\_\\____|___|_|\\_/_/ \\_\\\n");
    }

    @Override
    public void loginScreen() {

        Terminal.toggleMessages();

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
    public void gameNotStartedScreen() {

        this.splashScreen();
        Terminal.output(
                "Se non lo hai ancora fatto, vota l'arena che vuoi utilizzare con il comando"
                        + " \"votaarena\" seguito dal numero dell'arena che desideri.");
        Terminal.output("boards");
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

        this.infoMessage(value);
    }

    @Override
    public void completeDisconnect(String value) {

        this.splashScreen();
    }

    @Override
    public void showGames(String value) {

        try (JsonReader reader = Json.createReader(new StringReader(value))) {

            JsonArray jsonArray = reader.readArray();

            this.splashScreen();

            if (jsonArray.isEmpty()) {

                Terminal.output("Non sono ancora state create partite.");
                Terminal.output("Creane una con il comando \"creapartita nomePartita(nome) "
                        + "numeroMorti(numero intero) frenesia(vero/falso)\".");

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

        try (JsonReader reader = Json.createReader(new StringReader(value))) {

            JsonObject jsonObject = reader.readObject();

            this.gameNotStartedScreen();

            Terminal.output("Giocatori connessi: " + jsonObject.getJsonArray("playerList")
                    .stream()
                    .map(JsonValue::asJsonObject)
                    .map(y -> y.getString("playerId") + ": " + y
                            .getString("character") + (y.getBoolean("connected")
                            ? "" : " (disconnesso)"))
                    .collect(Collectors.toList()));
        }

    }

    @Override
    public void completeVoteBoard(String value) {

        Terminal.info("Hai votato per giocare con l'arena " + value + ".");
    }


    @Override
    public void showBoard(String value) {

        String[][] square;

        ColoredChars[][] fullBoard = new ColoredChars[16][12];

        try (JsonReader reader = Json.createReader(new StringReader(value))) {

            JsonArray jsonArray = reader.readObject().getJsonArray("arrays");

            Terminal.clearScreen();

            int i = 0;

            for (JsonValue rows : jsonArray) {

                for (JsonValue squaresLine : rows.asJsonArray().getJsonArray(i)) {

                    square = this.drawSquare(squaresLine.asJsonObject());

                    for (i = 0; i < 4; i++) {

                        for (int j = 0; j < 4; j++) {

                            System.out.println(square[i][j]);
                        }
                    }
                }
            }

        }
    }

    private String[][] drawSquare(JsonObject jsonSquare) {

        final int MAX_VERT_TILES = 4; //rows.
        final int MAX_HORIZ_TILES = 4; //cols.

        String[][] tiles = new String[MAX_VERT_TILES][MAX_HORIZ_TILES];

        if (jsonSquare.containsKey("empty")) {

            for (String[] row: tiles) {

                Arrays.fill(row, " ");
            }

            return tiles;
        }

        String squareColor = Color.ansiColor(jsonSquare.getString("color"));

        tiles[0][0] = squareColor + "╔";

        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {

            tiles[0][c] = "═";
        }

        tiles[0][MAX_HORIZ_TILES - 1] = "╗";

        for (int r = 1; r < MAX_VERT_TILES - 1; r++) {

            tiles[r][0] = "║";
            for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {

                tiles[r][c] = " ";
            }

            tiles[r][MAX_HORIZ_TILES - 1] = "║";
        }

        tiles[MAX_VERT_TILES - 1][0] = "╚";

        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {

            tiles[MAX_VERT_TILES - 1][c] = "═";
        }

        tiles[MAX_VERT_TILES - 1][MAX_HORIZ_TILES - 1] = "╝" + Color.ansiColor("ANY");

        return tiles;
    }

    int levenshteinDistance(String input, String match) {

        input = input.toLowerCase();
        match = match.toLowerCase();

        int[] costs = new int[match.length() + 1];

        for (int i = 0; i <= input.length(); i++) {

            int lastValue = i;

            for (int j = 0; j <= match.length(); j++) {

                if (i == 0) {

                    costs[j] = j;

                } else if (j > 0) {

                    int newValue = costs[j - 1];

                    if (input.charAt(i - 1) != match.charAt(j - 1)) {

                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }

                    costs[j - 1] = lastValue;

                    lastValue = newValue;
                }
            }

            if (i > 0) {

                costs[match.length()] = lastValue;
            }
        }

        return costs[match.length()];
    }
}
