package it.polimi.ingsw.server.model.points;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.bridges.Adrenalin;
import it.polimi.ingsw.server.model.players.bridges.Bridge;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PointHandler {

    private PointHandler() {

        //
    }

    public static void checkIfDead(List<Player> playerList) {

        playerList.forEach(x -> {

            if (x.isDead()) {

                deathUpdate(x.getBridge(), playerList);
            } else {

                x.checkAdrenalin();
            }
        });
    }

    public static void countKills(Deaths deaths, List<Player> playerList) {

        for (Player player : playerList) {

            if (!player.getBridge().getColor().equals(Color.ALL) && player.isDead()) {

                deaths.addKill(
                        player.getShots().get(player.getShots().size() - 1),
                        player.getShots().size() >= 12);

                if (deaths.getFirstFrenzyPlayer() != null && !(player.getBridge().isKillStreakCount())) {

                    player.getBridge().setFrenzy();
                    player.getBridge().setKillStreakCount();

                } else {

                    player.setPointsUsed();
                    player.addKill();

                    if (deaths.getFirstFrenzyPlayer() == null) {

                        player.getBridge().setAdrenalin(Adrenalin.NORMAL);
                    }
                }
            }
        }
    }

    public static boolean checkEndGame(Deaths deaths, List<Player> playerList) {

        if (!deaths.isGameEnded()) {

            return false;
        }

        if (deaths.isFrenzy()) {

            Player activePlayer = playerList.stream()
                    .filter(Player::isActivePlayer)
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            if (deaths.getFirstFrenzyPlayer() == null) {

                // Sets end game true so that adrenalin doesn't change !!
                playerList.forEach(
                        x -> x.setFrenzyActions(true));

                // Flips the damage bridge and gives new points for each kill
                //also there isn't the first blood damage!!
                playerList.stream()
                        .filter(x -> x.getShots().isEmpty())
                        .forEach(Player::setFrenzy);

                Player tempPlayer = getNextPlayer(playerList, activePlayer);

                while (!tempPlayer.isFirstPlayer()) {

                    tempPlayer.setAdrenalin(Adrenalin.FIRSTFRENZY);
                    tempPlayer = getNextPlayer(playerList, tempPlayer);
                }

                while (tempPlayer != activePlayer) {

                    tempPlayer.setAdrenalin(Adrenalin.SECONDFRENZY);
                    tempPlayer = getNextPlayer(playerList, tempPlayer);
                }

                deaths.setFirstFrenzyPlayer(getNextPlayer(playerList, activePlayer));

                return false;

            }

            return getNextPlayer(playerList, activePlayer) == deaths.getFirstFrenzyPlayer();
        }

        return true;
    }

    public static List<List<Player>> endGame(Deaths deaths, List<Player> playerList) {

        playerList.forEach(x -> deathUpdate(x.getBridge(), playerList));
        deathUpdate(deaths, playerList);

        int i = 0;
        int j = 0;

        List<List<Player>> winnerList = new ArrayList<>();

        List<PointStructure> pointStructures = playerList.stream()
                .map(x -> x.createPointStructure(deaths.getKillStreak())
                        .setNumberDamage(x.getPoints()))
                .sorted(new PointSorter())
                .collect(Collectors.toList());

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

    private static Player getNextPlayer(List<Player> playerList, Player player) {

        int nextPlayer = playerList.indexOf(player) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return playerList.get(nextPlayer);
    }

    private static void deathUpdate(Bridge bridge, List<Player> playerList) {

        boolean foundFirstBlood = false;
        boolean foundLastBlood = false;

        List<PointStructure> pointStructures = playerList.stream()
                .map(x -> x.createPointStructure(bridge.getShots()))
                .sorted(new PointSorter())
                .collect(Collectors.toList());

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
