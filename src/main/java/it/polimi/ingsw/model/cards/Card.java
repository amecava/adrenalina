package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ammo.Color;

public interface Card {

    CardType getCardType();

    String getName();

    Color getColor();
}
