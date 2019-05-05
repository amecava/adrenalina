package it.polimi.ingsw.model.decks;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicType;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class WeaponDeckTest {

    @Test
    void buildDeck() {

        WeaponDeck weaponDeck = new WeaponDeck.WeaponDeckBuilder(new EffectHandler()).build();

        WeaponCard tester;
        Effect effect;

        //LOCK RIFLE:

        tester = weaponDeck.getCard(1);

        assertEquals(1, tester.getId());
        assertEquals(
                Arrays.asList(Color.BLUE, Color.BLUE),
                tester.getReloadCost()
        );

        effect = tester.getMap().get(EffectType.PRIMARY);

        assertEquals(
                Arrays.asList(2),
                effect.getOptionalId()
        );

        assertEquals(
                Arrays.asList(AtomicType.DAMAGE, AtomicType.DAMAGE, AtomicType.MARK),
                effect.getAtomicEffectList().stream().map(AtomicEffect::getAtomicType)
                        .collect(Collectors.toList())
        );

        assertNull(effect.getActivated());

        effect = tester.getMap().get(EffectType.OPTIONAL_1);

        assertEquals(2, effect.getId());

        assertFalse(effect.getActivated());

        assertEquals(
                Arrays.asList(false),
                effect.getSameAsFather()
        );
    }
}