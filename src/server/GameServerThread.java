package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import async.AsyncReceive;
import async.AsyncSend;
import game.Player;
import interfaces.GameThread;

public class GameServerThread extends Thread implements GameThread {
    private Socket connectionSocket;

    public GameServerThread(final Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void run() {
        try {
            AsyncReceive receiver = new AsyncReceive(
                connectionSocket,
                this,
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))
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

    private void registerPlayer(final String name) {

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
}
