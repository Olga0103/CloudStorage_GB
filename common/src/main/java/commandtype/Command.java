package commandtype;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Command extends BaseMessage {

    public enum BaseCommand {
        DELETE, RENAME, NEW
    }

    private BaseCommand command;
    private String newPath;
    private String oldPath;

    public Command(Path path, BaseCommand command) {
        this.command = command;
        if (path != null) {
            this.newPath = Paths.get("server/src/main/java").resolve(path).toString();
        }
    }

    public Command(Path oldName, Path newName, BaseCommand command) {
        this.oldPath = Paths.get("server/src/main/java").resolve(oldName).toString();
        this.newPath = Paths.get("server/src/main/java").resolve(newName).toString();
        this.command = command;
    }

    public BaseCommand getCommand() {
        return command;
    }

    public String getPath() {
        return newPath;
    }

    public String getOldPath() {
        return oldPath;
    }

}
