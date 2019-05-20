package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.junit.jupiter.api.Test;

class ConsoleViewTest {


    @Test
    void showBoard() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();

        Player one = new Player("one", Color.GREEN);
        Player two = new Player("two", Color.GRAY);
        Player three = new Player("three", Color.YELLOW);
        Player four = new Player("four", Color.LIGHTBLUE);
        Player five = new Player("five", Color.VIOLET);

        one.movePlayer(board.getRoom(0).getSquare(0));
        two.movePlayer(board.getRoom(1).getSquare(0));
        three.movePlayer(board.getRoom(1).getSquare(2));
        four.movePlayer(board.getRoom(2).getSquare(0));
        five.movePlayer(board.getRoom(3).getSquare(1));

        StringBuilder cubesSubString = new StringBuilder();
        StringBuilder playersSubString = new StringBuilder();

        final int MAX_VERT_TILES = 5; //rows.

        StringBuilder squareLine[] = new StringBuilder[MAX_VERT_TILES];

        try (JsonReader reader = Json
                .createReader(new StringReader(board.toJsonObject().toString()))) {

            try (JsonReader fileReader = Json.createReader(new FileReader(
                    "/Users/federicocapaccio/Documents/GitHub/ing-sw-2019-Bertolini-Cavallo-Capaccio/src/main/resources/DrawBoards.json"))) {

                JsonArray jsonArray = reader.readObject().getJsonArray("arrays");

                JsonArray jDrawSquaresArray = fileReader.readArray();

                //for each line of the map
                for (int row = 0; row < 3; row++) {

                    squareLine[0] = new StringBuilder();
                    squareLine[1] = new StringBuilder();
                    squareLine[2] = new StringBuilder();
                    squareLine[3] = new StringBuilder();
                    squareLine[4] = new StringBuilder();

                    //for each square of that line
                    for (int squareInRow = 0; squareInRow < 4; squareInRow++) {

                        cubesSubString
                                .append(this.addTiles(new StringBuilder(), jsonArray, row,
                                        squareInRow));

                        playersSubString.append(this.addPlayers(new StringBuilder(), jsonArray, row,
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

                    /*
                    System.out.println(squareLine[0].toString());
                    System.out.println(squareLine[1].toString());
                    System.out.println(squareLine[2].toString());
                    System.out.println(squareLine[3].toString());
                    System.out.println(squareLine[4].toString());
                    */
                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
        }

    }

    private String fixLength(int length) {

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < 10 - length; i++) {

            line.append(" ");
        }

        return line.toString();
    }

    private String addTiles(StringBuilder cubesSubString, JsonArray jsonArray,
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

        cubesSubString.append(this.fixLength((int) jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow)
                .getJsonArray("tools")
                .getJsonObject(0)
                .getJsonArray("colors").stream()
                .map(x -> x.toString()).count() + 1));

        if ((Connection.valueOf(jsonArray.getJsonArray(row)
                .getJsonObject(squareInRow).getString("eastConnection"))
                .equals(Connection.ENDMAP))) {

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

    private String addPlayers(StringBuilder playersSubString, JsonArray jsonArray,
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

}