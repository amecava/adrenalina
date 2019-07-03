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

public class Client {

    private int discoveryPort;
    private int rmiPort;
    private int socketPort;

    private View view;

    private Client(int discoveryPort, int rmiPort, int socketPort) {

        this.discoveryPort = discoveryPort;
        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

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

    private static void launch(String[] args) {

        System.setProperty("javafx.preloader", "it.polimi.ingsw.client.view.gui.GUIPreloader");
        GUIView.launch(GUIView.class, args);
    }

    public static void main(String[] args) {

        Client client = new Client(4560, 4561, 4562);

        client.start(args);
    }
}