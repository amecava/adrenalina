package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.common.JsonUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Socket extension of the Presenter (it implements ita abstract methods):
 * The presenter of the Model - Passive View - Presenter
 * pattern. This class is responsible of furthering the correct requests by calling the correct
 * methods of the model (through the GameHandler) and by preparing the proper JsonObjects for the
 * View. Every method of this class not only performs the action but also sends the proper
 * notification to the client.
 */
public class SocketPresenter extends Presenter implements Runnable {

    /**
     * The Scanner for the input stream.
     */
    private Scanner in;

    /**
     * The PrintWriter for the output stream.
     */
    private PrintWriter out;

    /**
     * A queue for the game messages, not the ping/pong messages that say if a client is still
     * connected.
     */
    private final Queue<JsonObject> data = new ArrayDeque<>();

    /**
     * A queue for the ping/pong messages that say if a client is still connected.
     */
    private final Queue<JsonObject> ping = new ArrayDeque<>();

    /**
     * A Logger that prints updates on the server.
     */
    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    /**
     * Creates the SocketPresenter by initializing the input/output streams.
     *
     * @param socket The Socket used for the connection
     * @throws IOException Thrown by the Socket class.
     */
    public SocketPresenter(Socket socket) throws IOException {

        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Disconnects the Presenter by closing the output stream.
     */
    @Override
    public void disconnectPresenter() {

        this.out.close();
    }

    /**
     * Uses the output stream to call the method "method" on the object exported by the client, with
     * the parameter "value". Practical example: if method = "updateBoard", the method "updateBoard"
     * will be called on the client's View, and the board of that client will be updated.
     *
     * @param method The name of the method that will be called on the View.
     * @param value The value that will be given as a parameter to "method" method.
     */
    @Override
    public void callRemoteMethod(String method, String value) throws RemoteException {

        try {

            synchronized (this.ping) {

                this.out.println(Json.createObjectBuilder()
                        .add("method", method).add("value", value).build());
                this.out.flush();

                this.ping.wait(1000);

                this.ping.remove();
            }

        } catch (NoSuchElementException e) {

            throw new RemoteException();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    /**
     * Runs the Thread of the server.
     */
    @Override
    public void run() {

        ClientHandler.addClient(this);

        Thread input = new Thread(this::inputStream);

        input.setDaemon(true);
        input.start();

        try {

            while (Thread.currentThread().isAlive()) {

                synchronized (this.data) {

                    while (this.data.peek() == null) {

                        this.data.wait();
                    }

                    JsonObject object = this.data.remove();

                    this.getClass()
                            .getMethod(object.getString("method"), String.class)
                            .invoke(this, object.toString());
                }
            }

        } catch (NoSuchMethodException e) {

            LOGGER.log(Level.SEVERE, "Selected method does not exist.", e);

        } catch (IllegalAccessException e) {

            LOGGER.log(Level.SEVERE, "Method visibility qualifiers violated.", e);

        } catch (InvocationTargetException e) {

            LOGGER.log(Level.SEVERE, "Invocation target exception.", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reads the data in the input stream and sorts the messages in the correct queue.
     */
    private void inputStream() {

        try {

            while (Thread.currentThread().isAlive()) {

                JsonObject object = JsonUtility.jsonDeserialize(this.in.nextLine());

                synchronized (this) {

                    if (!object.getString("method").equals("pong")) {

                        synchronized (data) {

                            data.add(object);

                            data.notifyAll();
                        }

                    } else {

                        synchronized (ping) {

                            ping.add(object);

                            ping.notifyAll();
                        }
                    }
                }
            }
        } catch (NoSuchElementException e) {

            //
        }
    }
}