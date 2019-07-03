package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;

class EffectParser {

    private EffectParser() {

        //
    }

    /**
     * A final string used to search in the string the target the user wants to shoot.
     */
    private static final String TARGET = "target";

    /**
     * A final string used to search in the string the destination in which the user wants send his
     * targets.
     */
    private static final String DESTINAZIONE = "destinazione";

    /**
     * A final string used to search in the string the power ups the user wants to use.
     */
    private static final String POWER_UP = "powerup";

    /**
     * A final string used to search in the string the ammo cube the user wants to discard when
     * using the power up that needs it.
     */
    private static final String AMMOCOLOR = "paga";

    /**
     * Method used to convert the user's string into an effective EffectArgument by calling his
     * auxiliary methods.
     *
     * @param gameHandler This method needs the GmeHandler because it uses some methods of the model
     * to search some information.
     * @param request The string sent by the user.
     * @return An EffectArgument based on what the user asked.
     */
    static EffectArgument effectArgument(GameHandler gameHandler, String request)
            throws SquareException, ColorException {

        EffectArgument effectArgument = new EffectArgument();

        target(gameHandler, request)
                .forEach(effectArgument::appendTarget);

        effectArgument.setDestination(
                destination(gameHandler, request));

        return effectArgument;
    }

    /**
     * Method that inspects the users's String in order to find which ammo cube the user wants to
     * discard when using the power up that needs it.
     */
    static Color paymentCube(String line) throws ColorException {

        int startPar;
        int endPar;

        if (!line.contains(AMMOCOLOR)) {

            return null;
        }

        line = line
                .substring(line.indexOf(AMMOCOLOR), line.indexOf(')', line.indexOf(AMMOCOLOR)) + 1);
        line = line.replaceAll("paga(\\s*)", "paga");

        startPar = line.indexOf(AMMOCOLOR) + AMMOCOLOR.length();
        endPar = line.indexOf(')');

        line = line.substring(startPar + 1, endPar).trim();

        if (line.equals("") || line.equals(" ")) {

            throw new ColorException("Seleziona un colore valido.");
        }

        return Color.ofName(line);
    }

    /**
     * Method that inspects the users's String in order to find the targets the user wants to
     * shoot.
     *
     * @param gameHandler This method needs the GmeHandler because it uses some methods of the model
     * to search some information.
     * @param request The string sent by the user.
     * @return The list of targets found.
     */
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
                    request.indexOf(')', request.indexOf(TARGET)) + 1);
            targetLine = targetLine.replaceAll("target(\\s*)", TARGET);

            startPar = targetLine.indexOf(TARGET) + TARGET.length();
            endPar = targetLine.indexOf(')');

            targetLine = targetLine.substring(startPar + 1, endPar).trim();

            if (targetLine.equals(" ") || targetLine.equals("")) {

                return targetList;
            }

            String[] args = targetLine.split(" ");

            for (String x : args) {

                Color color = Color.ofName(x);

                if (x.contains("-")) {

                    String[] square = x.split("-");

                    if (square.length != 2) {

                        throw new SquareException("Istruzione non valida");
                    }

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

    /**
     * Method that inspects the users's String in order to find the destination in which the user
     * wants to send the targets.
     *
     * @param gameHandler This method needs the GameHandler because it uses some methods of the
     * model to search some information.
     * @param request The string sent by the user.
     * @return The square found.
     */
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
                            request.indexOf(')', request.indexOf(DESTINAZIONE)) + 1);
            destinationLine = destinationLine.replaceAll("destinazione(\\s*)", DESTINAZIONE);

            startPar = destinationLine.indexOf(DESTINAZIONE) + DESTINAZIONE.length();
            endPar = destinationLine.indexOf(')');

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

    /**
     * Method that inspects the users's String in order to find the power ups the user wants to
     * use.
     *
     * @param player This method needs the Player because it uses some methods of Player to search
     * some information.
     * @param request The string sent by the user.
     * @return The list of power ups found.
     */
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
                            request.indexOf(')', request.indexOf(POWER_UP)) + 1);
            powerUpsLine = powerUpsLine.replaceAll("powerup(\\s*)", POWER_UP);

            startPar = powerUpsLine.indexOf(POWER_UP) + POWER_UP.length();
            endPar = powerUpsLine.indexOf(')');

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
}
