package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.exceptions.effects.EffectCallException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.effects.EffectNotActivatedException;
import it.polimi.ingsw.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.cards.effects.properties.PropertiesAnalyzer;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EffectHandler {

    private Player activePlayer;
    private Square activeSquare;

    private List<Target> target = new ArrayList<>();

    private List<Player> active = new ArrayList<>();
    private List<Player> inactive = new ArrayList<>();

    private PropertiesAnalyzer propertiesAnalyzer = new PropertiesAnalyzer();

    public EffectHandler() {
    }

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {

        this.target.clear();
        this.active.clear();
        this.inactive.clear();

        this.activePlayer = activePlayer;
        this.activeSquare = activeSquare.getCurrentPosition();
    }

    public void useEffect(Effect effect, Square square, List<Target> target)
            throws EffectException, PropertiesException {

        if (effect.isUsed()) {
            throw new EffectUsedException("Effect already used!");
        }

        if (!effect.getActivated()) {
            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        if (effect.getArgs() == 0 && (square != null || target != null)) {
            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (effect.getArgs() == 1 && square != null) {
            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (effect.getArgs() == 2 && target != null) {
            target.add(0, square);
        }

        this.createTargetList(effect, target);

        this.checkProperties(effect, this.target).execute(this.activePlayer, this.target);

        this.executeNextIfPresent(effect);
        this.updateActiveInactive(square);
    }

    public void updateCardUsageVariables(Effect effect, WeaponCard card) {

        effect.setUsed(true);

        if (card.isLoaded()) {
            card.setLoaded(false);
        }

        effect.getOptionalID().forEach(x ->
                card.getOptional().forEach(y -> {
                    if (x == y.getId()) {
                        y.setActivated(true);
                    }
                })
        );
    }

    private void updateActiveInactive(Square square) {

        if (square != null) {
            this.activeSquare = square;
        }

        this.target.stream()
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

        } else if (effect.getSameAsFather().stream().allMatch(Boolean::booleanValue)) {
            this.target.addAll(this.active);

        } else if (effect.isDifferentSquares()) {
            this.target.addAll(this.activeSquare.getAdjacent());

        } else if (effect.getSameAsPlayer()) {
            if (effect.getEffectType().equals(EffectType.PLAYER)) {
                this.target.add(this.activePlayer);

            } else if (effect.getEffectType().equals(EffectType.SQUARE)) {
                this.target.add(0, this.activeSquare);
            }
        }
    }

    private void executeNextIfPresent(Effect effect) {

        if (effect.getNext().getEffectType() == EffectType.COMBINED) {
            if (effect.getNext().getSameAsFather().stream().allMatch(Boolean::booleanValue)) {

                if (effect.getNext().getMaxTargets() == null) {

                    effect.getNext().execute(this.activePlayer,
                            this.target.stream().map(Target::getCurrentPosition)
                                    .collect(Collectors.toList()));
                } else {

                    effect.getNext().execute(this.activePlayer,
                            this.target.subList(0, effect.getNext().getMaxTargets()));
                }

            } else if (effect.getNext().getSameAsPlayer()) {
                effect.getNext().execute(this.activePlayer, new ArrayList<>(Arrays.asList(
                        (this.target.get(effect.getMaxTargets() - 1)).getCurrentPosition(),
                        this.activePlayer)));

            } else if (effect.getNext().isDifferentSquares()) {
                this.target.addAll(this.active.stream()
                        .flatMap(x -> x.getOldPosition().getPlayers().stream()).distinct()
                        .collect(Collectors.toList()));

                effect.getNext().execute(this.activePlayer,
                        this.target.stream().distinct().collect(Collectors.toList()));
            }
        }
    }

    private Effect checkProperties(Effect effect, List<Target> target) throws PropertiesException {

        this.propertiesAnalyzer.setEffect(effect);

        this.propertiesAnalyzer
                .maxTargets(effect.getArgs() != 2 ? target : target.subList(1, target.size()));
        this.propertiesAnalyzer.sameAsFather(this.active, this.inactive,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));
        this.propertiesAnalyzer.targetView(this.activePlayer,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));
        this.propertiesAnalyzer.seenByActive(this.active, target);

        this.propertiesAnalyzer.checkDistance(this.activeSquare,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));
        this.propertiesAnalyzer.checkCardinal(this.activeSquare, target);

        this.target = this.propertiesAnalyzer
                .sameAsPlayer(effect.getArgs() == 2, this.activePlayer, this.activeSquare, target);

        if (effect.getNext() != null && effect.getNext().getEffectType() != EffectType.COMBINED) {

            return checkProperties(effect.getNext(), new ArrayList<>(Arrays.asList(target.get(0))));
        }

        return effect;
    }
}
