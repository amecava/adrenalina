package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class WeaponDeck {

    private List<WeaponCard> deck;


    private WeaponDeck(WeaponDeckBuilder builder) {

        this.deck = builder.deck;
        //Collections.shuffle(This.deck);
    }

    public WeaponCard getCard() throws IllegalActionException {
        if (deck.isEmpty())
            throw new IllegalActionException(" weapon deck is empty!!!");
        return deck.remove(0);
    }

    // useful for tests
    public WeaponCard getCard(int index) {

        return this.deck.get(index);
    }

    public List<Card> getCardsForSpawnSquares(){

        List<Card> threeCards = new ArrayList<>();

        for (int i = 0; i < 3; i++){

            try {
                threeCards.add(this.getCard());
            } catch (IllegalActionException e) {
                e.printStackTrace();
            }
        }
        return threeCards;
    }

    public static class WeaponDeckBuilder {

        private List<WeaponCard> deck = new ArrayList<>();

        private EffectHandler effectHandler;
        private List<Effect> effectsList = new ArrayList<>();

        public WeaponDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public WeaponDeck build() {

            this.readEffectsFromJson();
            this.readCardsFromJson();

            return new WeaponDeck(this);

        }

        private void readEffectsFromJson() {

            try (JsonReader eReader = Json.createReader(new FileReader("lib/cards/Effects.json"))) {

                JsonArray jEffectsArray = eReader.readObject().getJsonArray("effects");

                jEffectsArray.forEach(x ->
                        this.effectsList.add(new Effect.EffectBuilder(x.asJsonObject()).build())
                );

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }

        private void readCardsFromJson() {

            try (JsonReader cReader = Json.createReader(new FileReader("lib/cards/Cards.json"))) {

                JsonArray jCardsArray = cReader.readObject().getJsonArray("cards");

                jCardsArray.forEach(x ->
                        this.deck.add(new WeaponCard.WeaponCardBuilder(this.effectHandler)
                                .build(x.asJsonObject(), this.effectsList))
                );


            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }


    }

}
