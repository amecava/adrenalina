package it.polimi.ingsw;

import it.polimi.ingsw.view.console.ConsoleView;
import it.polimi.ingsw.view.View;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private InetAddress inetAddress;

    private int rmiPort;
    private int socketPort;

    private View view = new ConsoleView();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    private Client(int discoveryPort, int rmiPort, int socketPort) throws IOException {

        this.view.searchingForServer();

        this.inetAddress = Client.discoverServer(discoveryPort);

        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    private void start() {

        Runnable connection = this.view.selectConnection(this.inetAddress, this.rmiPort, this.socketPort);

        this.view.connectingToServer();

        connection.run();
    }

    private static InetAddress discoverServer(int port) throws IOException {

        InetAddress inetAddress = null;

        while (inetAddress == null) {

            try (DatagramSocket socket = new DatagramSocket()) {

                socket.setBroadcast(true);
                socket.setSoTimeout(5000);

                byte[] out = "DISCOVER_ADRENALINA_REQUEST".getBytes();

                socket.send(new DatagramPacket(out, out.length, InetAddress.getByName("255.255.255.255"), port));

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

    public static void main(String[] args) {

        try {

            Client client = new Client(4560, 4561, 4562);

            client.start();

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "Server not reachable.", e);
        }

    }
}