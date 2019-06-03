package it.polimi.ingsw.server;

import it.polimi.ingsw.server.presenter.ClientHandler;
import it.polimi.ingsw.server.presenter.AccessPoint;
import it.polimi.ingsw.server.presenter.SocketPresenter;
import it.polimi.ingsw.virtual.VirtualAccessPoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

    private static final Thread pingThread = new Thread(Server::pingServer);

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    private static void discoveryServer(int port) {

        try (DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"))) {

            socket.setBroadcast(true);

            while (Thread.currentThread().isAlive()) {

                byte[] buffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_ADRENALINA_REQUEST")) {
                    byte[] out = "DISCOVER_ADRENALINA_RESPONSE".getBytes();

                    socket.send(new DatagramPacket(out, out.length, packet.getAddress(),
                            packet.getPort()));
                }
            }
        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "Discovery server exception.", e);
        }
    }

    private static void rmiServer(int port) {

        LOGGER.log(Level.INFO, "Creating RMI server...");

        try {

            Registry registry = LocateRegistry.createRegistry(port);
            VirtualAccessPoint stub = (VirtualAccessPoint) UnicastRemoteObject
                    .exportObject(new AccessPoint(), 0);

            registry.bind("AccessPoint", stub);

            LOGGER.log(Level.INFO, "RMI server ready. Waiting for connections.");

        } catch (RemoteException | AlreadyBoundException e) {

            LOGGER.log(Level.SEVERE, "RMI server exception.", e);
        }

        if (!pingThread.isAlive()) {

            pingThread.setDaemon(true);
            pingThread.start();
        }
    }

    private static void socketServer(int port) {

        LOGGER.log(Level.INFO, "Creating socket server...");

        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            LOGGER.log(Level.INFO, "Socket server ready. Waiting for connections.");

            if (!pingThread.isAlive()) {

                pingThread.setDaemon(true);
                pingThread.start();
            }

            while (serverSocket.isBound()) {

                Socket socket = serverSocket.accept();

                executor.submit(new SocketPresenter(socket));
            }
        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "Socket server exception.", e);
        }
    }

    private static void pingServer() {

        while (Thread.currentThread().isAlive()) {

            try {

                Thread.sleep(5000);

                ClientHandler.broadcast(x -> true, "isConnected", "ping");


            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {

        try {

            Runtime.getRuntime().addShutdownHook(

                    new Thread(() -> ClientHandler.save("data.ser"))
            );

            LOGGER.log(Level.INFO, "Creating server...");

            System.setProperty("java.rmi.server.hostname",
                    InetAddress.getLocalHost().getHostAddress());

            Thread discovery = new Thread(() -> Server.discoveryServer(4560));

            discovery.setDaemon(true);
            discovery.start();

            ClientHandler.load("data.ser");

            new Thread(() -> Server.rmiServer(4561)).start();
            new Thread(() -> Server.socketServer(4562)).start();


        } catch (UnknownHostException e) {

            LOGGER.log(Level.SEVERE, "Unknown host InetAdress exception.", e);
        }
    }
}