package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;

public class Shots {
    private Color color;


    public Shots(Color color){
        this.color=color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
           if (color.equals(Color.GREEN))
               return new String(" green ");
           else if (color.equals(Color.GRAY))
                   return new String(" gray ");
           else if (color.equals(Color.RED))
                   return new String("red ");
           else if (color.equals(Color.VIOLET))
                   return new String(" violet ");
           else if (color.equals(Color.YELLOW))
                   return new String(" yellow ");
           else if (color.equals(Color.BLUE))
                    return new String("blue");
           else if (color.equals(Color.LIGHTBLUE))
                    return new String(" light blue ");
            return new String("no color!!");


    }

}