package it.polimi.ingsw.server.model.cards.effects.properties;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.exceptions.properties.CardinalException;
import it.polimi.ingsw.server.model.exceptions.properties.DuplicateException;
import it.polimi.ingsw.server.model.exceptions.properties.MaxTargetsException;
import it.polimi.ingsw.server.model.exceptions.properties.SameAsFatherException;
import it.polimi.ingsw.server.model.exceptions.properties.SameAsPlayerException;
import it.polimi.ingsw.server.model.exceptions.properties.SquareDistanceException;
import it.polimi.ingsw.server.model.exceptions.properties.TargetViewException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class analyzes the properties of every effect when it needs to be executed.
 */
public class PropertiesAnalyzer {

    /**
     * Private constructor to hide the public implicit one.
     */
    private PropertiesAnalyzer() {

        //
    }

    /**
     * Checks the maxTargets property given the Effect and the targetList.
     *
     * @param effect The effect to be executed.
     * @param target The list of Target.
     * @throws DuplicateException If some target are selected more than once.
     * @throws MaxTargetsException If there are too many targets.
     */
    public static void maxTargets(Effect effect, List<Target> target)
            throws DuplicateException, MaxTargetsException {

        // Launch exception if duplicates found
        if (target.size() != target.stream().distinct().collect(Collectors.toList()).size()) {

            throw new DuplicateException(
                    "Hai selezionato alcuni target più di una volta! Riprova.");
        }

        // Launch exception if max targets property is violated
        if (effect.getMaxTargets() != null &&
                target.size() > effect.getMaxTargets()) {

            throw new MaxTargetsException("Hai selezionato troppi target! Riprova.");
        }
    }

    /**
     * Checks the sameAsFather property given the Effect, the active list, inactive list and the
     * targetList.
     *
     * @param effect The effect to be executed.
     * @param active The activeList of the effecthandler.
     * @param inactive The inactiveList of the effecthandler.
     * @param target The list of Target.
     * @throws SameAsFatherException If the sameAsFather property is violated.
     */
    public static void sameAsFather(Effect effect, List<Target> active, List<Target> inactive,
            List<Target> target) throws SameAsFatherException {

        // If the same as father flag is present
        if (effect.getSameAsFather() != null) {

            // Create new List to avoid ConcurrentModificationException
            List<Target> notFound = new ArrayList<>(target);

            try {
                effect.getSameAsFather().forEach(x -> {

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

                throw new SameAsFatherException("Devi sparare allo stesso giocatore di prima.");
            }
        }
    }

    /**
     * Checks the targetView property given the effect, the activePlayer and the targetList.
     *
     * @param effect The Effect to be executed.
     * @param activePlayer The activePlayer.
     * @param targetList The list of target.
     * @throws TargetViewException If the targetView property is violated.
     */
    public static void targetView(Effect effect, Player activePlayer, List<Target> targetList)
            throws TargetViewException {

        // If the target view flag is present
        if (effect.getTargetView() != null) {

            if (!effect.getTargetType().equals(TargetType.ROOM)) {

                for (Target target : targetList) {

                    // Launch exception if target view flag violated
                    if ((ViewInspector
                            .targetView(activePlayer.getCurrentPosition(),
                                    target.getCurrentPosition())
                            && !effect.getTargetView()) || (!ViewInspector
                            .targetView(activePlayer.getCurrentPosition(),
                                    target.getCurrentPosition())
                            && effect.getTargetView())) {

                        throw new TargetViewException("Non puoi vedere il tuo target! Riprova.");
                    }
                }

                // Launch exception if source can't view the targeted rooms
            } else if (effect.getTargetView() && !ViewInspector
                    .roomView(activePlayer.getCurrentPosition(), targetList)) {

                throw new TargetViewException("Non vedi la stanza che hai scelto! Riprova.");
            }
        }
    }

    /**
     * Checks the seenByActive property given the effect, the activeList and the targetList.
     *
     * @param effect The Effect to be executed.
     * @param activeList The activeList.
     * @param targetList The targetList.
     * @throws TargetViewException If the targetView property is violated.
     */
    public static void seenByActive(Effect effect, List<Target> activeList, List<Target> targetList)
            throws TargetViewException {

        // If the seen by active flag is present
        if (effect.isSeenByActive()) {

            for (Target active : activeList) {
                for (Target target : targetList) {

                    // Launch exception if seen by active flag violated
                    if (!ViewInspector.targetView(active.getCurrentPosition(),
                            target.getCurrentPosition())) {

                        throw new TargetViewException(
                                "Devi scegliere qualcuno che può essere visto da chi hai appena colpito!");
                    }
                }
            }
        }
    }

    /**
     * Checks the seenByActive property given the effect, the activeSquare and the targetList.
     *
     * @param effect The Effect to be executed.
     * @param activeSquare The activeSquare.
     * @param targetList The targetList.
     * @throws SquareDistanceException If the distance property is violated.
     */
    public static void checkDistance(Effect effect, Square activeSquare, List<Target> targetList)
            throws SquareDistanceException {

        if (!effect.getTargetType().equals(TargetType.ROOM)) {

            int distance;

            for (Target target : targetList) {

                // Distance between two squares considering cardinal and throughWalls flags
                distance = ViewInspector
                        .computeDistance(activeSquare, target.getCurrentPosition(),
                                effect.isCardinal(), effect.isThroughWalls());

                // Launch exception if the distance is lower than the minDist property
                if (effect.getMinDist() != null && distance < effect.getMinDist()) {

                    throw new SquareDistanceException(
                            "Hai scelto un bersaglio troppo vicino a te! Riprova.");
                }

                // Launch exception if the distance is greater than the maxDist property
                if (effect.getMaxDist() != null && distance > effect.getMaxDist()) {

                    throw new SquareDistanceException(
                            "Hai scelto un bersaglio troppo lontano! Riprova.");
                }
            }
        }
    }

    /**
     * Checks the cardinal property given the effect, the activeSquare and the targetList.
     *
     * @param effect The Effect to be executed.
     * @param activeSquare The activeSquare.
     * @param targetList The targetList.
     * @throws CardinalException If the cardinal property is violated.
     */
    public static void checkCardinal(Effect effect, Square activeSquare, List<Target> targetList)
            throws CardinalException {

        // Launch exception if the cardinal flag is true and targets not in same direction
        if (effect.isCardinal() && !ViewInspector
                .sameDirection(activeSquare, targetList)) {

            throw new CardinalException(
                    "Attenzione: i bersagli devono essere lungo una direzione cardinale.");
        }

        // If the different squares flag is true
        if (effect.isDifferentSquares()) {

            // Duplicates will have the same key and will not be added to the set
            HashSet<Target> duplicate = new HashSet<>();

            // Launch exception if not all targets can be added to the set
            if (!targetList.stream().map(Target::getCurrentPosition).allMatch(duplicate::add)) {

                throw new CardinalException(
                        "Attenziona: alcuni bersagli sono nello stesso quadrato.");
            }
        }
    }

    /**
     * Checks the sameAsPlayer property given the effect, the activePlayer and the targetList.
     *
     * @param effect The Effect to be executed.
     * @param activePlayer The activePlayer.
     * @param target The targetList.
     * @throws SameAsPlayerException If the sameAsPlayer property is violated.
     */
    public static void sameAsPlayer(Effect effect, Player activePlayer, List<Target> target)
            throws SameAsPlayerException {

        // Same as player flag true
        if (effect.isSameAsPlayer()) {

            // Launch exception if any target on different position of active player
            if (effect.getArgs() == 2 && target.stream().anyMatch(
                    x -> !x.getCurrentPosition().equals(activePlayer.getCurrentPosition()))) {

                throw new SameAsPlayerException(
                        "I bersagli devono essere nello stesso quadrato in cui sei tu.");
            }

            // Launch exception if any target on same position of active player
        } else if (target.stream().anyMatch(x -> x.equals(activePlayer)) || (
                effect.getTargetType().equals(TargetType.ROOM) && target.stream()
                        .anyMatch(x -> x.equals(activePlayer.getCurrentPosition().getRoom())))) {

            throw new SameAsPlayerException(
                    "I bersagli non possono essere nel tuo stesso quadrato!");
        }
    }
}
