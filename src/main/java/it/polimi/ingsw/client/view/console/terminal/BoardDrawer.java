package it.polimi.ingsw.client.view.console.terminal;

import it.polimi.ingsw.client.view.console.ConsoleView;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.players.Color;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Class responsible of analyzing the JsonObject of the board that arrives from the server and
 * building the Strings that will be printed by the Terminal class.
 */
public class BoardDrawer {

    /**
     * Number of rows of the squares that will be printed.
     */
    private static final int MAX_VERT_TILES = 5; //rows.

    /**
     * StringBuilder to print the cubes in the square.
     */
    private static StringBuilder cubesSubString = new StringBuilder();

    /**
     * Total height of the array of strings that will be printed.
     */
    private static final int TOTAL_HEIGHT = 21; //rows.

    /**
     * StringBuilder to print the players in the square.
     */
    private static StringBuilder playersSubString = new StringBuilder();

    /**
     * Array of StringBuilders that will be printed.
     */
    private static StringBuilder[] squareLine = new StringBuilder[TOTAL_HEIGHT];

    /**
     * JsonArray containing all the characters of the squares.
     */
    private static JsonArray jDrawSquares;

    static {

        InputStream in = ConsoleView.class.getClassLoader().getResourceAsStream("DrawBoards.json");

        jDrawSquares = Json.createReader(in).readArray();
    }

    /**
     * Private constructor to hide the public implicit one.
     */
    private BoardDrawer() {

        //
    }

    /**
     * For each square this method combines walls and all the information contained in the
     * jsonObject given as a parameter to build dynamically the array of strings with players, ammo
     * cubes and the id of the cards.
     *
     * @param jsonObject JsonObject created every time the board needs to be updated containing
     * every necessary information.
     * @param playerId The id of the player that's playing on that client.
     * @return Built Array of strings.
     */
    public static StringBuilder[] drawBoard(JsonObject jsonObject, String playerId) {

        JsonObject thisPlayerObject = jsonObject.getJsonArray("playerList").stream()
                .map(JsonValue::asJsonObject)
                .filter(x -> x.getString("playerId").equals(playerId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

        JsonArray jsonArray = jsonObject.getJsonObject("board").getJsonArray("arrays");
        JsonArray jDrawSquaresArray = jDrawSquares
                .getJsonArray(jsonObject.getJsonObject("board").getInt("boardId"));

        squareLine[0] = new StringBuilder()
                .append(addBoardDeaths(jsonObject.getJsonObject("deaths")));

        //for each line of the map
        for (int row = 0; row < 3; row++) {

            squareLine[MAX_VERT_TILES * row + 1] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 2] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 3] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 4] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 5] = new StringBuilder();

            //for each square of that line
            for (int squareInRow = 0; squareInRow < 4; squareInRow++) {

                cubesSubString
                        .append(BoardDrawer.addTiles(new StringBuilder(), jsonArray, row,
                                squareInRow));

                playersSubString.append(BoardDrawer.addPlayers(new StringBuilder(), jsonArray, row,
                        squareInRow));

                //for each string of that square
                for (int lineInSquare = 0; lineInSquare < MAX_VERT_TILES; lineInSquare++) {

                    if ((lineInSquare == 2) && !jsonArray.getJsonArray(row)
                            .getJsonObject(squareInRow).containsKey("empty")) {

                        squareLine[MAX_VERT_TILES * row + lineInSquare + 1]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(playersSubString.toString());

                    } else if (lineInSquare == 3 && !jsonArray.getJsonArray(row)
                            .getJsonObject(squareInRow).containsKey("empty")) {

                        squareLine[MAX_VERT_TILES * row + lineInSquare + 1]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(cubesSubString.toString());


                    } else {

                        squareLine[MAX_VERT_TILES * row + lineInSquare + 1]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow)
                                        .getString(lineInSquare));
                    }
                }

                cubesSubString = new StringBuilder();
                playersSubString = new StringBuilder();
            }
        }

        for (int tmp = 16; tmp < 21; tmp++) {

            squareLine[tmp] = new StringBuilder();
        }

        addMyPowerUps(thisPlayerObject);

        addMyCards(thisPlayerObject);

        addMyCubes(thisPlayerObject);

        Arrays.stream(squareLine).forEach(x -> x.append("  "));

        return addPlayersBridges(squareLine, jsonObject);
    }

    /**
     * Adds the deaths of the match.
     *
     * @param object JsonObject of the board.
     * @return The string with the deaths updated.
     */
    private static String addBoardDeaths(JsonObject object) {

        JsonObject damageBridge = object.getJsonObject("damageBridge");
        StringBuilder line = new StringBuilder();

        line.append("Plancia delle morti: ");

        damageBridge.getJsonArray("shots")
                .stream()
                .map(JsonValue::toString)
                .map(x -> x.substring(1, x.length() - 1))
                .map(Color::ansiColorOf)
                .forEach(x -> line.append(x).append("x").append(" "));

        line.append(Color.ansiColorOf("ALL"));

        for (int i = 0;
                i < object.getInt("numberOfDeaths") - damageBridge.getJsonArray("shots").size();
                i++) {

            line.append("- ");
        }

        return line.append(fixLength(48, 21 + object.getInt("numberOfDeaths") * 2)).toString();
    }

    /**
     * Adds the ammo cubes of the player.
     *
     * @param thisPlayerObject JsonObject of the player.
     */
    private static void addMyCubes(JsonObject thisPlayerObject) {

        squareLine[20].append(Color.ansiColorOf(Color.getColor(thisPlayerObject
                .getString("character")).toString()))
                .append("Le tue munizioni: ");

        thisPlayerObject.getJsonArray("ammoCubes").stream()
                .map(x -> x.toString().substring(1, x.toString().length() - 1))
                .forEach(x -> squareLine[20].append(Color.ansiColorOf(x))
                        .append("◆"));

        squareLine[20].append(fixLength(48,
                squareLine[20].length() - 5 - (5 * thisPlayerObject.getJsonArray("ammoCubes")
                        .size()))).append(Color.ansiColorOf("ALL"));
    }

    /**
     * Adds the power ups of the player.
     *
     * @param thisPlayerObject JsonObject of the player.
     */
    private static void addMyPowerUps(JsonObject thisPlayerObject) {

        StringBuilder thisPlayerPowerUps = new StringBuilder();

        thisPlayerObject.getJsonArray("powerUps")
                .stream()
                .map(JsonValue::asJsonObject)
                .forEach(x ->
                        thisPlayerPowerUps.append(Color.ansiColorOf(x.getString("color")))
                                .append(x.getString("name").substring(0, 3))
                                .append(" ")
                );

        squareLine[16].append(Color.ansiColorOf(Color.getColor(thisPlayerObject
                .getString("character")).toString()))
                .append("PowerUp: ")
                .append(thisPlayerPowerUps.toString());

        squareLine[16].append(fixLength(48,
                squareLine[16].length() - 5 - (5 * thisPlayerObject.getJsonArray("powerUps")
                        .size())));
    }

    /**
     * Adds the cards of the player.
     *
     * @param thisPlayerObject JsonObject of the player.
     */
    private static void addMyCards(JsonObject thisPlayerObject) {

        for (int cards = 0; cards < thisPlayerObject.getJsonArray("weapons").size(); cards++) {

            squareLine[17 + cards].append(Color.ansiColorOf(Color.getColor(thisPlayerObject
                    .getString("character")).toString()))
                    .append("ID: ")
                    .append(thisPlayerObject.getJsonArray("weapons").getJsonObject(cards)
                            .getInt("id"))
                    .append(" ")
                    .append("Nome: ")
                    .append(thisPlayerObject.getJsonArray("weapons").getJsonObject(cards)
                            .getString("name"));

            if (!thisPlayerObject.getJsonArray("weapons").getJsonObject(cards)
                    .getBoolean("isLoaded")) {

                squareLine[17 + cards].append("(scarica)");
            }

            squareLine[17 + cards].append(fixLength(48, squareLine[17 + cards].length() - 5));

        }

        for (int i = 17 + thisPlayerObject.getJsonArray("weapons").size();
                i < MAX_VERT_TILES * 3 + 5; i++) {

            squareLine[i].append("                                                ");
        }
    }

    /**
     * Fixes the length of each string with enough spaces to be long distReach, starting from its
     * initial length.
     *
     * @param distReach Length of the final string.
     * @param length Initial length of the string.
     * @return A string full of spaces that will be appended by the caller.
     */
    private static String fixLength(int distReach, int length) {

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < distReach - length; i++) {

            line.append(" ");
        }

        return line.toString();
    }

    /**
     * Adds the ammo cubes in every square based on the information contained in the jsonArray.
     *
     * @param cubesSubString StringBuilder to be filled.
     * @param jsonArray Array containing all the information necessary to build the strings.
     * @param row Number of the row of the square that needs to be filled.
     * @param squareInRow Number of the column of the square that needs to be filled.
     * @return Filled string.
     */
    private static String addTiles(StringBuilder cubesSubString, JsonArray jsonArray,
            int row, int squareInRow) {

        cubesSubString.append(" ");

        if (jsonArray.getJsonArray(row).getJsonObject(squareInRow).containsKey("empty")) {

            return null;

        } else if ((jsonArray.getJsonArray(row).getJsonObject(squareInRow).containsKey("isSpawn")
                && jsonArray.getJsonArray(row).getJsonObject(squareInRow).getBoolean("isSpawn"))) {

            for (JsonValue cards : jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow)
                    .getJsonArray("tools")) {

                cubesSubString.append(cards.asJsonObject().getInt("id")).append(" ");
            }
            cubesSubString.append(fixLength(10, cubesSubString.length()));

        } else {

            for (JsonValue card : jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow)
                    .getJsonArray("tools")) {

                card.asJsonObject().getJsonArray("colors").stream()
                        .map(x -> x.toString().substring(1, x.toString().length() - 1))
                        .map(Color::ansiColorOf)
                        .forEach(y -> {

                            cubesSubString.append(y);
                            cubesSubString.append("◆");
                        });
            }

            if (jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow)
                    .getJsonArray("tools").isEmpty()) {

                cubesSubString.append(fixLength(10, 1));

            } else {

                cubesSubString.append(fixLength(10, (int) jsonArray.getJsonArray(row)
                        .getJsonObject(squareInRow)
                        .getJsonArray("tools")
                        .getJsonObject(0)
                        .getJsonArray("colors").stream()
                        .map(JsonValue::toString).count() + 1));
            }
        }

        if ((Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.ENDMAP)) || (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.WALL))) {

            cubesSubString
                    .append(Color.ansiColorOf(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else if (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.DOOR)) {

            cubesSubString
                    .append(Color.ansiColorOf(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┏");

        } else {

            cubesSubString.append(" ");
        }

        return cubesSubString.toString();
    }

    /**
     * Adds the players in every square based on the information contained in the jsonArray.
     *
     * @param playersSubString StringBuilder to be filled.
     * @param jsonArray Array containing all the information necessary to build the strings.
     * @param row Number of the row of the square that needs to be filled.
     * @param squareInRow Number of the column of the square that needs to be filled.
     * @return Filled string.
     */
    private static String addPlayers(StringBuilder playersSubString, JsonArray jsonArray,
            int row, int squareInRow) {

        playersSubString.append(" ");

        if (jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).containsKey("empty")) {

            return playersSubString
                    .append(" ")
                    .toString();
        }

        if (jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).get("playersIn") == JsonValue.NULL) {

            playersSubString.append(fixLength(10, 1));

        } else {

            for (Color color : jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow).getJsonArray("playersIn").stream()
                    .map(JsonValue::asJsonObject).map(x -> x.getString("character"))
                    .map(Color::getColor).collect(Collectors.toList())) {

                playersSubString
                        .append(Color.ansiColorOf(color.toString()))
                        .append(color.getCharacter().substring(0, 1));

            }

            playersSubString.append(fixLength(10, (int) jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow).getJsonArray("playersIn").stream()
                    .map(JsonValue::asJsonObject).map(x -> x.getString("character"))
                    .map(Color::getColor).count() + 1));
        }

        if ((Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.ENDMAP)) || (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.WALL))) {

            playersSubString
                    .append(Color.ansiColorOf(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else {

            playersSubString.append(" ");
        }

        return playersSubString.toString();
    }

    /**
     * Adds the bridges of each player to the array of strings that will be printed.
     *
     * @param squareLine Array of strings needed to build the bridges.
     * @param jGameHandlerObject JsonObject containing all the information needed to update the
     * bridges.
     * @return A StringBuilder with the strings updated.
     */
    private static StringBuilder[] addPlayersBridges(StringBuilder[] squareLine,
            JsonObject jGameHandlerObject) {

        JsonArray jPlayersArray = jGameHandlerObject.getJsonArray("playerList");

        StringBuilder name;
        StringBuilder actions;
        StringBuilder deaths;
        StringBuilder cards;

        //for each player
        for (int player = 0; player < jPlayersArray.size(); player++) {

            name = new StringBuilder();
            actions = new StringBuilder();
            deaths = new StringBuilder();
            cards = new StringBuilder();
            if (jPlayersArray.getJsonObject(player).getBoolean("isActivePlayer")) {

                name.append(Color.ansiColorOf("ALL")).append("Turno - ");
            }

            name.append(Color.ansiColorOf(
                    Color.getColor(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(jPlayersArray.getJsonObject(player).getString("character"))
                    .append("(" + jPlayersArray.getJsonObject(player).getString("playerId") + ")")
                    .append(addDamages(jPlayersArray.getJsonObject(player)));

            actions.append(Color.ansiColorOf(
                    Color.getColor(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(" -> Azioni:")
                    .append(addActions(jPlayersArray.getJsonObject(player)));

            deaths.append(Color.ansiColorOf(
                    Color.getColor(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(" -> Plancia Morti: ")
                    .append(addDeaths(jPlayersArray.getJsonObject(player)));

            cards.append(Color.ansiColorOf(
                    Color.getColor(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(addOthersCards(jPlayersArray.getJsonObject(player)));

            squareLine[4 * player].append(name.toString());
            squareLine[4 * player + 1].append(actions.toString());
            squareLine[4 * player + 2].append(deaths.toString());
            squareLine[4 * player + 3].append(cards.toString());
        }

        return squareLine;
    }

    /**
     * Auxiliary of addPlayersBridges. Adds the cards of each player to his bridge.
     *
     * @param jPlayerObject JsonObject containing all the information needed to update the strings.
     * @return The updated string with the cards.
     */
    private static String addOthersCards(JsonObject jPlayerObject) {

        StringBuilder cards = new StringBuilder();

        jPlayerObject.getJsonArray("weapons").stream()
                .map(JsonValue::asJsonObject)
                .forEach(x -> {

                    cards.append(x.getInt("id"));

                    if (!x.getBoolean("isLoaded")) {

                        cards.append("(scarica) ");
                    } else {

                        cards.append(" ");
                    }
                });

        return new StringBuilder().append(" -> Carte: ")
                .append(cards.toString())
                .append(" -> Numero power up: ")
                .append(jPlayerObject.getJsonArray("powerUps").size())
                .toString();

    }

    /**
     * Auxiliary of addPlayersBridges. Adds the damages of each player to his bridge.
     *
     * @param jPlayerObject JsonObject containing all the information needed to update the strings.
     * @return The updated string with the damages.
     */
    private static String addDamages(JsonObject jPlayerObject) {

        StringBuilder line = new StringBuilder();

        line.append(" -> Danni: ");

        jPlayerObject.getJsonObject("bridge").getJsonObject("damageBridge").getJsonArray("shots")
                .stream()
                .map(JsonValue::toString)
                .map(x -> x.substring(1, x.length() - 1))
                .map(Color::ansiColorOf)
                .forEach(x -> {

                    line.append(x);
                    line.append("◉");
                });

        return line.append(Color.ansiColorOf(
                Color.getColor(jPlayerObject.getString("character"))
                        .toString())).append(addMarks(jPlayerObject
                .getJsonObject("bridge")
                .getJsonObject("damageBridge")
                .getJsonArray("marks"))).toString();
    }

    /**
     * Auxiliary of addPlayersBridges. Adds the marks of each player to his bridge.
     *
     * @param jMarksArray JsonArray containing all the information needed to update the strings.
     * @return The updated string with the marks.
     */
    private static String addMarks(JsonArray jMarksArray) {

        StringBuilder line = new StringBuilder();

        line.append(" -> Marchi: ");

        jMarksArray.stream()
                .map(JsonValue::toString)
                .map(x -> x.substring(1, x.length() - 1))
                .map(Color::ansiColorOf)
                .forEach(x -> {

                    line.append(x);
                    line.append("◎");
                });

        return line.toString();
    }

    /**
     * Auxiliary of addPlayersBridges. Adds the possible actions of each player to his bridge.
     *
     * @param jPlayerObject JsonObject containing all the information needed to update the strings.
     * @return The updated string with the actions.
     */
    private static String addActions(JsonObject jPlayerObject) {

        StringBuilder line = new StringBuilder();

        line.append(" ");

        jPlayerObject.getJsonObject("bridge")
                .getJsonObject("actionBridge")
                .getJsonArray("possibleActionsArray").stream()
                .map(JsonValue::asJsonObject).forEach(x -> {

            if (x.getInt("move") != 0) {

                line.append("m(").append(x.getInt("move")).append(") ");
            }
            if (x.getBoolean("collect")) {

                line.append("c ");
            }
            if (x.getBoolean("shoot")) {

                line.append("s ");
            }
            if (x.getBoolean("reload")) {

                line.append("r ");
            }

            line.append("| ");
        });

        return line.toString();
    }

    /**
     * Auxiliary of addPlayersBridges. Adds the death points of each player to his bridge.
     *
     * @param jPlayerObject JsonObject containing all the information needed to update the strings.
     * @return The updated string with the death points.
     */
    private static String addDeaths(JsonObject jPlayerObject) {

        StringBuilder line = new StringBuilder();

        jPlayerObject.getJsonObject("bridge")
                .getJsonArray("deathBridgeArray").stream()
                .map(JsonValue::asJsonObject).forEach(x -> {

            if (x.getBoolean("used")) {

                line.append("\uD83D\uDC80");

            } else {

                line.append(x.getInt("value"));
            }

            line.append(" ");
        });

        return line.toString();
    }


}
