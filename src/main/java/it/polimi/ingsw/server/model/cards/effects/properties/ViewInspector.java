package it.polimi.ingsw.server.model.cards.effects.properties;

import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.board.rooms.Direction;
import it.polimi.ingsw.server.model.board.rooms.Room;
import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.exceptions.properties.SquareDistanceException;
import it.polimi.ingsw.server.model.exceptions.properties.TargetViewException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ViewInspector {

    private ViewInspector() {

        //
    }

    static int computeDistance(Square fromSquare, Square toSquare, boolean cardinal,
            boolean throughWalls) throws SquareDistanceException {

        // Return compute cardinal distance if cardinal true
        if (cardinal) {

            return computeCardinalDistance(fromSquare, toSquare, throughWalls);
        }

        // Return recursive distance if cardinal false
        return recursiveDistance(fromSquare.getMap(throughWalls), toSquare, throughWalls);

    }

    static boolean targetView(Square fromSquare, Square toSquare) {

        // Return true if same rooms
        if (fromSquare.getRoom().equals(toSquare.getRoom())) {

            return true;
        }

        // Check all directions
        for (Direction dir : Direction.values()) {

            // Return true if toSquare in adjacent room
            if (fromSquare.getConnection(dir) == Connection.DOOR && fromSquare.getAdjacent(dir)
                    .getRoom().getSquaresList().contains(toSquare)) {

                return true;
            }
        }

        return false;
    }

    static boolean roomView(Square fromSquare, List<Target> targetList) {

        int seen = 0;
        List<Room> targetStream;

        // Cast to rooms
        targetStream = targetList.stream()
                .map(x -> (Room) x)
                .collect(Collectors.toList());

        for (Direction dir : Direction.values()) {

            if (fromSquare.getConnection(dir) == Connection.DOOR && targetStream
                    .contains(fromSquare.getAdjacent(dir).getRoom())) {

                seen++;
            }
        }

        return seen == targetList.size();
    }

    static boolean sameDirection(Square fromSquare, List<Target> targetList) {

        int seen;
        Square tmpSquare;

        for (Direction dir : Direction.values()) {

            seen = 0;
            tmpSquare = fromSquare;

            // Check if targets in target list on same direction
            while (tmpSquare.getAdjacent(dir) != null) {

                tmpSquare = tmpSquare.getAdjacent(dir);

                if (targetList.stream().map(Target::getCurrentPosition)
                        .collect(Collectors.toList()).contains(tmpSquare)) {

                    seen++;
                }

                if (seen == targetList.size()) {

                    return true;
                }
            }
            if (seen > 0) {

                break;
            }
        }

        return false;
    }

    private static int computeCardinalDistance(Square fromSquare, Square toSquare,
            boolean throughWalls) throws SquareDistanceException {

        int count;
        Square square;

        // Compute cardinal distance
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

        // Launch exception if toSquare not found
        throw new SquareDistanceException("Target not found with selected properties!");
    }

    private static int recursiveDistance(Map<Square, Integer> map, Square toSquare,
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
                                    (throughWalls
                                            && x.getKey().getConnection(y) == Connection.WALL))) {

                                // Update starting node map
                                map.putIfAbsent(x.getKey().getAdjacent(y), x.getValue() + 1);

                                // Update visited adjacent nodes
                                x.getKey().getMap(throughWalls)
                                        .putIfAbsent(x.getKey().getAdjacent(y), 1);
                                x.getKey().getAdjacent(y).getMap(throughWalls)
                                        .putIfAbsent(x.getKey(), 1);
                            }
                        })
                );

        // Recursively call recursive distance until node found
        return recursiveDistance(map, toSquare, throughWalls);
    }
}
