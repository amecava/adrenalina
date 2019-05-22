package it.polimi.ingsw.server.model.board;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.board.rooms.Direction;
import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.decks.AmmoTilesDeck;
import it.polimi.ingsw.server.model.decks.PowerUpDeck;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.board.rooms.Room;
import it.polimi.ingsw.server.model.players.Player;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Board {

    private int boardId;

    private List<Room> roomsList;

    private WeaponDeck weaponDeck;
    private AmmoTilesDeck ammoTilesDeck;
    private PowerUpDeck powerUpDeck;

    private Board(BoardBuilder builder) {

        this.boardId = builder.boardId;

        this.roomsList = builder.roomsList;

        this.weaponDeck = builder.weaponDeck;
        this.ammoTilesDeck = builder.ammoTilesDeck;
        this.powerUpDeck = builder.powerUpDeck;
    }

    public List<Room> getRoomsList() {

        return this.roomsList;
    }


    public Room getRoom(int index) {

        return this.roomsList.get(index);
    }

    public WeaponDeck getWeaponDeck() {

        return this.weaponDeck;
    }

    public PowerUpDeck getPowerUpDeck() {

        return this.powerUpDeck;
    }

    public PowerUpCard getPowerUp() {

        return this.powerUpDeck.getPowerUpCard();
    }

    public void fillBoard() {

        this.roomsList.stream()
                .flatMap(x -> x.getSquaresList().stream())
                .forEach(x -> {
                    if (x.isSpawn()) {
                        while (x.getTools().size() < 3 && !this.weaponDeck.isEmpty()) {
                            x.addTool(this.weaponDeck.getCard());
                        }
                    } else if (x.getTools().isEmpty() && !this.ammoTilesDeck.isEmpty()) {

                        x.addTool(this.ammoTilesDeck.getTile());
                    }
                });
    }

    public JsonObject toJsonObject() {

        JsonArrayBuilder builder;
        List<JsonArray> board = new ArrayList<>();

        int i = 0;
        int j = 0;
        Square tmpSquare = this.roomsList.get(i).getSquare(0);

        while (board.size() < 3) {

            builder = Json.createArrayBuilder();

            if (i >= 1 && !tmpSquare.getAdjacent(Direction.NORTH)
                    .equals(this.roomsList.get(i - 1).getSquare(0))) {

                builder.add(Json.createObjectBuilder().add("empty", "").build());
                j++;
            }

            builder.add(tmpSquare.toJsonObject());

            j++;
            while (!tmpSquare.getConnection(Direction.EAST).equals(Connection.ENDMAP)) {


                tmpSquare = tmpSquare.getAdjacent(Direction.EAST);
                builder.add(tmpSquare.toJsonObject());
                j++;

                if (tmpSquare.getConnection(Direction.EAST).equals(Connection.ENDMAP)
                        && j < 4) {

                    builder.add(Json.createObjectBuilder().add("empty", "").build());
                }
            }

            board.add(builder.build());

            i++;

            //qui l'errore, la room 1 non è più sotto la 0, cazzo
            tmpSquare = this.roomsList.get(i).getSquare(0);
        }

        builder = Json.createArrayBuilder();
        board.forEach(builder::add);

        JsonArrayBuilder players = Json.createArrayBuilder();

        this.roomsList.stream()
                .flatMap(x -> x.getSquaresList().stream())
                .filter(y -> !y.getPlayers().isEmpty())
                .flatMap(z -> z.getPlayers().stream())
                .map(Player::toJsonObject)
                .forEach(players::add);

        return Json.createObjectBuilder()
                .add("boardId", this.boardId)
                .add("arrays", builder.build())
                .add("players", players.build())
                .build();
    }

    public static class BoardBuilder {

        private int boardId;

        private List<Room> roomsList = new ArrayList<>();

        private WeaponDeck weaponDeck;
        private PowerUpDeck powerUpDeck;
        private AmmoTilesDeck ammoTilesDeck;

        private static final String ROOMS = "rooms";
        private static final String ROOM_ID = "roomId";
        private static final String ROOM_COLOR = "roomColor";

        private static final String SQUARES = "squares";
        private static final String SQUARE_ID = "squareId";
        private static final String SPAWN = "spawn";
        private static final String ADJACENT = "adjacent";
        private static final String DIRECTION = "direction";
        private static final String CONNECTION = "connection";

        private static JsonArray object;

        static {

            InputStream in = BoardBuilder.class.getClassLoader().getResourceAsStream("Board.json");

            object = Json.createReader(in).readArray();
        }

        public BoardBuilder(EffectHandler effectHandler) {

            this.weaponDeck = new WeaponDeck.WeaponDeckBuilder(effectHandler).build();
            this.powerUpDeck = new PowerUpDeck.PowerUpDeckBuilder(effectHandler).build();
            this.ammoTilesDeck = new AmmoTilesDeck.AmmoTilesDeckBuilder(this.powerUpDeck).build();
        }

        private void readFromJson(int boardID) {

            this.boardId = boardID;

            JsonArray jRoomsArray = object.getJsonObject(boardID).getJsonArray(ROOMS);

            jRoomsArray.stream()
                    .map(JsonValue::asJsonObject)
                    .forEach(x -> {
                        this.roomsList.add(new Room(
                                Color.valueOf(x.getString(ROOM_COLOR))));

                        x.getJsonArray(SQUARES).stream()
                                .map(JsonValue::asJsonObject)
                                .forEach(y ->
                                        this.roomsList.get(x.getInt(ROOM_ID))
                                                .addSquare(new Square(y.getInt(SQUARE_ID),
                                                        y.getBoolean(SPAWN)))

                                );
                    });

            this.connectSquares(jRoomsArray);
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

        public Board build(int boardID) {

            this.readFromJson(boardID);

            return new Board(this);
        }
    }

}
