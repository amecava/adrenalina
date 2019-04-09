package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class PointHandler {

    private List<Player> playerList;
    private boolean eog = false;
    private boolean killStreakCount = false;
    private boolean frenzy;
    private List<PointStructure> pointStructures = new ArrayList<>();
    private Deaths deaths;

    public PointHandler(List<Player> playerList, int numberOfDeaths) {
        this.deaths = new Deaths(numberOfDeaths, this);
        this.playerList = playerList;
    }

    public List<Player> getPlayerList() {
        List<Player> playerList1 = new ArrayList<>();
        playerList1.addAll(this.playerList);
        return playerList1;
    }

    public void setKillStreakCount() {
        this.killStreakCount = true;
    }

    public void setDeaths(Deaths deaths) {
        this.deaths = deaths;
    }

    public boolean isEog() {
        return this.eog;
    }

    public void setEog(boolean eog) {
        this.eog = eog;
        for (Player player : playerList) {
            //player.getBridge().getDamageBridge().eog(); && makes all marker damage points
            this.deathUpdate(player.getBridge().getDamageBridge());
        }
    }

    public List<PointStructure> getPointStructures() {
        List<PointStructure> temp = new ArrayList<>();
        temp.addAll(this.pointStructures);
        return temp;
    }

    public void setFrenzy(boolean frenzy) {
        this.frenzy = frenzy;
    }

    public void checkIfdead()  {
        for (Player player : playerList) {
            if (player.checkIfdead()) {
                deathUpdate(player.getBridge().getDamageBridge());
            }
        }
        if (!eog && deaths.checkEndGame()) {
            if (!frenzy) {
                this.deaths.endgame();
            }
            else{
                 this.setKillStreakCount();
                 for (Player player: playerList){
                     if (player.getBridge().getDamageBridge().getShots().isEmpty()){
                         player.getBridge().getDamageBridge().setFrenzy();
                     }
                 }
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
                if (!foundFirstBlood && pointStructure1.getFirstDamage() == 1 && !killStreakCount) {
                    pointStructure1.getPlayer().setPoints(1);
                    foundFirstBlood = true;
                }
                if (!this.eog) {
                    if (!foundLastShot && pointStructure1.getLastDamage() == 12) {
                        pointStructure1.getPlayer().setMark(damageBridge.getColor(), 1);
                        foundLastShot = true;
                        killShot = pointStructure1.getPlayer().getPlayerColor();
                    }

                    if (!foundLastShot && pointStructure1.getLastDamage() == 11) {
                        killShot = pointStructure1.getPlayer().getPlayerColor();
                    }
                }
            }
        }
        if (!eog) {
            damageBridge.restorePoints();
            damageBridge.setKill();
            deaths.addKill(killShot, foundLastShot);

        }
    }
    public List<Player>  getWinner () {
        List<PointStructure> points = new ArrayList<>();
        List<Player> winner= new ArrayList<>();
        PointStructure pointStructure;
        for (Player player: playerList){
            pointStructure= player.countPoints(this.deaths.getKillStreak());
            pointStructure.setNumberDamage(player.getPoints());
            points.add(pointStructure);
        }
        points.sort(new PointSorter());
        System.out.println(points);
        winner.add(points.get(0).getPlayer());
            for (int i=0 ; i< points.size()-1; i++){
                if ((points.get(i).getNumberDamage()==points.get(i+1).getNumberDamage() && points.get(i).getLastDamage()==-1)){
                    winner.add(points.get(i+1).getPlayer());
                }
                else break;
            }
            return winner;
    }



}
