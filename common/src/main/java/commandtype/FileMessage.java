package commandtype;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends BaseMessage{

    private String dstPath;
    private String name;
    private byte[] data;

    public FileMessage(Path path) {
        this.name = path.getFileName().toString();
        try {
            this.data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileMessage(Path srcPath, Path dstPath) {
        this.dstPath = dstPath.resolve(srcPath.getFileName()).toString();
        try {
            this.data = Files.readAllBytes(srcPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getDstPath() {
        return dstPath;
    }

    public byte[] getData() {
        return data;
    }

}
