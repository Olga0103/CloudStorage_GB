package commandtype;


import java.nio.file.Path;
import java.nio.file.Paths;

public class DirRequest extends BaseMessage{
    String directory;

    public DirRequest(Path directory) {
        if (directory != null) {
            this.directory = Paths.get("server/src/main/java").resolve(directory).toString();
        }
    }

    public String getDirectory() {

        return directory;
    }
}
