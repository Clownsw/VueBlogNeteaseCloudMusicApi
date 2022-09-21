package cn.smilex.vueblog.config;

import cn.smilex.vueblog.netty.NettyClient;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author smilex
 * @date 2022/9/21/21:56
 * @since 1.0
 */
@Component
public class Start implements ApplicationListener<ServletWebServerInitializedEvent> {
    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        new NettyClient();
    }
}
