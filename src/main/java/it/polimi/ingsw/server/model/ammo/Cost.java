package it.polimi.ingsw.server.model.ammo;

import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.exceptions.cards.CostException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.List;

public class Cost {

    private Cost() {

        //
    }

    public static void checkCost(Player player, List<Color> cost, List<PowerUpCard> powerUpList) throws CostException {

        try {

            powerUpList.stream()
                    .map(PowerUpCard::getColor)
                    .forEach(x -> {

                        if (cost.contains(x)) {

                            cost.remove(x);

                        } else {

                            throw new IllegalArgumentException();
                        }
                    });

            cost.stream()
                    .distinct()
                    .forEach(x -> {
                        if (cost.stream().filter(y -> y.equals(x)).count() > player
                                .getAmmoCubesList().stream().filter(y -> !y.isUsed()).map(
                                        AmmoCube::getColor).filter(y -> y.equals(x)).count()) {

                            throw new IllegalArgumentException();
                        }
                    });

        } catch (IllegalArgumentException e) {

            throw new CostException("Non hai abbastanza risorse.");
        }
    }

}
