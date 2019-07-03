package it.polimi.ingsw.server.model.cards.effects.atomic;

import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;

public class AtomicDamage implements AtomicEffect, Serializable {

    /**
     * Applies one damage to the target in the target parameter by the player "source".
     *
     * @param source The source of the damage.
     * @param target The target(s) that will be damaged.
     */
    @Override
    public void execute(Player source, EffectArgument target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.damagePlayer(source.getColor(), target.isWeaponCard()));
    }
}
