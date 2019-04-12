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

class ViewInspector {

    int computeDistance(Square fromSquare, Square toSquare, boolean cardinal,
            boolean throughWalls) throws SquareDistanceException {

        if (cardinal) {
            return computeCardinalDistance(fromSquare, toSquare, throughWalls);
        }

        return this.distance(fromSquare.getMap(throughWalls), toSquare, throughWalls);

    }

    boolean targetView(Square fromSquare, Square toSquare) {

        if (fromSquare.getMyRoom().equals(toSquare.getMyRoom())) {
            return true;
        }

        for (Direction dir : Direction.values()) {
            if (fromSquare.getConnection(dir) == Connection.DOOR && fromSquare.getAdjacent(dir)
                    .getMyRoom().getSquaresList().contains(toSquare)) {

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

    private int distance(Map<Square, Integer> map, Square toSquare,
            boolean throughWalls) {

        if (map.containsKey(toSquare)) {

            return map.get(toSquare);
        }

        int distance = map.values().stream()
                .collect(Collectors.summarizingInt(Integer::intValue)).getMax();

        new HashMap<>(map).entrySet().stream()
                .filter(x -> x.getValue() >= distance)
                .forEach(x -> {

                    for (Direction dir : Direction.values()) {

                        if (((x.getKey().getConnection(dir) == Connection.SQUARE
                                || x.getKey().getConnection(dir) == Connection.DOOR) || (
                                throughWalls
                                        && x.getKey().getConnection(dir) == Connection.WALL))
                                && !map.containsKey(x.getKey().getAdjacent(dir))) {

                            map.put(x.getKey().getAdjacent(dir), x.getValue() + 1);

                        }

                    }
                });

        return this.distance(map, toSquare, throughWalls);
    }
}
