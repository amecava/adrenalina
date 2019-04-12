package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.ArrayList;
import java.util.List;

public class PointHandler {

    private List<Player> playerList;
    private boolean endOfGame = false;
    private boolean frenzy;
    private List<PointStructure> pointStructures = new ArrayList<>();
    private PointSorter pointSorter = new PointSorter();
    private Deaths deaths;
    private boolean frenzyRegenartion=false;

    public PointHandler(List<Player> playerList, int numberOfDeaths) {

        this.deaths = new Deaths(numberOfDeaths);
        this.playerList = playerList;
    }

    public List<Player> getPlayerList() {
        List<Player> playerList1 = new ArrayList<>();
        playerList1.addAll(this.playerList);
        return playerList1;
    }

    public void setDeaths(Deaths deaths) {
        this.deaths = deaths;
    }

    public boolean isEndOfGame() {
        return this.endOfGame;
    }

    public void setEndOfGame(boolean endOfGame) {
        this.endOfGame = endOfGame;
        for (Player player : playerList) {

            this.deathUpdate(player.getBridge());
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

    public void checkIfDead() {
        for (Player player : playerList) {
            if (player.isDead()) {
                deathUpdate(player.getBridge());
            }
        }
        if (deaths.checkEndGame()) {
            if (!frenzy) {
                this.setEndOfGame(true);
                this.deathUpdate(this.deaths);
            } else {
                for (Player player : playerList) {
                    if (player.getShots().isEmpty()) {
                        player.setFrenzy();
                    }
                }
                this.frenzyRegenartion=true;
            }
        }
    }

    public List<Shots> getDeaths() {

        return this.deaths.getKillStreak();
    }

    public void deathUpdate(Bridge bridge) {

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
                pointStructure.getPlayer().setPoints(bridge.calculatePoints());
                if (!foundFirstBlood && pointStructure.getFirstDamage() == 1 && !(bridge.isKillstreakCount())) {
                    pointStructure.getPlayer().setPoints(1);
                    foundFirstBlood = true;
                }
                if (!this.endOfGame) {
                    if (!foundLastShot && pointStructure.getLastDamage() == 12) {
                        pointStructure.getPlayer().markPlayer(bridge.getColor(), 1);
                        foundLastShot = true;
                        killShot = pointStructure.getPlayer().getPlayerColor();
                    }

                    if (!foundLastShot && pointStructure.getLastDamage() == 11) {
                        killShot = pointStructure.getPlayer().getPlayerColor();
                    }
                }
            }
        }
        if (!endOfGame){
            deaths.addKill(killShot,foundLastShot);
            if (frenzyRegenartion && !(bridge.isKillstreakCount())){
                bridge.setFrenzy();
                bridge.setKillStreakCount();
            }
            else{
                bridge.setPointsUsed();
                bridge.addKill();

            }
        }
    }

    public List<Player> getWinner() {

        List<PointStructure> points = new ArrayList<>();
        List<Player> winner = new ArrayList<>();
        PointStructure pointStructure;
        for (Player player : playerList) {
            pointStructure = player.createPointStructure(this.deaths.getKillStreak());
            pointStructure.setNumberDamage(player.getPoints());
            points.add(pointStructure);
        }
        points.sort(new PointSorter());
        winner.add(points.get(0).getPlayer());
        for (int i = 0; i < points.size() - 1; i++) {
            if ((points.get(i).getNumberDamage() == points.get(i + 1).getNumberDamage()
                    && points.get(i).getLastDamage() == -1)) {
                winner.add(points.get(i + 1).getPlayer());
            } else {
                break;
            }
        }
        return winner;
    }


}
