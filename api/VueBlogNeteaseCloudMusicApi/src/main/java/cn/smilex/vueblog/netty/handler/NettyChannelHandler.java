package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.netty.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author smilex
 * @date 2022/9/24/2:30
 * @since 1.0
 */
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Message) {
            var message = (Message) msg;
            Distribution.run(message);
        }
    }
}
