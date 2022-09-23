package cn.smilex.vueblog;

import cn.smilex.vueblog.handler.NettyChannelHandler;
import cn.smilex.vueblog.protocol.MessageCodec;
import cn.smilex.vueblog.protocol.ProtocolFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author smilex
 * @date 2022/9/21/22:09
 * @since 1.0
 */
public class Application {
    public static void main(String[] args) {
        final NioEventLoopGroup masterGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final LoggingHandler loggingHandler = new LoggingHandler();
        final MessageCodec messageCodec = new MessageCodec();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(masterGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, Integer.MAX_VALUE)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(loggingHandler)
                                    .addLast(new ProtocolFrameHandler())
                                    .addLast(messageCodec)
                                    .addLast(new NettyChannelHandler());
                        }
                    })
                    .bind("127.0.0.1", 1233)
                    .sync();
            channelFuture
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            masterGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
