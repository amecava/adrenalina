package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.players.Color;

public class Shots {

    private Color color;

    Shots(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }
}