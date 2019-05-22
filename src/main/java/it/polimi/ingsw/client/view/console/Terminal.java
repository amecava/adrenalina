package it.polimi.ingsw.client.view.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class Terminal {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static String ttyConfig;

    private static String screen = "";

    private static boolean messages = false;
    private static String[] broadcast = new String[6];
    private static String response = "";

    private static int inputIndex = 0;
    private static String input = "";

    private static int inputLogIndex;
    private static List<String> inputLog = new ArrayList<>();

    private static final Queue<String> queue = new ConcurrentLinkedQueue<>();

    static {

        Arrays.fill(broadcast, "");
    }

    static void addShutDownHook() {

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {

                    try {

                        stty(ttyConfig.trim());

                    } catch (IOException | InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }
                }));
    }

    static void terminalRefresh() {

        try {

            while (Thread.currentThread().isAlive()) {

                println("\033[H\033[2J" + screen);

                if (messages) {

                    Arrays.stream(broadcast).forEach(Terminal::println);
                }

                println(response);

                print(">>> " + ANSI_GREEN + input + ANSI_RESET);

                Thread.sleep(30);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    static void inputReader() {

        try {

            setTerminalCharacterMode();

            String typing = "";

            while (Thread.currentThread().isAlive()) {

                if (System.in.available() != 0) {

                    int c = System.in.read();

                    switch (c) {

                        case 127:

                            if (input.length() - 1 >= 0) {

                                input = input.substring(0, inputIndex - 1)
                                        + input.substring(inputIndex--);
                            }
                            break;

                        case 27:

                            if (System.in.available() != 0 && System.in.read() == 91) {

                                switch (System.in.read()) {

                                    case 65:

                                        if (inputLogIndex == inputLog.size()) {

                                            typing = input;
                                        }

                                        if (inputLogIndex - 1 >= 0) {

                                            input = inputLog.get(inputLogIndex - 1);
                                            inputIndex = input.length();
                                            inputLogIndex--;
                                        }
                                        break;

                                    case 66:

                                        if (inputLogIndex + 1 == inputLog.size()) {

                                            input = typing;
                                            inputIndex = input.length();
                                            inputLogIndex = inputLog.size();

                                        } else if (inputLogIndex + 1 < inputLog.size()) {

                                            input = inputLog.get(inputLogIndex + 1);
                                            inputIndex = input.length();
                                            inputLogIndex++;
                                        }
                                        break;

                                    case 67:

                                        break;

                                    case 68:

                                        break;

                                    default:
                                }
                            }
                            break;

                        case 10:

                            synchronized (queue) {

                                queue.add(input);

                                if (!input.replace(" ", "").equals("")) {

                                    inputLog.add(input);
                                }

                                inputLogIndex = inputLog.size();

                                input = "";
                                inputIndex = 0;

                                queue.notifyAll();
                            }
                            break;

                        default:

                            input = input.substring(0, inputIndex)
                                    + (char) c
                                    + input.substring(inputIndex);
                            inputIndex++;
                    }

                }

            }

        } catch (IOException | InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    static String input() {

        synchronized (queue) {

            while (queue.peek() == null) {

                try {
                    queue.wait();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }

            return queue.remove();
        }
    }

    static void output(String value) {

        screen = screen + value + "\n";
    }

    static void toggleMessages() {

        messages = !messages;
    }

    static void broadcast(String value) {

        value = "BROADCAST: " + value;

        if (broadcast[0] == "") {

            broadcast[0] = value;

        } else if (broadcast[1] == "") {

            broadcast[1] = value;

        } else if (broadcast[2] == "") {

            broadcast[2] = value;

        } else {

            broadcast[0] = broadcast[1];
            broadcast[1] = broadcast[2];
            broadcast[2] = value;
        }
    }

    static void gameBroadcast(String value) {

        value = "GAME: " + value;

        if (broadcast[3] == "") {

            broadcast[3] = value;

        } else if (broadcast[4] == "") {

            broadcast[4] = value;

        } else if (broadcast[5] == "") {

            broadcast[5] = value;

        } else {

            broadcast[3] = broadcast[4];
            broadcast[4] = broadcast[5];
            broadcast[5] = value;
        }
    }

    static void info(String value) {

        response = value;
    }

    static void error(String value) {

        response = ANSI_RED + "ERROR: " + value + ANSI_RESET;
    }

    static void clearScreen() {

        screen = "";
    }

    static void clearResponse() {

        response = "";
    }

    private static void print(String line) {

        System.out.print(line);
    }

    private static void println(String line) {

        System.out.println(line);
    }

    private static void setTerminalCharacterMode() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        stty("-icanon min 1");

        stty("-echo");
    }

    private static String stty(final String args) throws IOException, InterruptedException {

        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {"sh", "-c", cmd});
    }

    private static String exec(final String[] cmd) throws IOException, InterruptedException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {

            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {

            bout.write(c);
        }

        p.waitFor();

        return new String(bout.toByteArray());
    }
}
