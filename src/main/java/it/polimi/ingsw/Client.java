package it.polimi.ingsw;

import it.polimi.ingsw.view.ConsoleView;
import it.polimi.ingsw.view.View;
import java.rmi.RemoteException;

public class Client {

    private String ip;

    private int rmiPort;
    private int socketPort;

    private View view = new ConsoleView();

    private Client(String ip, int rmiPort, int socketPort) {

        this.ip = ip;

        this.rmiPort = rmiPort;
        this.socketPort = socketPort;
    }

    private void start() throws RemoteException {

        this.view.welcomeScreen();

        this.view.connect(this.ip, this.rmiPort, this.socketPort).run();
    }

    public static void main(String[] args) {

        Client client = new Client("127.0.0.1", 4561, 4562);

        try {

            client.start();
        } catch (RemoteException e) {

            //
        }
    }
}