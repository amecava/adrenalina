package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.console.ConsoleView;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.GUIView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * This is the main class of the client.
 */
public class Client {

    /**
     * The port to send the UDP broadcast messages to. Needed to retain the server IP address.
     */
    private int discoveryPort;
    /**
     * The RMI server port to establish the RMI connection.
     */
    private int rmiPort;
    /**
     * The socket server port to establish the socket connection.
     */
    private int socketPort;

    /**
     * The view, it can be a ConsoleView or a GUIView.
     */
    private View view;

    /**
     * The Client constructor method.
     *
     * @param discoveryPort The port to send the UDP broadcast messages to. Needed to retain the
     * server IP address.
     * @param rmiPort The RMI server port to establish the RMI connection.
     * @param socketPort The socket server port to establish the socket connection.
     */
    private Client(int discoveryPort, int rmiPort, int socketPort) {

        this.discoveryPort = discoveryPort;
        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    /**
     * This method parses the main args and starts the ConsoleView or the GUIView. The initialScreen
     * is then presented to the user.
     *
     * @param args The main method args.
     */
    private void start(String[] args) {

        if (Arrays.stream(args).anyMatch("c"::equals)) {

            this.view = new ConsoleView();

        } else {

            this.view = new GUIView();

            new Thread(() -> Client.launch(args)).start();
        }

        this.view.initialScreen(this.discoveryPort, this.rmiPort, this.socketPort);

        synchronized (View.connection) {

            while (View.connection.peek() == null) {

                try {

                    View.connection.wait();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }
        }

        View.connection.remove().run();
    }

    /**
     * This method sends UDP broadcast messages to the discoveryPort until the server is found.
     *
     * @param port The port to send the UDP broadcast messages to. Needed to retain the server IP
     * address.
     * @return The found server InetAddress.
     * @throws IOException If the connection is broken.
     */
    public static InetAddress discoverServer(int port) throws IOException {

        InetAddress inetAddress = null;

        while (inetAddress == null) {

            try (DatagramSocket socket = new DatagramSocket()) {

                socket.setBroadcast(true);
                socket.setSoTimeout(5000);

                byte[] out = "DISCOVER_ADRENALINA_REQUEST".getBytes();

                socket.send(new DatagramPacket(out, out.length,
                        InetAddress.getByName("255.255.255.255"), port));

                byte[] in = new byte[15000];
                DatagramPacket packet = new DatagramPacket(in, in.length);
                socket.receive(packet);

                String message = new String(packet.getData()).trim();

                if (message.equals("DISCOVER_ADRENALINA_RESPONSE")) {

                    inetAddress = packet.getAddress();
                }

            } catch (SocketTimeoutException e) {

                //
            }
        }

        return inetAddress;
    }

    /**
     * This method sets the GUI preloader and launches the GUIView.
     *
     * @param args The main method args.
     */
    private static void launch(String[] args) {

        System.setProperty("javafx.preloader", "it.polimi.ingsw.client.view.gui.GUIPreloader");
        GUIView.launch(GUIView.class, args);
    }

    /**
     * This is the main method of the Client class.
     *
     * @param args The main method args.
     */
    public static void main(String[] args) {

        Client client = new Client(4560, 4561, 4562);

        client.start(args);
    }
}