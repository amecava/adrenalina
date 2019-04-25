package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.effects.EffectTarget;
import it.polimi.ingsw.model.players.Player;

public class AtomicMove implements AtomicEffect {

    @Override
    public AtomicType getAtomicType() {

        return AtomicType.MOVE;
    }

    @Override
    public void execute(Player source, EffectTarget target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.movePlayer(target.getDestination()));
    }
}
