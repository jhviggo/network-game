package server;

import game.Player;
import interfaces.GameThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GameServer {
    private static final int PORT = 1337;
    private static boolean SERVER_RUNNING = true;
    private static Set<GameServerThread> connections = new HashSet<>();
    private static GameServerHandle gameServerHandle = new GameServerHandle();
    private static Set<Player> playerList = new HashSet<>();

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

        try {
            for (GameServerThread thread : connections) {
                if (thread.getSocket().isConnected()) {
                    thread.send(message);
                }
            }

            if (messageItems[0].equals("ADDPLAYER")) {
                for (Player player : playerList) {
                    connection.send(messageItems[0] + " " + player.getName() + " " + player.getXpos() + " " + player.getYpos());
                }

                playerList.add(new Player(messageItems[1], Integer.parseInt(messageItems[2]), Integer.parseInt(messageItems[3]), "UP"));
            }
            else if (messageItems[0].equals("MOVE")) {
                for (Player player : playerList) {
                    if (player.getName().equals(messageItems[1])) {
                        player.setXpos(Integer.parseInt(messageItems[2]));
                        player.setYpos(Integer.parseInt(messageItems[3]));
                        player.setDirection(messageItems[4]);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
