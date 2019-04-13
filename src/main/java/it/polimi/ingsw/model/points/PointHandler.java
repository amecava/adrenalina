package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.ArrayList;
import java.util.List;

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
