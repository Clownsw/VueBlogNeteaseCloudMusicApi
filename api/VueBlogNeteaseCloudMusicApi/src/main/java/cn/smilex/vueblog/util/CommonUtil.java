package cn.smilex.vueblog.util;

import cn.smilex.vueblog.concurrent.CounterThreadFactory;
import cn.smilex.vueblog.config.MusicType;
import cn.smilex.vueblog.config.RedisTtlType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.MusicDto;
import cn.smilex.vueblog.model.Tuple;
import cn.smilex.vueblog.service.MusicApiService;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.impl.HashMapBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author smilex
 * @date 2022/9/19/21:46
 * @since 1.0
 */
@Component
public class CommonUtil {
    public static ConfigurableApplicationContext APPLICATION_CONTEXT = null;
    public static final String EMPTY_STRING = "";
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(
            new CounterThreadFactory("common-thread-pool")
    );
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final HashMap<String, String> KW_REQUEST_HEADERS = (HashMap<String, String>) new HashMapBuilder<String, String>(1)
            .put("user-agent", "okhttp/3.10.0")
            .getMap();
    public static final String KW_REQUEST_PARAM_TEMPLATE = "corp=kuwo&p2p=1&type=convert_url2&sig=0&format=mp3&rid=%s";

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
        return CommonUtil.OBJECT_MAPPER.readTree(json);
    }

    public String toJsonStr(Object object) throws JsonProcessingException {
        return CommonUtil.OBJECT_MAPPER.writeValueAsString(object);
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
            MusicDto musicDto = CommonUtil.OBJECT_MAPPER.readValue(json, new TypeReference<MusicDto>() {
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
        submit(() -> musicService.cacheMusicNotFreeInAll(musicType, musicId, result));
        return result;
    }

    public static Future<?> submit(Runnable runnable) {
        return CommonUtil.THREAD_POOL.submit(runnable);
    }
}
