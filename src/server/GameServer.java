package server;

import interfaces.GameThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GameServer {
    private static final int PORT = 1337;
    private static boolean SERVER_RUNNING = true;
    private static Set<GameServerThread> connections = new HashSet<>();

    public static void main(final String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(PORT);
        System.out.println("Server running on port: [" + PORT + "]");

        while (SERVER_RUNNING) {
            final Socket connectionSocket = socket.accept();
            System.out.println("Connection from ["
                    + socket.getInetAddress().getHostAddress()
                    + "]");
             GameServerThread connection = new GameServerThread(connectionSocket);
             connection.start();
             connections.add(connection);
        }

        if (socket != null) {
            socket.close();
        }
    }

    public static void closeThread(GameThread thread) {
        connections.remove(thread);
    }

    public static void relay(GameThread connection, String message) {
        for (GameServerThread thread : connections) {
            if (thread != connection && thread.getSocket().isConnected()) {
                try {
                    thread.send(message);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
