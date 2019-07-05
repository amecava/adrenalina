package it.polimi.ingsw.client.view.connection;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.common.VirtualView;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * This class handles the socket connection between the view and the server.
 */
public class SocketConnection implements Runnable {

    /**
     * The server InetAdress received with the UDP broadcast message.
     */
    private InetAddress inetAddress;
    /**
     * The server socket port received with the UDP broadcast message.
     */
    private int port;

    /**
     * The view that the connection will use to communicate with the user (CLI or GUI).
     */
    private View view;

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * Constructor for the socket connection.
     *
     * @param inetAddress The server InetAdress received with the UDP broadcast message.
     * @param port The server socket port received with the UDP broadcast message.
     * @param view The view that the connection will use to communicate with the user (CLI or GUI).
     */
    public SocketConnection(InetAddress inetAddress, int port, View view) {

        this.inetAddress = inetAddress;
        this.port = port;

        this.view = view;
    }

    /**
     * This method connects to the socket of the server. When the connection is established there is
     * an input thread that receives data from the server and an output thread that sends the view
     * user's input to the server.
     */
    @Override
    public void run() {

        try (Socket socket = new Socket(this.inetAddress.getHostAddress(), this.port);
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            this.view.loginScreen();

            Thread input = new Thread(() -> {

                while (in.hasNext()) {

                    try {

                        JsonObject object = Json.createReader(new StringReader(in.nextLine()))
                                .readObject();

                        synchronized (out) {

                            out.println(this.jsonSerialize("pong", ""));
                            out.flush();
                        }

                        VirtualView.class
                                .getMethod(object.getString("method"), String.class)
                                .invoke(this.view, object.getString("value"));

                        if (object.getString("method").equals("completeDisconnect")) {

                            break;
                        }

                    } catch (NoSuchMethodException e) {

                        LOGGER.log(Level.SEVERE, "Selected method does not exist.", e);

                    } catch (IllegalAccessException e) {

                        LOGGER.log(Level.SEVERE, "Method visibility qualifiers violated.", e);

                    } catch (InvocationTargetException e) {

                        LOGGER.log(Level.SEVERE, "Invocation target exception.", e);

                    }
                }
            });

            Thread output = new Thread(() -> {

                while (Thread.currentThread().isAlive()) {

                    JsonObject object = this.view.userInput();

                    synchronized (out) {

                        out.println(object);
                        out.flush();
                    }
                }
            });

            output.setDaemon(true);

            input.start();
            output.start();

            input.join();

        } catch (UnknownHostException e) {

            LOGGER.log(Level.SEVERE, "Socket server not reachable.", e);

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "I/O exception for the socket connection.", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method creates the JsonObject required for the network communication.
     *
     * @param method The method to be called on the server.
     * @param value The method argument.
     * @return The JsonObject.
     */
    private JsonObject jsonSerialize(String method, String value) {

        return Json.createObjectBuilder()
                .add("method", method)
                .add("value", value)
                .build();
    }
}
