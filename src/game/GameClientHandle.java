package game;

import interfaces.GameThread;
import interfaces.RecieverHandle;

public class GameClientHandle implements RecieverHandle {
    private GameClient gameClient;

    public GameClientHandle(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void close(GameThread thread) {
        // NOOP
    }

    @Override
    public void action(GameThread thread, String message) {
        gameClient.interpretCommand(message);
    }
}
