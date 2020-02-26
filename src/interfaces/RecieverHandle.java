package interfaces;

public interface RecieverHandle {
    public void close(GameThread thread);

    public void action(GameThread thread, String message);
}
