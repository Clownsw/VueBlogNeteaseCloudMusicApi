package cn.smilex.vueblog.util;

import cn.smilex.vueblog.config.MusicType;
import cn.smilex.vueblog.config.RedisTtlType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.MusicDto;
import cn.smilex.vueblog.model.Tuple;
import cn.smilex.vueblog.service.MusicApiService;
import cn.smilex.vueblog.service.MusicService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * @author smilex
 * @date 2022/9/19/21:46
 * @since 1.0
 */
@Component
public class CommonUtil {
    public static final String EMPTY_STRING = "";

    private RedisTemplate<String, String> redisTemplate;
    private RequestConfig requestConfig;
    private MusicApiService musicApiService;
    private MusicService musicService;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Autowired
    public void setMusicApiService(MusicApiService musicApiService) {
        this.musicApiService = musicApiService;
    }

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    public Duration parseRedisTtlType() {
        RedisTtlType redisTtlType = requestConfig.getRedisTtlType();
        Duration ttl;

        switch (redisTtlType) {
            case NANOS: {
                ttl = Duration.ofNanos(requestConfig.getRedisTtl());
                break;
            }
            case MILLLIS: {
                ttl = Duration.ofMillis(requestConfig.getRedisTtl());
                break;
            }
            case MINUTES: {
                ttl = Duration.ofMinutes(requestConfig.getRedisTtl());
                break;
            }
            case SECONDS: {
                ttl = Duration.ofSeconds(requestConfig.getRedisTtl());
                break;
            }
            case HOURS: {
                ttl = Duration.ofHours(requestConfig.getRedisTtl());
                break;
            }
            case DAYS: {
                ttl = Duration.ofDays(requestConfig.getRedisTtl());
                break;
            }

            default: {
                throw new RuntimeException("unknown redis ttl type");
            }
        }

        return ttl;
    }

    public JsonNode toJsonNode(String json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json);
    }

    public String toJsonStr(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public Set<String> getRedisNetEaseCloudCacheSet() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        return setOperations.members(requestConfig.getRedisNetEaseCloudCache());
    }

    public MusicDto getMusicDtoInRedisNetEaseCloudCacheSetById(Long id) throws JsonProcessingException {
        Set<String> netEaseCloudCacheSet = getRedisNetEaseCloudCacheSet();
        if (netEaseCloudCacheSet == null || netEaseCloudCacheSet.size() == 0) {
            return null;
        }

        for (String json : netEaseCloudCacheSet) {
            MusicDto musicDto = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
            if (musicDto.getId() != null && musicDto.getId().equals(id) && musicDto.getKuWoId() != null) {
                return musicDto;
            }
        }

        return null;
    }

    public Tuple<Boolean, String> getMusicIsNoteFreeAndUrl(String musicId, String level) throws JsonProcessingException {
        String songJson = musicApiService.newSongUrl(musicId, level);
        JsonNode tmp = toJsonNode(songJson)
                .get("data")
                .get(0);
        return new Tuple<>(
                tmp
                        .get("freeTrialInfo")
                        .isNull(),
                tmp.get("url")
                        .asText()
        );
    }

    public Boolean musicIsNotFree(MusicType musicType, String musicId, String level) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(requestConfig.getRedisNetEaseCloudStatusCache() + musicId);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }

        Boolean musicNotFreeFiledByMusicId = musicService.getMusicNotFreeFiledByMusicId(Long.parseLong(musicId));
        if (musicNotFreeFiledByMusicId != null) {
            return musicNotFreeFiledByMusicId;
        }

        Boolean result = getMusicIsNoteFreeAndUrl(musicId, level).getLeft();
        createVirtualThread(() -> musicService.cacheMusicNotFreeInAll(musicType, musicId, result));
        return result;
    }

    public Thread createVirtualThread(Runnable runnable) {
        return Thread.ofVirtual()
                .start(runnable);
    }
}
