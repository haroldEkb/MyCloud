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
        if (!isAuthorised) {
            if (msg instanceof Command){
                Command command = (Command) msg;
                System.out.println("auth try");
                if (command.getId() == Commands.AUTH){
                    String[] user = command.getMsg().split(" ");
                    if (AuthService.checkUserByLoginAndPass(user[0], Integer.parseInt(user[1]))){
                        isAuthorised = true;
                        System.out.println("auth success");
                        ctx.writeAndFlush(new Command(Commands.AUTH));
                    } else {
                        System.out.println("auth failed");
                        ctx.writeAndFlush(new Command(Commands.ERROR, Errors.WRONG_LOGIN_OR_PASSWORD));
                    }
                }
            }
        } else
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof Command) {
                Command command = (Command) msg;
                System.out.println("Command is received");
                System.out.println(command.getId());
                if (command.getId() == Commands.RECEIVE_FILE){
                    if (Files.exists(Paths.get("server_storage/" + command.getFileName()))) {
                        System.out.println("File exists");
                        FileBox fb = new FileBox(Paths.get("server_storage/" + command.getFileName()));
                        ctx.writeAndFlush(fb);
                        System.out.println("File is sent");
                    }
                }
            }
            if (msg instanceof FileBox) {
                FileBox fb = (FileBox) msg;
                Files.write(Paths.get("server_storage/" + fb.getFileName()),
                        fb.getContent(),
                        StandardOpenOption.CREATE);
                ctx.writeAndFlush(new Command(Commands.SERVER_RESPONSE, fb.getFileName(), "доставлен успешно"));
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
