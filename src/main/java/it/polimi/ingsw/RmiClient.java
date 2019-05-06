package it.polimi.ingsw;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {

    private RmiClient() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            View stub = (View) registry.lookup("View");
            String response = stub.printToScreen();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
