package it.polimi.ingsw.server.model.board;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
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
import it.polimi.ingsw.virtual.JsonUtility;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Board implements Serializable {

    /**
     * An integer that identifies one board from the other three possible boards.
     */
    private int boardId;

    /**
     * The list of all rooms of this board.
     */
    private List<Room> roomsList;

    /**
     * The deck of weapon cards.
     */
    private WeaponDeck weaponDeck;

    /**
     * The deck of Ammo Tiles.
     */
    private AmmoTilesDeck ammoTilesDeck;

    /**
     * The deck of Power Ups.
     */
    private PowerUpDeck powerUpDeck;

    /**
     * It builds the board using the BoardBuilder.
     *
     * @param builder The builder.
     */
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

    public AmmoTilesDeck getAmmoTilesDeck() {

        return this.ammoTilesDeck;
    }

    public PowerUpDeck getPowerUpDeck() {

        return this.powerUpDeck;
    }

    public PowerUpCard getPowerUp() {

        return this.powerUpDeck.getPowerUpCard();
    }

    /**
     * This method updates the "tools" list of every square. For example, if a player collects
     * something, at the end of his turn, it has to be replaced.
     */
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

    /**
     * Searches the spawn square in a said room (represented by the color sent as a parameter).
     *
     * @param color The color of the room in which you want to search the spawn square.
     * @return The spawn square of the room.
     */

    public Square findSpawn(Color color) throws SquareException {

        return this.roomsList.stream()
                .filter(x -> x.getColor().equals(color))
                .flatMap(x -> x.getSquaresList().stream())
                .filter(Square::isSpawn)
                .findFirst()
                .orElseThrow(() -> new SquareException("In questa stanza non c'è uno spawn."));
    }

    /**
     * Searches a square from a color and an id. If it doesn't exist, it throws an exception.
     *
     * @param name The color of the square.
     * @param id The id of the square.
     * @return The square with "name" color and "id" id.
     */
    public Square findSquare(String name, String id) throws SquareException, ColorException {

        try {

            Color color = Color.ofName(name);

            if (color == null) {

                throw new ColorException("Il colore selezionato non esiste.");
            }

            return this.roomsList.stream()
                    .filter(x -> x.getColor().equals(color))
                    .flatMap(y -> y.getSquaresList().stream())
                    .filter(z -> z.getSquareId() == Integer.valueOf(id))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);

        } catch (NoSuchElementException | NumberFormatException e) {

            throw new SquareException("Il quadrato che hai scelto non esiste.");
        }
    }

    /**
     * Searches a specific card to get information about it. A card can be in the WeaponDeck, in a
     * spawn square or in a player's hand.
     *
     * @param cardId the id of the Card.
     * @return The JsonObject of the card.
     */
    public JsonObject getInfoCard(String cardId) {

        if (this.weaponDeck.getDeck().stream()
                .anyMatch(x -> x.getId() == Integer.valueOf(cardId))) {

            return this.weaponDeck.getDeck().stream()
                    .filter(x -> x.getId() == Integer.valueOf(cardId))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new)
                    .toJsonObject();
        }

        if (this.roomsList.stream().flatMap(x -> x.getSquaresList().stream())
                .filter(Square::isSpawn)
                .flatMap(x -> x.getTools().stream())
                .map(y -> (WeaponCard) y)
                .anyMatch(z -> z.getId() == Integer.valueOf(cardId))) {

            return this.roomsList.stream().flatMap(x -> x.getSquaresList().stream())
                    .filter(Square::isSpawn)
                    .flatMap(x -> x.getTools().stream())
                    .map(y -> (WeaponCard) y)
                    .filter(z -> z.getId() == Integer.valueOf(cardId))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new)
                    .toJsonObject();
        }

        return this.roomsList.stream()
                .flatMap(x -> x.getSquaresList().stream())
                .flatMap(y -> y.getPlayers().stream())
                .flatMap(z -> z.getWeaponCardList().stream())
                .filter(t -> t.getId() == Integer.valueOf(cardId))
                .findAny()
                .orElseThrow(NoSuchElementException::new)
                .toJsonObject();
    }

    /**
     * Searches in the powerUpDeck if the power up named "powerUpName".
     *
     * @param powerUpName The name of the power up that needs to be found.
     * @return The JsonObject of the power up.
     */
    public JsonObject getInfoPowerUp(String powerUpName) {

        if (this.getPowerUpDeck().getDeck().stream().anyMatch(x -> JsonUtility
                .levenshteinDistance(powerUpName, x.getName()) <= 3)) {

            return this.getPowerUpDeck().getDeck().stream().filter(x -> JsonUtility
                    .levenshteinDistance(powerUpName, x.getName()) <= 3).findAny().get()
                    .toJsonObject();
        }

        return null;
    }

    /**
     * For each line of squares it builds a JsonArray that will be added to a JsonArray containing
     * every line of the board. Building it in this way makes it possible to scan it with a
     * JsonArray used to build the strings of the cli. This method creates a JsonObject containing
     * all the information needed in the View. The said JsonObject will add up to every other
     * JsonObject of every other (necessary) class and will be sent to the view when needed.
     *
     * @return The JsonObject of the board.
     */
    public JsonObject toJsonObject() {

        JsonArrayBuilder builder;
        List<JsonArray> board = new ArrayList<>();

        int i = 0;
        int j = 0;

        Square tmpSquare = this.roomsList.get(i).getSquare(0);
        Square firstOfTheRow;

        while (board.size() < 3) {

            firstOfTheRow = tmpSquare;

            builder = Json.createArrayBuilder();

            if (i == 2 && (this.boardId == 0 || this.boardId == 1)) {

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

            if (firstOfTheRow.getAdjacent(Direction.SOUTH) != null) {

                tmpSquare = firstOfTheRow.getAdjacent(Direction.SOUTH);

            } else {

                tmpSquare = firstOfTheRow.getAdjacent(Direction.EAST).getAdjacent(Direction.SOUTH);
            }
            i++;
        }

        builder = Json.createArrayBuilder();
        board.forEach(builder::add);

        return Json.createObjectBuilder()
                .add("boardId", this.boardId)
                .add("arrays", builder.build())
                .build();
    }

    public static class BoardBuilder {

        /**
         * An integer that identifies one board from the other three possible boards.
         */
        private int boardId;

        /**
         * The list of all rooms of this board.
         */
        private List<Room> roomsList = new ArrayList<>();

        /**
         * The deck of weapon cards.
         */
        private WeaponDeck weaponDeck;

        /**
         * The deck of Ammo Tiles.
         */
        private AmmoTilesDeck ammoTilesDeck;

        /**
         * The deck of Power Ups.
         */
        private PowerUpDeck powerUpDeck;


        /**
         * The key of the JsonArray containing all the rooms.
         */
        private static final String ROOMS = "rooms";

        /**
         * The key for the integer of the room id.
         */
        private static final String ROOM_ID = "roomId";

        /**
         * The key for the String of the color of the room.
         */
        private static final String ROOM_COLOR = "roomColor";

        /**
         * The key for the JsonArray of every square of a room.
         */
        private static final String SQUARES = "squares";

        /**
         * The key of the integer of the square id.
         */
        private static final String SQUARE_ID = "squareId";

        /**
         * The key of the boolean of the spawn property of a square.
         */
        private static final String SPAWN = "spawn";

        /**
         * The key of the JsonObject of the adjacent Square.
         */
        private static final String ADJACENT = "adjacent";

        /**
         * The key of the direction of a square.
         */
        private static final String DIRECTION = "direction";

        /**
         * The key of a connection.
         */
        private static final String CONNECTION = "connection";

        /**
         * The JsonArray of every board.
         */
        private static JsonArray object;

        /**
         * Statically opens the "Boards.json" resource file.
         */
        static {

            InputStream in = BoardBuilder.class.getClassLoader().getResourceAsStream("Board.json");

            object = Json.createReader(in).readArray();
        }

        /**
         * Initializes the various decks and creates the builder.
         */
        public BoardBuilder(EffectHandler effectHandler) {

            this.weaponDeck = new WeaponDeck.WeaponDeckBuilder(effectHandler).build();
            this.powerUpDeck = new PowerUpDeck.PowerUpDeckBuilder(effectHandler).build();
            this.ammoTilesDeck = new AmmoTilesDeck.AmmoTilesDeckBuilder(this.powerUpDeck).build();
        }

        /**
         * Reads from the Json file the information needed to build the board with id "boardId".
         *
         * @param boardID The id of the board that has to be built.
         */
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

        /**
         * Reading the information in the Json file creates the graph of squares with every
         * connection.
         *
         * @param jRoomsArray The JsonArray of every room of that board.
         */
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

        /**
         * Build the board "boardId".
         *
         * @param boardID The id of the board that has to be built.
         * @return The board.
         */
        public Board build(int boardID) {

            this.readFromJson(boardID);

            return new Board(this);
        }
    }

}
