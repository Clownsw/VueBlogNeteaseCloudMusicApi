package cn.smilex.vueblog.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author smilex
 * @date 2022/9/11/18:14
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
public class RequestConfig {
    @Value("${api.server.url}")
    private String url;

    @Value("${api.server.serverUrl}")
    private String serverUrl;

    private String cookie;

    @Value("${api.server.email}")
    private String email;

    @Value("${api.server.passWord}")
    private String passWord;

    @Value("${api.server.splitCount}")
    private Integer splitCount;

    @Value("${api.server.defaultMusicLevel}")
    private String defaultMusicLevel;

    @Value("${api.server.redisTtlType}")
    private RedisTtlType redisTtlType;

    @Value("${api.server.redisTtl}")
    private Long redisTtl;

    @Value("${api.server.redisMusicInfoCachePrefix}")
    private String redisMusicInfoCachePrefix;

    @Value("${api.server.redisLyricCachePrefix}")
    private String redisLyricCachePrefix;

    @Value("${api.server.redisMusicUrlCachePrefix}")
    private String redisMusicUrlCachePrefix;

    @Value("${api.server.redisNetEaseCloudCache}")
    private String redisNetEaseCloudCache;

    @Value("${api.server.redisNetEaseCloudStatusCache}")
    private String redisNetEaseCloudStatusCache;
}
