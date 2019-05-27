package it.polimi.ingsw.server.model.cards.effects.atomic;


import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;

public class AtomicMark implements AtomicEffect {

    @Override
    public void execute(Player source, EffectArgument target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.markPlayer(source.getColor()));
    }
}

