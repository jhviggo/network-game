package server;

import interfaces.GameThread;
import interfaces.RecieverHandle;

import java.util.Arrays;

public class GameServerHandle implements RecieverHandle {
    @Override
    public void close(GameThread thread) {
        GameServer.closeThread(thread);
    }

    @Override
    public void action(GameThread thread, String message) {
        String[] messageItems = message.split(" ");
        if(Arrays.stream(messageItems).anyMatch(item -> Commands.contains(item))) {
            GameServer.relay(thread, message);
        }
    }
}
