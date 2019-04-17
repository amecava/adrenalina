package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PointHandler {

    private Deaths deaths;

    private boolean frenzyEnabled = false;
    private boolean frenzyRegeneration = false;

    private List<Player> playerList;
    private PointSorter pointSorter = new PointSorter();
    private List<PointStructure> pointStructures = new ArrayList<>();

    public PointHandler(List<Player> playerList, int numberOfDeaths) {

        this.playerList = playerList;
        this.deaths = new Deaths(numberOfDeaths);
    }

    public List<Shots> getDeaths() {

        return this.deaths.getKillStreak();
    }

    public void enableFrenzy() {

        this.frenzyEnabled = true;
    }

    public boolean isGameEnded() {

        return this.deaths.isGameEnded();
    }

    public void checkIfDead() {

        this.playerList.stream()
                .filter(Player::isDead)
                .forEach(x -> this.deathUpdate(x.getBridge()));

        if (deaths.isGameEnded()) {
            if (!frenzyEnabled) {

                this.playerList.forEach(x -> this.deathUpdate(x.getBridge()));
                this.deathUpdate(this.deaths);
            } else {

                this.playerList.stream()
                        .filter(x -> x.getShots().isEmpty())
                        .forEach(Player::setFrenzy);

                this.frenzyRegeneration = true;
            }
        }
    }

    public List<List<Player>> getWinner() {
        this.pointStructures.clear();
        int i=0;
        int j=0;
        List<List<Player>> winner = new ArrayList<>();
        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(this.deaths.getKillStreak()).setNumberDamage(x.getPoints()))
                .collect(Collectors.toList());
        this.pointStructures.sort(new PointSorter());
        while (i<pointStructures.size()){
             winner.add(new ArrayList<Player>());
             winner.get(j).add(pointStructures.get(i).getPlayer());
             i++;
             if (pointStructures.get(i-1).getLastDamage()==-1){
                 while (i<pointStructures.size() && pointStructures.get(i).getNumberDamage()==winner.get(j).get(0).getPoints()){
                     winner.get(j).add(pointStructures.get(i).getPlayer());
                     i++;
                 }
             }
             j++;
        }
        return  winner;
        /*
        int j=0;
        this.pointStructures.clear();
        List<List<Player>> winner = new ArrayList<>();

        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(this.deaths.getKillStreak()).setNumberDamage(x.getPoints()))
                .collect(Collectors.toList());
        this.pointStructures.sort(new PointSorter());
        winner.add(new ArrayList<>());
        winner.get(0).add(pointStructures.get(0).getPlayer());

        for (int i = 0; i < this.pointStructures.size() - 1; i++) {
            if ((this.pointStructures.get(i).getNumberDamage() == this.pointStructures.get(i + 1).getNumberDamage()
                    && this.pointStructures.get(i).getLastDamage() == -1)) {
                winner.get(j).add(this.pointStructures.get(i + 1).getPlayer());
            } else {
                j++;
                winner.add(new ArrayList<>());
                winner.get(j).add(pointStructures.get(i+1).getPlayer());
            }
        }
        return winner;
        */
    }

    private void deathUpdate(Bridge bridge) {

        Color killShot = Color.EOG;
        boolean foundFirstBlood = false;
        boolean foundLastShot = false;

        pointStructures.clear();

        for (Player player : playerList) {
            pointStructures.add(player.createPointStructure(bridge.getShots()));
        }

        pointStructures.sort(this.pointSorter);

        for (PointStructure pointStructure : pointStructures) {
            if (pointStructure.getNumberDamage() != 0) {
                pointStructure.getPlayer().setPoints(bridge.assignPoints());
                if (!foundFirstBlood && pointStructure.getFirstDamage() == 1 && !(bridge
                        .isKillStreakCount())) {
                    pointStructure.getPlayer().setPoints(1);
                    foundFirstBlood = true;
                }
                if (!this.deaths.isGameEnded()) {
                    if (!foundLastShot && pointStructure.getLastDamage() == 12) {
                        pointStructure.getPlayer().markPlayer(bridge.getColor());
                        foundLastShot = true;
                        killShot = pointStructure.getPlayer().getPlayerColor();
                    }

                    if (!foundLastShot && pointStructure.getLastDamage() == 11) {
                        killShot = pointStructure.getPlayer().getPlayerColor();
                    }
                }
            }
        }
        if (!this.deaths.isGameEnded()) {
            deaths.addKill(killShot, foundLastShot);
            if (frenzyRegeneration && !(bridge.isKillStreakCount())) {
                bridge.setFrenzy();
                bridge.setKillStreakCount();
            } else {
                bridge.setPointsUsed();
                bridge.addKill();

            }
        }
    }
}
