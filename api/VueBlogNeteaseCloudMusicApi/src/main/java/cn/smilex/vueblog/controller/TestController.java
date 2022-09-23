package cn.smilex.vueblog.controller;

import cn.smilex.vueblog.netty.NettyClient;
import cn.smilex.vueblog.protocol.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author YangLuJia
 * @date 2022/9/23 16:09
 */
@RestController
public class TestController {
    private NettyClient nettyClient;

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @SneakyThrows
    @GetMapping("/test")
    public String test(@RequestParam("value") String value) {
        var channel = nettyClient.getChannel();

        Message message = new Message();
        var content = new HashMap<String, Object>();
        content.put("name", "test");
        message.setContent(content);
        var buffer = new ObjectMapper().writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = channel.alloc().buffer(4 + buffer.length);
        buf.writeInt(buffer.length);
        buf.writeBytes(buffer);
        channel.writeAndFlush(buf);

        return "test";
    }
}
