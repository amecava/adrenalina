package it.polimi.ingsw.client.view.console;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.junit.jupiter.api.Test;

/**
 * Tests the ConsoleView methods.
 */
class ConsoleViewTest {

    /**
     * Checks if the cards infos are well built.
     */
    @Test
    void completeCardInfo() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(1);
        board.fillBoard();

        try (JsonReader reader = Json.createReader(
                new StringReader(board.getWeaponDeck().getCard().toJsonObject().toString()))) {

            JsonObject jCardObject = reader.readObject();

            StringBuilder info = new StringBuilder();

            info.append("Nome: ").append(jCardObject.getString("name")).append(" \n");
            info.append("Note: ").append(jCardObject.getString("notes")).append(" \n");

            info.append("Effetto primario: ").append(" \n");
            info.append(" -> Nome: ").append(jCardObject.getJsonObject("primary").getString("name"))
                    .append(" \n");
            info.append(" -> Descrizione: ")
                    .append(jCardObject.getJsonObject("primary").getString("description"))
                    .append(" \n");

            if (jCardObject.get("alternative") != JsonValue.NULL) {

                info.append("Effetto alternativo: ").append(" \n");
                info.append(" -> Nome: ")
                        .append(jCardObject.getJsonObject("alternative").getString("name"))
                        .append(" \n");
                info.append(" -> Descrizione: ")
                        .append(jCardObject.getJsonObject("alternative").getString("description"))
                        .append(" \n");
            }

            if (jCardObject.get("optional1") != JsonValue.NULL) {

                info.append("Effetto opzionale: ").append(" \n");
                info.append(" -> Nome: ")
                        .append(jCardObject.getJsonObject("optional1").getString("name"))
                        .append(" \n");
                info.append(" -> Descrizione: ")
                        .append(jCardObject.getJsonObject("optional1").getString("description"))
                        .append(" \n");
                info.append(" -> Costo: ")
                        .append(jCardObject.getJsonObject("optional1").getString("cost"))
                        .append(" \n");
            }

            if (jCardObject.get("optional2") != JsonValue.NULL) {

                info.append("Effetto opzionale: ").append(" \n");
                info.append(" -> Nome: ")
                        .append(jCardObject.getJsonObject("optional2").getString("name"))
                        .append(" \n");
                info.append(" -> Descrizione: ")
                        .append(jCardObject.getJsonObject("optional2").getString("description"))
                        .append(" \n");
                info.append(" -> Costo: ")
                        .append(jCardObject.getJsonObject("optional2").getString("cost"))
                        .append(" \n");
            }

            //System.out.println(info.toString());
        }
    }
}