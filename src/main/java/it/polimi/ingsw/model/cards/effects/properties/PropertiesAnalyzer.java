package it.polimi.ingsw.model.cards.effects.properties;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.TargetType;
import it.polimi.ingsw.model.exceptions.properties.CardinalException;
import it.polimi.ingsw.model.exceptions.properties.DuplicateException;
import it.polimi.ingsw.model.exceptions.properties.MaxTargetsException;
import it.polimi.ingsw.model.exceptions.properties.SameAsFatherException;
import it.polimi.ingsw.model.exceptions.properties.SameAsPlayerException;
import it.polimi.ingsw.model.exceptions.properties.SquareDistanceException;
import it.polimi.ingsw.model.exceptions.properties.TargetViewException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PropertiesAnalyzer {

    private Effect effect;

    private ViewInspector viewInspector = new ViewInspector();

    public void setEffect(Effect effect) {

        this.effect = effect;
    }

    public void maxTargets(List<Target> target) throws DuplicateException, MaxTargetsException {

        // Launch exception if duplicates found
        if (target.size() != target.stream().distinct().collect(Collectors.toList()).size()) {

            throw new DuplicateException("Too many targets in the target list!");
        }

        // Launch exception if max targets property is violated
        if (this.effect.getMaxTargets() != null &&
                target.size() > this.effect.getMaxTargets()) {

            throw new MaxTargetsException("Too many targets in the target list!");
        }
    }

    public void sameAsFather(List<Target> active, List<Target> inactive,
            List<Target> target) throws SameAsFatherException {

        // If the same as father flag is present
        if (this.effect.getSameAsFather() != null) {

            // Create new List to avoid ConcurrentModificationException
            List<Target> notFound = new ArrayList<>(target);

            try {
                this.effect.getSameAsFather().forEach(x -> {

                    // Check if all targets eaten
                    if (notFound.isEmpty()) {

                        throw new NullPointerException();
                    }

                    // Remove target if found
                    notFound.remove(new ArrayList<>(notFound).stream()
                            .filter(y -> (x && active.contains(y)) || (!x && !active.contains(y)
                                    && !inactive.contains(y)))
                            .findFirst()
                            .orElseThrow(IllegalArgumentException::new));

                });
            } catch (NullPointerException e) {
                //
            } catch (IllegalArgumentException e) {

                throw new SameAsFatherException("Same as father violated!");
            }
        }
    }

    public void targetView(Player activePlayer, List<Target> targetList)
            throws TargetViewException {

        // If the target view flag is present
        if (this.effect.getTargetView() != null) {

            if (!this.effect.getTargetType().equals(TargetType.ROOM)) {

                for (Target target : targetList) {

                    // Launch exception if target view flag violated
                    if ((this.viewInspector
                            .targetView(activePlayer.getCurrentPosition(),
                                    target.getCurrentPosition())
                            && !this.effect.getTargetView()) || (!this.viewInspector
                            .targetView(activePlayer.getCurrentPosition(),
                                    target.getCurrentPosition())
                            && this.effect.getTargetView())) {

                        throw new TargetViewException("Target view exception!");
                    }
                }

                // Launch exception if source can't view the targeted rooms
            } else if (this.effect.getTargetView() && !this.viewInspector
                    .roomView(activePlayer.getCurrentPosition(), targetList)) {

                throw new TargetViewException("Room view exception!");
            }
        }
    }

    public void seenByActive(List<Target> activeList, List<Target> targetList)
            throws TargetViewException {

        // If the seen by active flag is present
        if (this.effect.isSeenByActive()) {

            for (Target active : activeList) {
                for (Target target : targetList) {

                    // Launch exception if seen by active flag violated
                    if (!this.viewInspector.targetView(active.getCurrentPosition(),
                            target.getCurrentPosition())) {

                        throw new TargetViewException("Target not seen by active!");
                    }
                }
            }
        }
    }

    public void checkDistance(Square activeSquare, List<Target> targetList)
            throws SquareDistanceException {

        if (!this.effect.getTargetType().equals(TargetType.ROOM)) {

            int distance;

            for (Target target : targetList) {

                // Distance between two squares considering cardinal and throughWalls flags
                distance = this.viewInspector
                        .computeDistance(activeSquare, target.getCurrentPosition(),
                                this.effect.isCardinal(), this.effect.isThroughWalls());

                // Launch exception if the distance is lower than the minDist property
                if (this.effect.getMinDist() != null && distance < this.effect.getMinDist()) {

                    throw new SquareDistanceException("Distance metrics not satisfied!");
                }

                // Launch exception if the distance is greater than the maxDist property
                if (this.effect.getMaxDist() != null && distance > this.effect.getMaxDist()) {

                    throw new SquareDistanceException("Distance metrics not satisfied!");
                }
            }
        }
    }

    public void checkCardinal(Square activeSquare, List<Target> targetList)
            throws CardinalException {

        // Launch exception if the cardinal flag is true and targets not in same direction
        if (this.effect.isCardinal() && !this.viewInspector
                .sameDirection(activeSquare, targetList)) {

            throw new CardinalException("Targets are not on same cardinal direction!");
        }

        // If the different squares flag is true
        if (this.effect.isDifferentSquares()) {

            // Duplicates will have the same key and will not be added to the set
            HashSet<Target> duplicate = new HashSet<>();

            // Launch exception if not all targets can be added to the set
            if (!targetList.stream().map(Target::getCurrentPosition).allMatch(duplicate::add)) {

                throw new CardinalException("Some targets are in the same square!");
            }
        }
    }

    public void sameAsPlayer(Player activePlayer, List<Target> target)
            throws SameAsPlayerException {

        // Same as player flag true
        if (this.effect.isSameAsPlayer()) {

            // Launch exception if any target on different position of active player
            if (this.effect.getArgs() == 2 && target.stream().anyMatch(
                    x -> !x.getCurrentPosition().equals(activePlayer.getCurrentPosition()))) {

                throw new SameAsPlayerException("Targets on different position of active player!");
            }

            // Launch exception if any target on same position of active player
        } else if (target.stream().anyMatch(x -> x.equals(activePlayer)) || (
                this.effect.getTargetType().equals(TargetType.ROOM) && target.stream()
                        .anyMatch(x -> x.equals(activePlayer.getCurrentPosition().getRoom())))) {

            throw new SameAsPlayerException("Same as player flag is false!");
        }
    }
}
