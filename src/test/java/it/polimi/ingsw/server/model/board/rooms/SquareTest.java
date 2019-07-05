package it.polimi.ingsw.server.model.board.rooms;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.decks.AmmoTilesDeck;
import it.polimi.ingsw.server.model.decks.PowerUpDeck;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.EmptySquareException;
import org.junit.jupiter.api.Test;

/**
 * Tests the squares methods.
 */
class SquareTest {

    /**
     * Tests successes and failes of the collect action in non spawn squares.
     */
    @Test
    void collectAmmoTile() {

        AmmoTilesDeck ammoTilesDeck = new AmmoTilesDeck.AmmoTilesDeckBuilder(
                new PowerUpDeck.PowerUpDeckBuilder(new EffectHandler()).build()).build();

        Card ammoTile = ammoTilesDeck.getTile();

        Square tester = new Square(0, false);

        try {

            tester.collectAmmoTile();

            fail();

        } catch (EmptySquareException e) {

            assertTrue(true);
        }

        tester.addTool(ammoTile);

        try {

            assertEquals(tester.collectAmmoTile(), ammoTile);

        } catch (EmptySquareException e) {

            fail();
        }
    }

    /**
     * Tests successes and failes of the collect action in a spawn square.
     */
    @Test
    void collectWeaponCard() {

        WeaponDeck weaponDeck = new WeaponDeck.WeaponDeckBuilder(new EffectHandler()).build();

        Card weaponCard = weaponDeck.getCard();
        int cardId = ((WeaponCard) weaponCard).getId();

        Square tester = new Square(0, true);

        try {

            tester.collectWeaponCard(cardId);

            fail();

        } catch (EmptySquareException e) {

            assertTrue(true);
        }

        tester.addTool(weaponCard);

        try {

            assertEquals(tester.collectWeaponCard(cardId), weaponCard);

        } catch (EmptySquareException e) {

            fail();
        }
    }

    /**
     * Tests successes and failes of the collect action in a spawn square when the player already
     * has three weapons.
     */
    @Test
    void collectWeaponCard1() {

        WeaponDeck weaponDeck = new WeaponDeck.WeaponDeckBuilder(new EffectHandler()).build();

        WeaponCard get = weaponDeck.getCard();
        int cardId = get.getId();

        WeaponCard give = weaponDeck.getCard();

        Square tester = new Square(0, true);

        try {

            tester.collectWeaponCard(give, cardId);

            fail();

        } catch (EmptySquareException e) {

            assertTrue(true);
        }

        tester.addTool(get);

        try {

            assertEquals(tester.collectWeaponCard(give, cardId), get);

            assertEquals(tester.collectWeaponCard(give.getId()), give);

        } catch (EmptySquareException e) {

            fail();
        }
    }
}