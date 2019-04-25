package it.polimi.ingsw.model.cards.effects.atomic;


import it.polimi.ingsw.model.cards.effects.EffectTarget;
import it.polimi.ingsw.model.players.Player;

public class AtomicMark implements AtomicEffect {

    @Override
    public AtomicType getAtomicType() {

        return AtomicType.MARK;
    }

    @Override
    public void execute(Player source, EffectTarget target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.markPlayer(source.getColor()));
    }
}

