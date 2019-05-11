package it.polimi.ingsw;

import it.polimi.ingsw.view.ConsoleView;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.connection.Connection;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private String ip;

    private int rmiPort;
    private int socketPort;

    private View view = new ConsoleView();

    private static final Logger LOGGER = Logger.getLogger(

            Thread.currentThread().getStackTrace()[0].getClassName()
    );

    private Client(String ip, int rmiPort, int socketPort) {

        this.ip = ip;

        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    private void start() throws InterruptedException {

        this.view.welcomeScreen();

        this.view.selectConnection(this.ip, this.rmiPort, this.socketPort).connect();
    }

    private static InetAddress discoverServer(int port) throws IOException {

        try (DatagramSocket socket = new DatagramSocket()) {

            socket.setBroadcast(true);

            byte[] out = "DISCOVER_ADRENALINA_REQUEST".getBytes();

            socket.send(
                    new DatagramPacket(out, out.length, InetAddress.getByName("255.255.255.255"),
                            port));

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {

                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isLoopback() && networkInterface.isUp()) {

                    for (InterfaceAddress interfaceAddress : networkInterface
                            .getInterfaceAddresses()) {

                        InetAddress broadcast = interfaceAddress.getBroadcast();

                        if (broadcast != null) {

                            socket.send(new DatagramPacket(out, out.length, broadcast, port));
                        }
                    }
                }
            }

            byte[] in = new byte[15000];
            DatagramPacket packet = new DatagramPacket(in, in.length);
            socket.receive(packet);

            String message = new String(packet.getData()).trim();

            if (message.equals("DISCOVER_ADRENALINA_RESPONSE")) {

                return packet.getAddress();
            }

            return InetAddress.getByName("127.0.0.1");
        }
    }

    public static void main(String[] args) {

        try {

            InetAddress server = Client.discoverServer(4560);

            Client client = new Client(server.getHostAddress(), 4561, 4562);

            client.start();

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, "Server connection exception.", e);
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}