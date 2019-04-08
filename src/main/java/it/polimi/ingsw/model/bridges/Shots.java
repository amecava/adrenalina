package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;

public class Shots {

    private Color color;

    public Shots(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return this.color;
    }

    @Override
    public String toString() {

        if (color.equals(Color.GREEN)) {
            return " green ";
        } else if (color.equals(Color.GRAY)) {
            return " gray ";
        } else if (color.equals(Color.RED)) {
            return " red ";
        } else if (color.equals(Color.VIOLET)) {
            return " violet ";
        } else if (color.equals(Color.YELLOW)) {
            return " yellow ";
        } else if (color.equals(Color.BLUE)) {
            return " blue ";
        } else if (color.equals(Color.LIGHTBLUE)) {
            return " light blue ";
        }

        return " no color ";
    }
}