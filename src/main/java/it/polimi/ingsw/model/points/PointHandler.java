package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.exceptions.endGameException.FrenzyRegenerationException;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
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

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void checkIfDead() throws FrenzyRegenerationException, EndGameException {
        for (Player player : playerList){
            if (player.isDead())
                this.deathUpdate(player.getBridge());
            else
                player.controlAdrenalin();
        }
        if (deaths.isGameEnded()) {
            if (!frenzyEnabled) {
                this.playerList.forEach(x -> this.deathUpdate(x.getBridge()));
                this.deathUpdate(this.deaths);
                throw new EndGameException(" game ended with no frenzy actions!!", this.getWinner());

            } else {
                if (!frenzyRegeneration) {
                    this.playerList.stream().forEach(
                            x -> x.frenzyActions());//frenzy actions should be changed in base of player order
                    this.playerList.stream().filter(x -> x.getShots().isEmpty())
                            .forEach(Player::setFrenzy);
                    this.frenzyRegeneration = true;
                }
                throw new FrenzyRegenerationException(" Frenzy time!!!! ");
            }
        }
    }

    public List<List<Player>> getWinner() {

        int i = 0;
        int j = 0;
        List<List<Player>> winner = new ArrayList<>();

        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(this.deaths.getKillStreak())
                        .setNumberDamage(x.getPoints()))
                .collect(Collectors.toList());

        this.pointStructures.sort(new PointSorter());

        while (i < pointStructures.size()) {

            winner.add(new ArrayList<>());
            winner.get(j).add(pointStructures.get(i).getPlayer());
            i++;

            if (pointStructures.get(i - 1).getLastDamage() == -1) {

                while (i < pointStructures.size() && pointStructures
                        .get(i).getNumberDamage() == winner.get(j).get(0).getPoints()) {

                    winner.get(j).add(pointStructures.get(i).getPlayer());
                    i++;
                }
            }

            j++;
        }

        return winner;
    }

    private void deathUpdate(Bridge bridge) {

        Color killShot = Color.ANY;
        boolean foundFirstBlood = false;
        boolean foundLastShot = false;

        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(bridge.getShots()))
                .collect(Collectors.toList());

        this.pointStructures.sort(this.pointSorter);

        for (PointStructure pointStructure : pointStructures) {
            if (pointStructure.getNumberDamage() != 0) {
                pointStructure.getPlayer().setPoints(bridge.assignPoints());
                if (!foundFirstBlood && pointStructure.getFirstDamage() == 1 && !(bridge
                        .isKillStreakCount())) {
                    pointStructure.getPlayer().setPoints(1);
                    foundFirstBlood = true;
                }
                if (!bridge.getColor().equals(Color.ANY) && bridge.isDead()) {
                    if (!foundLastShot && pointStructure.getLastDamage() == 12) {
                        pointStructure.getPlayer().markPlayer(bridge.getColor());
                        foundLastShot = true;
                        killShot = pointStructure.getPlayer().getColor();
                    }

                    if (!foundLastShot && pointStructure.getLastDamage() == 11) {
                        killShot = pointStructure.getPlayer().getColor();
                    }
                }
            }
        }
        if (!bridge.getColor().equals(Color.ANY) && bridge.isDead() ) {
            deaths.addKill(killShot, foundLastShot);
            if (frenzyRegeneration && !(bridge.isKillStreakCount())) {
                bridge.setFrenzy();
                bridge.setKillStreakCount();
            } else {
                bridge.setPointsUsed();
                bridge.addKill();
                if (!frenzyRegeneration)
                    bridge.setAdrenalin(Adrenalin.NORMAL);

            }
        }
    }
}
