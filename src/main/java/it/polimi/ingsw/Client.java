package it.polimi.ingsw;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {

    private int port;
    private String ip;

    public Client(String ip, int port) {

        this.ip = ip;
        this.port = port;
    }

    public void startClient() {

        Scanner stdin = new Scanner(System.in);

        try (Socket socket = new Socket(ip, port);
                Scanner socketIn = new Scanner(socket.getInputStream());
                PrintWriter socketOut = new PrintWriter(socket.getOutputStream())){

            while (!socket.isClosed()) {

                String inputLine = stdin.nextLine();
                socketOut.println(inputLine);
                socketOut.flush();

                String socketLine = socketIn.nextLine();
                System.out.println(socketLine);

                if (socketLine.equals("Disconnetto client.")) {

                    socket.close();
                }
            }
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        Client client = new Client("127.0.0.1", 4562);

        client.startClient();
    }
}