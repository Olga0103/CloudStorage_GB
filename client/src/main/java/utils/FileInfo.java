package utils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
    public enum FileType {
        FILE("F"), DIRECTORY("D");
        private String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String name;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;

    public FileInfo(Path path) {
        try {
            this.name = path.getFileName().toString();
            this.size = Files.size(path);
            if (Files.isDirectory(path)) {
                type = FileType.DIRECTORY;
                this.size = -1L;
            } else {
                type = FileType.FILE;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            throw new RuntimeException(String.format("File initialization error at %s",path.normalize().toString()));
        }
    }

    public String getName() {

        return name;
    }

    public FileType getType() {

        return type;
    }

    public long getSize() {

        return size;
    }

    public LocalDateTime getLastModified() {

        return lastModified;
    }
}
