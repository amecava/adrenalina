package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

public class PowerUpDeck {

    private List<PowerUpCard> deck;

    private PowerUpDeck(PowerUpDeckBuilder builder) {

        this.deck = builder.deck;
        Collections.shuffle(this.deck);
    }

    public List<PowerUpCard> getDeck() {

        return this.deck;
    }

    public PowerUpCard getPowerUpCard() {

        return this.deck.remove(0);
    }

    public void addPowerUpCard(PowerUpCard powerUpCard) {

        this.deck.add(powerUpCard);
    }

    public static class PowerUpDeckBuilder {

        List<PowerUpCard> deck = new ArrayList<>();
        EffectHandler effectHandler;

        private static JsonArray object;

        static {

            InputStream in = PowerUpDeckBuilder.class.getClassLoader().getResourceAsStream("PowerUps.json");

            object = Json.createReader(in).readArray();
        }


        public PowerUpDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public PowerUpDeck build() {

            this.readFromJson();

            return new PowerUpDeck(this);

        }

        private void readFromJson() {

            object.forEach(x -> this.deck
                    .add(new PowerUpCard.PowerUpCardBuilder(effectHandler).build(x.asJsonObject()))
            );
        }

    }

}
