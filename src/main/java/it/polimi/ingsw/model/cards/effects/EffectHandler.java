package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.effects.EffectCallException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.effects.EffectNotActivatedException;
import it.polimi.ingsw.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.cards.effects.properties.PropertyChecker;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EffectHandler {

    private WeaponCard card;

    private Player activePlayer;
    private Square activeSquare;

    private List<Target> target = new ArrayList<>();

    private List<Player> active = new ArrayList<>();
    private List<Player> inactive = new ArrayList<>();

    private PropertyChecker propertyChecker = new PropertyChecker();

    public EffectHandler() {
    }

    public void setPlayerCard(Player activePlayer, WeaponCard card) throws CardNotLoadedException {

        if (!card.isLoaded()) {
            throw new CardNotLoadedException("Weapon not loaded!");
        }

        this.activePlayer = activePlayer;
        this.activeSquare = activePlayer.getCurrentPosition();

        this.card = card;

        this.target.clear();
        this.active.clear();
        this.inactive.clear();
    }

    public void useEffect(Effect effect, Square square, List<Target> target)
            throws EffectException, PropertiesException {

        if (effect.getArgs() == 0 && (square != null || target != null)) {
            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (effect.getArgs() == 1 && square != null) {
            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (effect.getArgs() == 2 && target != null) {
            target.add(0, square);
        }

        if (effect.isUsed()) {
            throw new EffectUsedException("Effect already used!");
        }

        if (!effect.getEffectProperties().getActivated()) {
            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        this.createTargetList(effect, target);

        this.checkProperties(effect, this.target).execute(this.activePlayer, this.target);

        this.executeNextIfEffectType(effect);

        this.updateCardVariables(effect);
        this.updateActiveInactive(square, this.target);
    }

    private void updateCardVariables(Effect effect) {

        effect.setUsed(true);

        if (this.card.isLoaded()) {
            this.card.unloadWeapon();
        }

        effect.getOptionalID().forEach(x ->
                this.card.getOptional().forEach(y -> {
                    if (x == y.getId()) {
                        y.getEffectProperties().setActivated(true);
                    }
                })
        );
    }

    private void updateActiveInactive(Square square, List<Target> target) {

        if (square != null) {
            this.activeSquare = square;
        }

        target.stream()
                .filter(x -> x != this.activePlayer)
                .forEach(x -> {
                    if (this.active.contains(x)) {
                        this.inactive.add(this.active.remove(this.active.indexOf(x)));
                    } else {
                        this.active.add((Player) x);
                    }
                });
    }

    private void createTargetList(Effect effect, List<Target> target) {

        this.target.clear();

        if (target != null) {
            this.target.addAll(target);
        } else if (effect.getEffectProperties().getSameAsFather().get(0)) {
            this.target.add(this.active.get(0));
        } else if (effect.getEffectProperties().isCardinal()) {
            this.target.addAll(this.activeSquare.getAdjacent());
        } else if (effect.getEffectProperties().getSameAsPlayer()) {
            if (effect.getEffectType() == EffectType.PLAYER) {
                this.target.add(this.activePlayer);
            } else if (effect.getEffectType() == EffectType.SQUARE) {
                this.target.add(0, this.activeSquare);
            }
        }
    }

    private void executeNextIfEffectType(Effect effect) {

        if (effect.getEffectType() == EffectType.COMBINED) {
            if (effect.getEffectProperties().getMaxTargets() == 0) {
                this.target.addAll(this.active.get(0).getOldPosition().getPlayers());

                effect.getNext().execute(this.activePlayer, this.target);
            } else if (effect.getEffectProperties().getMaxTargets() == 1) {
                effect.getNext().execute(
                        this.activePlayer,
                        this.target.stream()
                                .map(x -> ((Player) x).getCurrentPosition())
                                .collect(Collectors.toList()));
            } else if (effect.getEffectProperties().getMaxTargets() == 2) {
                effect.getNext().execute(
                        this.activePlayer,
                        this.target
                                .subList(0, effect.getNext().getEffectProperties().getMaxDist()));
            }
        } else if (effect.getEffectType() == EffectType.MOVETT) {
            effect.getNext().execute(
                    this.activePlayer,
                    new ArrayList<>(Arrays.asList(
                            ((Player) this.target
                                    .get(effect.getEffectProperties().getMaxTargets() - 1))
                                    .getCurrentPosition(),
                            this.activePlayer)));
        }
    }

    private Effect checkProperties(Effect effect, List<Target> target) throws PropertiesException {

        propertyChecker.setProperties(effect.getEffectProperties());

        /*
        propertyChecker.maxTargets(target); if args == 2 target.subList(1, size())
        propertyChecker.sameAsFather(this.active, this.inactive, target); if args == 2  target.subList(1, size())
        propertyChecker.targetView(this.activePlayer, target.getSquare()); if args == 2 target.subList(1, size())
        propertyChecker.seenByActive(this.active, target);

        propertyChecker.minDist(this.activeSquare, target.getSquare()); if args == 2  target.subList(1, size()).getSquare()
        propertyChecker.maxDist(this.activeSquare, target.getSquare()); if args == 2(target.get(0), target.subList(1, size()).getSquare())
        propertyChecker.cardinal(this.activeSquare, target.getSquare());
        propertyChecker.throughWalls(this.activeSquare, target.getSquare());
        propertyChecker.checkDifferentSquares(target)

        propertyChecker.sameAsPlayer(this.activePlayer, target);
                                                                if (effect.getEffectProperties().getSameAsPlayer()) {
                                                                    if args == 0 1
                                                                        if (effect.getEffectType() == EffectType.PLAYER)
                                                                            this.target.add(this.activePlayer);
                                                                        else if (effect.getEffectType() == EffectType.SQUARE)
                                                                            this.target.add(0, this.activeSquare);
                                                                    else if args == 2
                                                                        if target.getCurrentPosition != this.activePlayer.getCurrentPosition
                                                                            throw new exception
                                                                 else
                                                                    if activePlayer in target
                                                                        throw new exception

        if args == 2 ad != COMBI
            return checkProperties(effect.getSequence(), target.get(0))
        */

        return effect;
    }
}
