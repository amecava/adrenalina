package it.polimi.ingsw;

import it.polimi.ingsw.presenter.ClientHandler;
import it.polimi.ingsw.presenter.AccessPoint;
import it.polimi.ingsw.presenter.SocketPresenter;
import it.polimi.ingsw.view.virtual.VirtualAccessPoint;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private int rmiPort;
    private int socketPort;

    private ClientHandler clientHandler = new ClientHandler();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    private Server(int rmiPort, int socketPort) {

        LOGGER.log(Level.INFO, "Creating server...");

        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    private void start() {

        Thread ping = new Thread(() -> {

            while (Thread.currentThread().isAlive()) {

                try {

                    Thread.sleep(5000);

                    this.clientHandler.removeDisconnected();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }
        });

        ping.setDaemon(true);
        ping.start();

        new Thread(() -> {

            LOGGER.log(Level.INFO, "Creating RMI server...");

            try {

                System.setProperty("java.rmi.server.hostname", "192.168.43.105");

                Registry registry = LocateRegistry.createRegistry(this.rmiPort);
                VirtualAccessPoint stub = (VirtualAccessPoint) UnicastRemoteObject
                        .exportObject(new AccessPoint(this.clientHandler), 0);

                registry.bind("AccessPoint", stub);

                LOGGER.log(Level.INFO, "RMI server ready. Waiting for connections.");

            } catch (RemoteException | AlreadyBoundException e) {

                LOGGER.log(Level.SEVERE, "RMI server exception.", e);
            }
        }).start();

        new Thread(() -> {

            LOGGER.log(Level.INFO, "Creating socket server...");

            ExecutorService executor = Executors.newCachedThreadPool();

            try (ServerSocket serverSocket = new ServerSocket(this.socketPort)) {

                LOGGER.log(Level.INFO, "Socket server ready. Waiting for connections.");

                while (serverSocket.isBound()) {

                    Socket socket = serverSocket.accept();

                    executor.submit(new SocketPresenter(socket, this.clientHandler));
                }
            } catch (IOException e) {

                LOGGER.log(Level.SEVERE, "Socket server exception!", e);
            }
        }).start();
    }

    public static void main(String[] args) {

        Server server = new Server(4561, 4562);

        server.start();
    }
}