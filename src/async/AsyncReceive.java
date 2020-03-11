package async;

import interfaces.GameThread;
import interfaces.RecieverHandle;
import server.GameServer;

import java.io.BufferedReader;
import java.net.Socket;

public class AsyncReceive extends Thread{

    Socket socket;
    GameThread thread;
    BufferedReader br;
    RecieverHandle handle;

    public AsyncReceive(Socket socket, GameThread thread, BufferedReader br, RecieverHandle handle) {
        this.socket = socket;
        this.thread = thread;
        this.br = br;
        this.handle = handle;
    }

    public void run() {
        try {
            while(true) {
                String message = br.readLine();
                if (message == null) {
                    handle.close(thread);
                    socket.close();
                    break;
                }
                System.out.println("[AsyncReceive] " + message);
                handle.action(thread, message);
            }
            System.out.println("Connection terminated");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
