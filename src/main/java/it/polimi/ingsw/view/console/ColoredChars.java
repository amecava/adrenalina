package it.polimi.ingsw.view.console;

public class ColoredChars {

    char symbol;

    String color;

    public ColoredChars(char symbol, String color) {

        this.symbol = symbol;
        this.color = color;
    }

    public char getSymbol() {

        return this.symbol;
    }

    public String getColor() {

        return this.color;
    }
}
