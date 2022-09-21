package cn.smilex.vueblog.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * @author smilex
 * @date 2022/9/21/22:02
 * @since 1.0
 */
public class NettyClient {

    @SneakyThrows
    public NettyClient() {

        final NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);

        final LoggingHandler loggingHandler = new LoggingHandler();

        ChannelFuture channelFuture = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(loggingHandler);
                    }
                })
                .connect("127.0.0.1", 1234);
        channelFuture.sync()
                .channel();
        System.out.println("123");
    }
}
