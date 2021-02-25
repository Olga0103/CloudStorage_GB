import commandtype.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CommandHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command cmd = (Command) msg;
        try {
            if (cmd.getCommand() == Command.BaseCommand.NEW) {
                Path path = Paths.get(cmd.getPath());
                new File(path.toString()).mkdir();
                System.out.println("File created");
            } else if (cmd.getCommand() == Command.BaseCommand.RENAME) {
                try {
                    Path oldPath = Paths.get(cmd.getOldPath());
                    Path newPath = Paths.get(cmd.getPath());
                    Files.copy(oldPath, newPath);
                    Files.delete(oldPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("File renamed");
            } else if (cmd.getCommand() == Command.BaseCommand.DELETE) {
                Path path = Paths.get(cmd.getPath());
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    System.out.println("File deleted");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
