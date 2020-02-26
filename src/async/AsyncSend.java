package async;

import interfaces.GameThread;
import server.GameServer;

import java.io.BufferedReader;
import java.net.Socket;

public class AsyncSend extends Thread {

    Socket socket;
    GameThread thread;
    BufferedReader br;

    public AsyncSend(Socket socket, GameThread thread, BufferedReader br) {
        this.socket = socket;
        this.thread = thread;
        this.br = br;
    }

    public void run() {
        try {
            while (socket.isConnected()) {
                String message = br.readLine();
                GameServer.relay(thread, message);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
