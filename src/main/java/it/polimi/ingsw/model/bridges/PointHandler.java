package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PointHandler {
    private List<Player> playerList;

    public List<Player> getPlayerList() {
        List<Player> playerList1=  new ArrayList<>();
        playerList1.addAll(this.playerList);
        return playerList1;
    }

    private List<PointStructure> pointStructures= new ArrayList<>();

    public PointHandler(List<Player> playerList) {
        this.playerList = playerList;
    }


    public List<PointStructure> getPointStructures() {
        List<PointStructure> temp = new ArrayList<>();
        temp.addAll(this.pointStructures);
        return  temp;

    }

    public void deathUpdate (DamageBridge damageBridge) {
        boolean foundFirstBlood = false;
        boolean foundLastShot = false;
        pointStructures.clear();
        for (Player player : playerList) {
            pointStructures.add(player.countPoints(damageBridge));
        }
        pointStructures.sort(new PointSorter());
        for (PointStructure pointStructure1 : pointStructures) {
            if (pointStructure1.getNumberDamage() != 0) {
                pointStructure1.getPlayer().setPoints(damageBridge.getPoints());
                if (!foundFirstBlood) {
                    if (pointStructure1.getFirstDamage() == 1) {
                        pointStructure1.getPlayer().setPoints(1);
                        foundFirstBlood = true;
                    }
                }
                if (!foundLastShot) {
                    if (pointStructure1.getLastDamage() == 12) {
                        pointStructure1.getPlayer().setMark(damageBridge.getColor(), 1);
                        foundLastShot = true;
                        //find the player who killed the target player
                    }
                }
            }
        }
        damageBridge.restorePoints();
        damageBridge.setKill();
    }
}
