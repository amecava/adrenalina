package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.players.Color;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class BoardDrawer {

    private static final int MAX_VERT_TILES = 5; //rows.

    private static StringBuilder cubesSubString = new StringBuilder();
    private static StringBuilder playersSubString = new StringBuilder();
    private static StringBuilder[] squareLine = new StringBuilder[MAX_VERT_TILES * 3];

    private static JsonArray jDrawSquares;

    static {

        InputStream in = ConsoleView.class.getClassLoader().getResourceAsStream("DrawBoards.json");

        jDrawSquares = Json.createReader(in).readArray();
    }

    public static StringBuilder[] drawBoard(JsonObject jsonObject) {

        JsonArray jsonArray = jsonObject.getJsonObject("board").getJsonArray("arrays");
        JsonArray jDrawSquaresArray = jDrawSquares.getJsonArray(jsonObject.getJsonObject("board").getInt("boardId"));

        //for each line of the map
        for (int row = 0; row < 3; row++) {

            squareLine[MAX_VERT_TILES * row] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 1] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 2] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 3] = new StringBuilder();
            squareLine[MAX_VERT_TILES * row + 4] = new StringBuilder();

            //for each square of that line
            for (int squareInRow = 0; squareInRow < 4; squareInRow++) {

                cubesSubString
                        .append(BoardDrawer.addTiles(new StringBuilder(), jsonArray, row,
                                squareInRow));

                playersSubString.append(BoardDrawer.addPlayers(new StringBuilder(), jsonArray, row,
                        squareInRow));

                //for each string of that square
                for (int lineInSquare = 0; lineInSquare < MAX_VERT_TILES; lineInSquare++) {

                    if ((lineInSquare == 1) && !jsonArray.getJsonArray(row)
                            .getJsonObject(squareInRow).containsKey("empty")) {

                        squareLine[MAX_VERT_TILES * row + lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(playersSubString.toString());

                    } else if (lineInSquare == 3 && !jsonArray.getJsonArray(row)
                            .getJsonObject(squareInRow).containsKey("empty") && !jsonArray
                            .getJsonArray(row)
                            .getJsonObject(squareInRow).getBoolean("isSpawn")) {

                        squareLine[MAX_VERT_TILES * row + lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(cubesSubString.toString());


                    } else {

                        squareLine[MAX_VERT_TILES * row + lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow)
                                        .getString(lineInSquare));
                    }
                }

                cubesSubString = new StringBuilder();
                playersSubString = new StringBuilder();
            }
        }

        Arrays.stream(squareLine).forEach(x -> x.append("  "));

        return addPlayersBridges(squareLine, jsonObject);
    }

    private static String fixLength(int length) {

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < 10 - length; i++) {

            line.append(" ");
        }

        return line.toString();
    }

    private static String addTiles(StringBuilder cubesSubString, JsonArray jsonArray,
            int row, int squareInRow) {

        cubesSubString.append(" ");

        if ((jsonArray.getJsonArray(row).getJsonObject(squareInRow).containsKey("isSpawn")
                && jsonArray.getJsonArray(row).getJsonObject(squareInRow).getBoolean("isSpawn"))
                || jsonArray.getJsonArray(row).getJsonObject(squareInRow).containsKey("empty")) {

            return null;

        }

        for (String color : jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow)
                .getJsonArray("tools")
                .getJsonObject(0)
                .getJsonArray("colors").stream()
                .map(x -> x.toString().substring(1, x.toString().length() - 1))
                .map(Color::ansiColor)
                .collect(Collectors.toList())) {

            cubesSubString
                    .append(color)
                    .append("◆");
        }

        cubesSubString.append(fixLength((int) jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow)
                .getJsonArray("tools")
                .getJsonObject(0)
                .getJsonArray("colors").stream()
                .map(x -> x.toString()).count() + 1));

        if ((Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.ENDMAP)) || (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.WALL))) {

            cubesSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else if (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.DOOR)) {

            cubesSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┏");

        } else {

            cubesSubString.append(" ");
        }

        return cubesSubString.toString();
    }

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

            playersSubString.append(fixLength(1));

        } else {

            for (Color color : jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow).getJsonArray("playersIn").stream()
                    .map(JsonValue::asJsonObject).map(x -> x.getString("character"))
                    .map(Color::ofCharacter).collect(Collectors.toList())) {

                playersSubString
                        .append(Color.ansiColor(color.toString()))
                        .append(color.getCharacter().substring(0, 1));

            }

            playersSubString.append(fixLength((int) jsonArray.getJsonArray(row)
                    .getJsonObject(squareInRow).getJsonArray("playersIn").stream()
                    .map(JsonValue::asJsonObject).map(x -> x.getString("character"))
                    .map(Color::ofCharacter).count() + 1));
        }

        if ((Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.ENDMAP)) || (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.WALL))) {

            playersSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else if (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.DOOR)) {

            playersSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┗");

        } else {

            playersSubString.append(" ");
        }

        return playersSubString.toString();
    }


    private static StringBuilder[] addPlayersBridges(StringBuilder[] squareLine,
            JsonObject jGameHandlerObject) {

        JsonArray jPlayersArray = jGameHandlerObject.getJsonArray("playerList");

        StringBuilder name;
        StringBuilder actions;
        StringBuilder deaths;

        //for each player
        for (int player = 0; player < jPlayersArray.size(); player++) {

            name = new StringBuilder();
            actions = new StringBuilder();
            deaths = new StringBuilder();

            if (jPlayersArray.getJsonObject(player).getBoolean("isActivePlayer")) {

                name.append("Turno - ");
            }

            name.append(Color.ansiColor(
                    Color.ofCharacter(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(jPlayersArray.getJsonObject(player).getString("character"))
                    .append("(" + jPlayersArray.getJsonObject(player).getString("playerId") + ")")
                    .append(addDamages(jPlayersArray.getJsonObject(player)));

            actions.append(Color.ansiColor(
                    Color.ofCharacter(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(" -> Azioni:")
                    .append(addActions(jPlayersArray.getJsonObject(player)));

            deaths.append(Color.ansiColor(
                    Color.ofCharacter(jPlayersArray.getJsonObject(player).getString("character"))
                            .toString()))
                    .append(" -> Plancia Morti: ")
                    .append(addDeaths(jPlayersArray.getJsonObject(player)));

            squareLine[3 * player].append(name.toString());
            squareLine[3 * player + 1].append(actions.toString());
            squareLine[3 * player + 2].append(deaths.toString());
        }

        return squareLine;
    }

    private static String addDamages(JsonObject jPlayerObject) {

        StringBuilder line = new StringBuilder();

        line.append(" -> Danni: ");

        jPlayerObject.getJsonObject("bridge").getJsonObject("damageBridge").getJsonArray("shots")
                .stream()
                .map(JsonValue::toString)
                .map(x -> x.substring(1, x.length() - 1))
                .map(Color::ansiColor)
                .forEach(x -> {

                    line.append(x);
                    line.append("◉");
                });

        return line.append(Color.ansiColor(
                Color.ofCharacter(jPlayerObject.getString("character"))
                        .toString())).append(addMarks(jPlayerObject
                .getJsonObject("bridge")
                .getJsonObject("damageBridge")
                .getJsonArray("marks"))).toString();
    }

    private static String addMarks(JsonArray jMarksArray) {

        StringBuilder line = new StringBuilder();

        line.append(" -> Marchi: ");

        jMarksArray.stream()
                .map(JsonValue::toString)
                .map(x -> x.substring(1, x.length() - 1))
                .map(Color::ansiColor)
                .forEach(x -> {

                    line.append(x);
                    line.append("◎");
                });

        return line.toString();
    }

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

    private static String addDeaths(JsonObject jPlayerObject) {

        StringBuilder line = new StringBuilder();

        jPlayerObject.getJsonObject("bridge")
                .getJsonArray("deathBridgeArray").stream()
                .map(JsonValue::asJsonObject).forEach(x -> {

            if (x.getBoolean("used")) {

                line.append("x");
            } else {

                line.append(x.getInt("value"));
            }

            line.append(" ");
        });

        return line.toString();
    }
}
