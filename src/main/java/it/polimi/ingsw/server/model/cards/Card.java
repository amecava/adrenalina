package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.players.Color;

public interface Card {

    CardType getCardType();

    String getName();

    Color getColor();
}
