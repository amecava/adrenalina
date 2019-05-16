package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.players.Color;
import javax.json.Json;
import javax.json.JsonObject;

public interface Card {

    CardType getCardType();

    String getName();

    Color getColor();

    JsonObject toJsonObject();
}
