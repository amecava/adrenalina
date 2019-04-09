package it.polimi.ingsw.model.board;


import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.exceptions.FileException;
import it.polimi.ingsw.model.exceptions.board.BoardFileException;
import java.io.FileReader;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonArray;
import javax.json.JsonValue;

public class BoardBuilder {

    private Board board = new Board();

    private static final String NORTH = "north";
    private static final String SOUTH = "south";
    private static final String EAST = "east";
    private static final String WEST = "west";

    private static final String ROOMS = "rooms";
    private static final String ROOM_COLOR = "roomColor";
    private static final String SQUARES = "squares";
    private static final String ROOM_INDEX = "roomIndex";
    private static final String ROOM_ID = "roomId";
    private static final String SQUARE_INDEX = "squareIndex";
    private static final String SQUARE_ID = "squareId";
    private static final String CONNECTION = "connection";


    public BoardBuilder() {
    }

    public Board buildBoard(int boardID) throws FileException {

        this.readFromJson(boardID);

        return this.board;
    }

    private void readFromJson(int boardID) throws FileException {

        try (JsonReader reader = Json.createReader(new FileReader("lib/board/Board.json"))) {

            JsonArray jBoardsArray = reader.readArray();
            JsonArray jRoomsArray = jBoardsArray.getJsonObject(boardID).getJsonArray(ROOMS);

            jRoomsArray.forEach(x -> {
                this.board.addRoom(new Room(
                        Color.valueOf(x.asJsonObject().getString(ROOM_COLOR))));

                x.asJsonObject().getJsonArray(SQUARES).forEach(y ->
                        this.board.addSquare(
                                x.asJsonObject().getInt(ROOM_ID),
                                new Square(
                                        this.board.getRoomsList()
                                                .get(x.asJsonObject().getInt(ROOM_ID)),
                                        y.asJsonObject().getInt(SQUARE_ID)))
                );
            });

            this.connectSquares(jRoomsArray);

        } catch (IOException e) {
            throw new BoardFileException("Board Json file error!");
        }
    }

    private void connectSquares(JsonArray jRoomsArray) {

        int i = 0;
        int j;

        for (JsonValue room : jRoomsArray) {

            j = 0;

            for (JsonValue square : room.asJsonObject().getJsonArray(SQUARES)) {

                if (this.board.getRoomsList(i).getSquaresList(j).setNorthConnection(
                        square.asJsonObject().getJsonObject(NORTH).getString(CONNECTION))
                        != Connection.ENDMAP) {
                    this.board.getRoomsList(i).getSquaresList(j)
                            .setNorth(this.board.getRoomsList().get(
                                    square.asJsonObject().getJsonObject(NORTH).getInt(ROOM_INDEX))
                                    .getSquaresList(square.asJsonObject()
                                            .getJsonObject(NORTH).getInt(SQUARE_INDEX)));
                }

                if (this.board.getRoomsList(i).getSquaresList(j).setSouthConnection(
                        square.asJsonObject().getJsonObject(SOUTH).getString(CONNECTION))
                        != Connection.ENDMAP) {
                    this.board.getRoomsList(i).getSquaresList(j)
                            .setSouth(this.board.getRoomsList(
                                    square.asJsonObject().getJsonObject(SOUTH).getInt(ROOM_INDEX))
                                    .getSquaresList(square.asJsonObject()
                                            .getJsonObject(SOUTH).getInt(SQUARE_INDEX)));
                }

                if (this.board.getRoomsList(i).getSquaresList(j).setEastConnection(
                        square.asJsonObject().getJsonObject(EAST).getString(CONNECTION))
                        != Connection.ENDMAP) {
                    this.board.getRoomsList(i).getSquaresList(j)
                            .setEast(this.board.getRoomsList(
                                    square.asJsonObject().getJsonObject(EAST).getInt(ROOM_INDEX))
                                    .getSquaresList(square.asJsonObject()
                                            .getJsonObject(EAST).getInt(SQUARE_INDEX)));
                }

                if (this.board.getRoomsList(i).getSquaresList(j).setWestConnection(
                        square.asJsonObject().getJsonObject(WEST).getString(CONNECTION))
                        != Connection.ENDMAP) {
                    this.board.getRoomsList(i).getSquaresList(j)
                            .setWest(this.board.getRoomsList(
                                    square.asJsonObject().getJsonObject(WEST).getInt(ROOM_INDEX))
                                    .getSquaresList(square.asJsonObject()
                                            .getJsonObject(WEST).getInt(SQUARE_INDEX)));
                }

                j++;

            }

            i++;
        }
    }

}
