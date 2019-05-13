package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.board.Deaths;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PointHandler {

    private Deaths deaths;

    private Player firstFrenzyPlayer;
    private boolean frenzyEnabled = false;

    private List<Player> playerList;

    private PointSorter pointSorter = new PointSorter();
    private List<PointStructure> pointStructures = new ArrayList<>();

    public PointHandler(List<Player> playerList, int numberOfDeaths) {

        this.playerList = playerList;
        this.deaths = new Deaths(numberOfDeaths);
    }

    public int getNumberOfDeaths() {

        return deaths.getNumberOfDeaths();
    }

    public boolean isFrenzy() {

        return this.frenzyEnabled;
    }

    public void enableFrenzy() {

        this.frenzyEnabled = true;
    }

    public void checkIfDead() {

        this.playerList.forEach(x -> {

            if (x.isDead()) {

                this.deathUpdate(x.getBridge());
            } else {

                x.checkAdrenalin();
            }
        });
    }

    public void countKills() {
        boolean foundLastShot;
        Color killShot;

        for (Player player : playerList) {
            if (!player.getBridge().getColor().equals(Color.ALL) && player.isDead()) {

                killShot = player.getShots().get(player.getShots().size() - 1).getColor();
                foundLastShot = player.getShots().size() >= 12;
                deaths.addKill(killShot, foundLastShot);
                if (this.firstFrenzyPlayer != null && !(player.getBridge().isKillStreakCount())) {
                    player.getBridge().setFrenzy();
                    player.getBridge().setKillStreakCount();
                } else {
                    player.setPointsUsed();
                    player.addKill();
                    if (this.firstFrenzyPlayer == null) {
                        player.getBridge().setAdrenalin(Adrenalin.NORMAL);
                    }
                }
            }
        }
    }

    public boolean checkEndGame() {

        if (deaths.isGameEnded()) {

            if (!frenzyEnabled) {

                return true;
            } else {

                Player activePlayer = this.playerList.stream().filter(Player::isActivePlayer)
                        .findFirst().get();

                if (this.firstFrenzyPlayer == null) {

                    // Sets end game true so that adrenalin doesn't change !!
                    this.playerList.forEach(
                            x -> x.setFrenzyActions(true));

                    // Flips the damage bridge and the possible actions!!!
                    this.playerList.stream()
                            .filter(x -> x.getShots().isEmpty())
                            .forEach(Player::setFrenzy);

                    Player tempPlayer = this.getNextPlayer(activePlayer);

                    while (!tempPlayer.isFirstPlayer()) {

                        tempPlayer.setAdrenalin(Adrenalin.FIRSTFRENZY);
                        tempPlayer = this.getNextPlayer(tempPlayer);
                    }

                    while (tempPlayer != activePlayer) {

                        tempPlayer.setAdrenalin(Adrenalin.SECONDFRENZY);
                        tempPlayer = this.getNextPlayer(tempPlayer);
                    }

                    this.firstFrenzyPlayer = this.getNextPlayer(activePlayer);

                } else {

                    if (this.getNextPlayer(activePlayer) == firstFrenzyPlayer) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<List<Player>> endGame() {

        this.playerList.forEach(x -> this.deathUpdate(x.getBridge()));
        this.deathUpdate(this.deaths);

        int i = 0;
        int j = 0;

        List<List<Player>> winnerList = new ArrayList<>();

        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(this.deaths.getKillStreak())
                        .setNumberDamage(x.getPoints()))
                .collect(Collectors.toList());

        this.pointStructures.sort(new PointSorter());

        while (i < pointStructures.size()) {

            winnerList.add(new ArrayList<>());
            winnerList.get(j).add(pointStructures.get(i).getPlayer());
            i++;

            if (pointStructures.get(i - 1).getLastDamage() == -1) {

                while (i < pointStructures.size() && pointStructures
                        .get(i).getNumberDamage() == winnerList.get(j).get(0).getPoints()) {

                    winnerList.get(j).add(pointStructures.get(i).getPlayer());
                    i++;
                }
            }

            j++;
        }

        return winnerList;
    }

    private Player getNextPlayer(Player player) {

        int nextPlayer = playerList.indexOf(player) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return this.playerList.get(nextPlayer);
    }

    private void deathUpdate(Bridge bridge) {

        boolean foundFirstBlood = false;
        boolean foundLastBlood = false;

        this.pointStructures = this.playerList.stream()
                .map(x -> x.createPointStructure(bridge.getShots()))
                .collect(Collectors.toList());

        this.pointStructures.sort(this.pointSorter);

        for (PointStructure pointStructure : pointStructures) {

            if (pointStructure.getNumberDamage() != 0) {

                pointStructure.getPlayer().setPoints(bridge.assignPoints());

                if (!foundFirstBlood && pointStructure.getFirstDamage() == 1
                        && !(bridge.isKillStreakCount())) {

                    pointStructure.getPlayer().setPoints(1);
                    foundFirstBlood = true;
                }

                if (!foundLastBlood && pointStructure.getLastDamage() == 12
                        && bridge.getColor() != Color.ALL) {

                    pointStructure.getPlayer().markPlayer(bridge.getColor());
                    foundLastBlood = true;
                }
            }
        }

    }
}
