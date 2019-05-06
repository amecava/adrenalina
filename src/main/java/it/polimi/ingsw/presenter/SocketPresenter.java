package it.polimi.ingsw.presenter;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SocketPresenter implements Runnable {

    private Socket socket;

    private Player player;
    private GameHandler currentGame;

    private List<GameHandler> gamesList;

    public SocketPresenter(Socket socket, List<GameHandler> gamesList) {

        this.socket = socket;

        this.gamesList = gamesList;
    }

    public void run() {

        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            while (socket.isConnected()) {

                String line = in.nextLine();

                if (line.equals("disconnetti")) {

                    if (this.currentGame == null) {

                        out.println("Disconnetto client.");
                        out.flush();

                        this.socket.close();
                    } else {

                        out.println("Prima effettua il logout dalla partita corrente.");
                        out.flush();
                    }
                } else if (line.equals("mostra partite disponibili")) {

                    if (this.gamesList.isEmpty()) {

                        out.println("Non ci sono partite disponibili.");
                        out.flush();
                    } else {

                        out.println(this.gamesList.stream()
                                        .map(GameHandler::getGameId)
                                        .collect(Collectors.toList()));
                        out.flush();
                    }

                } else if (line.startsWith("crea partita")) {

                    if (this.gamesList.stream()
                            .anyMatch(x -> x.getGameId().equals(line.substring(13)))) {

                        out.println("La partita esiste già.");
                        out.flush();
                    } else {

                        this.currentGame = new GameHandler(line.substring(13));
                        this.gamesList.add(this.currentGame);

                        out.println("Partita creata.");
                        out.flush();
                    }

                } else if (line.startsWith("seleziona partita")) {

                    try {

                        this.currentGame = this.gamesList.stream()
                                .filter(x -> x.getGameId().equals(line.substring(18)))
                                .findFirst()
                                .orElseThrow(IllegalArgumentException::new);

                        out.println("Partita selezionata.");
                        out.flush();
                    } catch (IllegalArgumentException e) {

                        out.println("La partita selezionata non esiste.");
                        out.flush();
                    }

                } else if (line.equals("mostra giocatori connessi")) {

                    if (this.currentGame == null) {

                        out.println("Prima seleziona una partita.");
                        out.flush();
                    } else {

                        out.println(this.currentGame.getPlayerList().stream()
                                .map(x -> Arrays.asList(x.getPlayerId(), x.getColor()))
                                .collect(Collectors.toList()));
                        out.flush();
                    }

                } else if (line.startsWith("login")) {

                    if (this.player != null) {

                        out.println("Sei già registrato.");
                        out.flush();
                    } else if (this.currentGame == null) {

                        out.println("Prima seleziona una partita.");
                        out.flush();
                    } else {

                        try {

                            this.player = this.currentGame.addPlayer("playerId", "sprog");

                            out.println("Ciao sprog.");
                            out.flush();
                        } catch (LoginException e) {

                            out.println(e.getMessage());
                            out.flush();
                        }
                    }

                } else if (line.equals("logout")) {

                    if (this.currentGame != null) {

                        this.player = null;
                        this.currentGame = null;

                        out.println("Logout da partita corrente effettuato.");
                        out.flush();

                    } else {

                        out.println("Non sei connesso a nessuna partita.");
                        out.flush();
                    }

                } else {

                    out.println("Received: " + line);
                    out.flush();
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}