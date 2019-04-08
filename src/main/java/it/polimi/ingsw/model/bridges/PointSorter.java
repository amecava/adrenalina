package it.polimi.ingsw.model.bridges;

import java.util.Comparator;

public class PointSorter implements Comparator<PointStructure> {

    // sorted in a decreasing way  so that the first element of the list  is the "biggest" element
    @Override
    public int compare(PointStructure o1, PointStructure o2) {
        if (o1.getNumberDamage()>o2.getNumberDamage())
            return -1;
        else if (o1.getNumberDamage() < o2.getNumberDamage())
            return +1;
        else if (o1.getNumberDamage()==o2.getNumberDamage() && o1.getFirstDamage()<o2.getFirstDamage())
            return -1;
        else
            return +1;

    }
}
