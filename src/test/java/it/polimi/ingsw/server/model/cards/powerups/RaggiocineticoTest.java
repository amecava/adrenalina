package it.polimi.ingsw.server.model.cards.powerups;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests "raggiocinetico" power up.
 */
class RaggiocineticoTest {

    private EffectHandler effectHandler = new EffectHandler();

    /**
     * Tests the general correctness of this power up implementation.
     */
    @Test
    void raggioCinetico() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.GIALLO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(1));
        target2.movePlayer(board.getRoom(0).getSquare(1));
        target3.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        // Not the turn of the card owner
        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        source.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("RAGGIOCINETICO")).findAny().get());

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(1));

        // Wrong method call
        try {

            source.getPowerUpsList().get(0).useCard(effectArgument, Color.ROSSO);
            fail();

        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        target1.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("RAGGIOCINETICO")).findAny().get());

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(1), Arrays.asList(source));

        // Not active
        try {

            target1.getPowerUpsList().get(0).useCard(effectArgument);
            fail();

        } catch (PropertiesException | EffectException e) {
            fail();
        } catch (CardException e) {
            assertTrue(true);
        }

        source.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("RAGGIOCINETICO")).findAny().get());

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(1), Arrays.asList(source));

        // Same as player
        try {

            source.getPowerUpsList().get(0).useCard(effectArgument);
            fail();

        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }
    }
}
