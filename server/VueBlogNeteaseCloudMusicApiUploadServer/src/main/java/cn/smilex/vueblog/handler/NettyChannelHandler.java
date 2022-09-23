package cn.smilex.vueblog.handler;

import cn.smilex.vueblog.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author smilex
 * @date 2022/9/21/22:22
 * @since 1.0
 */
@Slf4j
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null) {
            if (msg instanceof Message) {
                var message = (Message) msg;
                Distribution.run(ctx, message);
            }
        }
    }
}
