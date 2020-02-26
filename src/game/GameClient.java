package game;

import async.AsyncReceive;
import interfaces.GameThread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameClient extends Thread implements GameThread {
    private final String ServerIP = "127.0.0.1";
    private final int PORT = 1337;

    private Socket clientSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToServer;
    private GameClientHandle gameClientHandle = new GameClientHandle(this);

    private Main gui;

    public GameClient(Main gui) {
        this.gui = gui;
    }

    public void run() {
        try {
            connectToServer();
        } catch (IOException e) {
            System.out.println(e);
        }

        AsyncReceive reciever = new AsyncReceive(clientSocket, this, inFromClient, gameClientHandle);
        reciever.start();
    }

    public void connectToServer() throws IOException {
        clientSocket = new Socket(ServerIP, PORT);
        inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void send(final String message) {
        try {
            outToServer.writeBytes(message + "\n");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String recieve() throws IOException {
        return inFromClient.readLine();
    }

    public void interpretCommand(final String command) {
        final var commands = command.split(" ");

        switch (commands[0]) {
            case "ADDPLAYER":
                gui.addEnemyPlayer(commands[1], Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
                break;
        }
    }
}
