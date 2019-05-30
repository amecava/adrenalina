package it.polimi.ingsw.server.presenter;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.List;
import org.junit.jupiter.api.Test;

class EffectParserTest {

    @Test
    void test() {

        try {

            String line = "  MIRINO-rosso | sprog | giallo";

            Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
            board.fillBoard();

            Player source = new Player("source", Color.GRAY);

            source.addPowerUp(board.getPowerUpDeck().getDeck().stream()
                    .filter(x -> x.getName().equals("MIRINO") && x.getColor().equals(Color.RED))
                    .findAny().get());

            EffectArgument effectArgument = new EffectArgument();

            PowerUpCard powerUp = EffectParser
                    .powerUps(source, line.substring(0, line.indexOf("|"))).get(0);

            //System.out.println(powerUp.getName() + " " + powerUp.getColor());

            line = EffectParser.updateString(line);

            //System.out.println(line.substring(0, line.indexOf("|")));

            line = EffectParser.updateString(line);

            line = line.replaceAll("|", "");

            Color color = Color.ofName(line);

            //System.out.println(color);

        } catch (CardException e) {

            e.printStackTrace();
        }


    }
}