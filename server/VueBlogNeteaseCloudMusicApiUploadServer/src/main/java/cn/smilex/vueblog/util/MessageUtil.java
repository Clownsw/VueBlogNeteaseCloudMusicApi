package cn.smilex.vueblog.util;

import cn.smilex.vueblog.Application;
import cn.smilex.vueblog.config.MessageCode;
import cn.smilex.vueblog.protocol.Message;
import cn.smilex.vueblog.util.impl.HashMapBuilder;
import io.netty.channel.Channel;

import java.util.Map;

import static cn.smilex.vueblog.config.MessageCode.DEFAULT_REQUEST_OR_RESPONSE;

/**
 * @author smilex
 * @date 2022/9/24/11:08
 * @since 1.0
 */
public final class MessageUtil {

    public static Message buildMessage(Map<String, Object> content) {
        return buildMessage(DEFAULT_REQUEST_OR_RESPONSE, content);
    }

    public static Message buildMessage(Integer actionType, Map<String, Object> content) {
        return new Message(actionType, content);
    }

    public static void buildAndMessageMessage(Channel channel, Integer actionType, Map<String, Object> content) {
        channel.writeAndFlush(buildMessage(actionType, content));
    }

    public static void buildAndSendResponseMessage(Channel channel, String filePath, String musicId) {
        MessageUtil.buildAndMessageMessage(
                channel,
                MessageCode.RESPONSE_UPLOAD_RESULT,
                new HashMapBuilder<String, Object>(2)
                        .put("url", Application.COMMON_CONFIG.getUrl() + filePath)
                        .put("musicId", musicId)
                        .getMap()
        );
    }
}
