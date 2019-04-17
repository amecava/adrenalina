package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
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

    private AtomicTarget target;

    private List<Player> active = new ArrayList<>();
    private List<Player> inactive = new ArrayList<>();

    private PropertiesAnalyzer propertiesAnalyzer = new PropertiesAnalyzer();

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {

        this.active.clear();
        this.inactive.clear();

        this.activePlayer = activePlayer;
        this.activeSquare = activePlayer.getCurrentPosition();
    }

    public void useEffect(Effect effect, AtomicTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if atomic target is null
        if (target == null) {

            throw new NullPointerException();
        }

        // Launch exception if effect already used
        if (effect.isUsed()) {

            throw new EffectUsedException("Effect already used!");
        }

        // Launch exception if effect is not activated
        if (effect.getActivated() != null && !effect.getActivated()) {

            throw new EffectNotActivatedException("You can't use this effect right now!");
        }

        // Launch exception if wrong method call
        if (effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        // Create properties related target list
        this.target = this.createPropertiesRelatedTarget(effect, target);

        // Check properties and execute effect
        this.checkProperties(effect, this.target).execute(this.activePlayer, this.target);

        // Update class variables after effect execution
        this.updateActiveInactiveVariables(effect);

        // Execute sequence types of effects
        if (effect.getNext() != null && !effect.getNext().getTargetType().equals(TargetType.MOVE)
                && !effect.getNext().getTargetType().equals(TargetType.RECOIL)) {

            this.target = this.createTargetForNoArgumentsEffects(effect.getNext(), target);
            effect.getNext().execute(this.activePlayer, this.target);
        }
    }

    public void updateCardUsageVariables(Effect effect, WeaponCard card) {

        effect.setUsed(true);

        // Unload card if effect executed and no exceptions launched
        if (card.isLoaded()) {
            card.setLoaded(false);
        }

        // Activate all optional effects related to the executed effect
        effect.getOptionalId().forEach(x ->
                card.getOptionalList().forEach(y -> {
                    if (x == y.getId()) {
                        y.setActivated(true);
                    }
                })
        );
    }

    private void updateActiveInactiveVariables(Effect effect) {

        // Update active square
        if (this.target.getDestination() != null) {

            this.activeSquare = this.target.getDestination();
        }

        if (effect.isSeenByActive()) {

            this.inactive.addAll(this.active);
            this.active.clear();
        }

        // Move targets already in active list to inactive list and add new targets to active list
        this.target.getTargetList().stream()
                .filter(x -> x != this.activePlayer)
                .forEach(x -> {
                    if (this.active.contains(x)) {
                        this.inactive.add(this.active.remove(this.active.indexOf(x)));
                    } else {
                        try {
                            this.active.add((Player) x);
                        } catch (ClassCastException e) {
                            //
                        }
                    }
                });
    }

    private AtomicTarget createPropertiesRelatedTarget(Effect effect, AtomicTarget target) {

        // Create target if no user interaction needed
        if (target.getArgs() == 0) {

            return this.createTargetForNoArgumentsEffects(effect, target);
        }

        if (target.getArgs() == 1 && effect.isSameAsPlayer()) {

            // Move active player types of effects
            if (effect.getTargetType().equals(TargetType.PLAYER)
                    && target.getDestination() != null) {

                target.appendTarget(this.activePlayer);

                // Move targets to active square / Apply effect on active square
            } else if (effect.getTargetType().equals(TargetType.SQUARE) || !target.getTargetList()
                    .isEmpty()) {

                target.setDestination(this.activeSquare);
            }
        }

        return target;
    }

    private AtomicTarget createTargetForNoArgumentsEffects(Effect effect, AtomicTarget target) {

        // Create target list for adjacent types of effect
        if (effect.getMinDist() != null && effect.getMinDist() == 1 &&
                effect.getMaxDist() != null && effect.getMaxDist() == 1) {

            this.activeSquare.getAdjacent().forEach(target::appendTarget);

            // Create target list for square effect on old target position types of effects
        } else if (effect.isDifferentSquares()) {

            // Collect all players in the inactive old positions
            this.inactive.stream().flatMap(x -> x.getOldPosition().getPlayers().stream())
                    .forEach(target::appendTarget);

            // Remove duplicates from target list
            target.setTargetList(
                    target.getTargetList().stream().distinct().collect(Collectors.toList()));

            // Create target list for same as player type of effects
        } else if (effect.isSameAsPlayer()) {

            // Move to target position types of effects
            if (effect.getTargetType().equals(TargetType.PLAYER)) {

                target.setDestination(target.getTargetList().stream().filter(x ->
                        !x.getCurrentPosition().getAdjacent().contains(this.activeSquare))
                        .findFirst().orElse(target.getTargetList().get(0)).getCurrentPosition());

                target.setTargetList(Arrays.asList(this.activePlayer));

                // Apply on active square types of effects
            } else if (effect.getTargetType().equals(TargetType.SQUARE)) {

                target.appendTarget(this.activeSquare);
            }

            // Create target list for same as father types of effect
        } else if (effect.getSameAsFather().stream().allMatch(Boolean::booleanValue)) {

            if (effect.getTargetType().equals(TargetType.PLAYER)) {

                this.active.forEach(target::appendTarget);

                // Square effect on all current squares of targets
            } else if (effect.getTargetType().equals(TargetType.SQUARE)
                    && effect.getMaxTargets() == null) {

                target.setTargetList(target.getTargetList().stream().map(Target::getCurrentPosition)
                        .collect(Collectors.toList()));

                // Square effect on limited number of square of targets
            } else if (effect.getTargetType().equals(TargetType.SQUARE)
                    && effect.getMaxTargets() != null) {

                target.setTargetList(target.getTargetList().subList(0, effect.getMaxTargets()));
            }
        }

        return target;
    }

    private Effect checkProperties(Effect effect, AtomicTarget target) throws PropertiesException {

        this.propertiesAnalyzer.setEffect(effect);

        // Check max number of targets property
        this.propertiesAnalyzer.maxTargets(target.getTargetList());

        // Check same as father property (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.sameAsFather(this.active, this.inactive, target.getTargetList());

        // Check target view property (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.targetView(this.activePlayer, target.getTargetList());

        // Check seen by active property
        this.propertiesAnalyzer.seenByActive(this.active, target.getTargetList());

        // Check distance properties (to sublist of target list if destination square appended)
        this.propertiesAnalyzer.checkDistance(target.getDestination() == null
                ? this.activeSquare : target.getDestination(), target.getTargetList());

        // Check cardinal properties
        this.propertiesAnalyzer.checkCardinal(target.getDestination() == null
                ? this.activeSquare : target.getDestination(), target.getTargetList());

        // Check same as player property and update target list if needed
        this.propertiesAnalyzer.sameAsPlayer(this.activePlayer, target.getTargetList());

        // Recursively call check properties for move effect types
        if (effect.getNext() != null && effect.getNext().getTargetType().equals(TargetType.MOVE)) {

            // Destination to target list to check distance and cardinal properties from active square
            return checkProperties(effect.getNext(), new AtomicTarget(Arrays.asList(target.getDestination())));
        }

        // Recursively call check properties for recoil effect types
        if (effect.getNext() != null && effect.getNext().getTargetType().equals(TargetType.RECOIL)) {

            // Remove destination to check distance and cardinal properties from active square
            return checkProperties(effect.getNext(), new AtomicTarget(target.getTargetList()));
        }

        return effect;
    }
}
