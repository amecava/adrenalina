package it.polimi.ingsw.model.cards.effects.properties;

import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Direction;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.properties.SquareDistanceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ViewInspector {

    int computeDistance(Square fromSquare, Square toSquare, boolean cardinal,
            boolean throughWalls) throws SquareDistanceException {

        if (cardinal) {
            return computeCardinalDistance(fromSquare, toSquare, throughWalls);
        }

        return this.recursiveDistance(fromSquare.getMap(throughWalls), toSquare, throughWalls);

    }

    boolean targetView(Square fromSquare, Square toSquare) {

        if (fromSquare.getRoom().equals(toSquare.getRoom())) {
            return true;
        }

        for (Direction dir : Direction.values()) {
            if (fromSquare.getConnection(dir) == Connection.DOOR && fromSquare.getAdjacent(dir)
                    .getRoom().getSquaresList().contains(toSquare)) {

                return true;
            }
        }

        return false;
    }

    boolean sameDirection(Square fromSquare, List<Target> targetList) {

        // TODO

        return true;
    }

    private int computeCardinalDistance(Square fromSquare, Square toSquare,
            boolean throughWalls) throws SquareDistanceException {

        int count = 0;
        Square square;

        for (Direction dir : Direction.values()) {

            if ((fromSquare.getConnection(dir) == Connection.SQUARE
                    || fromSquare.getConnection(dir) == Connection.DOOR) || (throughWalls
                    && fromSquare.getConnection(dir) == Connection.WALL)) {

                count = 0;
                square = fromSquare;

                while (square.getAdjacent(dir) != null && (
                        square.getConnection(dir) != Connection.WALL || throughWalls)) {

                    square = square.getAdjacent(dir);

                    count++;

                    if (square.equals(toSquare)) {
                        return count;
                    }
                }
            }
        }

        throw new SquareDistanceException("Target not in cardinal direction!");
    }

    private int recursiveDistance(Map<Square, Integer> map, Square toSquare,
            boolean throughWalls) {

        // Return distance if node found
        if (map.containsKey(toSquare)) {

            return map.get(toSquare);
        }

        // If node not found in map search from last known distance
        int distance = map.values().stream()
                .collect(Collectors.summarizingInt(Integer::intValue)).getMax();

        // Create new HashMap to avoid ConcurrentModificationException
        new HashMap<>(map).entrySet().stream()
                .filter(x -> x.getValue() >= distance)
                .forEach(x ->

                    // Iterate on all directions
                    Stream.of(Direction.values()).forEach(y -> {

                        // If current movement direction is legal
                        if (((x.getKey().getConnection(y) == Connection.SQUARE ||
                                x.getKey().getConnection(y) == Connection.DOOR) ||
                                (throughWalls && x.getKey().getConnection(y) == Connection.WALL))) {

                            // Update starting node map
                            map.putIfAbsent(x.getKey().getAdjacent(y), x.getValue() + 1);

                            // Update visited adjacent nodes
                            x.getKey().getMap(throughWalls).putIfAbsent(x.getKey().getAdjacent(y), 1);
                            x.getKey().getAdjacent(y).getMap(throughWalls).putIfAbsent(x.getKey(), 1);
                        }
                    })
                );

        // Recursively call recursive distance until node found
        return this.recursiveDistance(map, toSquare, throughWalls);
    }
}
