package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;

class EffectParser {

    static Color color(String line) throws ColorException {


        line = line.trim();

        if (line.equals("")) {

            throw new ColorException("Seleziona un colore valido.");
        }


        return Color.ofName(line);
    }

    static int cardId(String line) throws CardException {

        line = line.replaceAll("\\s+", " ");

        if (line.equals(" ")) {

            throw new CardException("Seleziona un id valido.");
        }

        line = line.trim();

        return Integer.parseInt(line);
    }

    static List<Target> target(GameHandler gameHandler, String request)
            throws SquareException, ColorException {

        List<Target> targetList = new ArrayList<>();

        try {

            request = request.replaceAll("\\s+", " ");

            if (request.equals(" ")) {

                return targetList;
            }

            request = request.trim();

            String[] args = request.split(" ");

            for (String x : args) {

                Color color = Color.ofName(x);

                if (x.contains("-")) {

                    if (x.length() != 2) {

                        throw new SquareException("Istruzione non valida");
                    }

                    String[] square = x.split("-");

                    targetList.add(gameHandler.getModel().getBoard()
                            .findSquare(square[0], square[1]));

                } else if (color != null) {

                    targetList.add(
                            gameHandler.getModel().getBoard().getRoomsList().stream()
                                    .filter(y -> y.getColor().equals(color))
                                    .findAny()
                                    .orElseThrow(() -> new ColorException(
                                            "La stanza che hai selezionato non esiste.")));
                } else {

                    targetList.add(gameHandler.getModel().searchPlayer(x));
                }
            }
        } catch (NumberFormatException e) {

            throw new SquareException("Il numero del quadrato che hai selezionato non esiste.");
        }

        return targetList;
    }

    static Square destination(GameHandler gameHandler, String request)
            throws SquareException, ColorException {

        try {

            request = request.replaceAll("\\s+", " ");

            if (request.equals(" ")) {

                return null;
            }

            request = request.trim();

            if (request.contains("-")) {

                String[] square = request.split("-");

                if (square.length == 2) {

                    return gameHandler.getModel().getBoard()
                            .findSquare(square[0], square[1]);
                }
            }

            throw new SquareException("Istruzione non valida");

        } catch (NumberFormatException e) {

            throw new SquareException("Il numero del quadrato che hai selezionato non esiste.");
        }
    }

    static List<PowerUpCard> powerUps(Player player, String request) throws CardException {

        List<PowerUpCard> list = new ArrayList<>();

        try {

            request = request.replaceAll("\\s+", " ");

            if (request.equals(" ")) {

                return list;
            }

            request = request.trim();

            String[] args = request.split(" ");

            for (String x : args) {

                if (x.contains("-")) {

                    String[] square = x.split("-");

                    if (square.length != 2) {

                        throw new CardException("Istruzione non valida");
                    }


                    Color color = Color.ofName(square[1]);

                    list.add(player.findPowerUp(square[0], color));

                } else  {

                    throw new CardException("Istruzione non valida");
                }
            }
        } catch (NumberFormatException e) {

            throw new SquareException("Il numero del quadrato che hai selezionato non esiste.");
        }

        return list;
    }

    static EffectType effectType(String line) throws EffectException{

        line.replaceAll("\\s*", "");
        line.replaceAll("|", "");

        return EffectType.ofString(line);
    }

    static String updateString(String line) {

        line = line.substring(line.indexOf("|"));


        return line.substring(line.indexOf("|") + 1);
    }

}
