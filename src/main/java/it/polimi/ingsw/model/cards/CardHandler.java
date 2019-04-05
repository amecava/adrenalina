package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.properties.EffectType;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.effects.EffectInputException;
import it.polimi.ingsw.model.exceptions.effects.EffectNotActivatedException;
import it.polimi.ingsw.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.cards.effects.properties.PropertyChecker;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardHandler {

    private WeaponCard card;

    private Player activePlayer;
    private Square activeSquare;

    private List<Target> target = new ArrayList<>();

    private List<Player> active = new ArrayList<>();
    private List<Player> inactive = new ArrayList<>();

    private PropertyChecker propertyChecker = new PropertyChecker();

    public CardHandler() {
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.activeSquare = activePlayer.getCurrentPosition();
    }

    public void setCard(WeaponCard card) throws CardException {
        if (!card.isLoaded())
            throw new CardNotLoadedException("Weapon not loaded!");

        this.card = card;
    }

    public void useEffect(Effect effect) throws EffectException {
        if (effect.getInputQuantity() != 0) {
            throw new EffectInputException("Wrong method call, no arguments needed!");
        }

        if (effect.isUsed()) {
            throw new EffectUsedException("Effect already used!");
        }

        if (!effect.isActivated()) {
            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        if (effect.getEffectProperties().getSameAsFather()) {
            if (effect.getEffectProperties().getEffectType() == EffectType.PLAYER) {
                this.target.add(this.active.get(0));
            } else if (effect.getEffectProperties().getEffectType() == EffectType.SQUARE) {
                this.target.add(this.activeSquare);
            }
        } // else if (effect.getEffectProperties().getCardinal())
        //this.target.add(the four directions);

        // Catch ClassCastException
        effect.execute(this.activePlayer, target);

        this.updateOptionals(effect);
        this.updateActiveInactive();
    }

    public void useEffect(Effect effect, List<Target> target) throws EffectException, PropertiesException {
        if (effect.getInputQuantity() != 1) {
            throw new EffectInputException("Wrong method call, one argument needed!");
        }

        if (effect.isUsed()) {
            throw new EffectUsedException("Effect already used!");
        }

        if (!effect.isActivated()) {
            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        if (effect.getEffectProperties().getEffectType() == EffectType.MOVE) {
            if (effect.getEffectProperties().getSameAsFather()) {
                target.add(0, this.activeSquare);
            } else {
                target.add(this.activePlayer);
            }
        }

        this.checkProperties(effect, target);

        effect.execute(this.activePlayer, target);

        this.updateOptionals(effect);
        this.updateActiveInactive();
    }

    public void useEffect(Effect effect, Square square, List<Target> target) throws EffectException, PropertiesException {
        if (effect.getInputQuantity() != 3) {
            throw new EffectInputException("Wrong method call, two arguments needed!");
        }

        if (effect.isUsed()) {
            throw new EffectUsedException("Effect already used!");
        }

        if (!effect.isActivated()) {
            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        this.checkProperties(effect, target);
        this.updateActiveInactive();

        this.checkProperties(effect.getSequence(), new ArrayList<>(Arrays.asList(square)));

        effect.getSequence().execute(this.activePlayer, target);
    }

    private void updateOptionals(Effect effect) {
        effect.getOptionalID().stream()
                .forEach(x -> {
                    this.card.getOptional().stream()
                            .forEach(y -> {
                                if (x == y.getID()) {
                                    y.setActivated(true);
                                }
                            });
                });
    }

    private void updateActiveInactive() {
        this.target.stream()
                .forEach(x -> {
                    if (this.active.contains(x)) {
                        this.inactive.add(this.active.remove(this.active.indexOf(x)));
                    } else {
                        this.active.add((Player) x);
                    }
                });
    }

    private void checkProperties(Effect effect, List<Target> target) throws PropertiesException {
        propertyChecker.setProperties(effect.getEffectProperties());

        /*
        propertyChecker.maxTargets(target);
        propertyChecker.sameAsFather(this.active, this.inactive, target);
        propertyChecker.sameAsPlayer(this.activePlayer, this.activeSquare, target);
        propertyChecker.targetView(this.activePlayer, target);
        propertyChecker.seenByActive(this.active, target);

        propertyChecker.minDist(this.activeSquare, target);
        propertyChecker.maxDist(this.activeSquare, target);
        propertyChecker.cardinal(this.activeSquare, target);
        propertyChecker.throughWalls(this.activeSquare, target);
        */
    }
}
