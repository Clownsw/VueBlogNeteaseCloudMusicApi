package cn.smilex.vueblog.util;

import cn.smilex.vueblog.config.MessageCode;
import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.util.impl.HashMapBuilder;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static cn.smilex.vueblog.config.MessageCode.DEFAULT_REQUEST_OR_RESPONSE;

/**
 * @author smilex
 * @date 2022/9/24/11:08
 * @since 1.0
 */
@Slf4j
public final class MessageUtil {
    public static Message buildMessage(Map<String, Object> content) {
        return buildMessage(DEFAULT_REQUEST_OR_RESPONSE, content);
    }

    public static Message buildMessage(Integer actionType, Map<String, Object> content) {
        return new Message(actionType, content);
    }

    public static void buildAndMessageMessage(Channel channel, Integer actionType, Map<String, Object> content) {
        if (channel != null) {
            channel.writeAndFlush(buildMessage(actionType, content));
        }
    }

    public static void sendMessageToServer(Channel channel, String url, String id, String filePath) {
        CommonUtil.submit(() -> {
            try {
                MessageUtil.buildAndMessageMessage(
                        channel,
                        MessageCode.REQUEST_DOWNLOAD_AND_UPLOAD,
                        new HashMapBuilder<String, Object>(3)
                                .put("url", url)
                                .put("musicId", id)
                                .put("filePath", filePath + id)
                                .getMap()
                );
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }
}
