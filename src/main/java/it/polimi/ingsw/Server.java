package it.polimi.ingsw;

import it.polimi.ingsw.model.GameHandler;
import it.polimi.ingsw.presenter.SocketPresenter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int rmiPort;
    private int socketPort;

    private List<GameHandler> gamesList = new ArrayList<>();

    public Server(int rmiPort, int socketPort) {

        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    public void run() {

        ServerSocket serverSocket;
        ExecutorService executor = Executors.newCachedThreadPool();

        try {

            serverSocket = new ServerSocket(this.socketPort);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        System.out.println("Socket server in attesa di connessioni.");

        while (!serverSocket.isClosed()) {

            try {

                Socket socket = serverSocket.accept();
                executor.submit(new SocketPresenter(socket, this.gamesList));
            } catch (IOException e) {

                executor.shutdown();
            }
        }
    }

    public static void main(String[] args) {

        Server server = new Server(4561, 4562);

        server.run();
    }
}