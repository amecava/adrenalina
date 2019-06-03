package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;

class EffectParser {

    private static final String TIPO = "tipo";
    private static final String TARGET = "target";
    private static final String DESTINAZIONE = "destinazione";
    private static final String POWER_UP = "powerup";
    private static final String AMMOCOLOR = "paga";
    private static final String CARD_ID = "id";



    static EffectArgument effectArgument(GameHandler gameHandler, String request)
            throws SquareException, ColorException {

        EffectArgument effectArgument = new EffectArgument();

        target(gameHandler, request)
                .forEach(effectArgument::appendTarget);

        effectArgument.setDestination(
                destination(gameHandler, request));

        return effectArgument;
    }

    static Color paymentCube(String line) throws ColorException {

        int startPar;
        int endPar;

        if (!line.contains(AMMOCOLOR)) {

            return null;
        }

        line = line
                .substring(line.indexOf(AMMOCOLOR), line.indexOf(")", line.indexOf(AMMOCOLOR)) + 1);
        line = line.replaceAll("paga(\\s*)", "paga");

        startPar = line.indexOf(AMMOCOLOR) + AMMOCOLOR.length();
        endPar = line.indexOf(")");

        line = line.substring(startPar + 1, endPar).trim();

        if (line.equals("") || line.equals(" ")) {

            throw new ColorException("Seleziona un colore valido.");
        }

        return Color.ofName(line);
    }

    static int cardId(String request) throws CardException, NumberFormatException {

        int startPar;
        int endPar;

        String cardLine = request.substring(request.indexOf(CARD_ID),
                request.indexOf(")", request.indexOf(CARD_ID)) + 1);
        cardLine = cardLine.replaceAll("id(\\s*)", "id");

        startPar = cardLine.indexOf(CARD_ID) + CARD_ID.length();
        endPar = cardLine.indexOf(")");

        cardLine = cardLine.substring(startPar + 1, endPar).trim();

        if (request.equals(" ") || request.equals("")) {

            throw new CardException("Seleziona un id valido.");
        }

        return Integer.parseInt(cardLine);
    }

    static List<Target> target(GameHandler gameHandler, String request)
            throws SquareException, ColorException {

        List<Target> targetList = new ArrayList<>();

        if (!request.contains(TARGET)) {

            return targetList;
        }

        try {

            int startPar;
            int endPar;

            String targetLine = request.substring(request.indexOf(TARGET),
                    request.indexOf(")", request.indexOf(TARGET)) + 1);
            targetLine = targetLine.replaceAll("target(\\s*)", "target");

            startPar = targetLine.indexOf(TARGET) + TARGET.length();
            endPar = targetLine.indexOf(")");

            targetLine = targetLine.substring(startPar + 1, endPar).trim();

            if (targetLine.equals(" ") || targetLine.equals("")) {

                return targetList;
            }

            String[] args = targetLine.split(" ");

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

        if (!request.contains(DESTINAZIONE)) {

            return null;
        }

        try {

            int startPar;
            int endPar;

            String destinationLine = request
                    .substring(request.indexOf(DESTINAZIONE),
                            request.indexOf(")", request.indexOf(DESTINAZIONE)) + 1);
            destinationLine = destinationLine.replaceAll("destinazione(\\s*)", "destinazione");

            startPar = destinationLine.indexOf(DESTINAZIONE) + DESTINAZIONE.length();
            endPar = destinationLine.indexOf(")");

            destinationLine = destinationLine.substring(startPar + 1, endPar).trim();

            if (destinationLine.equals(" ") || destinationLine.equals("")) {

                return null;
            }

            if (destinationLine.contains("-")) {

                String[] square = destinationLine.split("-");

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

        if (!request.contains(POWER_UP)) {

            return list;
        }

        try {

            int startPar;
            int endPar;

            String powerUpsLine = request
                    .substring(request.indexOf(POWER_UP),
                            request.indexOf(")", request.indexOf(POWER_UP)) + 1);
            powerUpsLine = powerUpsLine.replaceAll("powerup(\\s*)", "powerup");

            startPar = powerUpsLine.indexOf(POWER_UP) + POWER_UP.length();
            endPar = powerUpsLine.indexOf(")");

            powerUpsLine = powerUpsLine.substring(startPar + 1, endPar).trim();

            if (powerUpsLine.equals(" ") || powerUpsLine.equals("")) {

                return list;
            }

            String[] args = powerUpsLine.split(" ");

            for (String x : args) {

                if (x.contains("-")) {

                    String[] square = x.split("-");

                    if (square.length != 2) {

                        throw new CardException("Istruzione non valida");
                    }

                    Color color = Color.ofName(square[1]);

                    list.add(player.findPowerUp(square[0], color));

                } else {

                    throw new CardException("Istruzione non valida");
                }
            }
        } catch (NumberFormatException e) {

            throw new SquareException("Il numero del quadrato che hai selezionato non esiste.");
        }

        return list;
    }

    //  tipo(cose) target(cose) destinazione(cose) powerup(cose)

    static EffectType effectType(String line) throws EffectException {

        int startPar;
        int endPar;

        line = line.substring(line.indexOf(TIPO), line.indexOf(")", line.indexOf(TIPO)) + 1);
        line = line.replaceAll("tipo(\\s*)", "tipo");

        startPar = line.indexOf(TIPO) + TIPO.length();
        endPar = line.indexOf(")");

        line = line.substring(startPar + 1, endPar).trim();

        return EffectType.ofName(line);

    }
}
