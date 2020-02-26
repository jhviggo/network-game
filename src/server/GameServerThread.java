package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import game.Player;

public class GameServerThread extends Thread {
    private Socket connectionSocket;

    public GameServerThread(final Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void run() {
        try {
            final BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            final DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            outToClient.writeBytes("Sir lancelot\n");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void registerPlayer(final String name) {

    }
}
