package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Direction;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.decks.AmmoTilesDeck;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.players.Player;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Board {

    private List<Room> roomsList;

    private WeaponDeck weaponDeck;
    private AmmoTilesDeck ammoTilesDeck;

    private Board(BoardBuilder builder, EffectHandler effectHandler) {

        this.roomsList = builder.roomsList;
        this.weaponDeck = new WeaponDeck.WeaponDeckBuilder(effectHandler).build();
    }

    public Room getRoom(int index) {

        return this.roomsList.get(index);
    }

    public void fill() {
        this.roomsList.stream().flatMap(x -> x.getSquaresList().stream()).forEach(y -> {
            if (y.isSpawn()){
                y.addTools(this.weaponDeck.getCardsForSpawnSquares());
            }

        });
    }

    public WeaponDeck getWeaponDeck() {

        return this.weaponDeck;
    }

    public static class BoardBuilder {

        private List<Room> roomsList = new ArrayList<>();

        private static final String ROOMS = "rooms";
        private static final String ROOM_ID = "roomId";
        private static final String ROOM_COLOR = "roomColor";

        private static final String SQUARES = "squares";
        private static final String SQUARE_ID = "squareId";
        private static final String SPAWN = "spawn";
        private static final String ADJACENT = "adjacent";
        private static final String DIRECTION = "direction";
        private static final String CONNECTION = "connection";

        public BoardBuilder(int boardID) {

            this.readFromJson(boardID);
        }

        private void readFromJson(int boardID) {

            try (JsonReader reader = Json.createReader(new FileReader("lib/board/Board.json"))) {

                JsonArray jBoardsArray = reader.readArray();
                JsonArray jRoomsArray = jBoardsArray.getJsonObject(boardID).getJsonArray(ROOMS);

                jRoomsArray.forEach(x -> {
                    this.roomsList.add(new Room(
                            Color.valueOf(x.asJsonObject().getString(ROOM_COLOR))));

                    x.asJsonObject().getJsonArray(SQUARES).forEach(y ->
                            this.roomsList.get(x.asJsonObject().getInt(ROOM_ID))
                                    .addSquare(new Square(y.asJsonObject().getInt(SQUARE_ID),
                                            y.asJsonObject().getBoolean(SPAWN)))

                    );
                });

                this.connectSquares(jRoomsArray);

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }

        private void connectSquares(JsonArray jRoomsArray) {

            int i = 0;
            int j;

            for (JsonValue room : jRoomsArray) {

                j = 0;

                for (JsonValue square : room.asJsonObject().getJsonArray(SQUARES)) {

                    for (JsonValue adjacent : square.asJsonObject().getJsonArray(ADJACENT)) {

                        if (this.roomsList.get(i).getSquare(j).setConnection(
                                Direction.valueOf(adjacent.asJsonObject().getString(DIRECTION)),
                                Connection
                                        .valueOf(adjacent.asJsonObject().getString(CONNECTION)))) {

                            this.roomsList.get(i).getSquare(j).setAdjacent(
                                    Direction.valueOf(adjacent.asJsonObject().getString(DIRECTION)),
                                    this.roomsList.get(adjacent.asJsonObject().getInt(ROOM_ID))
                                            .getSquare(adjacent.asJsonObject().getInt(SQUARE_ID))
                            );
                        }
                    }

                    j++;
                }

                i++;
            }
        }

        public Board build(EffectHandler effectHandler) {

            return new Board(this, effectHandler);
        }
    }
}
