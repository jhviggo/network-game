package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameClient {
    private static final String ServerIP = "127.0.0.1";
    private static final int PORT = 1337;

    private static Socket clientSocket;
    private static BufferedReader inFromClient;
    private static DataOutputStream outToServer;

    public static void connectToServer() throws IOException {
        clientSocket = new Socket(ServerIP, PORT);
        inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
    }

    public static void send(final String message) throws IOException {
        outToServer.writeBytes(message + "\n");
    }

    public static String recieve() throws IOException {
        return inFromClient.readLine();
    }
}
