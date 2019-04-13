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

        // Launch exception if effect already used
        if (effect.isUsed()) {

            throw new EffectUsedException("Effect already used!");
        }

        // Launch exception if effect is not activated
        if (effect.getActivated() != null && !effect.getActivated()) {

            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        // Launch exception if wrong method call
        if (effect.getArgs() == 0 && (square != null || target != null)) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        // Launch exception if wrong method call
        if (effect.getArgs() == 1 && square != null) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        // Launch exception if wrong method call
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

        // Unload card if effect executed and no exceptions launched
        if (card.isLoaded()) {
            card.setLoaded(false);
        }

        // Activate all optional effects related to the executed effect
        effect.getOptionalID().forEach(x ->
                card.getOptional().forEach(y -> {
                    if (x == y.getId()) {
                        y.setActivated(true);
                    }
                })
        );
    }

    private void updateActiveInactive(Square square) {

        // Update active square
        if (square != null) {
            this.activeSquare = square;
        }

        // Move targets already in active list to inactive list and add new targets to active list
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

        // Return target list if present
        if (target != null) {
            this.target.addAll(target);

            // If use effect called with no arguments
        } else {

            // Create target list for same as father types of effect
            if (effect.getSameAsFather().stream().allMatch(Boolean::booleanValue)) {
                this.target.addAll(this.active);

                // Create target list for adjacent types of effect
            } else if (effect.isDifferentSquares()) {
                this.target.addAll(this.activeSquare.getAdjacent());

                // Create target list for same as player types of effect
            } else if (effect.getSameAsPlayer()) {

                // Create target list for move active player types of effect
                if (effect.getEffectType().equals(EffectType.PLAYER)) {
                    this.target.add(this.activePlayer);

                    // Create target list for move to active square types of effect
                } else if (effect.getEffectType().equals(EffectType.SQUARE)) {
                    this.target.add(0, this.activeSquare);
                }
            }
        }
    }

    private void executeNextIfPresent(Effect effect) {

        // Execute next effect if effect type is multi
        if (effect.getNext().getEffectType() == EffectType.MULTI) {

            // Square effect on same square of targets types of effects
            if (effect.getNext().getSameAsFather().stream().allMatch(Boolean::booleanValue)) {

                // Square effect on all squares of targets
                if (effect.getNext().getMaxTargets() == null) {
                    effect.getNext().execute(this.activePlayer,
                            this.target.stream().map(Target::getCurrentPosition)
                                    .collect(Collectors.toList()));

                    // Square effect on limited number of square of targets
                } else {
                    effect.getNext().execute(this.activePlayer,
                            this.target.subList(0, effect.getNext().getMaxTargets()));
                }

                // Move active player to target position types of effects
            } else if (effect.getNext().getSameAsPlayer()) {
                effect.getNext().execute(this.activePlayer, new ArrayList<>(Arrays.asList(
                        (this.target.get(effect.getMaxTargets() - 1)).getCurrentPosition(),
                        this.activePlayer)));

                // Square effect on targets original squares and targets even if you move it types of effects
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

        // Check max number of targets property (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.maxTargets(
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));

        // Check same as father property (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.sameAsFather(
                this.active,
                this.inactive,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));

        // Check target view property (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.targetView(
                this.activePlayer,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));

        // Check seen by active property
        this.propertiesAnalyzer.seenByActive(
                this.active,
                target);

        // Check distance properties (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.checkDistance(
                this.activeSquare,
                effect.getArgs() != 2 ? target : target.subList(1, target.size()));

        // Check cardinal properties
        this.propertiesAnalyzer.checkCardinal(
                this.activeSquare,
                target);

        // Check same as player property and update target list if needed
        this.target = this.propertiesAnalyzer.sameAsPlayer(
                effect.getArgs() == 2,
                this.activePlayer,
                this.activeSquare,
                target);

        // Recursively call check properties if move type of effect
        if (effect.getNext() != null && effect.getNext().getEffectType() != EffectType.MULTI) {

            return checkProperties(effect.getNext(), new ArrayList<>(Arrays.asList(target.get(0))));
        }

        return effect;
    }
}
