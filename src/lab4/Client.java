package lab4;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class Client {

    private Socket socket;
    private ServerSocket sSocket;

    public Client () {}

    public final boolean listen (int port) {
        try {
            sSocket = new ServerSocket(port);
            socket = sSocket.accept();
        }
        catch (IOException e) {
            System.out.println(e);
            return false;
        }
        System.out.println("Listening on port " + port);
        return true;
    }

    public final void connect (String host, int port) {
        try {
            socket = new Socket(host, port);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Connected to " + host + " on port " + port);
    }

    public final void closeSocket () {
        try {
            if (sSocket != null) {
                sSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public final boolean isConnected () {
        return socket.isConnected();
    }

    public final InputStream getInput () {
        if (!socket.isConnected()) {
            System.out.println("Socket is not connected.");
            return null;
        }

        try {
            return socket.getInputStream();
        }
        catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public final OutputStream getOutput () {
        if (!socket.isConnected()) {
            System.out.println("Socket is not connected.");
            return null;
        }

        try {
            return socket.getOutputStream();
        }
        catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
}
