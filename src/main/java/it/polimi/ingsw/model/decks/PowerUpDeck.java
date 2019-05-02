package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class PowerUpDeck {

    List<PowerUpCard> deck;

    private PowerUpDeck(PowerUpDeckBuilder builder) {

        this.deck = builder.deck;
        // Collections.shuffle(this.deck);
    }

    //useful for tests
    public List<PowerUpCard> getDeck() {

        return this.deck;
    }

    public PowerUpCard getCard(){

        return this.deck.remove(0);
    }

    /* -------------------------- BUILDER -------------------------- */

    public static class PowerUpDeckBuilder {

        List<PowerUpCard> deck = new ArrayList<>();
        EffectHandler effectHandler;


        public PowerUpDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public PowerUpDeck build() {

            this.readFromJson();

            return new PowerUpDeck(this);

        }


        private void readFromJson() {

            try (JsonReader pReader = Json
                    .createReader(new FileReader("lib/cards/PowerUps.json"))) {

                JsonArray jEffectsArray = pReader.readArray();

                jEffectsArray.forEach(x ->

                        this.deck.add(new PowerUpCard.PowerUpCardBuilder(effectHandler)
                                .build(x.asJsonObject()))
                );

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }

    }

}
