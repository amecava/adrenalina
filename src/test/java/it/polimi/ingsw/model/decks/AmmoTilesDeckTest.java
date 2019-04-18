package it.polimi.ingsw.model.decks;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class AmmoTilesDeckTest {

    AmmoTilesDeck deck = new AmmoTilesDeck.AmmoTilesDeckBuilder().build();

    AmmoTile tileTester;

    @Test
    void buildDeck() {

        tileTester = deck.getTile(0);

        assertEquals(Arrays.asList(Color.YELLOW, Color.RED), tileTester.getAmmoCubesList());
        assertEquals(true, tileTester.hasPowerUpCard());


    }

    @Test
    void buildDeck2() {

        tileTester = deck.getTile(4);

        assertEquals(Arrays.asList(Color.YELLOW, Color.RED, Color.RED), tileTester.getAmmoCubesList());
        assertEquals(false, tileTester.hasPowerUpCard());

    }

    @Test
    void buildDeck3() {

        tileTester = deck.getTile(35);

        assertEquals(Arrays.asList(Color.BLUE, Color.RED), tileTester.getAmmoCubesList());
        assertEquals(true, tileTester.hasPowerUpCard());

    }

    @Test
    void buildDeck4() {

        tileTester = deck.getTile(27);

        assertEquals(Arrays.asList(Color.RED, Color.RED, Color.BLUE), tileTester.getAmmoCubesList());
        assertEquals(false, tileTester.hasPowerUpCard());

    }
}