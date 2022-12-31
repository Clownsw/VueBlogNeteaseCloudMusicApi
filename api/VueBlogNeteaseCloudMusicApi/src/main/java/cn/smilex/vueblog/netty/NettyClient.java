package cn.smilex.vueblog.netty;

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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
    private final AtomicInteger count;
    private final AtomicLong nextTime;

    private RequestConfig requestConfig;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public Channel getChannel() {
        if (this.channel == null || !this.channel.isActive()) {
            if (requestConfig.getEnableUploadServer()) {
                if (this.nextTime.get() > System.currentTimeMillis()) {
                    this.channel = null;
                    return null;
                } else {
                    this.nextTime.set(0);
                }

                // 允许一分钟内重连三次
                if (this.count.incrementAndGet() == 3) {
                    // 更新下一次时间
                    this.nextTime.set(System.currentTimeMillis() + 60000);

                    // 重置计数器
                    this.count.set(0);

                    this.channel = null;
                    return null;
                }

                try {
                    connection("127.0.0.1", 1233);
                } catch (InterruptedException ignore) {
                }
            }
        }
        return channel;
    }

    @SneakyThrows
    public NettyClient() {
        this.count = new AtomicInteger(0);
        this.nextTime = new AtomicLong(0);

        try {
            createClient();
            connection("127.0.0.1", 1233);
        } catch (Exception ignore) {
        }
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