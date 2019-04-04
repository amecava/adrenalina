package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class WeaponDeck {

    List<Card> cards;

    public WeaponDeck() {
        cards= new ArrayList<>();
      /*  for (int i = 0; i <= 20; i++) {
            cards.add(new Card());
        }
      */
    }

    public Card  getcard (){
        return cards.remove(0);
    }
    public void pushCard(Card card ){
        cards.add(0, card);
    }
}


