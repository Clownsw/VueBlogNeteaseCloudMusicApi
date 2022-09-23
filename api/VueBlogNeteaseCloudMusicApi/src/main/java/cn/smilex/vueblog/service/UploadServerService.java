package cn.smilex.vueblog.service;

import cn.smilex.vueblog.netty.protocol.Message;

/**
 * @author smilex
 * @date 2022/9/24/11:06
 * @since 1.0
 */
public interface UploadServerService {
    /**
     * 向上传服务器发送一条消息
     *
     * @param message 消息对象
     */
    void sendMessage(Message message);
}
