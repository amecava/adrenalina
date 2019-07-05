package it.polimi.ingsw.server.model.points;

import java.util.Comparator;

/**
 * class for sorting the player's contribute in killing a player
 */
public class PointSorter implements Comparator<PointStructure> {

    /**
     * sorts all  the point structure of all the players in order to assign
     * each player the correct number of damage, the player that has done the most amount
     * of damage and has done the first damage gets the highest points
     * @param o1 first point structure to compare
     * @param o2 second point structure to compare
     * @return +1 if o2 has done more damages to the dead player than 01
     * or if they are equal in number of damage but  02 has made damage
     * before 01.
     * -1 if o1 has done more damages to the dead player than 02
     * or if they are equal in number of damage but  01 has shot the
     * dead player before 01
     */
    @Override
    public int compare(PointStructure o1, PointStructure o2) {

        if (o1.getNumberDamage() > o2.getNumberDamage()) {
            return -1;
        }

        if (o1.getNumberDamage() < o2.getNumberDamage()) {
            return +1;
        }

        if (o1.getFirstDamage() < o2.getFirstDamage()) {
            return -1;
        }
        if (o1.getFirstDamage() > o2.getFirstDamage()) {
            return +1;

        } else {
            o1.setLastDamage(-1);
            o2.setLastDamage(-1);

            return 0;
        }
    }
}
