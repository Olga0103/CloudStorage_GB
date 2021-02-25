package commandtype;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirMessage extends BaseMessage {
    private String [] dir;
    private String path;

    public DirMessage(Path path) {
        Path p = path;
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < p.getNameCount(); i++) {
            sb.append(path.getName(i)).append("\\");
        }
        this.path = sb.toString();
        sb.setLength(0);
        try {
            dir = Files.list(path).map(str -> str.toString()).toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getDir() {

        return dir;
    }

    public String getPath() {

        return path;
    }
}
