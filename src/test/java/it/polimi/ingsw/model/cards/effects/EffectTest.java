package it.polimi.ingsw.model.cards.effects;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.model.cards.effects.atomic.MovePlayer;
import it.polimi.ingsw.model.cards.effects.atomic.PlayerDamage;
import it.polimi.ingsw.model.cards.effects.atomic.PlayerMark;
import it.polimi.ingsw.model.cards.effects.atomic.RoomDamage;
import it.polimi.ingsw.model.cards.effects.properties.Properties;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EffectTest {

    @Test
    void appendAtomicEffect() {

        Properties properties = new Properties.PropertiesBuilder().build();
        Effect tester = new Effect.EffectBuilder(
                1,
                1,
                "effect name",
                EffectType.PLAYER,
                properties,
                "description").build();

        AtomicEffect damage = new PlayerDamage(1);
        AtomicEffect mark = new PlayerMark(2);

        tester.appendAtomicEffect(damage);

        assertTrue(tester.getAtomicEffectList().contains(damage));

        tester.appendAtomicEffect(mark);

        assertTrue(tester.getAtomicEffectList().contains(damage));
        assertTrue(tester.getAtomicEffectList().contains(mark));

        try {
            tester.appendAtomicEffect(null);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    void execute1() {

        Properties properties = new Properties.PropertiesBuilder().build();
        Effect tester = new Effect.EffectBuilder(
                1,
                1,
                "effect name",
                EffectType.PLAYER,
                properties,
                "description").build();

        AtomicEffect damage = new PlayerDamage(1);
        AtomicEffect mark = new PlayerMark(2);

        tester.appendAtomicEffect(damage);
        tester.appendAtomicEffect(mark);

        Player player = new Player("player", Color.GRAY);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.YELLOW);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target1, target2)));
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertSame(
                target1.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target2.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target1.getBridge()
                        .getDamageBridge()
                        .getMarkers()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target2.getBridge()
                        .getDamageBridge()
                        .getMarkers()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        Room room = new Room(Color.RED);
        Square square = new Square(room, 1);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void execute2() {

        Properties properties = new Properties.PropertiesBuilder().build();
        Effect tester = new Effect.EffectBuilder(
                1,
                1,
                "effect name",
                EffectType.ROOM,
                properties,
                "description").build();

        AtomicEffect damage = new RoomDamage(1);
        tester.appendAtomicEffect(damage);

        Player player = new Player("player", Color.GRAY);

        Room room = new Room(Color.RED);
        Square square1 = new Square(room, 1);
        Square square2 = new Square(room, 2);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.VIOLET);

        room.addSquaresList(new ArrayList<>(Arrays.asList(square1, square2)));

        square1.addPlayer(target1);
        square2.addPlayer(target2);
        square2.addPlayer(player);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertSame(
                target1.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target2.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                player.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .size(),
                0
        );

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target1, target2)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square1, square2)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void execute3() {

        Properties properties = new Properties.PropertiesBuilder().build();
        Effect tester = new Effect.EffectBuilder(
                1,
                2,
                "effect name",
                EffectType.PLAYER,
                properties,
                "description").build();

        AtomicEffect move = new MovePlayer();
        AtomicEffect damage = new PlayerDamage(1);

        tester.appendAtomicEffect(move);
        tester.appendAtomicEffect(damage);

        Player player = new Player("player", Color.GRAY);
        Player target = new Player("target", Color.GREEN);

        Room room = new Room(Color.RED);
        Square square = new Square(room, 1);
        Square destination = new Square(room, 2);

        square.addPlayer(target);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(destination, target)));
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertTrue(destination.getPlayers().contains(target));
        assertEquals(target.getCurrentPosition(), destination);

        assertSame(
                target.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}