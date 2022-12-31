package cn.smilex.vueblog;

import cn.smilex.vueblog.util.CommonUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author smilex
 * @date 2022/9/11/18:13
 * @since 1.0
 */
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
public class VueBlogNeteaseCloudMusicApiApplication {

    public static void main(String[] args) {
        CommonUtil.APPLICATION_CONTEXT = SpringApplication.run(VueBlogNeteaseCloudMusicApiApplication.class, args);
    }

}
