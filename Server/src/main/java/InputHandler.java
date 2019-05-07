import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class InputHandler extends ChannelInboundHandlerAdapter {
    private boolean isAuthorised = false;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof MessageBox) {
                MessageBox mb = (MessageBox) msg;
                System.out.println("Command is received");
                if (mb instanceof FileMessage){
                    FileMessage fm = (FileMessage) mb;
                    if (Files.exists(Paths.get("server_storage/" + fm.getFilename()))) {
                        System.out.println("File exists");
                        FileBox fb = new FileBox(Paths.get("server_storage/" + fm.getFilename()));
                        ctx.writeAndFlush(fb);
                        System.out.println("File is sent");
                    } else {
                        ctx.writeAndFlush(new ErrorMessage(Errors.FILE_DOES_NOT_EXIST));
                    }
                }
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
