import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private boolean isAuthorised = false;
//    private int usedID = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (!isAuthorised) {
            if (msg instanceof MessageBox){
                MessageBox mb = (MessageBox) msg;
                System.out.println("auth try");
                if (mb instanceof AuthMessage){
                    AuthMessage am = (AuthMessage) mb;
                    String[] user = am.getMsg().split(" ");
                    int id = AuthService.getUserIDByLoginAndPass(user[0], Integer.parseInt(user[1]));
                    if (id != 0){
                        if (!Server.isAuthorized(id)){
//                            usedID = id;
                            Server.addUser(id);
                            isAuthorised = true;
                            System.out.println("auth success");
                            ctx.writeAndFlush(new AuthMessage("/authOk"));
                            am.setId(id);
                            ctx.fireChannelRead(mb);
                        } else ctx.writeAndFlush(new ErrorMessage(Errors.THIS_USED_IS_ALREADY_AUTHORIZED));
                    } else {
                        System.out.println("auth failed");
                        ctx.writeAndFlush(new ErrorMessage(Errors.WRONG_LOGIN_OR_PASSWORD));
                    }
                }
            }
        } else {
            ctx.fireChannelRead(msg);
//            AbstractBox ab = (AbstractBox) msg;
//            ab.setId(usedID);
//            ctx.fireChannelRead(ab);
        }
    }
    //Регистрация еще не доработана до конца
    private void userRegistration(String login, int password) throws IOException {
        AuthService.addUser(login, password);
        Files.createDirectory(Paths.get("server_storage/" + AuthService.getUserIDByLoginAndPass(login, password)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}