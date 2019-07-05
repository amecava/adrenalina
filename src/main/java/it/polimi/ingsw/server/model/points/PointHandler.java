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

    /**
     *  it is called at the end of the turn
     *  for each player it looks if they are dead , if sow
     *  calls the method death update of PointHandler , otherwise
     *  it changes their state of adrenalin
     * @param playerList all the players that are playing the same game
     */
    public static void checkIfDead(List<Player> playerList) {

        playerList.forEach(x -> {

            if (x.isDead()) {

                deathUpdate(x.getBridge(), playerList);
            } else {

                x.checkAdrenalin();
            }
        });
    }

    /**
     * adds the kill of the dead player to the general kill streak of the game
     * @param deaths kill streak of the general game
     * @param playerList list of players in the game
     */
    public static void countKills(Deaths deaths, List<Player> playerList) {

        for (Player player : playerList) {

            if (!player.getBridge().getColor().equals(Color.ALL) && player.isDead()) {

                deaths.addKill(
                        player.getShots().get(player.getShots().size() - 1),
                        player.getShots().size() >= 12);

                if (deaths.getFirstFrenzyPlayer() != null && !(player.getBridge()
                        .isKillStreakCount())) {

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

    /**
     * checks if the game is ended or if it has entered the frenzy stage
     * @param deaths kill streak of the entire game
     * @param playerList list of players in the same game
     * @return true if  the game has ended  or not
     */
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

                while (tempPlayer != getNextPlayer(playerList, activePlayer)) {

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

    /**
     * gives the correct points to all the players based on the killstreak of all the players
     * and on the damages that are still on players non dead
     * @param deaths all the deaths of the game
     * @param playerList all players in the same game
     * @return a list a player in winning order
     */
    public static List<List<Player>> endGame(Deaths deaths, List<Player> playerList) {

        playerList.forEach(x -> deathUpdate(x.getBridge(), playerList));
        deathUpdate(deaths, playerList);
        /**
         * index for placing the player in the correct winning order
         */
        int i = 0;
        /**
         * index for placing the player in the correct winning order
         */
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


    /**
     * creates the point structure for all the players ,  all the
     * point structures are linked to the bridge of the dead player .After
     * creating all the point structure it sorts them based on the PointSorter class
     * @param bridge essentially a list of damage taken by the dead player
     * @param playerList list of all players in the same game
     */
    private static void deathUpdate(Bridge bridge, List<Player> playerList) {
        /**
         * it checks if it has found the player that has done the first damage
         * to the dead player
         */
        boolean foundFirstBlood = false;

        /**
         * it check if it has found or not the player that has done the 12th damage
         * to the dead player
         */
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
