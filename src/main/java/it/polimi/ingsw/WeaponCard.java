package it.polimi.ingsw;

public class WeaponCard implements Card {

    private String name;
    private String color;
    private Effect primary;
    private Effect alternative;
    private Effect optional;


    public WeaponCard(String name, String color) {
        this.name = name;
        this.color = color;
        this.primary = new Effect();
        this.alternative = new Effect();
        this.optional = new Effect();

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getColor() {
        return this.color;
    }


}
