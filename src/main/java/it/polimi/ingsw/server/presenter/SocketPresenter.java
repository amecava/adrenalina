package it.polimi.ingsw.server.presenter;

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

public class SocketPresenter extends Presenter implements Runnable {

    private Scanner in;
    private PrintWriter out;

    private final Queue<JsonObject> data = new ArrayDeque<>();
    private final Queue<JsonObject> ping = new ArrayDeque<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public SocketPresenter(Socket socket) throws IOException {

        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void disconnectPresenter() {

        this.out.close();
    }

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

    private void inputStream() {

        try {

            while (Thread.currentThread().isAlive()) {

                JsonObject object = this.jsonDeserialize(this.in.nextLine());

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