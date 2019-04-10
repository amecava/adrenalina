package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.players.Player;
import java.util.List;
import java.util.stream.Stream;

public class RoomDamage implements AtomicEffect {

    private int quantity;

    public RoomDamage(int quantity) {

        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> targetList) {

        Stream<Player> target;

        try {
            // Get the targets from rooms, cast them to Player.class and filter the source
            target = targetList.stream()
                    .map(x -> (Room) x)
                    .flatMap(x -> x.getPlayers().stream())
                    .filter(x -> x != source);

            // Execute the room damage atomic effect
            target.forEach(x -> x.setDamage(((Player) source).getPlayerColor(), this.quantity));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}