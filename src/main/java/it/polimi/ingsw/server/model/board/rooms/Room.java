package it.polimi.ingsw.server.model.board.rooms;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class Room implements Target, Serializable {

    /**
     * The color of the room.
     */
    private Color color;

    /**
     * The ordered list of every square of the room.
     */
    private List<Square> squaresList = new ArrayList<>();

    public Room(Color color) {

        this.color = color;
    }

    /**
     * Since a room can be a target of a weapon, this class implements this method to differentiate the target types.
     * @return The enum that defines the target type "room".
     */
    @Override
    public TargetType getTargetType() {

        return TargetType.ROOM;
    }

    /**
     * An unsupported operation that occurs because of the the target interface the is implemented.
     * @return An Exception is always thrown.
     */
    @Override
    public Square getCurrentPosition() {

        throw new UnsupportedOperationException();
    }

    /**
     * Searches in every square of the squaresList the players in those squares.
     * @return
     */
    @Override
    public List<Player> getPlayers() {

        return this.squaresList.stream()
                .flatMap(x -> x.getPlayers().stream())
                .collect(Collectors.toList());
    }

    public Color getColor() {

        return this.color;
    }

    public void setColor(Color color) {

        this.color = color;
    }

    public List<Square> getSquaresList() {

        return this.squaresList;
    }

    public Square getSquare(int index) {

        return this.squaresList.get(index);
    }

    public void addSquare(Square square) {

        this.squaresList.add(square);
        square.setRoom(this);
    }

    /**
     * It builds a JsonArray of every square and adds it to a JsonObect. It starts from the
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be sent to the
     * view when needed.
     * @return The JsonObject of the class.
     */
    public JsonObject toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.squaresList.stream()
                .map(Square::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("color", this.color.toString())
                .add("squares", builder.build())
                .build();
    }
}
