package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.model.cards.Card;
import java.util.List;

public class PowerUpDeck {

    List<Card> deck;

    private PowerUpDeck(PowerUpDeckBuilder builder) {

        this.deck = builder.deck;
    }


    public static class PowerUpDeckBuilder {

        List<Card> deck;


    }

}
