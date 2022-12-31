package cn.smilex.vueblog.netty;

import cn.smilex.vueblog.config.ErrorCode;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.netty.handler.MessageCodec;
import cn.smilex.vueblog.netty.handler.NettyChannelHandler;
import cn.smilex.vueblog.netty.handler.ProtocolFrameHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author smilex
 * @date 2022/9/21/22:02
 * @since 1.0
 */
@SuppressWarnings("UnusedReturnValue")
@Slf4j
@Component
public class NettyClient {
    private Bootstrap bootstrap;
    private Channel channel;

    private RequestConfig requestConfig;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public Channel getChannel() {
        if (this.channel == null || !this.channel.isActive()) {
            if (requestConfig.getEnableUploadServer()) {
                log.error(ErrorCode.CONNECTION_EXCEPTION_CLOSE.getErrorMessage());
                System.exit(ErrorCode.CONNECTION_EXCEPTION_CLOSE.getErrorCode());
            }
        }
        return channel;
    }

    @SneakyThrows
    public NettyClient() {
        createClient();
        connection("127.0.0.1", 1233);
    }

    public void createClient() {
        try {
            final NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);

            final LoggingHandler loggingHandler = new LoggingHandler();
            final MessageCodec messageCodec = new MessageCodec();

            this.bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline()
                                    .addLast(loggingHandler)
                                    .addLast(new ProtocolFrameHandler())
                                    .addLast(messageCodec)
                                    .addLast(new NettyChannelHandler());
                        }
                    });
        } catch (Exception ignore) {
        }
    }

    public boolean connection(String host, int port) throws InterruptedException {
        this.channel = this.bootstrap.connect(host, port)
                .sync()
                .channel();
        return this.channel.isActive();
    }
}