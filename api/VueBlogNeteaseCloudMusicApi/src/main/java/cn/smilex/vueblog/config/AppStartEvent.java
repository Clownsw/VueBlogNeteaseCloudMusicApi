package cn.smilex.vueblog.config;

import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author smilex
 * @date 2022/9/24/22:17
 * @since 1.0
 */
@Slf4j
@Component
public class AppStartEvent implements ApplicationListener<ApplicationContextEvent> {

    private RequestConfig requestConfig;
    private MusicService musicService;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationContextEvent event) {
        List<Music> musicList = musicService.list();

        if (musicList.size() > 0) {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            for (Music music : musicList) {
                CommonUtil.THREAD_POOL.submit(() -> {
                    valueOperations.set(requestConfig.getRedisMusicUrlCachePrefix() + music.getMusicId(), music.getMusicUrl());
                    valueOperations.set(requestConfig.getRedisNetEaseCloudStatusCache() + music.getMusicId(), music.getNotFree().toString());
                });
            }
        }
    }
}
