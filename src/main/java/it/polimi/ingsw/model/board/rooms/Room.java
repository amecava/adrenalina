package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Room implements Target {

    private Color color;

    private List<Square> squaresList = new ArrayList<>();

    public Room(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }

    public void setColor(Color color) {

        this.color = color;
    }

    public void setSquaresList(List<Square> squaresList) {

        this.squaresList.addAll(squaresList);
    }

    public Square getSquaresList(int index) {

        return this.squaresList.get(index);
    }

    public List<Square> getSquaresList() {

        return this.squaresList;
    }

    public void addSquare(Square square) {

        this.squaresList.add(square);
    }

    public List<Player> getPlayers() {

        return this.squaresList.stream()
                .flatMap(x -> x.getPlayers().stream())
                .collect(Collectors.toList());
    }
}
