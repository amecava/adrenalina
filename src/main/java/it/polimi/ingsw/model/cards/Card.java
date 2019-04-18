package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import java.util.List;

public interface Card {

    CardType getCardType();

    String getName();

    Color getColor();
}
