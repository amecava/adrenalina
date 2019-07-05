package it.polimi.ingsw.client.view.console.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is the virtual terminal used to separate input and output streams on the real
 * terminal.
 */
public class Terminal {

    /**
     * Private constructor to hide the public implicit one.
     */
    private Terminal() {

        //
    }

    /**
     * Clear screen escape code constant.
     */
    private static final String ANSI_RESET = "\u001B[0m";
    /**
     * . Red text escape code constant
     */
    private static final String ANSI_RED = "\u001B[31m";
    /**
     * Green text escape code constant.
     */
    private static final String ANSI_GREEN = "\u001B[32m";

    /**
     * Terminal state save to restore it from character mode.
     */
    private static String ttyConfig;

    /**
     * This is the virtual screen of this virtual terminal. All standard outputs are appended to
     * this string.
     */
    private static String screen = "";

    /**
     * Boolean property that defines if broadcast and response messages are shown or not.
     */
    private static boolean messages = false;
    /**
     * These are 6 slots for broadcast and gameBroadcast messages. They're placed below the screen
     * on the virtual terminal.
     */
    private static String[] broadcast = new String[6];
    /**
     * The response message that the server sends to communicate with the client. It's placed below
     * the broadcast's slots on the virtual terminal.
     */
    private static String response = "";

    /**
     * The cursor position on the input string the user is typing.
     */
    private static int inputIndex = 0;
    /**
     * The input string the user is typing.
     */
    private static String input = "";

    /**
     * The index of the input log list element the user is viewing.
     */
    private static int inputLogIndex;
    /**
     * Bash history for the user's inputs.
     */
    private static List<String> inputLog = new ArrayList<>();

    /**
     * This queue is used to send the input strings from the input reader to the input function. The
     * input string typed by the user is placed in this queue when he presses "enter".
     */
    private static final Queue<String> queue = new ConcurrentLinkedQueue<>();

    static {

        Arrays.fill(broadcast, "");
    }

    /**
     * This methods restores the terminal previous state with the previously saved one.
     */
    public static void addShutDownHook() {

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {

                    try {

                        stty(ttyConfig.trim());

                    } catch (IOException | InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }
                }));
    }

    /**
     * This method refreshes the terminal continuously clearing the screen and printing the new
     * received data or the old saved one.
     */
    public static void terminalRefresh() {

        try {

            while (Thread.currentThread().isAlive()) {

                println("\033[H\033[2J" + screen);

                if (messages) {

                    Arrays.stream(broadcast).forEach(Terminal::println);
                    println("Info: " + JsonRegex.getInfo());
                    println("Comandi: " + JsonRegex.getCommands() + "\n");
                }

                println(response);

                print(">>> " + ANSI_GREEN + input + ANSI_RESET);

                Thread.sleep(80);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method parses the single characters typed by the users. When the user presses the arrow
     * keys the inputLog is shown, when the user presses "enter" the previously typed characters are
     * sent to the input function.
     */
    public static void inputReader() {

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

    /**
     * This methods waits for an input from the users and returns it when available.
     *
     * @return The user's input string.
     */
    public static String input() {

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

    /**
     * This method prints the selected string to the virtual screen.
     *
     * @param value The string to print on the virtual screen.
     */
    public static void output(String value) {

        screen = screen + value + "\n";
    }

    /**
     * This methods toggles the broadcast, gameBroadcast and response messages.
     */
    public static void toggleMessages() {

        messages = !messages;
    }

    /**
     * This method finds the first available spot for a new broadcast message.
     *
     * @param value The broadcast message.
     */
    public static void broadcast(String value) {

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

    /**
     * This method finds the first available spot for a new gameBroadcast message.
     *
     * @param value The gameBroadcast message.
     */
    public static void gameBroadcast(String value) {

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

    /**
     * This method prints the server info response on the virtual terminal.
     *
     * @param value The info message.
     */
    public static void info(String value) {

        response = value;
    }

    /**
     * This method prints the server error response on the virtual terminal.
     *
     * @param value The error message.
     */
    public static void error(String value) {

        response = ANSI_RED + "ERROR: " + value + ANSI_RESET;
    }

    /**
     * This method clears the virtual screen.
     */
    public static void clearScreen() {

        screen = "";
    }

    /**
     * This method clears the response slot.
     */
    public static void clearResponse() {

        response = "";
    }

    /**
     * This method is used to print on the real terminal.
     *
     * @param line The line to print.
     */
    private static void print(String line) {

        System.out.print(line);
    }

    /**
     * This method is used to print a new line on the real terminal.
     *
     * @param line The line to println.
     */
    private static void println(String line) {

        System.out.println(line);
    }

    /**
     * This method sets the real terminal in character mode.
     *
     * @throws IOException If an input or output problem occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
    private static void setTerminalCharacterMode() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        stty("-icanon min 1");

        stty("-echo");
    }

    /**
     * This method configures and executes the stty command.
     *
     * @param args The stty arguments.
     * @return The terminal output for the execution.
     * @throws IOException If an input or output problem occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
    private static String stty(final String args) throws IOException, InterruptedException {

        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[]{"sh", "-c", cmd});
    }

    /**
     * This method executes commands on the real terminal.
     *
     * @param cmd The command to be executed.
     * @return The terminal output for the execution.
     * @throws IOException If an input or output problem occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
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
