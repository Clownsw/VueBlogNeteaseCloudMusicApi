package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author smilex
 * @date 2022/9/24/2:30
 * @since 1.0
 */
@Slf4j
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Message) {
            CommonUtil.submit(() -> {
                try {
                    Distribution.run((Message) msg);
                } catch (Exception e) {
                    log.error("", e);
                }
            });
        }
    }
}
