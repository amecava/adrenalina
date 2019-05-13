package it.polimi.ingsw.presenter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
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

    private Queue<JsonObject> data = new ArrayDeque<>();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    public SocketPresenter(Socket socket) throws IOException {

        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());

        this.setPlayerId("Socket client");
    }

    @Override
    public void disconnectPresenter() {

        this.out.close();
    }

    @Override
    public synchronized void callRemoteMethod(String method, String value) throws RemoteException {

        try {

            this.out.println(this.jsonSerialize(method, value));
            this.out.flush();

            wait(1000);

            JsonObject object = data.remove();

            if (!object.getString("method").equals("pong")) {

                data.add(object);

                notifyAll();
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

        Thread input = new Thread(() -> {

            try {

                while (Thread.currentThread().isAlive()) {

                    JsonObject object = this.jsonDeserialize(this.in.nextLine());

                    synchronized (this) {

                        data.add(object);

                        notifyAll();
                    }
                }
            } catch (NoSuchElementException e) {

                //
            }
        });

        input.setDaemon(true);
        input.start();

        try {

            while (Thread.currentThread().isAlive()) {

                synchronized (this) {

                    while (data.peek() == null) {

                        wait();
                    }

                    JsonObject object = data.remove();

                    if (object.getString("method").equals("pong")) {

                        data.add(object);

                        notifyAll();

                    } else {

                        this.getClass()
                                .getMethod(object.getString("method"), String.class)
                                .invoke(this, object.getString("value"));
                    }
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

    private JsonObject jsonSerialize(String method, String value) {

        return Json.createObjectBuilder()
                .add("method", method)
                .add("value", value)
                .build();
    }

    private JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }
}