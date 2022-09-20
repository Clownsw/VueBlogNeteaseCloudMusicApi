package cn.smilex.vueblog.util;

import cn.smilex.vueblog.config.RedisTtlType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.MusicDto;
import cn.smilex.vueblog.model.Tuple;
import cn.smilex.vueblog.service.MusicApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
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

    private RedisTemplate<String, String> redisTemplate;
    private RequestConfig requestConfig;
    private MusicApiService musicApiService;

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

    public Duration parseRedisTtlType() {
        RedisTtlType redisTtlType = requestConfig.getRedisTtlType();
        Duration ttl;

        switch (redisTtlType) {
            case NANOS -> ttl = Duration.ofNanos(requestConfig.getRedisTtl());
            case MILLLIS -> ttl = Duration.ofMillis(requestConfig.getRedisTtl());
            case MINUTES -> ttl = Duration.ofMinutes(requestConfig.getRedisTtl());
            case SECONDS -> ttl = Duration.ofSeconds(requestConfig.getRedisTtl());
            case HOURS -> ttl = Duration.ofHours(requestConfig.getRedisTtl());
            case DAYS -> ttl = Duration.ofDays(requestConfig.getRedisTtl());

            default -> throw new RuntimeException("unknown redis ttl type");
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

    public Tuple<Boolean, String> isNotFree(String id, String level) throws JsonProcessingException {
        String songJson = musicApiService.newSongUrl(id, level);
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
}
