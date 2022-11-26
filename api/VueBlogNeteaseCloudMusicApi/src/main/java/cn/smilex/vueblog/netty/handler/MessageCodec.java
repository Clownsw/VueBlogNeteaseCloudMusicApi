package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.util.CommonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author smilex
 * @date 2022/9/23 16:28
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        byte[] jsonBytes = CommonUtil.OBJECT_MAPPER
                .writeValueAsString(msg)
                .getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = ctx.alloc()
                .buffer(4 + jsonBytes.length);
        buf.writeInt(jsonBytes.length);
        buf.writeBytes(jsonBytes);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readInt();
        ByteBuf buf = msg.readBytes(length);
        out.add(
                CommonUtil.OBJECT_MAPPER
                        .readValue(new String(ByteBufUtil.getBytes(buf), StandardCharsets.UTF_8), new TypeReference<Message>() {
                        })
        );
    }
}
