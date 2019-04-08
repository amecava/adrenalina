package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class Room implements Target {

    private Color color;

    private List<Square> squaresList = new ArrayList<>();
    private List<Player> playersInRoom = new ArrayList<>();

    public Room(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }

    public void setColor(Color color) {

        this.color = color;
    }

    public void addSquaresList(List<Square> squaresList) {

        this.squaresList.addAll(squaresList);
    }

    public List<Square> getSquaresList() {

        return this.squaresList;
    }

    public List<Player> getPlayers() {
        this.playersInRoom.clear();

        for (Square s : squaresList) {
            this.playersInRoom.addAll(s.getPlayers());
        }

        return this.playersInRoom;
    }
}
