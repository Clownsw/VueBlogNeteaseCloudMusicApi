package cn.smilex.vueblog;

import cn.smilex.vueblog.config.CommonConfig;
import cn.smilex.vueblog.handler.NettyChannelHandler;
import cn.smilex.vueblog.protocol.MessageCodec;
import cn.smilex.vueblog.protocol.ProtocolFrameHandler;
import cn.smilex.vueblog.util.CommonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.upyun.RestManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * @author smilex
 * @date 2022/9/21/22:09
 * @since 1.0
 */
@Slf4j
public class Application {

    public static CommonConfig COMMON_CONFIG = null;
    public static RestManager REST_MANAGER = null;

    public static void initConfig() {
        try (InputStream resourceAsStream = Application.class.getResourceAsStream("/application.json")) {
            assert resourceAsStream != null;

            COMMON_CONFIG = CommonUtil.OBJECT_MAPPER
                    .readValue(
                            IOUtils.toByteArray(resourceAsStream),
                            new TypeReference<CommonConfig>() {
                            }
                    );
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void initRestManager() {
        REST_MANAGER = new RestManager(
                COMMON_CONFIG.getBucketName(),
                COMMON_CONFIG.getUserName(),
                COMMON_CONFIG.getPassword()
        );
        REST_MANAGER.setTimeout(COMMON_CONFIG.getUploadTimeOut());
    }

    public static void initServer() {
        final NioEventLoopGroup masterGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        final MessageCodec messageCodec = new MessageCodec();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(masterGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, Integer.MAX_VALUE)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
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

    public static void main(String[] args) {
        initConfig();
        initRestManager();
        initServer();
    }
}
