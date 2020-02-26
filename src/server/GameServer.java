package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private static final int PORT = 1337;
    private static boolean SERVER_RUNNING = true;

    public static void main(final String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(PORT);
        System.out.println("Server running on port: [" + PORT + "]");

        while (SERVER_RUNNING) {
            final Socket connectionSocket = socket.accept();
            System.out.println("Connection from ["
                    + socket.getInetAddress().getHostAddress()
                    + "]");
            (new GameServerThread(connectionSocket)).start();
        }

        if (socket != null) {
            socket.close();
        }
    }
}
