package cn.smilex.vueblog.util;

import cn.smilex.vueblog.config.RedisTtlType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.MusicDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
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
}
