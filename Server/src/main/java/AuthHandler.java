import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private boolean isAuthorised = false;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (!isAuthorised) {
            if (msg instanceof MessageBox){
                MessageBox mb = (MessageBox) msg;
                System.out.println("auth try");
                if (mb instanceof AuthMessage){
                    AuthMessage am = (AuthMessage) mb;
                    String[] user = am.getMsg().split(" ");
                    if (AuthService.checkUserByLoginAndPass(user[0], Integer.parseInt(user[1]))){
                        isAuthorised = true;
                        System.out.println("auth success");
                        ctx.writeAndFlush(new AuthMessage("/authOk"));
                    } else {
                        System.out.println("auth failed");
                        ctx.writeAndFlush(new ErrorMessage(Errors.WRONG_LOGIN_OR_PASSWORD));
                    }
                }
            }
        } else ctx.fireChannelRead(msg);;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}