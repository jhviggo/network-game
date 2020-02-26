package async;

import interfaces.GameThread;
import server.GameServer;

import java.io.BufferedReader;
import java.net.Socket;

public class AsyncReceive extends Thread{

    Socket socket;
    GameThread thread;
    BufferedReader br;


    public AsyncReceive(Socket socket, GameThread thread, BufferedReader br) {
        this.socket = socket;
        this.thread = thread;
        this.br = br;
    }

    public void run() {
        try {
            while(true) {
                String message = br.readLine();
                if (message == null) {
                    GameServer.closeThread(thread);
                    socket.close();
                    break;
                }
                System.out.println(message);
                GameServer.relay(thread, message);
            }
            System.out.println("Connection terminated");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
