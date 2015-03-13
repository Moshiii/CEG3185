package lab4;

import java.io.*;
import java.util.Scanner;

public final class ClientUI {

    private static final String REQUEST_TO_SEND = "RTS";
    private static final String CLEAR_TO_SEND = "CTS";
    private static final String ACKNOWLEDGED = "ACK";
    private static final String QUIT = "QUIT";
    private static final int DEFAULT_PORT = 5555;
    private static final String DEFAULT_HOST = "localhost";

    private final Client client = new Client();
    private final Scanner userInput = new Scanner (System.in);
    private final Encoder encoder = Encoder.getInstance(Encoder.HDB3);

    private String host = null;
    private int port = 0;

    public ClientUI (String host, int port) {
        if (host != null) {
            this.host = host;
        }
        if (port != 0) {
            this.port = port;
        }
    }

    public final void mainMenu () {
        while (true) {
            char answer;
            do {
                System.out.println("1) Act as encoding program\n" +
                        "2) Act as decoding program\n" +
                        "3) Quit");
                answer = userInput.next().charAt(0);
            }
            while ("123".indexOf(answer) == -1);

            if (answer == '1') {
                String host;
                if (this.host == null) {
                    host = DEFAULT_HOST;
                }
                else {
                    host = this.host;
                }
                int port;
                if (this.port == 0) {
                    port = DEFAULT_PORT;
                }
                else {
                    port = this.port;
                }
                client.connect(host, port);
                encodingProgram();
            } else if (answer == '2') {
                int port;
                if (this.port == 0) {
                    port = DEFAULT_PORT;
                }
                else {
                    port = this.port;
                }
                client.listen(port);
                decodingProgram();
            } else if (answer == '3') {
                return;
            }
        }
    }

    private final void encodingProgram () {
        DataInputStream input = new DataInputStream(client.getInput());
        PrintStream output = new PrintStream(client.getOutput());
        while (true) {
            System.out.println("Enter the binary message you would like to send (or quit to return to the menu):");
            String msg;
            do {
                msg = userInput.next();
                if (msg.equals("quit")) {
                    output.println(QUIT);
                    client.closeSocket();
                    return;
                }
            }
            while (!encoder.checkBinaryValidity(msg));
            output.println(REQUEST_TO_SEND);
            while (client.isConnected()) {
                try {
                    String resp = input.readLine();
                    System.out.println("Received: " + resp);
                    if (resp.equals(CLEAR_TO_SEND)) {
                        output.println(encoder.encode(msg));
                    }
                    else if (resp.equals(ACKNOWLEDGED)) {
                        break;
                    }
                }
                catch (IOException e) {
                    System.out.println(e);
                    client.closeSocket();
                    return;
                }
            }
        }
    }

    private final void decodingProgram () {
        DataInputStream input = new DataInputStream(client.getInput());
        PrintWriter output = new PrintWriter(client.getOutput());
        boolean cts = false;
        while (client.isConnected()) {
            try {
                String resp = input.readLine();
                System.out.println("Received: " + resp);
                if (resp.equals(QUIT)) {
                    client.closeSocket();
                    return;
                }
                else if (resp.equals(REQUEST_TO_SEND)) {
                    cts = true;
                    output.println(CLEAR_TO_SEND);
                    output.flush();
                }
                else if (cts && encoder.checkEncodedValidity(resp)) {
                    output.println(ACKNOWLEDGED);
                    output.flush();
                    System.out.println("Decoded: " + encoder.decode(resp));
                    cts = false;
                }
            }
            catch (IOException e) {
                System.out.println(e);
                client.closeSocket();
                return;
            }
        }
    }

    /**
     * Runs the ClientUI, which supports both listener (decoder) and sender (encoder) functionality.
     *
     * @param args Takes either 0 or 2 args. If 0 args, host and port are default (localhost and
     *             5555, respectively). If 2 args, the first is the host name and the second is the
     *             port.
     */
    public static void main (String[] args) {
        ClientUI x;
        if (args.length == 2) {
            x = new ClientUI(args[0], Integer.parseInt(args[1]));
        }
        else {
            x = new ClientUI(null, 0);
        }
        x.mainMenu();
    }
}
