import commandtype.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass() + " received");
        try {
            if (msg instanceof Command) {
                Command cmd = (Command) msg;
                ctx.fireChannelRead(cmd);
                System.out.println(cmd.getClass() + " transferred CommandHandler");
            } else if (msg instanceof FileRequest) {
                FileRequest request = (FileRequest) msg;
                Path path = Paths.get(request.getPath());
                System.out.println(path.toString());
                FileMessage file = new FileMessage(path);
                ctx.writeAndFlush(file);
                System.out.println(file.getClass() + " sent");
            } else if (msg instanceof FileMessage) {
                FileMessage file = (FileMessage) msg;
                Path path = Paths.get("server/src/main/java").resolve(Paths.get(file.getDstPath()));
                Files.write(path, file.getData(), StandardOpenOption.CREATE);
                System.out.println(path.toString() + " created");
            } else if (msg instanceof DirRequest) {
                DirRequest dir = (DirRequest) msg;
                DirMessage dirMsg;
                if (dir.getDirectory() == null) {
                    dirMsg = new DirMessage(Paths.get("server/src/main/java/root"));
                } else {
                    dirMsg = new DirMessage(Paths.get(dir.getDirectory()));
                }
                ctx.writeAndFlush(dirMsg);
                System.out.println(dirMsg.getClass() + " sent");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}


