package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.view.View;
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

public class SocketConnection implements Runnable {

    private InetAddress inetAddress;
    private int port;

    private View view;

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public SocketConnection(InetAddress inetAddress, int port, View view) {

        this.inetAddress = inetAddress;
        this.port = port;

        this.view = view;
    }

    @Override
    public void run() {

        try (Socket socket = new Socket(this.inetAddress.getHostAddress(), this.port);
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            this.view.splashScreen();
            this.view.loginScreen();

            Thread input = new Thread(() -> {

                while (in.hasNext()) {

                    try {

                        JsonObject object = Json.createReader(new StringReader(in.nextLine()))
                                .readObject();

                        out.println(this.jsonSerialize("pong", ""));
                        out.flush();

                        this.view.getClass()
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

                    out.println(this.view.userInput());
                    out.flush();
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

    private JsonObject jsonSerialize(String method, String value) {

        return Json.createObjectBuilder()
                .add("method", method)
                .add("value", value)
                .build();
    }
}
