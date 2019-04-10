package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.List;
import java.util.stream.Stream;

public class PlayerMark implements AtomicEffect {


    private int quantity;

    public PlayerMark(int quantity) {

        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> targetList) {

        Stream<Player> target;

        try {
            // Cast the targets to Player.class
            target = targetList.stream()
                    .map(x -> (Player) x);

            // Execute the player mark atomic effect
            target.forEach(x -> x.setMark(((Player) source).getPlayerColor(), this.quantity));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}

