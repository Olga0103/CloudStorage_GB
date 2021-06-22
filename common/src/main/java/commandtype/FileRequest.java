package commandtype;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRequest extends BaseMessage {

    private String path;

    public FileRequest(Path path) {
        this.path = Paths.get("server/src/main/java").resolve(path).toString();
    }

    public String getPath() {
        return path;
    }

}
