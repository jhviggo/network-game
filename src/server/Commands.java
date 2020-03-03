package server;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    private static List<String> commands = List.of("ADDPLAYER", "GETPLAYER");

    public static boolean contains(final String command) {
        return commands.contains(command);
    }

    public static List<String> getAll() {
        return new ArrayList<>(commands);
    }
}
