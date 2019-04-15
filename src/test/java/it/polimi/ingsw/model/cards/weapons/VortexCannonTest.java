package it.polimi.ingsw.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class VortexCannonTest {

    private EffectHandler effectHandler = new EffectHandler();
    private WeaponDeck weaponDeck = new WeaponDeck.WeaponDeckBuilder(this.effectHandler).build();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(0).build();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        AtomicTarget atomicTarget;

        board.getRoom(1).getSquare(1).addPlayer(source);
        board.getRoom(0).getSquare(2).addPlayer(target1);
        board.getRoom(3).getSquare(1).addPlayer(target2);

        WeaponCard tester = weaponDeck.getCard(7);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(1), Arrays.asList(target1));

        try {
            tester.useCard();

            tester.usePrimary(atomicTarget);
            fail();
        } catch (CardException | EffectException e){
            fail();
        } catch (PropertiesException e){
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(3).getSquare(1), Arrays.asList(target1));

        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e){
            fail();
        } catch (PropertiesException e){
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target2));

        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e){
            fail();
        } catch (PropertiesException e){
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        try {
            tester.usePrimary(atomicTarget);

            assertSame(target1.getBridge().getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getBridge().getShots().get(1).getColor(), Color.GRAY);
            assertEquals(target1.getCurrentPosition(), board.getRoom(0).getSquare(2));

        } catch (EffectException | PropertiesException e){
            fail();
        }
    }
}
