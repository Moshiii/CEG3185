package lab6.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;


public class Server {
	 //Get Port to run server on
	static int listenPort = Integer.parseInt(JOptionPane.showInputDialog("Enter Port to listen on\n"));
    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(listenPort);
        System.out.println("Server is running on port " + listenPort);
        try {
            while (true) {
                new Decoder(listener.accept(), clientNumber++).start();
                
            }
        } finally {
            listener.close();
        }
    }

    private static class Decoder extends Thread {
        private Socket socket;
        private int clientNumber;

        public Decoder(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the capitalized version of the string.
         */
        
        public String HDB3_decoder(String HDB3Input) {
        	String bin_str = "";
        	String pattern = "(\\+00\\+)(\\-00\\-)";
        	bin_str = HDB3Input.replaceAll("(\\+00\\+)", "0000");
        	bin_str = bin_str.replaceAll("(\\-00\\-)", "0000");
        	bin_str = bin_str.replaceAll("[\\+\\-]", "1");
/*        	String nxt_valid_level = "+";
    		int zero_counter = 0;
    		int one_counter = 0;
        	for (char bit: HDB3Input.toCharArray()) {
        		if (bit == '0') {
        			if (one_counter == 1) {
        				bin_str=bin_str + 1;
        			}
        			bin_str=bin_str + 0;
        		} else if (bit == '+' && nxt_valid_level == "+") {
        			if (one_counter == 1) {
        				bin_str=bin_str + 1;
        			}
        			one_counter = 1;
        			nxt_valid_level = "-";
        		} else if (bit == '-' && nxt_valid_level == "-") {
        			if (one_counter == 1) {
        				bin_str=bin_str + 1;
        			}
        			one_counter = 1;
        			nxt_valid_level = "+";
        		} else if (bit == '-' && nxt_valid_level == "+") {
        			bin_str=bin_str + "00";
        			one_counter = 0;
        			nxt_valid_level = "-";
        		} else if (bit == '+' && nxt_valid_level == "-") {
        			bin_str=bin_str + "00";
        			one_counter = 0;
        			nxt_valid_level = "+";
        		}
        	}
        	if (one_counter == 1) {
				bin_str=bin_str + 1;
			}*/
        	
        	System.out.println(HDB3Input + " encoded in HDB3 is " + bin_str);
        	return bin_str;
        }
        
        public void run() {
            try {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Welcome, you have been assigned client #" + clientNumber + ".");
                out.println("Type quit to end session\n");
                // Get messages from the client, line by line
                while (true) {
                	out.flush();
                	out.write("Send -->");
                	out.flush();
                    String input = in.readLine();
                    if (input == null || input == "quit\n") {
                        break;
                    }
                    out.flush();
                    out.println("Server: RX \"" + input + "\"");
                    out.flush();
                    
                    System.out.println (clientNumber + ": " + input);
                    System.out.println (clientNumber + ": Decoded " + HDB3_decoder(input));
                }
                
            } catch (IOException e) {
                System.out.println("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket!");
                }
                System.out.println("Connection with client# " + clientNumber + " terminated");
            }
        }


    }
}