package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import org.junit.jupiter.api.Test;

class EffectParserTest {

    @Test
    void test() {

        String line = "tipo( PRIMARY) target(sprog) destinazione() powerup()";

        int startPar;
        int endPar;

        line = line.substring(line.indexOf("powerup"), line.indexOf(")", line.indexOf("powerup")) + 1);
        line = line.replaceAll("powerup(\\s*)", "powerup");

        startPar = line.indexOf("powerup") + "powerup".length();
        endPar = line.indexOf(")");

        line = line.substring(startPar + 1, endPar).trim();

        System.out.println("*" + line + "*");

        try {

            System.out.println(EffectType.ofString(line));
        } catch (EffectException e){

        }

    }
}