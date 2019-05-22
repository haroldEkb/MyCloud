import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileBoxHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof MessageBox) {
                ctx.fireChannelRead(msg);
            }
            if (msg instanceof FileBox) {
                FileBox fb = (FileBox) msg;
                if (fb.isFirst()){
                    Files.write(Paths.get("server_storage/" + fb.getFileName()),
                            fb.getContent(),
                            StandardOpenOption.CREATE);
                } else {
                    Files.write(Paths.get("server_storage/" + fb.getFileName()),
                            fb.getContent(),
                            StandardOpenOption.APPEND);
                }
                if (fb.isLast()){
                    ctx.writeAndFlush(new FileMessage(fb.getFileName(), "доставлен успешно"));
                }
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
