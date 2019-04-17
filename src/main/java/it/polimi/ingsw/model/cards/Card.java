package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import java.util.List;

public interface Card {

    String getName();

    Color getColor();

    List<Color> getAmmoCubesList();

    boolean isPowerUpCard();

}
