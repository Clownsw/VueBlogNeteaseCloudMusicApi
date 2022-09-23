package cn.smilex.vueblog.util;

import cn.smilex.vueblog.netty.protocol.Message;

import java.util.Map;

import static cn.smilex.vueblog.config.ResponseCode.DEFAULT_REQUEST_OR_RESPONSE;

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
}
