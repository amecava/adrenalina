package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PointHandler {

    private List<Player> playerList;
    private boolean EOG = false;
    private List<PointStructure> pointStructures = new ArrayList<>();
    private Deaths deaths;

    public List<Player> getPlayerList() {
        List<Player> playerList1 = new ArrayList<>();
        playerList1.addAll(this.playerList);
        return playerList1;
    }

    public void setDeaths(Deaths deaths) {
        this.deaths = deaths;
    }

    public boolean isEOG() {
        return EOG;
    }

    public void setEOG(boolean EOG) {
        this.EOG = EOG;
        for (Player player : playerList) {
            player.getBridge().getDamageBridge().EOG();
            this.deathUpdate(player.getBridge().getDamageBridge());
        }
    }

    public PointHandler(List<Player> playerList) {
        this.playerList = playerList;
    }


    public List<PointStructure> getPointStructures() {
        List<PointStructure> temp = new ArrayList<>();
        temp.addAll(this.pointStructures);
        return temp;

    }

    public void checkIfdead() {
        for (Player player : playerList) {
            if (player.checkIfdead()) {
                deathUpdate(player.getBridge().getDamageBridge());
            }
        }
    }

    public List<Shots> getDeaths() {
        return this.deaths.getKillStreak();
    }

    public void deathUpdate(DamageBridge damageBridge) {
        Color killShot = Color.EOG;
        boolean foundFirstBlood = false;
        boolean foundLastShot = false;
        pointStructures.clear();
        for (Player player : playerList) {
            pointStructures.add(player.countPoints(damageBridge.getShots()));
        }
        pointStructures.sort(new PointSorter());
        for (PointStructure pointStructure1 : pointStructures) {
            if (pointStructure1.getNumberDamage() != 0) {
                pointStructure1.getPlayer().setPoints(damageBridge.getPoints());
                if (!this.EOG) {
                    if (!foundFirstBlood ) {
                        if (pointStructure1.getFirstDamage() == 1) {
                            pointStructure1.getPlayer().setPoints(1);
                            foundFirstBlood = true;
                        }
                    }
                    if (!foundLastShot) {
                        if (pointStructure1.getLastDamage() == 12) {
                            pointStructure1.getPlayer().setMark(damageBridge.getColor(), 1);
                            foundLastShot = true;
                            killShot = pointStructure1.getPlayer().getPlayerColor();
                        }
                    }
                    if (!foundLastShot ) {
                        if (pointStructure1.getLastDamage() == 11) {
                            killShot = pointStructure1.getPlayer().getPlayerColor();
                        }
                    }
                }
            }
        }
        if (EOG) {
            deaths.addKill(Color.EOG, false);
        } else {
            deaths.addKill(killShot, foundLastShot);
            damageBridge.restorePoints();
            damageBridge.setKill();
        }
    }


}
