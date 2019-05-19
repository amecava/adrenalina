package it.polimi.ingsw.view.console;

import static org.junit.jupiter.api.Assertions.*;


import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.junit.jupiter.api.Test;

class ConsoleViewTest {

    @Test
    void showBoard() {

        Player fede = new Player("fede", Color.GREEN);
        Player ame = new Player("ame", Color.GRAY);
        Player jacop = new Player("jacop", Color.LIGHTBLUE);

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();

        fede.movePlayer(board.getRoom(0).getSquare(0));
        ame.movePlayer(board.getRoom(2).getSquare(1));
        jacop.movePlayer(board.getRoom(1).getSquare(1));

        String value = board.toJsonObject().toString();

        String subString = "";

        final int MAX_VERT_TILES = 5; //rows.

        String squareLine[] = new String[MAX_VERT_TILES];

        try (JsonReader reader = Json.createReader(new StringReader(value))) {

            try (JsonReader fileReader = Json
                    .createReader(new FileReader("lib/board/DrawBoards.json"))) {

                JsonArray jsonArray = reader.readObject().getJsonArray("arrays");
                JsonArray jSquaresArray = fileReader.readArray();

                //for each line of the map
                for (int row = 0; row < 3; row++) {

                    squareLine[0] = "";
                    squareLine[1] = "";
                    squareLine[2] = "";
                    squareLine[3] = "";
                    squareLine[4] = "";

                    //for each square of that line
                    for (int squareInRow = 0; squareInRow < 4; squareInRow++) {

                        if (!jsonArray.getJsonArray(row).getJsonObject(squareInRow)
                                .containsKey("empty") && jsonArray.getJsonArray(row)
                                .getJsonObject(squareInRow)
                                .containsKey("isSpawn") && !jsonArray.getJsonArray(row)
                                .getJsonObject(squareInRow)
                                .getBoolean("isSpawn")) {

                            for (String color : jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow)
                                    .getJsonObject("tile")
                                    .getJsonArray("colors").stream()
                                    .map(x -> x.toString().substring(1, x.toString().length() - 1))
                                    .map(Color::ansiColor)
                                    .collect(Collectors.toList())) {

                                subString = subString + color + "◆";
                            }

                            if (jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow)
                                    .getJsonObject("tile")
                                    .getJsonArray("colors").size() == 2) {

                                subString = subString + "      ";

                            } else {

                                subString = subString + "    ";
                            }

                            if (!jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow)
                                    .getBoolean("isSpawn") && (jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow).getString("east").equals("ENDMAP")
                                    || jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow).getString("east").equals("DOOR"))) {

                                subString = subString + Color.ansiColor(jsonArray.getJsonArray(row)
                                        .getJsonObject(squareInRow).getString("color")) + "┃";

                            } else {

                                subString = subString + " ";
                            }

                        }

                        //for each string of that square
                        for (int lineInSquare = 0; lineInSquare < MAX_VERT_TILES; lineInSquare++) {

                            if (lineInSquare == 3 && !jsonArray.getJsonArray(row)
                                    .getJsonObject(squareInRow).containsKey("empty") && !jsonArray
                                    .getJsonArray(row)
                                    .getJsonObject(squareInRow).getBoolean("isSpawn")) {

                                squareLine[lineInSquare] = squareLine[lineInSquare] +
                                        jSquaresArray.getJsonArray(row).getJsonArray(squareInRow)
                                                .getString(lineInSquare) + subString;
                            }
                            squareLine[lineInSquare] = squareLine[lineInSquare] +
                                    jSquaresArray.getJsonArray(row).getJsonArray(squareInRow)
                                            .getString(lineInSquare);
                        }

                        subString = "";

                    }

                    System.out.println(squareLine[0]);
                    System.out.println(squareLine[1]);
                    System.out.println(squareLine[2]);
                    System.out.println(squareLine[3]);
                    System.out.println(squareLine[4]);

                }

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }
    }
}