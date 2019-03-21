package it.polimi.ingsw;

import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(
            Thread.currentThread().getStackTrace()[0].getClassName());

    public static void main(String[] args) {

        LOGGER.info("Hello, World!");
        LOGGER.info("Hello, World from jacopo!");

    }
}
