package cn.smilex.vueblog.protocol;

import cn.smilex.vueblog.util.CommonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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
        byte[] data = CommonUtil.encrypt(
                CommonUtil.OBJECT_MAPPER
                        .writeValueAsString(msg)
                        .getBytes(StandardCharsets.UTF_8)
        );

        ByteBuf buf = ctx.alloc()
                .buffer(4 + data.length);
        buf.writeInt(data.length);
        buf.writeBytes(data);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        int length = msg.readInt();
        if (length > 0) {
            try {
                byte[] data = CommonUtil.decrypt(
                        ByteBufUtil.getBytes(msg.readBytes(length))
                );

                out.add(
                        CommonUtil.OBJECT_MAPPER
                                .readValue(new String(data, StandardCharsets.UTF_8), new TypeReference<Message>() {
                                })
                );
            } catch (Exception ignore) {
                log.error("解密失败, remoteAddress: {}", ctx.channel().remoteAddress());
            }
        }
    }
}
