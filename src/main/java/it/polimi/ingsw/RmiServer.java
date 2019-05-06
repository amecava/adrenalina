package it.polimi.ingsw;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer implements View {

    public RmiServer() {}

    @Override
    public String printToScreen() {

        return "rmi server test";
    }

    public static void main(String args[]) {

        try {
            RmiServer obj = new RmiServer();
            View stub = (View) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("View", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
