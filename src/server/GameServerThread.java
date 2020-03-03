package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import async.AsyncReceive;
import async.AsyncSend;
import interfaces.GameThread;
import interfaces.RecieverHandle;

public class GameServerThread extends Thread implements GameThread {
    private Socket connectionSocket;
    private RecieverHandle gameServerHandle;
    private String message;
    private AsyncReceive receiver;

    public GameServerThread(final Socket connectionSocket, GameServerHandle gameServerHandle) {
        this.connectionSocket = connectionSocket;
        this.gameServerHandle = gameServerHandle;
    }

    public void run() {
        try {
            receiver = new AsyncReceive(
                connectionSocket,
                this,
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())),
                gameServerHandle
            );
            AsyncSend sender = new AsyncSend(
                connectionSocket,
                this,
                new BufferedReader(new InputStreamReader(System.in))
            );
            receiver.start();
            sender.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void send(String message) {
        try {
            final DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            outToClient.writeBytes(message + "\n");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public Socket getSocket() {
        return connectionSocket;
    }

    public AsyncReceive getReceiver() { return receiver; }
}
