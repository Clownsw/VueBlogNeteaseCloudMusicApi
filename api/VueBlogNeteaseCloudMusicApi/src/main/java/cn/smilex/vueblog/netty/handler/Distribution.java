package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.netty.protocol.Message;
import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.CommonUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;

import static cn.smilex.vueblog.config.MessageCode.*;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Slf4j
public class Distribution {
    private static final RequestConfig REQUEST_CONFIG;
    private static final RedisTemplate<String, String> REDIS_TEMPLATE;

    private static final MusicService MUSIC_SERVICE;

    static {
        REQUEST_CONFIG = CommonUtil.APPLICATION_CONTEXT
                .getBean(RequestConfig.class);
        REDIS_TEMPLATE = (RedisTemplate<String, String>) CommonUtil.APPLICATION_CONTEXT
                .getBean("stringRedisTemplate");

        MUSIC_SERVICE = CommonUtil.APPLICATION_CONTEXT
                .getBean(MusicService.class);
    }

    public static void run(Message message) {
        log.info("{}", message);
        switch (message.getActionType()) {
            case RESPONSE_UPLOAD_RESULT: {
                CommonUtil.submit(() -> {
                    Map<String, Object> content = message.getContent();
                    String url = (String) content.get("url");
                    String musicId = (String) content.get("musicId");

                    ValueOperations<String, String> valueOperations = REDIS_TEMPLATE.opsForValue();

                    valueOperations.set(REQUEST_CONFIG.getRedisMusicUrlCachePrefix() + musicId, url);
                    valueOperations.set(REQUEST_CONFIG.getRedisNetEaseCloudStatusCache() + musicId, "true");

                    Music music = new Music();
                    music.setId(Long.parseLong(musicId));
                    music.setMusicUrl(url);
                    boolean result = MUSIC_SERVICE.update(
                            new LambdaUpdateWrapper<Music>()
                                    .eq(Music::getMusicId, Long.parseLong(musicId))
                                    .set(Music::getMusicUrl, url)
                    );
                    if (!result) {
                        log.info("update music url error!");
                    }
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
