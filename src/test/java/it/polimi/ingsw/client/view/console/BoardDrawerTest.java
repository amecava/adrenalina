package it.polimi.ingsw.client.view.console;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.StringReader;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.jupiter.api.Test;

class BoardDrawerTest {

    @Test
    void  drawBoard() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(1);
        board.fillBoard();

        Player one = new Player("ame", Color.GRAY);
        Player two = new Player("fede", Color.GREEN);
        Player three = new Player("jacop", Color.VIOLET);
        Player four = new Player("edo", Color.YELLOW);
        Player five = new Player("frank", Color.LIGHTBLUE);

        one.movePlayer(board.getRoom(0).getSquare(0));
        one.addPowerUp(board.getPowerUp());
        one.addWeaponCard(board.getWeaponDeck().getCard());
        one.addWeaponCard(board.getWeaponDeck().getCard());
        one.addWeaponCard(board.getWeaponDeck().getCard());

        two.movePlayer(board.getRoom(1).getSquare(1));

        three.movePlayer(board.getRoom(2).getSquare(0));

        four.movePlayer(board.getRoom(3).getSquare(0));

        five.movePlayer(board.getRoom(2).getSquare(0));

        try (JsonReader reader = Json.createReader(new StringReader(board.toJsonObject().toString()))) {


            JsonObject jsonObject = reader.readObject();

            StringBuilder[] builder = BoardDrawer.drawBoard(jsonObject, "ame");

            //Arrays.stream(builder).map(StringBuilder::toString).forEach(System.out::println);

        } catch (IllegalArgumentException e ) {

            e.printStackTrace();
        }
    }

}