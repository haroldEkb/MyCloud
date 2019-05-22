import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class InputHandler extends ChannelInboundHandlerAdapter {
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
                    Path path = Paths.get("server_storage/" + fm.getFilename());
                    if (Files.exists(path)) {
                        System.out.println("File exists");
                        if (Files.size(path) <= FileBox.PACKAGE_VOLUME){
                            FileBox fb = new FileBox(path);
                            ctx.writeAndFlush(fb);
                            System.out.println("File is sent");
                        } else {
                            FileBox fb = new FileBox(path.getFileName().toString());
                            System.out.println(fb.getFileName());
                            RandomAccessFile raf = new RandomAccessFile("server_storage/" + path.getFileName(), "r");
                            raf.seek(fm.getFilePointer());
                            System.out.println(fm.getFilePointer());
                            System.out.println(raf.getFilePointer());
                            int count = 0;
                            while (raf.length() - raf.getFilePointer() > (long)FileBox.PACKAGE_VOLUME && count < 50){
                                FileBox.writeInBox(fb, raf, false);
                                ctx.writeAndFlush(fb);
                                count++;
                            }
                            System.out.println("File is partially sent");
                            System.out.println(raf.getFilePointer());
                            if (count == 50) ctx.writeAndFlush(new FileMessage(
                                    MessageType.RELOAD,
                                    fm.getFilename(),
                                    raf.getFilePointer()));
//                            do {
//                                count++;
//                                FileBox.writeInBox(fb, raf, false);
//                                ctx.writeAndFlush(fb);
//                                if (count == 50) {
//                                    break;
//                                }
//                            } while (raf.length() - raf.getFilePointer() > (long)FileBox.PACKAGE_VOLUME);
                            else {
                                FileBox.writeInBox(fb, raf, true);
                                ctx.writeAndFlush(fb);
                                System.out.println("File is completely sent");
                            }
                            raf.close();
                        }
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
