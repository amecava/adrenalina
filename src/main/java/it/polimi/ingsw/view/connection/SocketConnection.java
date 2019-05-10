package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.view.View;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;

public class SocketConnection implements Runnable {

    private String ip;
    private int port;

    private View view;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public SocketConnection(String ip, int port, View view) {

        this.ip = ip;
        this.port = port;

        this.view = view;
    }

    @Override
    public void run() {

        try (Socket socket = new Socket(this.ip, this.port);
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            LOGGER.log(Level.INFO, "Connected to socket server.");

            /*
            Thread ping = new Thread(() -> {

                while (Thread.currentThread().isAlive()) {

                    try {

                        Thread.sleep(1000);

                        out.println(Json.createObjectBuilder().add("method", "ping").add("value", "ping").build());
                        out.flush();

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }
                }
            });

            ping.setDaemon(true);
            ping.start();
            */

            Thread input = new Thread(() -> {

                while (in.hasNext()) {

                    try {

                        this.view.serverInteraction(Json.createReader(new StringReader(in.nextLine())).readObject());

                    } catch (RemoteException e) {

                        LOGGER.log(Level.SEVERE, "Reflective operation exception.", e);
                    }
                }

                Thread.currentThread().interrupt();
            });

            Thread output = new Thread(() -> {

                while (Thread.currentThread().isAlive()) {

                    try {

                        out.println(this.view.userInteraction());
                        out.flush();

                    } catch (RemoteException e) {

                        LOGGER.log(Level.SEVERE, "Remote exception.", e);
                    }
                }
            });

            output.setDaemon(true);

            input.start();
            output.start();

            input.join();

        } catch (UnknownHostException e) {

            LOGGER.log(Level.SEVERE, "Socket connection exception.", e);

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "I/O exception for the socket connection.", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
