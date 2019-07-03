package it.polimi.ingsw.server.model.decks;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.util.Arrays;
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
                Arrays.asList(Color.BLU, Color.BLU),
                tester.getReloadCost()
        );

        effect = tester.getMap().get(EffectType.PRIMARY);

        assertEquals(
                Arrays.asList(2),
                effect.getOptionalId()
        );

        assertNull(tester.getNotes());

        assertEquals(
                tester.getName(),
                "DISTRUTTORE"
        );

        assertEquals(tester.getOptionalList().get(0).getName(), "secondo aggancio");

        assertEquals(tester.getOptionalList().get(0).getDescription(), "Dai 1 marchio a un altro bersaglio che puoi vedere.");

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