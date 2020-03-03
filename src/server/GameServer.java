package server;

import game.Player;
import interfaces.GameThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameServer {
    private static final int PORT = 1337;
    private static boolean SERVER_RUNNING = true;
    private static Set<GameServerThread> connections = new HashSet<>();
    private static GameServerHandle gameServerHandle = new GameServerHandle();
    private static List<Player> playerList = new ArrayList<>();

    public static void main(final String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(PORT);
        System.out.println("Server running on port: [" + PORT + "]");

        while (SERVER_RUNNING) {
            final Socket connectionSocket = socket.accept();
            System.out.println("Connection from ["
                    + socket.getInetAddress().getHostAddress()
                    + "]");
             GameServerThread connection = new GameServerThread(connectionSocket, gameServerHandle);
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
        String[] messageItems = message.split(" ");

        for (GameServerThread thread : connections) {
            if (thread.getSocket().isConnected()) {
                try {
                    if (messageItems[0] == "ADDPLAYER") {
                        playerList.add(new Player(messageItems[1], Integer.parseInt(messageItems[2]), Integer.parseInt(messageItems[3]), "UP"));
                    }

                    thread.send(message);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
