package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static cn.smilex.vueblog.config.MessageCode.*;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@Slf4j
public class Distribution {
    @SuppressWarnings("unchecked")
    public static void run(Message message) {
        log.info("{}", message);
        switch (message.getActionType()) {
            case RESPONSE_UPLOAD_RESULT: {
                Thread.ofVirtual()
                        .start(() -> {
                            Map<String, Object> content = message.getContent();
                            var url = (String) content.get("url");
                            var musicId = (String) content.get("musicId");

                            var requestConfig = CommonUtil.APPLICATION_CONTEXT
                                    .getBean(RequestConfig.class);
                            var redisTemplate = (RedisTemplate<String, String>) CommonUtil.APPLICATION_CONTEXT
                                    .getBean("stringRedisTemplate");
                            var musicService = CommonUtil.APPLICATION_CONTEXT
                                    .getBean(MusicService.class);
                            var valueOperations = redisTemplate.opsForValue();

                            valueOperations.set(requestConfig.getRedisMusicUrlCachePrefix() + musicId, url);
                            valueOperations.set(requestConfig.getRedisNetEaseCloudStatusCache() + musicId, "true");

                            var music = new Music();
                            music.setId(Long.parseLong(musicId));
                            music.setMusicUrl(url);
                            musicService.updateById(music);
                        });
                break;
            }

            case DEFAULT_REQUEST_OR_RESPONSE:
            case REQUEST_DOWNLOAD_AND_UPLOAD:
            default: {
            }
        }
    }
}
