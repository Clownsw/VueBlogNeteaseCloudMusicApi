package cn.smilex.vueblog.netty;

import cn.smilex.vueblog.config.ErrorCode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author smilex
 * @date 2022/9/21/22:02
 * @since 1.0
 */
@Slf4j
@Component
public class NettyClient {
    private final Channel channel;

    public Channel getChannel() {
        if (this.channel == null || !this.channel.isActive()) {
            log.error(ErrorCode.CONNECTION_EXCEPTION_CLOSE.getErrorMessage());
            System.exit(ErrorCode.CONNECTION_EXCEPTION_CLOSE.getErrorCode());
        }
        return channel;
    }

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
                .connect("127.0.0.1", 1233);
        channel = channelFuture.sync()
                .channel();
    }
}
