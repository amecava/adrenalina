package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MovePlayerTest {

    @Test
    void execute() {

        AtomicEffect tester = new MovePlayer();

        EffectHandler effectHandler = new EffectHandler();
        Board board = new Board.BoardBuilder(effectHandler).build(0);

        Player player = new Player("player", Color.GRAY, effectHandler);
        Player target = new Player("target", Color.GREEN, effectHandler);

        target.movePlayer(board.getRoom(0).getSquare(0));

        assertEquals(target.getCurrentPosition(), board.getRoom(0).getSquare(0));
        assertTrue(board.getRoom(0).getSquare(0).getPlayers().contains(target));

        try {
            tester.execute(player,
                    new AtomicTarget(board.getRoom(3).getSquare(0), Arrays.asList(target)));

            assertTrue(board.getRoom(3).getSquare(0).getPlayers().contains(target));
            assertEquals(target.getCurrentPosition(), board.getRoom(3).getSquare(0));
            assertEquals(target.getOldPosition(), board.getRoom(0).getSquare(0));

        } catch (EffectTypeException e) {
            fail();
        }

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(target)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new AtomicTarget(board.getRoom(3).getSquare(0),
                    Arrays.asList(board.getRoom(2).getSquare(0))));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }
    }
}