package cn.smilex.vueblog.util;

import cn.smilex.vueblog.Application;
import cn.smilex.vueblog.config.MessageCode;
import cn.smilex.vueblog.protocol.Message;
import io.netty.channel.Channel;

import java.util.HashMap;
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
        var responseMessageContent = new HashMap<String, Object>(1);
        responseMessageContent.put("url", Application.COMMON_CONFIG.getUrl() + filePath);
        responseMessageContent.put("musicId", musicId);
        MessageUtil.buildAndMessageMessage(
                channel,
                MessageCode.RESPONSE_UPLOAD_RESULT,
                responseMessageContent
        );
    }
}
