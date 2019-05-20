package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.players.Color;
import java.io.InputStream;
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
    private static StringBuilder[] squareLine = new StringBuilder[MAX_VERT_TILES];

    private static JsonArray jDrawSquares;

    static {

        InputStream in = ConsoleView.class.getClassLoader().getResourceAsStream("DrawBoards.json");

        jDrawSquares = Json.createReader(in).readArray();
    }

    public static List<StringBuilder[]> drawBoard(JsonObject jsonObject) {

        List<StringBuilder[]> rows = new ArrayList<>();

        JsonArray jsonArray = jsonObject.getJsonArray("arrays");
        JsonArray jDrawSquaresArray = jDrawSquares.getJsonArray(jsonObject.getInt("boardId"));

        //for each line of the map
        for (int row = 0; row < 3; row++) {

            squareLine = new StringBuilder[MAX_VERT_TILES];
            squareLine[0] = new StringBuilder();
            squareLine[1] = new StringBuilder();
            squareLine[2] = new StringBuilder();
            squareLine[3] = new StringBuilder();
            squareLine[4] = new StringBuilder();

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

                        squareLine[lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(playersSubString.toString());

                    } else if (lineInSquare == 3 && !jsonArray.getJsonArray(row)
                            .getJsonObject(squareInRow).containsKey("empty") && !jsonArray
                            .getJsonArray(row)
                            .getJsonObject(squareInRow).getBoolean("isSpawn")) {

                        squareLine[lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow).getString(lineInSquare))
                                .append(cubesSubString.toString());


                    } else {

                        squareLine[lineInSquare]
                                .append(jDrawSquaresArray.getJsonArray(row)
                                        .getJsonArray(squareInRow)
                                        .getString(lineInSquare));
                    }
                }

                cubesSubString = new StringBuilder();
                playersSubString = new StringBuilder();
            }

            rows.add(squareLine);
        }

        return rows;
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
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.ENDMAP))) {

            cubesSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else if (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.DOOR)){

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
                .equals(Connection.ENDMAP))) {

            playersSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┃");

        } else if (Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection")).equals(Connection.DOOR)){

            playersSubString
                    .append(Color.ansiColor(jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                            .getString("color")))
                    .append("┗");

        } else {

            playersSubString.append(" ");
        }

        return playersSubString.toString();
    }
}
