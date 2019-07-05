package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.players.Color;
import javax.json.JsonObject;

/**
 * Card interface with basic methods declaration for the "card" object.
 */
public interface Card {

    /**
     * Gets the name of the Card.
     *
     * @return The name of the Card (if possible).
     */
    String getName();

    /**
     * Gets the color of the Card.
     *
     * @return The color of the Card (if possible).
     */
    Color getColor();

    /**
     * Build the JsonObject of the Card.
     *
     * @return The JsonObject of the Card.
     */
    JsonObject toJsonObject();
}
