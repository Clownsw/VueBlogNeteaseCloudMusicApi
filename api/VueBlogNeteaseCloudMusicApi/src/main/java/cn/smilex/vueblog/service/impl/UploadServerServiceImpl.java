package cn.smilex.vueblog.service.impl;

import cn.smilex.vueblog.netty.NettyClient;
import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.service.UploadServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author smilex
 * @date 2022/9/24/11:06
 * @since 1.0
 */
@Service
public class UploadServerServiceImpl implements UploadServerService {
    private NettyClient nettyClient;

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * 向上传服务器发送一条消息
     *
     * @param message 消息对象
     */
    @Override
    public void sendMessage(Message message) {
        nettyClient.getChannel()
                .writeAndFlush(message);
    }
}
