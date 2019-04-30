import java.util.Vector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {

    //private Vector<ClientHandler> clients;

    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        AuthService.connect();
        System.out.println("Сервер включен!");
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new InputHandler()
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(8189).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            AuthService.disconnect();
        }
    }

//    public void broadcastMsg(ClientHandler client, String msg) {
//        for (ClientHandler o: clients) {
//            o.sendMsg(dateFormat.format(date) + "\n" + msg);
//        }
//    }
//
//    public ClientHandler getClientByNick(String nick){
//        ClientHandler client = null;
//        for (ClientHandler c:clients) {
//            if (c.getNick().equals(nick)){
//                client = c;
//            }
//        }
//        return client;
//    }

}
