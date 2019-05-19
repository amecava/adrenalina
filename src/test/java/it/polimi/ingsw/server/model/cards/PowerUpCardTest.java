package it.polimi.ingsw.server.model.cards;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PowerUpCardTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void useCardMirino() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(target1);

        // Not the turn of the card owner
        try {
            tester.activateCard();
            fail();
        } catch (CardException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));
        effectHandler.setActivePlayer(source);

        // Use primary
        try {

            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            target1.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("GRANATA VENOM")).findAny().get());

            target1.getPowerUpsList().get(0).useCard(new EffectArgument());

            assertSame(target1.getShots().get(0), Color.GRAY);
            assertSame(target1.getShots().get(1), Color.GRAY);
            assertSame(target1.getShots().size(), 2);

            assertSame(target1.getMarks().get(0), Color.GRAY);
            assertSame(target1.getMarks().size(), 1);
            assertSame(source.getMarks().size(), 1);

            source.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("MIRINO")).findAny().get());
            source.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("RAGGIO CINETICO")).findAny().get());
            source.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("TELETRASPORTO")).findAny().get());

            source.getPowerUpsList().get(0).useCard(effectArgument, source.getAmmoCubesList().stream()
                    .filter(x -> (x.getColor().equals(Color.RED) && !x.isUsed())).findFirst()
                    .get());

            assertSame(3, target1.getShots().size());

            effectArgument = new EffectArgument(board.getRoom(1).getSquare(0), Arrays.asList(target1));

            source.getPowerUpsList().get(1).useCard(effectArgument);

            assertSame(board.getRoom(1).getSquare(0), target1.getCurrentPosition());
            assertSame(board.getRoom(0).getSquare(0), source.getCurrentPosition());


            effectArgument = new EffectArgument(board.getRoom(3).getSquare(1));

            source.getPowerUpsList().get(2).useCard(effectArgument);

            assertSame(board.getRoom(3).getSquare(1), source.getCurrentPosition());

        } catch (EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        } catch (CardException e) {

            e.printStackTrace();
            fail();
        }
    }

    @Test
    void useCard1() {
    }
}