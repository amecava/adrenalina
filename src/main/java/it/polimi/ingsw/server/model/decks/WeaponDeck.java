package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

public class WeaponDeck {

    private List<WeaponCard> deck;


    private WeaponDeck(WeaponDeckBuilder builder) {

        this.deck = builder.deck;
        Collections.shuffle(this.deck);
    }

    public WeaponCard getCard() {

        return deck.remove(0);
    }

    public WeaponCard getCard(int id) {

        return this.deck.stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElseThrow(NullPointerException::new);
    }

    public boolean isEmpty() {

        return this.deck.isEmpty();
    }

    public static class WeaponDeckBuilder {

        private List<WeaponCard> deck = new ArrayList<>();

        private EffectHandler effectHandler;
        private List<Effect> effectsList = new ArrayList<>();

        private static JsonArray effects;
        private static JsonArray weapons;

        static {

            InputStream in;

            in = WeaponDeckBuilder.class.getClassLoader().getResourceAsStream("Effects.json");

            effects = Json.createReader(in).readArray();

            in = WeaponDeckBuilder.class.getClassLoader().getResourceAsStream("WeaponCards.json");

            weapons = Json.createReader(in).readArray();
        }

        public WeaponDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public WeaponDeck build() {

            this.readEffectsFromJson();
            this.readCardsFromJson();

            return new WeaponDeck(this);

        }

        private void readEffectsFromJson() {

            effects.forEach(x -> this.effectsList
                    .add(new Effect.EffectBuilder(x.asJsonObject()).build())
            );
        }

        private void readCardsFromJson() {

            weapons.forEach(x -> this.deck
                    .add(new WeaponCard.WeaponCardBuilder(this.effectHandler)
                            .build(x.asJsonObject(), this.effectsList))
            );
        }


    }

}
